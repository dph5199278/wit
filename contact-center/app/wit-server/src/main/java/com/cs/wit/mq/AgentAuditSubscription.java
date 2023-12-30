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

package com.cs.wit.mq;

import com.cs.wit.basic.Constants;
import com.cs.wit.cache.Cache;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserAudit;
import com.cs.wit.mq.broker.BrokerPublisher;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.proxy.AgentAuditProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.util.SerializeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Optional;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 会话监控
 */
@Component
@RequiredArgsConstructor
public class AgentAuditSubscription {
    private final static Logger logger = LoggerFactory.getLogger(AgentAuditSubscription.class);

    @NonNull
    private final Cache cache;

    @NonNull
    private final AgentAuditProxy agentAuditProxy;

    @NonNull
    private final AgentUserRepository agentUserRes;

    @Resource(name = "topicBrokerPublisher")
    private BrokerPublisher brokerPublisher;

    /**
     * 发送坐席会话监控消息
     * @param payload
     */
    public void publish(JsonObject payload) {
        brokerPublisher.send(Constants.AUDIT_AGENT_MESSAGE, payload.toString());
    }

    /**
     * 接收坐席会话监控消息
     */
    @JmsListener(destination = Constants.AUDIT_AGENT_MESSAGE, containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String msg) {
        logger.info("[onMessage] payload {}", msg);
        try {
            final JsonObject json = JsonParser.parseString(msg).getAsJsonObject();

            if (json.has("orgi") && json.has("data") &&
                    json.has("agentUserId") &&
                    json.has("event") && json.has("agentno")) {

                // 查找关联的会话监控信息
                String agentUserId = json.get("agentUserId").getAsString();
                final AgentUserAudit agentUserAudit = cache.findOneAgentUserAuditByOrgiAndId(
                        json.get("orgi").getAsString(),
                        agentUserId).orElseGet(() -> {
                    Optional<AgentUser> optional = agentUserRes.findById(agentUserId);
                    if (optional.isPresent()) {
                        final AgentUser agentUser = optional.get();
                        return agentAuditProxy.updateAgentUserAudits(agentUser);
                    } else {
                        logger.warn(
                                "[onMessage] can not find agent user by id {}", agentUserId);
                    }
                    return null;
                });

                if (agentUserAudit != null) {
                    final String agentno = json.get("agentno").getAsString();
                    logger.info(
                            "[onMessage] agentno {}, subscribers size {}, subscribers {}", agentno,
                            agentUserAudit.getSubscribers().size(),
                            StringUtils.join(agentUserAudit.getSubscribers().keySet(), "|"));

                    // 发送消息给坐席监控，不需要分布式，因为这条消息已经是从MQ使用Topic多机广播
                    for (final String subscriber : agentUserAudit.getSubscribers().keySet()) {
                        logger.info("[onMessage] process subscriber {}", subscriber);
                        if (!StringUtils.equals(subscriber, agentno)) {
                            logger.info("[onMessage] publish event to {}", subscriber);
                            NettyClients.getInstance().publishAuditEventMessage(
                                    subscriber,
                                    json.get("event").getAsString(),
                                    SerializeUtil.deserialize(json.get("data").getAsString()));
                        }
                    }
                } else {
                    logger.warn(
                            "[onMessage] can not resolve agent user audit object for agent user id {}",
                            agentUserId);
                }
            } else {
                throw new CSKefuException("Invalid payload.");
            }
        } catch (Exception e) {
            logger.error("[onMessage] error", e);
        }
    }
}
