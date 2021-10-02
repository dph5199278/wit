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

import com.alibaba.fastjson.JSONObject;
import com.cs.wit.activemq.BrokerPublisher;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.AgentStatusRepository;
import com.cs.wit.persistence.repository.WorkSessionRepository;
import com.cs.wit.proxy.AgentProxy;
import com.cs.wit.proxy.AgentSessionProxy;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.socketio.message.AgentStatusMessage;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.socketio.message.InterventMessage;
import com.cs.wit.socketio.message.Message;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;

public class AgentEventHandler {
    final static private Logger logger = LoggerFactory.getLogger(AgentEventHandler.class);

    public AgentEventHandler(@NonNull final SocketIOServer server
            , @NonNull final BrokerPublisher brokerPublisher
            , @NonNull final AgentStatusRepository agentStatusRes
            , @NonNull final AgentUserProxy agentUserProxy
            , @NonNull final AgentProxy agentProxy
            , @NonNull final AgentSessionProxy agentSessionProxy
            , @NonNull final UserProxy userProxy
    ) {
        this.server = server;

        this.brokerPublisher = brokerPublisher;
        this.agentStatusRes = agentStatusRes;
        this.agentUserProxy = agentUserProxy;
        this.agentProxy = agentProxy;
        this.agentSessionProxy = agentSessionProxy;
        this.userProxy = userProxy;
    }

