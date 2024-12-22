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

package com.cs.wit.controller.apps;

import com.cs.wit.acd.ACDAgentService;
import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.acd.basic.ACDMessageHelper;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.AgentService;
import com.cs.wit.model.AgentServiceSummary;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserTask;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.Organ;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.User;
import com.cs.wit.mq.BlackListEventSubscription;
import com.cs.wit.peer.PeerSyncIM;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.persistence.repository.AgentUserTaskRepository;
import com.cs.wit.persistence.repository.ChatMessageRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.ServiceSummaryRepository;
import com.cs.wit.persistence.repository.TagRelationRepository;
import com.cs.wit.persistence.repository.TagRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.AgentServiceProxy;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.proxy.BlackEntityProxy;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.socketio.message.Message;
import com.cs.wit.util.Menu;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/apps/cca")
@RequiredArgsConstructor
public class AgentAuditController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(AgentAuditController.class);

    @NonNull
    private final AgentUserProxy agentUserProxy;

    @NonNull
    private final ACDPolicyService acdPolicyService;

    @NonNull
    private final ACDMessageHelper acdMessageHelper;

    @NonNull
    private final AgentUserRepository agentUserRes;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final AgentUserRepository agentUserRepository;

    @NonNull
    private final ChatMessageRepository chatMessageRepository;

    @NonNull
    private final AgentServiceRepository agentServiceRes;

    @NonNull
    private final AgentUserTaskRepository agentUserTaskRes;

    @NonNull
    private final ServiceSummaryRepository serviceSummaryRes;

    @NonNull
    private final UserProxy userProxy;

    @NonNull
    private final OnlineUserRepository onlineUserRes;

    @NonNull
    private final TagRepository tagRes;

    @NonNull
    private final Cache cache;

    @NonNull
    private final PeerSyncIM peerSyncIM;

    @NonNull
    private final TagRelationRepository tagRelationRes;

    @NonNull
    private final BlackEntityProxy blackEntityProxy;

    @NonNull
    private final BlackListEventSubscription blackListEventSubscription;

    @NonNull
    private final AgentServiceProxy agentServiceProxy;

    @NonNull
    private final ACDAgentService acdAgentService;

    @RequestMapping(value = "/index")
    @Menu(type = "cca", subtype = "cca", access = true)
    public ModelAndView index(
            ModelMap map,
            HttpServletRequest request,
            @Valid final String skill,
            @Valid final String agentno,
            @Valid String sort
    ) {
        final String orgi = super.getOrgi(request);
        final User logined = super.getUser(request);
        logger.info("[index] skill {}, agentno {}, logined {}", skill, agentno, logined.getId());

        ModelAndView view = request(super.createAppsTempletResponse("/apps/cca/index"));
        Sort defaultSort = null;

        if (StringUtils.isNotBlank(sort)) {
            List<Sort.Order> criterias = new ArrayList<>();
            switch (sort) {
                case "lastmessage":
                    criterias.add(new Sort.Order(Sort.Direction.DESC, "status"));
                    criterias.add(new Sort.Order(Sort.Direction.DESC, "lastmessage"));
                    break;
                case "logintime":
                    criterias.add(new Sort.Order(Sort.Direction.DESC, "status"));
                    criterias.add(new Sort.Order(Sort.Direction.DESC, "createtime"));
                    break;
                case "default":
                    defaultSort = Sort.by(Sort.Direction.DESC, "status");
                    break;
            }
            if (criterias.size() > 0) {
                defaultSort = Sort.by(criterias);
                map.addAttribute("sort", sort);
            }
        } else {
            defaultSort = Sort.by(Sort.Direction.DESC, "status");
        }

        // 坐席对话列表
        List<AgentUser> agentUsers;

        if (StringUtils.isBlank(skill) && StringUtils.isBlank(agentno)) {
            agentUsers = agentUserRes.findByOrgiAndStatusAndAgentnoIsNot(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), logined.getId(), defaultSort);
        } else if (StringUtils.isNotBlank(skill) && StringUtils.isNotBlank(agentno)) {
            view.addObject("skill", skill);
            view.addObject("agentno", agentno);
            agentUsers = agentUserRes.findByOrgiAndStatusAndSkillAndAgentno(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else if (StringUtils.isNotBlank(skill)) {
            view.addObject("skill", skill);
            agentUsers = agentUserRes.findByOrgiAndStatusAndSkillAndAgentnoIsNot(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else {
            // agent is not Blank
            view.addObject("agentno", agentno);
            agentUsers = agentUserRes.findByOrgiAndStatusAndAgentno(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentno, defaultSort);
        }

        logger.info("[index] agent users size: {}", agentUsers.size());

        if (agentUsers.size() > 0) {
            view.addObject("agentUserList", agentUsers);

            /*
             * 当前对话
             */
            final AgentUser currentAgentUser = agentUsers.get(0);
            agentServiceProxy.bundleDialogRequiredDataInView(view, map, currentAgentUser, orgi, logined);
        }

        // 查询所有技能组
        List<Organ> skills = organRes.findByOrgiAndSkill(orgi, true);
        List<User> agents = userRes.findByOrgiAndAgentAndDatastatusAndIdIsNot(orgi, true, false, logined.getId());

        view.addObject("skillGroups", skills);
        view.addObject("agentList", agents);

        return view;
    }

    @RequestMapping("/query")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView query(HttpServletRequest request, String skill, String agentno) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/cca/chatusers"));

        final String orgi = super.getOrgi(request);
        final User logined = super.getUser(request);

        Sort defaultSort = Sort.by(Sort.Direction.DESC, "status");

        // 坐席对话列表
        List<AgentUser> agentUsers;

        if (StringUtils.isBlank(skill) && StringUtils.isBlank(agentno)) {
            agentUsers = agentUserRes.findByOrgiAndStatusAndAgentnoIsNot(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), logined.getId(), defaultSort);
        } else if (StringUtils.isNotBlank(skill) && StringUtils.isNotBlank(agentno)) {
            agentUsers = agentUserRes.findByOrgiAndStatusAndSkillAndAgentno(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else if (StringUtils.isNotBlank(skill)) {
            agentUsers = agentUserRes.findByOrgiAndStatusAndSkillAndAgentnoIsNot(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else {
            // agent is not Blank
            agentUsers = agentUserRes.findByOrgiAndStatusAndAgentno(
                    orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentno, defaultSort);
        }

        view.addObject("agentUserList", agentUsers);

        return view;
    }

    @RequestMapping("/agentusers")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView agentusers(HttpServletRequest request, String userid) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/cca/agentusers"));
        User logined = super.getUser(request);
        final String orgi = super.getOrgi(request);
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "status");
        view.addObject(
                "agentUserList", agentUserRes.findByOrgiAndStatusAndAgentnoIsNot(
                        orgi, MainContext.AgentUserStatusEnum.INSERVICE.toString(), logined.getId(), defaultSort));
        List<AgentUser> agentUserList = agentUserRepository.findByUseridAndOrgi(userid, logined.getOrgi());
        view.addObject(
                "curagentuser", agentUserList != null && agentUserList.size() > 0 ? agentUserList.get(0) : null);

        return view;
    }

    @RequestMapping("/agentuser")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView agentuser(
            ModelMap map,
            HttpServletRequest request,
            String id,
            String channel
    ) throws IOException, TemplateException {
        String mainagentuser = "/apps/cca/mainagentuser";
        if (channel.equals("phone")) {
            mainagentuser = "/apps/cca/mainagentuser_callout";
        }
        ModelAndView view = request(super.createRequestPageTempletResponse(mainagentuser));
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        AgentUser agentUser = agentUserRepository.findByIdAndOrgi(id, orgi);

        if (agentUser != null) {
            view.addObject("curagentuser", agentUser);

            CousultInvite invite = OnlineUserProxy.consult(agentUser.getAppid(), agentUser.getOrgi());
            if (invite != null) {
                view.addObject("ccaAisuggest", invite.isAisuggest());
            }
            view.addObject("inviteData", OnlineUserProxy.consult(agentUser.getAppid(), agentUser.getOrgi()));
            List<AgentUserTask> agentUserTaskList = agentUserTaskRes.findByIdAndOrgi(id, orgi);
            if (agentUserTaskList.size() > 0) {
                AgentUserTask agentUserTask = agentUserTaskList.get(0);
                agentUserTask.setTokenum(0);
                agentUserTaskRes.save(agentUserTask);
            }

            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceidAndOrgi(
                        agentUser.getAgentserviceid(), orgi);
                if (summarizes.size() > 0) {
                    view.addObject("summary", summarizes.get(0));
                }
            }

            PageRequest pageRequest = PageRequest.of(0, 20, Sort.Direction.DESC, "updatetime");
            view.addObject("agentUserMessageList",
                    this.chatMessageRepository.findByUsessionAndOrgi(agentUser.getUserid(), orgi, pageRequest));
            AgentService agentService = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                Optional<AgentService> optional = this.agentServiceRes.findById(agentUser.getAgentserviceid());
                if (optional.isPresent()) {
                    agentService = optional.get();
                    /*
                     * 获取关联数据
                     */
                    agentServiceProxy.processRelaData(logined.getId(), orgi, agentService, map);
                }
                view.addObject("curAgentService", agentService);
            }
            if (MainContext.ChannelType.WEBIM.toString().equals(agentUser.getChannel())) {
                Optional<OnlineUser> optional = onlineUserRes.findById(agentUser.getUserid());
                if (optional.isPresent()) {
                    OnlineUser onlineUser = optional.get();
                    if (onlineUser.getLogintime() != null) {
                        if (MainContext.OnlineUserStatusEnum.OFFLINE.toString().equals(onlineUser.getStatus())) {
                            onlineUser.setBetweentime(
                                    (int) (onlineUser.getUpdatetime().getTime() - onlineUser.getLogintime().getTime()));
                        } else {
                            onlineUser.setBetweentime(
                                    (int) (System.currentTimeMillis() - onlineUser.getLogintime().getTime()));
                        }
                    }
                    view.addObject("onlineUser", onlineUser);
                }
            }
            view.addObject("serviceCount", this.agentServiceRes
                    .countByUseridAndOrgiAndStatus(agentUser.getUserid(), orgi, MainContext.AgentUserStatusEnum.END.toString()));
            view.addObject("tagRelationList", tagRelationRes.findByUserid(agentUser.getUserid()));
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(super.getOrgi(request));

        view.addObject("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            view.addObject("topicList", OnlineUserProxy.search(null, orgi, super.getUser(request)));
        }

        view.addObject("tags", tagRes.findByOrgiAndTagtype(orgi, MainContext.ModelType.USER.toString()));
        return view;
    }


    /**
     * 坐席转接窗口
     */
    @RequestMapping(value = "/transfer")
    @Menu(type = "apps", subtype = "transfer")
    public ModelAndView transfer(
            ModelMap map,
            final HttpServletRequest request,
            final @Valid String userid,
            final @Valid String agentserviceid,
            final @Valid String agentnoid,
            final @Valid String agentuserid
    ) {
        logger.info("[transfer] userId {}, agentUser {}", userid, agentuserid);
        final String orgi = super.getOrgi(request);
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid)) {
            // 列出所有技能组
            final List<Organ> skillGroups = OnlineUserProxy.organ(orgi, true);

            // 选择当前用户的默认技能组
            Set<String> organs = super.getUser(request).getOrgans().keySet();
            String currentOrgan = organs.size() > 0 ? (new ArrayList<>(organs)).get(0) : null;

            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillGroups.isEmpty()) {
                    currentOrgan = skillGroups.get(0).getId();
                }
            }

            // 列出所有在线的坐席，排除本身
            final Map<String, AgentStatus> agentStatusMap = cache.findAllReadyAgentStatusByOrgi(orgi);

            List<String> userids = new ArrayList<>();

            for (final String o : agentStatusMap.keySet()) {
                if (!StringUtils.equals(o, agentnoid)) {
                    userids.add(o);
                }
            }

            final List<User> userList = userRes.findAllById(userids);
            for (final User o : userList) {
                o.setAgentStatus(agentStatusMap.get(o.getId()));
                // find user's skills
                userProxy.attachOrgansPropertiesForUser(o);
            }

            map.addAttribute("userList", userList);
            map.addAttribute("userid", userid);
            map.addAttribute("agentserviceid", agentserviceid);
            map.addAttribute("agentuserid", agentuserid);
            map.addAttribute("agentnoid", agentnoid);
            map.addAttribute("skillGroups", skillGroups);
            map.addAttribute("agentservice", this.agentServiceRes.findByIdAndOrgi(agentserviceid, orgi));
            map.addAttribute("currentorgan", currentOrgan);
        }

        return request(super.createRequestPageTempletResponse("/apps/cca/transfer"));
    }


    /**
     * 查找一个组织机构中的在线坐席
     */
    @RequestMapping(value = "/transfer/agent")
    @Menu(type = "apps", subtype = "transferagent")
    public ModelAndView transferagent(
            ModelMap map,
            HttpServletRequest request,
            @Valid String agentnoid,
            @Valid String organ
    ) {
        final String orgi = super.getOrgi(request);
        if (StringUtils.isNotBlank(organ)) {
            List<String> usersids = new ArrayList<>();
            final List<AgentStatus> agentStatusList = cache.getAgentStatusBySkillAndOrgi(organ, orgi);
            if (agentStatusList.size() > 0) {
                for (AgentStatus agentStatus : agentStatusList) {
                    if (agentStatus != null && !StringUtils.equals(agentStatus.getAgentno(), agentnoid)) {
                        usersids.add(agentStatus.getAgentno());
                    }
                }
            }
            List<User> userList = userRes.findAllById(usersids);
            for (User user : userList) {
                userProxy.attachOrgansPropertiesForUser(user);
                for (final AgentStatus as : agentStatusList) {
                    if (StringUtils.equals(as.getAgentno(), user.getId())) {
                        user.setAgentStatus(as);
                        break;
                    }
                }
            }

            map.addAttribute("userList", userList);
            map.addAttribute("currentorgan", organ);
        }
        return request(super.createRequestPageTempletResponse("/apps/cca/transferagentlist"));
    }

    /**
     * 执行坐席转接
     */
    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(
            HttpServletRequest request,
            // 访客ID
            @Valid final String userid,
            // 服务记录ID
            @Valid final String agentserviceid,
            // 坐席访客ID
            @Valid final String agentuserid,
            @Valid final String currentAgentnoid,
            // 会话转接给下一个坐席
            @Valid final String agentno,
            @Valid final String memo
    ) throws CSKefuException {

        final String orgi = super.getOrgi(request);

        if (StringUtils.isNotBlank(userid) &&
                StringUtils.isNotBlank(agentuserid) &&
                StringUtils.isNotBlank(agentno)) {
            final User targetAgent = userRes.findById(agentno)
                    .orElseThrow(() -> {
                        String reason = String.format("Agent %s not found", agentno);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
                    });
            final AgentService agentService = agentServiceRes.findByIdAndOrgi(agentserviceid, super.getOrgi(request));
            /*
             * 更新AgentUser
             */
            final AgentUser agentUser = agentUserProxy.resolveAgentUser(userid, agentuserid, orgi);
            agentUser.setAgentno(agentno);
            agentUser.setAgentname(targetAgent.getUname());
            agentUserRes.save(agentUser);

            /*
             * 坐席状态
             */
            // 转接目标坐席
            final AgentStatus transAgentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);

            // 转接源坐席
            final AgentStatus currentAgentStatus = cache.findOneAgentStatusByAgentnoAndOrig(currentAgentnoid, orgi);

            if (StringUtils.equals(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentUser.getStatus())) { //转接 ， 发送消息给 目标坐席

                // 更新当前坐席的服务访客列表
                if (currentAgentStatus != null) {
                    cache.deleteOnlineUserIdFromAgentStatusByUseridAndAgentnoAndOrgi(userid, currentAgentnoid, orgi);
                    agentUserProxy.updateAgentStatus(currentAgentStatus, super.getOrgi(request));
                }

                if (transAgentStatus != null) {
                    agentService.setAgentno(agentno);
                    agentService.setAgentusername(transAgentStatus.getUsername());
                }

                // 转接坐席提示消息
                try {
                    Message outMessage = new Message();
                    outMessage.setMessage(
                            acdMessageHelper.getSuccessMessage(agentService, agentUser.getChannel(), orgi));
                    outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
                    outMessage.setCalltype(MainContext.CallType.IN.toString());
                    outMessage.setCreatetime(MainUtils.dateFormate.get().format(new Date()));
                    outMessage.setAgentUser(agentUser);
                    outMessage.setAgentService(agentService);

                    if (StringUtils.isNotBlank(agentUser.getUserid())) {
                        peerSyncIM.send(
                                MainContext.ReceiverType.VISITOR,
                                MainContext.ChannelType.toValue(agentUser.getChannel()),
                                agentUser.getAppid(),
                                MainContext.MessageType.STATUS,
                                agentUser.getUserid(),
                                outMessage,
                                true
                        );
                    }

                    // 通知转接消息给新坐席
                    outMessage.setChannelMessage(agentUser);
                    outMessage.setAgentUser(agentUser);
                    peerSyncIM.send(
                            MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                            agentUser.getAppid(), MainContext.MessageType.NEW, agentService.getAgentno(),
                            outMessage, true
                    );

                } catch (Exception ex) {
                    logger.error("[transfersave]", ex);
                }
            }

            if (agentService != null) {
                agentService.setAgentno(agentno);
                if (StringUtils.isNotBlank(memo)) {
                    agentService.setTransmemo(memo);
                }
                agentService.setTrans(true);
                agentService.setTranstime(new Date());
                agentServiceRes.save(agentService);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/cca/index"));

    }


    /**
     * 结束对话
     * 如果当前对话属于登录用户或登录用户为超级用户，则可以结束这个对话
     */
    @RequestMapping({"/end"})
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView end(HttpServletRequest request, @Valid String id) {
        final String orgi = super.getOrgi(request);
        final User logined = super.getUser(request);

        final AgentUser agentUser = agentUserRes.findByIdAndOrgi(id, orgi);

        if (agentUser != null) {
            if ((StringUtils.equals(
                    logined.getId(), agentUser.getAgentno()) || logined.isAdmin())) {
                // 删除访客-坐席关联关系，包括缓存
                try {
                    acdAgentService.finishAgentUser(agentUser, orgi);
                } catch (CSKefuException e) {
                    // 未能删除成功
                    logger.error("[end]", e);
                }
            } else {
                logger.info("[end] Permission not fulfill.");
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/cca/index"));
    }

    @RequestMapping({"/blacklist/add"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklistadd(ModelMap map, HttpServletRequest request, @Valid String agentuserid, @Valid String agentserviceid, @Valid String userid) {
        map.addAttribute("agentuserid", agentuserid);
        map.addAttribute("agentserviceid", agentserviceid);
        map.addAttribute("userid", userid);
        map.addAttribute("agentUser", agentUserRes.findByIdAndOrgi(userid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/cca/blacklistadd"));
    }

    @RequestMapping({"/blacklist/save"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklist(
            HttpServletRequest request,
            @Valid String agentuserid,
            @Valid String agentserviceid,
            @Valid String userid,
            @Valid BlackEntity blackEntity)
            throws Exception {
        logger.info("[blacklist] userid {}", userid);
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();

        if (StringUtils.isBlank(userid)) {
            throw new CSKefuException("Invalid userid");
        }
        /*
         * 添加黑名单
         * 一定时间后触发函数
         */
        JsonObject payload = new JsonObject();

        int timeSeconds = blackEntity.getControltime() * 3600;
        payload.addProperty("userId", userid);
        payload.addProperty("orgi", orgi);
        ModelAndView view = end(request, agentuserid);
        // 更新或创建黑名单
        blackEntityProxy.updateOrCreateBlackEntity(blackEntity, logined, userid, orgi, agentserviceid);

        // 创建定时任务 取消拉黑
        blackListEventSubscription.publish(payload, timeSeconds);

        return view;
    }

}
