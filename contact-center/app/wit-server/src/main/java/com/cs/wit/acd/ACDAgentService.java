/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cs.wit.acd;

import com.cs.wit.acd.basic.ACDComposeContext;
import com.cs.wit.acd.basic.ACDMessageHelper;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.cache.RedisCommand;
import com.cs.wit.cache.RedisKey;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.AgentService;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserTask;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.User;
import com.cs.wit.peer.PeerSyncIM;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.AgentStatusRepository;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.persistence.repository.AgentUserTaskRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.AgentStatusProxy;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.socketio.message.Message;
import com.cs.wit.util.HashMapUtils;
import com.cs.wit.util.SerializeUtil;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ACDAgentService {
    private final static Logger logger = LoggerFactory.getLogger(ACDAgentService.class);

    private final RedisCommand redisCommand;

    private final ACDMessageHelper acdMessageHelper;

    private final AgentStatusProxy agentStatusProxy;

    private final ACDPolicyService acdPolicyService;

    private final PeerSyncIM peerSyncIM;

    private final Cache cache;

    private final AgentUserRepository agentUserRes;

    private final AgentServiceRepository agentServiceRes;

    private final AgentUserTaskRepository agentUserTaskRes;

    private final AgentStatusRepository agentStatusRes;

    private final OnlineUserRepository onlineUserRes;

    private final UserRepository userRes;

    private final AgentUserProxy agentUserProxy;

    /**
     * ACD????????????
     */
    public void notifyAgentUserProcessResult(@NonNull final ACDComposeContext ctx) {
        if (StringUtils.isNotBlank(ctx.getMessage())) {
            logger.info("[onConnect] find available agent for onlineUser id {}", ctx.getOnlineUserId());

            /*
             * ?????????????????????
             * ????????????AgentService??????AgentService???????????????AgentService???????????????????????????
             */
            if (ctx.getAgentService() != null && (!ctx.isNoagent()) && !StringUtils.equals(
                    MainContext.AgentUserStatusEnum.INQUENE.toString(),
                    ctx.getAgentService().getStatus())) {
                // ?????????????????????
                MainContext.getPeerSyncIM().send(MainContext.ReceiverType.AGENT,
                        MainContext.ChannelType.WEBIM,
                        ctx.getAppid(),
                        MainContext.MessageType.NEW,
                        ctx.getAgentService().getAgentno(),
                        ctx, true);
            }

            /*
             * ?????????????????????
             */
            Message outMessage = new Message();
            outMessage.setMessage(ctx.getMessage());
            outMessage.setMessageType(MainContext.MessageType.MESSAGE.toString());
            outMessage.setCalltype(MainContext.CallType.IN.toString());
            outMessage.setCreatetime(MainUtils.dateFormate.get().format(new Date()));
            outMessage.setNoagent(ctx.isNoagent());
            if (ctx.getAgentService() != null) {
                outMessage.setAgentserviceid(ctx.getAgentService().getId());
            }

            MainContext.getPeerSyncIM().send(MainContext.ReceiverType.VISITOR,
                    MainContext.ChannelType.WEBIM, ctx.getAppid(),
                    MainContext.MessageType.NEW, ctx.getOnlineUserId(), outMessage, true);


        } else {
            logger.info("[onConnect] Message not found for user {}", ctx.getOnlineUserId());
        }
    }

    /**
     * ?????????????????????????????????????????????????????? ????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????
     */
    @SuppressWarnings("UnusedReturnValue")
    public AgentService assignVisitorAsInvite(final String agentno, final AgentUser agentUser, final String orgi) {
        final AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);
        return pickupAgentUserInQueue(agentUser, agentStatus);
    }

    /**
     * ???????????????????????????
     */
    public void assignVisitors(String agentno, String orgi) {
        logger.info("[assignVisitors] agentno {}, orgi {}", agentno, orgi);
        // ???????????????????????????
        AgentStatus agentStatus = SerializeUtil.deserialize(
                redisCommand.getHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno));

        if (agentStatus == null) {
            logger.warn("[assignVisitors] can not find AgentStatus for agentno {}", agentno);
            return;
        }
        logger.info("[assignVisitors] agentStatus id {}, status {}, service {}/{}, skills {}, busy {}",
                agentStatus.getId(), agentStatus.getStatus(), agentStatus.getUsers(), agentStatus.getMaxusers(),
                HashMapUtils.concatKeys(agentStatus.getSkills(), "|"), agentStatus.isBusy());

        if ((!StringUtils.equals(
                MainContext.AgentStatusEnum.READY.toString(), agentStatus.getStatus())) || agentStatus.isBusy()) {
            // ?????????????????????????????????????????????????????????
            // ???????????????
            return;
        }

        // ????????????????????????????????????
        final Map<String, AgentUser> pendingAgentUsers = cache.getAgentUsersInQueByOrgi(orgi);
        final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
        // ??????????????????????????????
        int assigned = 0;
        int currentAssigned = cache.getInservAgentUsersSizeByAgentnoAndOrgi(
                agentStatus.getAgentno(), agentStatus.getOrgi());

        logger.info(
                "[assignVisitors] agentno {}, name {}, current assigned {}, batch size in queue {}",
                agentStatus.getAgentno(),
                agentStatus.getUsername(), currentAssigned, pendingAgentUsers.size());

        for (Map.Entry<String, AgentUser> entry : pendingAgentUsers.entrySet()) {
            AgentUser agentUser = entry.getValue();
            boolean process = false;

            if ((StringUtils.equals(agentUser.getAgentno(), agentno))) {
                // ????????????????????????????????????
                process = true;
            } else if (agentStatus.getSkills() != null && agentStatus.getSkills().size() > 0) {
                // ??????????????????????????????????????????????????????
                if ((StringUtils.isBlank(agentUser.getAgentno()) &&
                        StringUtils.isBlank(agentUser.getSkill()))) {
                    // ????????????????????????????????????????????????????????????????????????
                    process = true;
                } else if (StringUtils.isBlank(agentUser.getAgentno()) &&
                        agentStatus.getSkills().containsKey(agentUser.getSkill())) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????
                    process = true;
                }
            } else if (StringUtils.isBlank(agentUser.getAgentno()) &&
                    StringUtils.isBlank(agentUser.getSkill())) {
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                // ???????????????????????????????????????????????????????????????
                process = true;
            }

            if (!process) {
                continue;
            }

            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????(initMaxuser)
            if (((currentAssigned + assigned) < sessionConfig.getMaxuser()) && (assigned < sessionConfig.getInitmaxuser())) {
                assigned++;
                pickupAgentUserInQueue(agentUser, agentStatus);
            } else {
                logger.info(
                        "[assignVisitors] agentno {} reach the max users limit {}/{} or batch assign limit {}/{}",
                        agentno,
                        (currentAssigned + assigned),
                        sessionConfig.getMaxuser(), assigned, sessionConfig.getInitmaxuser());
                break;
            }
        }
        agentStatusProxy.broadcastAgentsStatus(orgi, "agent", "success", agentno);
    }

    /**
     * ????????????????????????????????????
     */
    public AgentService pickupAgentUserInQueue(final AgentUser agentUser, final AgentStatus agentStatus) {
        // ?????????????????????
        cache.deleteAgentUserInqueByAgentUserIdAndOrgi(agentUser.getUserid(), agentUser.getOrgi());
        AgentService agentService = null;
        // ????????????????????????????????????????????????
        try {
            agentService = resolveAgentService(
                    agentStatus, agentUser, agentUser.getOrgi(), false);

            // ?????????????????? agentService
            Message outMessage = new Message();
            outMessage.setMessage(acdMessageHelper.getSuccessMessage(
                    agentService,
                    agentUser.getChannel(),
                    agentUser.getOrgi()));
            outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
            outMessage.setCalltype(MainContext.CallType.IN.toString());
            outMessage.setCreatetime(MainUtils.dateFormate.get().format(new Date()));

            if (StringUtils.isNotBlank(agentUser.getUserid())) {
                outMessage.setAgentUser(agentUser);
                outMessage.setChannelMessage(agentUser);

                // ?????????????????????
                peerSyncIM.send(
                        MainContext.ReceiverType.VISITOR,
                        MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                        MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                );

                // ?????????????????????
                peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                        agentUser.getAppid(),
                        MainContext.MessageType.NEW, agentUser.getAgentno(), outMessage, true);
            }
        } catch (Exception ex) {
            logger.warn("[assignVisitors] fail to process service", ex);
        }
        return agentService;
    }

    /**
     * ??????????????????
     */
    public void finishAgentService(final AgentUser agentUser, final String orgi) {
        if (agentUser != null) {
            /*
             * ??????AgentUser
             */
            // ??????????????????
            AgentStatus agentStatus = null;
            if (StringUtils.equals(MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentUser.getStatus()) &&
                    agentUser.getAgentno() != null) {
                agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentUser.getAgentno(), orgi);
            }

            // ?????????AgentUser?????????
            agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            if (agentUser.getServicetime() != null) {
                agentUser.setSessiontimes(System.currentTimeMillis() - agentUser.getServicetime().getTime());
            }

            // ??????????????????agentUser??????
            agentUserRes.save(agentUser);

            final SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);

            /*
             * ????????????
             */
            AgentService service = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                service = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), agentUser.getOrgi());
            } else if (agentStatus != null) {
                // ????????????????????????????????????????????? AgentService
                // ??????????????????????????????????????? AgentService
                service = resolveAgentService(agentStatus, agentUser, orgi, true);
            }

            if (service != null) {
                service.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                service.setEndtime(new Date());
                if (service.getServicetime() != null) {
                    service.setSessiontimes(System.currentTimeMillis() - service.getServicetime().getTime());
                }

                Optional<AgentUserTask> optional = agentUserTaskRes.findById(
                        agentUser.getId());
                if (optional.isPresent()) {
                    final AgentUserTask agentUserTask = optional.get();
                    service.setAgentreplyinterval(agentUserTask.getAgentreplyinterval());
                    service.setAgentreplytime(agentUserTask.getAgentreplytime());
                    service.setAvgreplyinterval(agentUserTask.getAvgreplyinterval());
                    service.setAvgreplytime(agentUserTask.getAvgreplytime());

                    service.setUserasks(agentUserTask.getUserasks());
                    service.setAgentreplys(agentUserTask.getAgentreplys());

                    // ???????????????????????????????????????
                    if (sessionConfig.isQuality()) {
                        // ?????????????????????
                        service.setQualitystatus(MainContext.QualityStatusEnum.NODIS.toString());
                    }
                }

                /*
                 * ????????????????????????????????????
                 */
                if ((!sessionConfig.isQuality()) || service.getUserasks() == 0) {
                    // ??????????????? ???????????????????????????
                    service.setQualitystatus(MainContext.QualityStatusEnum.NO.toString());
                }
                agentServiceRes.save(service);
            }

            /*
             * ??????AgentStatus
             */
            if (agentStatus != null) {
                agentStatus.setUsers(
                        cache.getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi()));
                agentStatusRes.save(agentStatus);
            }

            /*
             * ???????????????????????????
             */
            switch (MainContext.ChannelType.toValue(agentUser.getChannel())) {
                case WEBIM:
                    // WebIM ????????????????????????
                    // ?????????????????????
                    Message outMessage = new Message();
                    outMessage.setAgentStatus(agentStatus);
                    outMessage.setMessage(acdMessageHelper.getServiceFinishMessage(agentUser.getChannel(), orgi));
                    outMessage.setMessageType(MainContext.AgentUserStatusEnum.END.toString());
                    outMessage.setCalltype(MainContext.CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.get().format(new Date()));
                    outMessage.setAgentUser(agentUser);

                    // ?????????????????????
                    peerSyncIM.send(
                            MainContext.ReceiverType.VISITOR,
                            MainContext.ChannelType.toValue(agentUser.getChannel()), agentUser.getAppid(),
                            MainContext.MessageType.STATUS, agentUser.getUserid(), outMessage, true
                    );

                    if (agentStatus != null) {
                        // ?????????????????????????????????
                        outMessage.setChannelMessage(agentUser);
                        outMessage.setAgentUser(agentUser);
                        peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                agentUser.getAppid(),
                                MainContext.MessageType.END, agentUser.getAgentno(), outMessage, true);
                    }
                    break;
                case PHONE:
                    // ???????????????????????????
                    logger.info(
                            "[finishAgentService] send notify to callout channel agentno {}", agentUser.getAgentno());
                    NettyClients.getInstance().sendCalloutEventMessage(
                            agentUser.getAgentno(), MainContext.MessageType.END.toString(), agentUser);
                    break;
                default:
                    logger.info(
                            "[finishAgentService] ignore notify agent service end for channel {}, agent user id {}",
                            agentUser.getChannel(), agentUser.getId());
            }

            // ??????????????????????????????????????????
            final OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(
                    agentUser.getUserid(), agentUser.getOrgi());
            if (onlineUser != null) {
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                onlineUserRes.save(onlineUser);
                logger.info(
                        "[finishAgentService] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                        onlineUser.getStatus(), onlineUser.getInvitestatus());
            }

            // ?????????????????????????????????????????????????????????
            if (agentStatus != null) {
                if ((agentStatus.getUsers() - 1) < sessionConfig.getMaxuser()) {
                    assignVisitors(agentStatus.getAgentno(), orgi);
                }
            }
            agentStatusProxy.broadcastAgentsStatus(
                    orgi, "end", "success", agentUser.getId());
        } else {
            logger.info("[finishAgentService] orgi {}, invalid agent user, should not be null", orgi);
        }
    }


    /**
     * ??????AgentUser
     * ????????????????????????????????????
     */
    public void finishAgentUser(final AgentUser agentUser, final String orgi) throws CSKefuException {
        logger.info("[finishAgentUser] userId {}, orgi {}", agentUser.getUserid(), orgi);

        if (agentUser.getId() == null) {
            throw new CSKefuException("Invalid agentUser info");
        }

        if (!StringUtils.equals(MainContext.AgentUserStatusEnum.END.toString(), agentUser.getStatus())) {
            /*
             * ??????????????????????????????????????????????????????
             */
            // ????????????
            finishAgentService(agentUser, orgi);
        }

        // ?????????????????????AgentUser??????
        agentUserRes.delete(agentUser);
    }

    /**
     * ???agentUser???????????????AgentService
     * ???????????????
     * 1. ???AgentUser????????????????????????????????????AgentService
     * 2. ?????????????????????????????????
     *
     * @param agentStatus ????????????
     * @param agentUser   ??????????????????
     * @param orgi        ??????ID
     * @param finished    ????????????
     */
    public AgentService resolveAgentService(
            AgentStatus agentStatus,
            final AgentUser agentUser,
            final String orgi,
            final boolean finished) {

        final AgentService agentService;
        if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
            AgentService existAgentService = agentServiceRes.findByIdAndOrgi(agentUser.getAgentserviceid(), orgi);
            if (existAgentService != null) {
                agentService = existAgentService;
            } else {
                agentService = new AgentService();
                agentService.setId(agentUser.getAgentserviceid());
            }
        } else {
            agentService = new AgentService();
        }
        agentService.setOrgi(orgi);

        final Date now = new Date();
        // ??????????????????
        MainUtils.copyProperties(agentUser, agentService);
        agentService.setChannel(agentUser.getChannel());
        agentService.setSessionid(agentUser.getSessionid());

        // ??????????????????loginDate?????????
        agentUser.setLogindate(now);
        OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(agentUser.getUserid(), orgi);

        if (finished) {
            // ????????????
            agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.END.toString());
            if (agentStatus == null) {
                // ????????????????????????????????????
                agentService.setLeavemsg(true);
                agentService.setLeavemsgstatus(MainContext.LeaveMsgStatus.NOTPROCESS.toString()); //??????????????????
            }

            if (onlineUser != null) {
                //  ??????OnlineUser???????????????????????????????????????????????????
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
            }
        } else if (agentStatus != null) {
            agentService.setAgent(agentStatus.getAgentno());
            agentService.setSkill(agentUser.getSkill());
            agentUser.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.INSERVICE.toString());
            // ??????????????????
            agentService.setAgentno(agentStatus.getUserid());
            agentService.setAgentusername(agentStatus.getUsername());
        } else {
            // ??????????????????????????????????????????????????????
            // ??????????????????
            agentUser.setStatus(MainContext.AgentUserStatusEnum.INQUENE.toString());
            agentService.setStatus(MainContext.AgentUserStatusEnum.INQUENE.toString());
            agentService.setSessiontype(MainContext.AgentUserStatusEnum.INQUENE.toString());
        }

        if (finished || agentStatus != null) {
            agentService.setAgentuserid(agentUser.getId());
            agentService.setInitiator(MainContext.ChatInitiatorType.USER.toString());

            long waittingtime = 0;
            if (agentUser.getWaittingtimestart() != null) {
                waittingtime = System.currentTimeMillis() - agentUser.getWaittingtimestart().getTime();
            } else {
                if (agentUser.getCreatetime() != null) {
                    waittingtime = System.currentTimeMillis() - agentUser.getCreatetime().getTime();
                }
            }

            agentUser.setWaittingtime((int) waittingtime);
            agentUser.setServicetime(now);
            agentService.setOwner(agentUser.getOwner());
            agentService.setTimes(0);

            Optional<User> optional = userRes.findById(agentService.getAgentno());
            final User agent = optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", agentService.getAgentno())));
            agentUser.setAgentname(agent.getUname());
            agentUser.setAgentno(agentService.getAgentno());

            if (StringUtils.isNotBlank(agentUser.getName())) {
                agentService.setName(agentUser.getName());
            }
            if (StringUtils.isNotBlank(agentUser.getPhone())) {
                agentService.setPhone(agentUser.getPhone());
            }
            if (StringUtils.isNotBlank(agentUser.getEmail())) {
                agentService.setEmail(agentUser.getEmail());
            }
            if (StringUtils.isNotBlank(agentUser.getResion())) {
                agentService.setResion(agentUser.getResion());
            }

            if (StringUtils.isNotBlank(agentUser.getSkill())) {
                agentService.setAgentskill(agentUser.getSkill());
            }

            agentService.setServicetime(now);

            if (agentUser.getCreatetime() != null) {
                agentService.setWaittingtime((int) (System.currentTimeMillis() - agentUser.getCreatetime().getTime()));
                agentUser.setWaittingtime(agentService.getWaittingtime());
            }
            if (onlineUser != null) {
                agentService.setOsname(onlineUser.getOpersystem());
                agentService.setBrowser(onlineUser.getBrowser());
                // ??????onlineUser???id
                agentService.setDataid(onlineUser.getId());
            }

            agentService.setLogindate(agentUser.getCreatetime());
            agentServiceRes.save(agentService);

            agentUser.setAgentserviceid(agentService.getId());
            agentUser.setLastgetmessage(now);
            agentUser.setLastmessage(now);
        }

        agentService.setDataid(agentUser.getId());

        /*
         * ????????????????????? ????????????????????????????????????????????????
         * ??? AgentUser ????????????????????????????????????
         */
        agentUserRes.save(agentUser);

        /*
         * ??????OnlineUser??????????????????????????????????????????
         */
        if (onlineUser != null && !finished) {
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.INSERV.toString());
            onlineUserRes.save(onlineUser);
        }

        // ??????????????????????????????????????????????????????
        if (agentStatus != null) {
            agentUserProxy.updateAgentStatus(agentStatus, orgi);
        }
        return agentService;
    }


}
