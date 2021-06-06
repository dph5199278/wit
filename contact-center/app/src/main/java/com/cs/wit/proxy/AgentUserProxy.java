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
package com.cs.wit.proxy;

import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.*;
import com.cs.wit.peer.PeerSyncIM;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.persistence.repository.*;
import com.cs.wit.socketio.message.Message;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AgentUserProxy {
    private final static Logger logger = LoggerFactory.getLogger(AgentUserProxy.class);

    // 权限符号
    private final static String AUDIT_PERMISSION_READ = "R";
    private final static String AUDIT_PERMISSION_WRITE = "W";
    private final static String AUDIT_PERMISSION_TRANS = "T";
    private final static String AUDIT_PERMISSION_TOTAL = "RWT";

    // 浏览聊天
    private final static String AUTH_KEY_AUDIT_READ = "A13_A01_A01";

    // 回复聊天
    private final static String AUTH_KEY_AUDIT_WRITE = "A13_A01_A02";

    // 转接聊天
    private final static String AUTH_KEY_AUDIT_TRANS = "A13_A01_A03";

    @NonNull
    private final ACDPolicyService acdPolicyService;

    @NonNull
    private final AgentUserRepository agentUserRes;

    @NonNull
    private final RoleAuthRepository roleAuthRes;

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final UserRoleRepository userRoleRes;

    @NonNull
    private final AgentServiceRepository agentServiceRes;

    @NonNull
    private final SNSAccountRepository snsAccountRes;

    @NonNull
    private final AgentServiceProxy agentServiceProxy;

    @NonNull
    private final ContactsRepository contactsRes;

    @NonNull
    private final OnlineUserRepository onlineUserRes;

    @NonNull
    private final AgentUserContactsRepository agentUserContactsRes;

    @NonNull
    private final Cache cache;

    @NonNull
    private final AgentStatusRepository agentStatusRes;

    @NonNull
    private final PeerSyncIM peerSyncIM;


    /**
     * 与联系人主动聊天前查找获取AgentUser
     */
    public AgentUser figureAgentUserBeforeChatWithContactInfo(final String channels, final String contactid, final User logined) throws CSKefuException {
        // 聊天依赖的对象
        AgentUser agentUser = null;
        OnlineUser onlineUser;

        // 检查触达方式是否存在
        if (StringUtils.isNotBlank(channels)) {
            // 选择第一个作为首要联系渠道
            String channel = StringUtils.split(channels, ",")[0];

            // 查找联系人
            final Contacts contact = contactsRes.findById(contactid)
                    .orElseThrow(() -> {
                        String reason = String.format("Contacts %s not found", contactid);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
                    });

            // 查找 OnlineUser
            onlineUser = onlineUserRes.findOneByContactidAndOrigAndChannel(contactid, logined.getOrgi(), channel)
                    .orElseGet(() -> OnlineUserProxy.createNewOnlineUserWithContactAndChannel(contact, logined, channel));

            // 查找满足条件的 AgentUser
            final Optional<AgentUser> op = agentUserRes.findOneByContactIdAndChannelAndOrgi(contactid, channel,
                    logined.getOrgi());
            if (op.isPresent()) {
                if (StringUtils.equals(op.get().getAgentno(), logined.getId())) {
                    agentUser = op.get();
                } else {
                    logger.info(
                            "[chat] can not bind to current agentno due to inserv status with another AgentUser id {}, Agent {}",
                            op.get().getId(), op.get().getUserid());
                    // do nothing
                }
            } else {
                // 创建新的AgentUser
                agentUser = createAgentUserWithContactAndAgentAndChannelAndStatus(
                        onlineUser, contact, logined, channel, MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                        logined);
            }
        }

        if (agentUser != null) {
            logger.info("[figureAgentUserBeforeChatWithContactInfo] agent user is not null");
            // 通知坐席
            // 通知消息到坐席
            Message outMessage = new Message();
            outMessage.setChannelMessage(agentUser);
            outMessage.setAgentUser(agentUser);
            peerSyncIM.send(MainContext.ReceiverType.AGENT,
                    MainContext.ChannelType.WEBIM,
                    agentUser.getAppid(),
                    MainContext.MessageType.NEW,
                    logined.getId(),
                    outMessage, true);
        } else {
            logger.info("[figureAgentUserBeforeChatWithContactInfo] agent user is null");
        }


        return agentUser;
    }

    /**
     * 聊天列表数据
     */
    public void buildIndexViewWithModels(
            final ModelAndView view,
            final ModelMap map,
            final HttpServletRequest request,
            final HttpServletResponse response,
            String sort,
            final User logined,
            final String orgi,
            final AgentUser agentUser) throws IOException, TemplateException {
        Sort defaultSort = null;
        if (StringUtils.isBlank(sort)) {
            Cookie[] cookies = request.getCookies();// 这样便可以获取一个cookie数组
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("sort")) {
                        sort = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(sort)) {
            List<Sort.Order> list = new ArrayList<>();
            if (sort.equals("lastmessage")) {
                list.add(new Sort.Order(Sort.Direction.DESC, "status"));
                list.add(new Sort.Order(Sort.Direction.DESC, "lastmessage"));
            } else if (sort.equals("logintime")) {
                list.add(new Sort.Order(Sort.Direction.DESC, "status"));
                list.add(new Sort.Order(Sort.Direction.DESC, "createtime"));
            } else if (sort.equals("default")) {
                defaultSort = Sort.by(Sort.Direction.DESC, "status");
                Cookie name = new Cookie("sort", null);
                name.setMaxAge(0);
                response.addCookie(name);
            }
            if (list.size() > 0) {
                defaultSort = Sort.by(list);
                Cookie name = new Cookie("sort", sort);
                name.setMaxAge(60 * 60 * 24 * 365);
                response.addCookie(name);
                map.addAttribute("sort", sort);
            }
        } else {
            defaultSort = Sort.by(Sort.Direction.DESC, "status");
        }

        List<AgentUser> agentUserList = agentUserRes.findByAgentnoAndOrgi(
                logined.getId(), logined.getOrgi(), defaultSort);

        if (agentUserList.size() > 0) {
            if (agentUser != null) {
                List<AgentUser> agentUserListSimple = new ArrayList<>();
                agentUserListSimple.add(agentUser);

                for (final AgentUser x : agentUserList) {
                    if (StringUtils.equals(x.getId(), agentUser.getId())) {
                        continue;
                    }
                    agentUserListSimple.add(x);
                }

                agentUserList = agentUserListSimple;
            }
        } else if (agentUser != null) {
            agentUserList.add(agentUser);
        }

        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(logined.getOrgi());

        view.addObject("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            view.addObject("topicList", OnlineUserProxy.search(null, logined.getOrgi(), logined));
        }

        if (agentUserList.size() > 0) {
            view.addObject("agentUserList", agentUserList);
            agentServiceProxy.bundleDialogRequiredDataInView(view,
                    map, agentUserList.get(0), orgi, logined);
        }
    }

    /**
     * 获得坐席访客会话相关的订阅者（会话监控人员）
     */
    public HashMap<String, String> getAgentUserSubscribers(final String orgi, final AgentUser agentUser) {
        HashMap<String, String> result = new HashMap<>();
        HashSet<String> bypass = new HashSet<>();

        // 跳过自己
        if (StringUtils.isNotBlank(agentUser.getAgentno())) {
            bypass.add(agentUser.getAgentno());
        }

        // 首先查看管理员
        List<User> admins = userRes.findByAdminAndOrgi(true, orgi);

        for (final User user : admins) {
            if (bypass.contains(user.getId())) continue;
            addPermissions(user.getId(), AUDIT_PERMISSION_TOTAL, result);
            bypass.add(user.getId());
        }

        // 查看浏览权限
        loadPermissionsFromDB(orgi, AUTH_KEY_AUDIT_READ, bypass, result);

        // 查看回复权限
        loadPermissionsFromDB(orgi, AUTH_KEY_AUDIT_WRITE, bypass, result);

        // 查看转接权限
        loadPermissionsFromDB(orgi, AUTH_KEY_AUDIT_TRANS, bypass, result);

        // DEBUG
        for (final String userId : result.keySet()) {
            logger.info(
                    "[getAgentUserSubscribers] agentUserId {} user {} permissions {}", agentUser.getId(), userId,
                    result.get(userId));
        }

        return result;
    }


    /**
     * 根据指定的KEY从数据库加载权限数据
     */
    private void loadPermissionsFromDB(final String orgi, final String key, final HashSet<String> bypass, final HashMap<String, String> result) {
        List<RoleAuth> roleAuths = roleAuthRes.findByDicvalueAndOrgi(key, orgi);
        for (final RoleAuth roleAuth : roleAuths) {
            List<String> users = userRoleRes.findByOrgiAndRoleId(orgi, roleAuth.getRoleid());
            for (String user : users) {
                if (!bypass.contains(user)) {
                    addPermission(user, key, result);
                }
            }
        }
    }

    /**
     * 同时增加权限
     */
    @SuppressWarnings("SameParameterValue")
    private void addPermissions(final String user, final String permissions, final HashMap<String, String> result) {
        result.put(user, permissions);
    }

    /**
     * 增加权限
     */
    private void addPermission(final String userId, final String authKey, final HashMap<String, String> result) {
        if (!result.containsKey(userId)) {
            result.put(userId, "");
        }

        switch (authKey) {
            case AUTH_KEY_AUDIT_READ:
                if (!result.get(userId).contains(AUDIT_PERMISSION_READ)) {
                    result.put(userId, result.get(userId) + AUDIT_PERMISSION_READ);
                }
                break;
            case AUTH_KEY_AUDIT_WRITE:
                if (!result.get(userId).contains(AUDIT_PERMISSION_WRITE)) {
                    result.put(userId, result.get(userId) + AUDIT_PERMISSION_WRITE);
                }
                break;
            case AUTH_KEY_AUDIT_TRANS:
                if (!result.get(userId).contains(AUDIT_PERMISSION_TRANS)) {
                    result.put(userId, result.get(userId) + AUDIT_PERMISSION_TRANS);
                }
                break;
            default:
                logger.info("[addPermission] invalid key {}", authKey);
        }

    }


    /**
     * 创建AgentUser
     */
    public AgentUser createAgentUserWithContactAndAgentAndChannelAndStatus(
            final OnlineUser onlineUser,
            final Contacts contact,
            final User agent,
            final String channel,
            final String status, final User creator) throws CSKefuException {
        logger.info("[createAgentUserWithContactAndAgentAndChannelAndStatus] create new agent user");
        final Date now = new Date();
        AgentUser agentUser = new AgentUser();
        agentUser.setNickname(contact.getNickname());
        agentUser.setUsername(contact.getName());
        agentUser.setAgentno(agent.getId());
        agentUser.setAgentname(agent.getUname());
        agentUser.setCreatetime(now);
        agentUser.setUpdatetime(now);
        agentUser.setChannel(channel);
        agentUser.setLogindate(now);
        agentUser.setUserid(onlineUser.getId());
        agentUser.setCreater(creator.getId());
        agentUser.setStatus(status);
        agentUser.setServicetime(now);
        agentUser.setOrgi(creator.getOrgi());

        // 获取 appId
        if (StringUtils.equals(channel, MainContext.ChannelType.SKYPE.toString())) {
            final SNSAccount snsAccount = snsAccountRes.findOneBySnstypeAndOrgi(
                    MainContext.ChannelType.SKYPE.toString(), agent.getOrgi());
            if (snsAccount != null) {
                agentUser.setAppid(snsAccount.getSnsid());
            } else {
                throw new CSKefuException("Skype Channel is not available.");
            }
        }

        // 创建 AgentService
        AgentService agentService = new AgentService();
        MainUtils.copyProperties(agentUser, agentService);
        agentUser.setAgentserviceid(agentService.getId());
        agentService.setAgentuserid(agentUser.getId());
        agentService.setAgentusername(agentUser.getAgentname());

        agentServiceRes.save(agentService);
        agentUserRes.save(agentUser);

        // 创建AgentUserContact
        AgentUserContacts agentUserContact = new AgentUserContacts();
        agentUserContact.setAppid(agentUser.getAppid());
        agentUserContact.setChannel(agentUser.getChannel());
        agentUserContact.setContactsid(contact.getId());
        agentUserContact.setUserid(onlineUser.getId());
        agentUserContact.setCreater(creator.getId());
        agentUserContact.setOrgi(creator.getOrgi());
        agentUserContact.setUsername(contact.getUsername());
        agentUserContact.setCreatetime(now);
        agentUserContactsRes.save(agentUserContact);

        return agentUser;
    }

    /**
     * 查找AgentUser
     * 先从缓存查找，再从数据库查找
     */
    public AgentUser resolveAgentUser(final String userid, final String agentuserid, final String orgi) throws CSKefuException {
        Optional<AgentUser> opt = cache.findOneAgentUserByUserIdAndOrgi(userid, orgi);
        if (!opt.isPresent()) {
            return agentUserRes.findById(agentuserid)
                    .orElseThrow(() -> new CSKefuException("Invalid transfer request, agent user not exist."));
        }
        return opt.get();
    }

    /**
     * 更新坐席当前服务中的用户状态
     * #TODO 需要分布式锁
     */
    public synchronized void updateAgentStatus(AgentStatus agentStatus, String orgi) {
        int users = cache.getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), orgi);
        agentStatus.setUsers(users);
        agentStatus.setUpdatetime(new Date());
        agentStatusRes.save(agentStatus);
    }

    /**
     * 使用AgentUser查询
     */
    public Optional<AgentUser> findOne(final String id) {
        return agentUserRes.findById(id);
    }

    /**
     * 保存
     */
    public void save(final AgentUser agentUser) {
        agentUserRes.save(agentUser);
    }


}
