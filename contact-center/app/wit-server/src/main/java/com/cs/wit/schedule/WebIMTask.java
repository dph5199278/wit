/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.schedule;

import com.cs.wit.acd.ACDAgentService;
import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserTask;
import com.cs.wit.model.JobDetail;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.peer.PeerSyncIM;
import com.cs.wit.persistence.repository.AgentUserTaskRepository;
import com.cs.wit.persistence.repository.JobDetailRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.socketio.message.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class WebIMTask {

    private final static Logger logger = LoggerFactory.getLogger(WebIMTask.class);

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private JobDetailRepository jobDetailRes;

    @Autowired
    private TaskExecutor webimTaskExecutor;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private Cache cache;

    @Scheduled(fixedDelay = 5000, initialDelay = 20000) // ????????????????????????5???????????????
    @Async("scheduleTaskExecutor")
    public void task() {
        if(null != MainContext.getContext()) {
            Optional.ofNullable(acdPolicyService.initSessionConfigList())
                    .ifPresent(sessionConfigList -> {
                        sessionConfigList.parallelStream()
                                .forEach(sessionConfig -> {
                                    // ??????????????????????????????
                                    if(sessionConfig.isAgentreplaytimeout()) {
                                        agentUserTaskRes.findByLastgetmessageLessThanAndStatusAndOrgi(
                                                MainUtils.getLastTime(sessionConfig.getAgenttimeout()),
                                                MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi())
                                                .parallelStream()
                                                .forEach(task -> {
                                                    // ???????????????
                                                    cache.findOneAgentUserByUserIdAndOrgi(
                                                            task.getUserid(), sessionConfig.getOrgi()).ifPresent(p -> {
                                                        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                                                p.getAgentno(), task.getOrgi());
                                                        if (agentStatus != null && (task.getReptimes() == null || task.getReptimes().equals("0"))) {
                                                            task.setReptimes("1");
                                                            task.setReptime(new Date());

                                                            //??????????????????
                                                            processMessage(
                                                                    sessionConfig, sessionConfig.getAgenttimeoutmsg(),
                                                                    sessionConfig.getServicename(), p, agentStatus, task);
                                                            agentUserTaskRes.save(task);
                                                        }
                                                    });
                                                });
                                    }

                                    // ??????????????????????????????
                                    final Date timeout = MainUtils.getLastTime(sessionConfig.getTimeout());
                                    final Date reTimeout = MainUtils.getLastTime(sessionConfig.getRetimeout());
                                    if (sessionConfig.isSessiontimeout()) {
                                        //??????????????? ????????????
                                        agentUserTaskRes.findByLastmessageLessThanAndStatusAndOrgi(
                                                timeout,
                                                MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi())
                                                .parallelStream()
                                                .forEach(task -> {
                                                    // ???????????????
                                                    cache.findOneAgentUserByUserIdAndOrgi(
                                                            task.getUserid(), sessionConfig.getOrgi()).ifPresent(p -> {
                                                        if (StringUtils.isNotBlank(p.getAgentno())) {
                                                            AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                                                    p.getAgentno(), task.getOrgi());
                                                            task.setAgenttimeouttimes(task.getAgenttimeouttimes() + 1);
                                                            if (agentStatus != null && (task.getWarnings() == null || task.getWarnings().equals(
                                                                    "0"))) {
                                                                task.setWarnings("1");
                                                                task.setWarningtime(new Date());

                                                                // ??????????????????
                                                                processMessage(
                                                                        sessionConfig, sessionConfig.getTimeoutmsg(), agentStatus.getUsername(),
                                                                        p, agentStatus, task);
                                                                agentUserTaskRes.save(task);
                                                            } else if (sessionConfig.isResessiontimeout() && agentStatus != null && task.getWarningtime() != null && reTimeout.after(task.getWarningtime())) {    //?????????????????????
                                                                /**
                                                                 * ?????????????????????,??????
                                                                 */
                                                                processMessage(
                                                                        sessionConfig, sessionConfig.getRetimeoutmsg(),
                                                                        sessionConfig.getServicename(),
                                                                        p, agentStatus, task);
                                                                try {
                                                                    acdAgentService.finishAgentService(p, task.getOrgi());
                                                                } catch (Exception e) {
                                                                    logger.warn("[task] exception: ", e);
                                                                }
                                                            }
                                                        }
                                                    });
                                                });
                                    } else if (sessionConfig.isResessiontimeout()) {
                                        //????????????????????????????????????????????????
                                        agentUserTaskRes.findByLastmessageLessThanAndStatusAndOrgi(
                                                reTimeout,
                                                MainContext.AgentUserStatusEnum.INSERVICE.toString(), sessionConfig.getOrgi())
                                                .parallelStream()
                                                .forEach(task -> {
                                                    // ???????????????
                                                    cache.findOneAgentUserByUserIdAndOrgi(
                                                            task.getUserid(), sessionConfig.getOrgi()).ifPresent(p -> {
                                                        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                                                                p.getAgentno(), task.getOrgi());
                                                        if (agentStatus != null && task.getWarningtime() != null && reTimeout.after(task.getWarningtime())) {    //?????????????????????
                                                            /**
                                                             * ?????????????????????,??????
                                                             */
                                                            processMessage(
                                                                    sessionConfig, sessionConfig.getRetimeoutmsg(), agentStatus.getUsername(),
                                                                    p, agentStatus, task);
                                                            try {
                                                                acdAgentService.finishAgentService(p, task.getOrgi());
                                                            } catch (Exception e) {
                                                                logger.warn("[task] exception: ", e);
                                                            }
                                                        }
                                                    });
                                                });
                                    }

                                    // ???????????????????????????????????????
                                    if (sessionConfig.isQuene()) {
                                        agentUserTaskRes.findByLogindateLessThanAndStatusAndOrgi(
                                                MainUtils.getLastTime(sessionConfig.getQuenetimeout()),
                                                MainContext.AgentUserStatusEnum.INQUENE.toString(), sessionConfig.getOrgi())
                                                .parallelStream()
                                                .forEach(task -> {
                                                    // ???????????????
                                                    cache.findOneAgentUserByUserIdAndOrgi(
                                                            task.getUserid(), MainContext.SYSTEM_ORGI).ifPresent(p -> {
                                                        /**
                                                         * ???????????????,??????
                                                         */
                                                        processMessage(
                                                                sessionConfig, sessionConfig.getQuenetimeoutmsg(), sessionConfig.getServicename(),
                                                                p, null, task);
                                                        try {
                                                            acdAgentService.finishAgentService(p, task.getOrgi());
                                                        } catch (Exception e) {
                                                            logger.warn("[task] exception: ", e);
                                                        }
                                                    });
                                                });
                                    }
                                });
                    });
        }
    }

    /**
     * ?????????????????????????????????OnlineUser???????????????
     * ????????????????????????????????????????????????????????????
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 20000)
    public void onlineuser() {
        final Page<OnlineUser> pages = onlineUserRes.findByStatusAndCreatetimeLessThan(
                MainContext.OnlineUserStatusEnum.ONLINE.toString(),
                MainUtils.getLastTime(60), PageRequest.of(0, 1000));
        if (pages.getContent().size() > 0) {
            for (final OnlineUser onlineUser : pages.getContent()) {
                try {
                    logger.info(
                            "[save] put onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
                            onlineUser.getStatus(),
                            onlineUser.getInvitestatus());
                    OnlineUserProxy.offline(onlineUser);
                } catch (Exception e) {
                    logger.warn("[onlineuser] error", e);
                }
            }
        }
    }

    /**
     * appid : appid ,
     * userid:userid,
     * sign:session,
     * touser:touser,
     * session: session ,
     * orgi:orgi,
     * username:agentstatus,
     * nickname:agentstatus,
     * message : message
     *
     * @param sessionConfig
     * @param agentUser
     * @param task
     */

    private void processMessage(
            SessionConfig sessionConfig, String message, String servicename, AgentUser
            agentUser, AgentStatus agentStatus, AgentUserTask task) {

        Message outMessage = new Message();
        if (StringUtils.isNotBlank(message)) {
            outMessage.setMessage(message);
            outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
            outMessage.setCalltype(MainContext.CallType.OUT.toString());
            outMessage.setAgentUser(agentUser);
            outMessage.setSnsAccount(null);

            ChatMessage chatMessage = new ChatMessage();
            if (agentUser != null) {
                chatMessage.setAppid(agentUser.getAppid());

                chatMessage.setUserid(agentUser.getUserid());
                chatMessage.setUsession(agentUser.getUserid());
                chatMessage.setTouser(agentUser.getUserid());
                chatMessage.setOrgi(agentUser.getOrgi());
                chatMessage.setUsername(agentUser.getUsername());
                chatMessage.setMessage(message);

                chatMessage.setId(MainUtils.getUUID());
                chatMessage.setContextid(agentUser.getContextid());

                chatMessage.setAgentserviceid(agentUser.getAgentserviceid());

                chatMessage.setCalltype(MainContext.CallType.OUT.toString());
                if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                    chatMessage.setTouser(agentUser.getUserid());
                }
                chatMessage.setChannel(agentUser.getChannel());
                chatMessage.setUsession(agentUser.getUserid());

                outMessage.setContextid(agentUser.getContextid());

                outMessage.setChannelMessage(chatMessage);
                if (StringUtils.isNotBlank(agentUser.getAgentname())) {
                    // OUT?????????????????????????????????
                    chatMessage.setUsername(agentUser.getAgentname());
                } else {
                    chatMessage.setUsername(servicename);
                }
                outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(chatMessage.getCreatetime()));

                /**
                 * ???????????????????????????
                 */
                // ????????????
                if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
                    peerSyncIM.send(MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                    agentUser.getAppid(),
                                    MainContext.MessageType.MESSAGE, agentUser.getAgentno(), outMessage, true);
                }

                // ????????????
                if (StringUtils.isNotBlank(chatMessage.getTouser())) {
                    peerSyncIM.send(MainContext.ReceiverType.VISITOR,
                                    MainContext.ChannelType.toValue(agentUser.getChannel()),
                                    agentUser.getAppid(),
                                    MainContext.MessageType.MESSAGE,
                                    agentUser.getUserid(),
                                    outMessage, true);
                }
            }
        }
    }

    /**
     * ????????? , ?????? ?????????????????????????????? ??????????????? ????????????
     * TODO ???????????????????????????????????????????????????????????????
     * https://airflow.apache.org/
     * ???????????????10??????????????????????????????????????????????????????????????????
     */
    @Scheduled(fixedDelay = 600000) //
    public void jobDetail() {
        List<JobDetail> allJob = new ArrayList<JobDetail>();
        Page<JobDetail> readyTaskList = jobDetailRes.findByTaskstatus(
                MainContext.TaskStatusType.READ.getType(), PageRequest.of(0, 100));
        allJob.addAll(readyTaskList.getContent());
        Page<JobDetail> planTaskList = jobDetailRes.findByPlantaskAndTaskstatusAndNextfiretimeLessThan(
                true, MainContext.TaskStatusType.NORMAL.getType(), new Date(), PageRequest.of(0, 100));
        allJob.addAll(planTaskList.getContent());
        if (allJob.size() > 0) {
            for (JobDetail jobDetail : allJob) {
                if (!cache.existJobByIdAndOrgi(jobDetail.getId(), jobDetail.getOrgi())) {
                    jobDetail.setTaskstatus(MainContext.TaskStatusType.QUEUE.getType());
                    jobDetailRes.save(jobDetail);
                    cache.putJobByIdAndOrgi(jobDetail.getId(), jobDetail.getOrgi(), jobDetail);
                    /**
                     * ???????????????????????????
                     */
                    webimTaskExecutor.execute(new Task(jobDetail, jobDetailRes));
                }
            }
        }
    }


}
