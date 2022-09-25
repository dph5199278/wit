/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */
package com.cs.wit.mq;

import com.cs.wit.acd.ACDAgentDispatcher;
import com.cs.wit.acd.ACDWorkMonitor;
import com.cs.wit.acd.basic.ACDComposeContext;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.cache.Cache;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.mq.broker.BrokerPublisher;
import com.cs.wit.persistence.repository.AgentStatusRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 处理SocketIO的离线事件
 */
@Component
public class SocketioConnEventSubscription {

    private final static Logger logger = LoggerFactory.getLogger(SocketioConnEventSubscription.class);

    @Resource
    private ACDAgentDispatcher acdAgentDispatcher;

    @Resource
    private ACDWorkMonitor acdWorkMonitor;

    @Resource
    private AgentStatusRepository agentStatusRes;

    @Resource
    private Cache cache;

    @Resource(name = "queueBrokerPublisher")
    private BrokerPublisher brokerPublisher;

    @PostConstruct
    public void setup() {
        logger.info("Subscription is setup successfully.");
    }

    /**
     * Publish Message
     *
     * @param payload
     */
    public void publish(final JsonObject payload) {
        brokerPublisher.send(Constants.WEBIM_SOCKETIO_AGENT_DISCONNECT, payload.toString(), Constants.WEBIM_SOCKETIO_AGENT_OFFLINE_THRESHOLD);
    }

    @JmsListener(destination = Constants.WEBIM_SOCKETIO_AGENT_DISCONNECT, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);

        try {
            JsonObject j = JsonParser.parseString(payload).getAsJsonObject();
            if (j.has("userId") && j.has("orgi") && j.has("isAdmin")) {
                final AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                        j.get("userId").getAsString(),
                        j.get("orgi").getAsString());
                if (agentStatus != null && (!cache.existConnectAlive(j.get("orgi").getAsString(), j.get("userId").getAsString()))) {
                    // 处理该坐席为离线
                    // 重分配坐席
                    ACDComposeContext ctx = new ACDComposeContext();
                    ctx.setAgentno(agentStatus.getAgentno());
                    ctx.setOrgi(agentStatus.getOrgi());
                    acdAgentDispatcher.dequeue(ctx);
                    if (ctx.isResolved()) {
                        logger.info("[onMessage] re-allotAgent for user's visitors successfully.");
                    } else {
                        logger.info("[onMessage] re-allotAgent, error happens.");
                    }

                    // 更新数据库
                    agentStatus.setBusy(false);
                    agentStatus.setStatus(MainContext.AgentStatusEnum.OFFLINE.toString());
                    agentStatus.setUpdatetime(new Date());

                    // 设置该坐席状态为离线
                    cache.deleteAgentStatusByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi());
                    agentStatusRes.save(agentStatus);

                    // 记录坐席工作日志
                    acdWorkMonitor.recordAgentStatus(agentStatus.getAgentno(),
                                                     agentStatus.getUsername(),
                                                     agentStatus.getAgentno(),
                                                     j.get("isAdmin").getAsBoolean(),
                                                     agentStatus.getAgentno(),
                                                     agentStatus.getStatus(),
                                                     MainContext.AgentStatusEnum.OFFLINE.toString(),
                                                     MainContext.AgentWorkType.MEIDIACHAT.toString(),
                                                     agentStatus.getOrgi(), null);
                } else if (agentStatus == null) {
                    // 该坐席已经完成离线设置
                    logger.info("[onMessage] agent is already offline, skip any further operations");
                } else {
                    // 该坐席目前在线，忽略该延迟事件
                    logger.info("[onMessage] agent is online now, ignore this message.");
                }
            }
        } catch (Exception e) {
            logger.error("onMessage", e);
        }
    }

}
