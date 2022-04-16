/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.socketio.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.cs.wit.acd.ACDServiceRouter;
import com.cs.wit.acd.ACDVisitorDispatcher;
import com.cs.wit.acd.basic.ACDComposeContext;
import com.cs.wit.acd.basic.ACDMessageHelper;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.socketio.message.AgentStatusMessage;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.socketio.util.HumanUtils;
import com.cs.wit.socketio.util.IMServiceUtils;
import com.cs.wit.util.IP;
import com.cs.wit.util.IPTools;
import java.net.InetSocketAddress;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

@SuppressWarnings("unused")
public class IMEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(IMEventHandler.class);

    public IMEventHandler(@NonNull final SocketIOServer server
            , @NonNull final AgentUserProxy agentUserProxy
            , @NonNull final AgentServiceRepository agentServiceRepository
            , @NonNull final ACDVisitorDispatcher acdVisitorDispatcher
    ) {
        this.server = server;

        this.agentUserProxy = agentUserProxy;
        this.agentServiceRepository = agentServiceRepository;
        this.acdVisitorDispatcher = acdVisitorDispatcher;
    }

    private final SocketIOServer server;
    private final AgentUserProxy agentUserProxy;
    private final AgentServiceRepository agentServiceRepository;
    private final ACDVisitorDispatcher acdVisitorDispatcher;

    private AgentUserProxy getAgentUserProxy() {
        return agentUserProxy;
    }

    private AgentServiceRepository getAgentServiceRepository() {
        return agentServiceRepository;
    }

    private ACDVisitorDispatcher getAcdVisitorDispatcher() {
        return acdVisitorDispatcher;
    }

    /**
     * 接入访客并未访客寻找坐席服务人员
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            final String user = client.getHandshakeData().getSingleUrlParam("userid");
            final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
            final String session = MainUtils.getContextID(client.getHandshakeData().getSingleUrlParam("session"));
            // 渠道标识
            final String appid = client.getHandshakeData().getSingleUrlParam("appid");
            // 要求目标坐席服务
            final String agent = client.getHandshakeData().getSingleUrlParam("agent");
            // 要求目标技能组服务
            final String skill = client.getHandshakeData().getSingleUrlParam("skill");
            // 是否是邀请后加入会话
            final boolean isInvite = StringUtils.equalsIgnoreCase(
                    client.getHandshakeData().getSingleUrlParam("isInvite"), "true");

            final String title = client.getHandshakeData().getSingleUrlParam("title");
            final String url = client.getHandshakeData().getSingleUrlParam("url");
            final String traceid = client.getHandshakeData().getSingleUrlParam("traceid");

            String nickname = client.getHandshakeData().getSingleUrlParam("nickname");

            final String osname = client.getHandshakeData().getSingleUrlParam("osname");
            final String browser = client.getHandshakeData().getSingleUrlParam("browser");

            logger.info(
                    "[onConnect] user {}, orgi {}, session {}, appid {}, agent {}, skill {}, title {}, url {}, traceid {}, nickname {}, isInvite {}",
                    user, orgi, session, appid, agent, skill, title, url, traceid, nickname, isInvite);

            // save connection info
            client.set("session", session);
            client.set("userid", user);
            client.set("appid", appid);
            client.set("isInvite", isInvite);

            // 保证传入的Nickname不是null
            if (StringUtils.isBlank(nickname)) {
                logger.info("[onConnect] reset nickname as it does not present.");
                nickname = "Guest_" + user;
            }

            if (StringUtils.isNotBlank(user)) {
                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());

                /*
                 * 加入到 缓存列表
                 */
                NettyClients.getInstance().putIMEventClient(user, client);

                /*
                 * 更新坐席服务类型
                 */
                IMServiceUtils.shiftOpsType(user, orgi, MainContext.OptType.HUMAN);

                IP ipdata = null;
                if ((StringUtils.isNotBlank(ip))) {
                    ipdata = IPTools.getInstance().findGeography(ip);
                }

                /*
                 * 用户进入到对话连接 ， 排队用户请求 , 如果返回失败，
                 * 表示当前坐席全忙，用户进入排队状态，当前提示信息 显示 当前排队的队列位置，
                 * 不可进行对话，用户发送的消息作为留言处理
                 */
                final ACDComposeContext ctx = ACDMessageHelper.getWebIMComposeContext(
                        user,
                        nickname,
                        orgi,
                        session,
                        appid,
                        ip,
                        osname,
                        browser,
                        "",
                        ipdata,
                        MainContext.ChannelType.WEBIM.toString(),
                        skill,
                        agent,
                        title,
                        url,
                        traceid,
                        user,
                        isInvite,
                        MainContext.ChatInitiatorType.USER.toString());
                getAcdVisitorDispatcher().enqueue(ctx);
                ACDServiceRouter.getAcdAgentService().notifyAgentUserProcessResult(ctx);
            } else {
                logger.warn("[onConnect] invalid connection, no user present.");
                //非法链接
                client.disconnect();
            }
        } catch (Exception e) {
            logger.error("[onConnect] error", e);
            client.disconnect();
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        final String user = client.getHandshakeData().getSingleUrlParam("userid");
        final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        logger.info("[onDisconnect] user {}, orgi {}", user, orgi);
        if (user != null) {
            try {
                /*
                 * 用户主动断开服务
                 */
                MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi)
                        .ifPresent(p -> ACDServiceRouter.getAcdAgentService().finishAgentService(p, orgi));
            } catch (Exception e) {
                logger.warn("[onDisconnect] error", e);
            }
            NettyClients.getInstance().removeIMEventClient(
                    user, MainUtils.getContextID(client.getSessionId().toString()));
        }
    }

    // 消息接收入口，用于接受网站资源用户传入的 个人信息
    @OnEvent(value = "new")
    public void onNewEvent(SocketIOClient client, AckRequest request, Contacts contacts) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");

        MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            p.setResion(contacts.getMemo());
            p.setChatbotops(false); // 非机器人客服
            p.setOpttype(MainContext.OptType.HUMAN.toString());
            getAgentUserProxy().save(p);
        });

        getAgentServiceRepository().findOneByUseridAndOrgiOrderByLogindateDesc(
                user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            p.setResion(contacts.getMemo());
            agentServiceRepository.save(p);
        });
    }

    // 消息接收入口，坐席状态更新
    @OnEvent(value = "agentstatus")
    public void onAgentStatusEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {
        logger.info("[onEvent] {}", data.getMessage());
    }

    // 消息接收入口，收发消息，用户向 坐席发送消息 和 向用户发送消息
    @OnEvent(value = "message")
    public void onMessageEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        if (data.getType() == null) {
            data.setType("message");
        }
        /*
         * 以下代码主要用于检查 访客端的字数限制
         */
        CousultInvite invite = OnlineUserProxy.consult(data.getAppid(), data.getOrgi());

        int dataLength = data.getMessage().length();
        if (invite != null && invite.getMaxwordsnum() > 0) {
            if (StringUtils.isNotBlank(data.getMessage()) && dataLength > invite.getMaxwordsnum()) {
                data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
            }
        }
//        else if (StringUtils.isNotBlank(data.getMessage()) && dataLength > 600) {
//            data.setMessage(data.getMessage().substring(0, 600));
//        }
        /*
         * 处理表情
         */
        data.setMessage(MainUtils.processEmoti(data.getMessage()));
        HumanUtils.processMessage(data, data.getUserid());
    }

}