    private final SocketIOServer server;
    private final BrokerPublisher brokerPublisher;
    private final AgentStatusRepository agentStatusRes;
    private final AgentUserProxy agentUserProxy;
    private final AgentProxy agentProxy;
    private final AgentSessionProxy agentSessionProxy;
    private final UserProxy userProxy;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        final String userid = client.getHandshakeData().getSingleUrlParam("userid");
        final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        final String session = client.getHandshakeData().getSingleUrlParam("session");
        final String admin = client.getHandshakeData().getSingleUrlParam("admin");
        final String connectid = MainUtils.getUUID();
        logger.info(
                "[onConnect] user: {}, orgi: {}, session: {}, admin: {}, connectid {}", userid, orgi, session, admin,
                connectid);

        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(session)) {

            // 验证当前的SSO中的session是否和传入的session匹配
            if (getAgentSessionProxy().isInvalidSessionId(userid, session, orgi)) {
                // 该session信息不合法
                logger.info("[onConnect] invalid sessionId {}", session);
                return;
            }

            client.set("agentno", userid);
            client.set("session", session);
            client.set("connectid", connectid);

            // 更新AgentStatus到数据库
            getAgentStatusRes().findOneByAgentnoAndOrgi(userid, orgi).ifPresent(p -> {
                p.setUpdatetime(new Date());
                p.setConnected(true);
                // 设置agentSkills
                p.setSkills(getUserProxy().getSkillsMapByAgentno(userid));
                getAgentStatusRes().save(p);
            });

            // 工作工作效率
            InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
            String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());

            WorkSessionRepository workSessionRepository = MainContext.getContext().getBean(WorkSessionRepository.class);
            int count = workSessionRepository.countByAgentAndDatestrAndOrgi(
                    userid, MainUtils.simpleDateFormat.get().format(new Date()), orgi);

            workSessionRepository.save(
                    MainUtils.createWorkSession(userid, MainUtils.getContextID(client.getSessionId().toString()),
                                                session, orgi, ip, address.getHostName(), admin, count == 0));

            NettyClients.getInstance().putAgentEventClient(userid, client);
        }
    }

    // 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String userid = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        String admin = client.getHandshakeData().getSingleUrlParam("admin");
        String session = client.getHandshakeData().getSingleUrlParam("session");
        String connectid = client.get("connectid");
        logger.info(
                "[onDisconnect] userId {}, orgi {}, admin {}, session {}, connectid {}", userid, orgi, admin, session,
                connectid);

        /**
         * 连接断开
         */
        if (NettyClients.getInstance().removeAgentEventClient(
                userid, MainUtils.getContextID(client.getSessionId().toString()), connectid) == 0) {
            // 该坐席和服务器没有连接了，但是也不能保证该坐席是停止办公了，可能稍后TA又打开网页
            // 所以，此处做一个30秒的延迟，如果该坐席30秒内没重新建立连接，则撤退该坐席
            // 更新该坐席状态，设置为"无连接"，不会分配新访客
            final AgentStatus agentStatus = MainContext.getCache().findOneAgentStatusByAgentnoAndOrig(userid, orgi);

            if (agentStatus != null) {
                agentStatus.setConnected(false);
                MainContext.getCache().putAgentStatusByOrgi(agentStatus, agentStatus.getOrgi());
            }

            /**
             * 业务断开
             * 在超时发生了一段时间后触发
             */
            JSONObject payload = new JSONObject();
            payload.put("userId", userid);
            payload.put("orgi", orgi);
            payload.put("isAdmin", StringUtils.isNotBlank(admin) && admin.equalsIgnoreCase("true"));
            getBrokerPublisher().send(Constants.WEBIM_SOCKETIO_AGENT_DISCONNECT, payload.toJSONString(),
                                      false,
                                      Constants.WEBIM_SOCKETIO_AGENT_OFFLINE_THRESHOLD);
        }
    }

    // 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "service")
    public void onServiceEvent(SocketIOClient client, AckRequest request, Message data) {

    }

    // 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "status")
    public void onStatusEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {

    }

    /**
     * 会话监控干预消息
     *
     * @param client
     * @param request
     * @param received
     */
    @OnEvent(value = "intervention")
    public void onIntervetionEvent(
            final SocketIOClient client,
            final AckRequest request,
            final InterventMessage received) throws JsonProcessingException {
        final String agentno = client.get("agentno");
        final String session = client.get("session");
        final String connectid = client.get("connectid");
        logger.info(
                "[onIntervetionEvent] intervention: agentno {}, session {}, connectid {}, payload {}", agentno, session,
                connectid,
                received.toJsonObject());

        if (received.valid()) {

            // 获得AgentUser
            final AgentUser agentUser = getAgentUserProxy().findOne(received.getAgentuserid()).get();

            // 验证当前的SSO中的session是否和传入的session匹配
            if (getAgentSessionProxy().isInvalidSessionId(
                    agentno, session, agentUser.getOrgi())) {
                // 该session信息不合法
                logger.info("[onIntervetionEvent] invalid sessionId {}", session);
                // 强制退出
                client.sendEvent(MainContext.MessageType.LEAVE.toString());
                return;
            }

            final User supervisor = getUserProxy().findOne(received.getSupervisorid());
            final Date now = new Date();

            // 创建消息
            /**
             * 消息体
             */
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(MainUtils.getUUID());
            chatMessage.setMessage(received.getContent());
            chatMessage.setCreatetime(now);
            chatMessage.setUpdatetime(now.getTime());

            // 访客接收消息，touser设置为agentUser userid
            chatMessage.setTouser(agentUser.getUserid());

            // 坐席发送消息，username设置为坐席
            chatMessage.setUsername(agentUser.getAgentname());
            chatMessage.setContextid(agentUser.getContextid());
            chatMessage.setUsession(agentUser.getUserid());
            chatMessage.setAgentserviceid(agentUser.getAgentserviceid());
            chatMessage.setAgentuser(agentUser.getId());

            /**
             * note 消息为会话监控干预消息的区分
             * 消息 setCalltype 是呼出，并且 intervented = true
             */
            // 消息中继续使用该会话的坐席发出，所以，访客看到的消息，依然是以同一坐席的名义
            chatMessage.setUserid(agentUser.getAgentno());
            // 坐席会话监控消息，设置为监控人员
            chatMessage.setCreater(supervisor.getId());
            // 监控人员名字
            chatMessage.setSupervisorname(supervisor.getUname());
            chatMessage.setChannel(agentUser.getChannel());
            chatMessage.setAppid(agentUser.getAppid());
            chatMessage.setOrgi(agentUser.getOrgi());
            chatMessage.setIntervented(true);


            // 消息类型
            chatMessage.setType(MainContext.MessageType.MESSAGE.toString());
            chatMessage.setMsgtype(received.toMediaType().toString());
            chatMessage.setCalltype(MainContext.CallType.OUT.toString());

            getAgentProxy().sendChatMessageByAgent(chatMessage, agentUser);
        } else {
            logger.warn("[onEvent] intervention invalid message", received.toString());
        }
    }

    /**
     * 接收到坐席通过WebIM发送的消息
     *
     * @param client
     * @param request
     * @param received
     * @throws IOException
     */
    // 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "message")
    public void onMessageEvent(
            final SocketIOClient client,
            final AckRequest request,
            final ChatMessage received) throws IOException {
        final String agentno = client.get("agentno");
        final String session = client.get("session");
        final String connectid = client.get("connectid");
        received.setSessionid(session);

        // 此处user代表坐席的ID
//        String agentno = client.getHandshakeData().getSingleUrlParam("userid");

        logger.info(
                "[onMessageEvent] message: agentUserId {}, agentno {}, toUser {}, channel {}, orgi {}, appId {}, userId {}, sessionId {}, connectid {}",
                received.getAgentuser(), agentno, received.getTouser(),
                received.getChannel(), received.getOrgi(), received.getAppid(), received.getUserid(),
                session, connectid);


        // 验证当前的SSO中的session是否和传入的session匹配
        if (getAgentSessionProxy().isInvalidSessionId(
                agentno, session, received.getOrgi())) {
            // 该session信息不合法
            logger.info("[onMessageEvent] invalid sessionId {}", session);
            // 强制退出
            client.sendEvent(MainContext.MessageType.LEAVE.toString());
            return;
        }

        AgentUser agentUser = MainContext.getCache().findOneAgentUserByUserIdAndOrgi(
                received.getTouser(), received.getOrgi()).orElseGet(null);


        /**
         * 判断用户在线状态，如果用户在线则通过webim发送
         * 检查收发双方的信息匹配
         */
        if (agentUser != null &&
                agentno != null &&
                StringUtils.equals(agentno, agentUser.getAgentno()) &&
                !StringUtils.equals(agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            logger.info("[onEvent] condition：visitor online.");

            /**
             * 消息体
             */
            received.setCalltype(MainContext.CallType.OUT.toString());
            if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                received.setTouser(agentUser.getUserid());
            }

            received.setId(MainUtils.getUUID());
            received.setChannel(agentUser.getChannel());
            received.setUsession(agentUser.getUserid());
            received.setUsername(agentUser.getAgentname());
            received.setContextid(agentUser.getContextid());

            received.setAgentserviceid(agentUser.getAgentserviceid());
            received.setCreater(agentUser.getAgentno());

            if (StringUtils.equals(MainContext.MediaType.COOPERATION.toString(), received.getMsgtype())) {
                received.setMsgtype(MainContext.MediaType.COOPERATION.toString());
            } else {
                received.setMsgtype(MainContext.MediaType.TEXT.toString());
            }

            getAgentProxy().sendChatMessageByAgent(received, agentUser);
        } else {
            logger.warn("[onEvent] message: unknown condition.");
        }
    }

    private AgentStatusRepository getAgentStatusRes() {
        return agentStatusRes;
    }

    private BrokerPublisher getBrokerPublisher() {
        return brokerPublisher;
    }

    private AgentUserProxy getAgentUserProxy() {
        return agentUserProxy;
    }

    private AgentProxy getAgentProxy() {
        return agentProxy;
    }

    private AgentSessionProxy getAgentSessionProxy() {
        return agentSessionProxy;
    }

    public UserProxy getUserProxy() {
        return userProxy;
    }
}