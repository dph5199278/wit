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

package com.cs.wit.mq.broker;

import com.google.gson.JsonObject;
import java.util.Map;
import jakarta.jms.Destination;
import org.apache.activemq.artemis.api.core.Message;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.lang.NonNull;

/**
 * The interface Broker publisher.
 */
public interface BrokerPublisher {

    /**
     * 获取日志器
     *
     * @return the logger
     */
    @NonNull
    Logger getLogger();

    /**
     * 获取JMS模板
     *
     * @return the jms template
     */
    @NonNull
    JmsTemplate getJmsTemplate();

    /**
     * 构建消息目标
     *
     * @param destination the destination
     * @return the destination
     */
    @NonNull
    Destination buildDestination(final String destination);

    /**
     * 时延消息
     *
     * @param destination the destination
     * @param payload     the payload
     * @param delay       available by delayed seconds
     */
    default void send(final String destination, final String payload, final long delay) {
        try {
            getJmsTemplate().convertAndSend(buildDestination(destination), payload, m -> {
                if(delay > 0) {
                    m.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(),
                        1000L * delay + System.currentTimeMillis());
                }
                return m;
            });
            getLogger().debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            getLogger().warn("[send] error happens.", e);
        }
    }

    /**
     * 时延消息
     *
     * @param destination the destination
     * @param payload     the payload
     */
    default void send(final String destination, final String payload) {
        send(destination, payload, -1);
    }

    /**
     * 时延消息
     *
     * @param destination the destination
     * @param payload     the payload
     */
    default void send(final String destination, final Map<String, String> payload) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, String> entry : payload.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }

        send(destination, obj.toString());
    }
}
