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

package com.cs.wit.activemq;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BrokerPublisher {

    final static private Logger logger = LoggerFactory.getLogger(BrokerPublisher.class);

    @NonNull
    private final JmsTemplate jmsTemplate;

    @PostConstruct
    public void setup() {
        logger.info("[ActiveMQ Publisher] setup successfully.");
    }


    /**
     * 时延消息
     *
     * @param delay available by delayed seconds
     */
    public void send(final String destination, final String payload, final boolean isTopic, final int delay) {
        try {
            jmsTemplate.convertAndSend(isTopic ? new ActiveMQTopic(destination) : new ActiveMQQueue(destination),
                    payload, m -> {
                m.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(),  1000L * delay + System.currentTimeMillis());
                return m;
            });
            logger.debug("[send] send succ, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    public void send(final String destination, final String payload, boolean isTopic) {
        try {
            jmsTemplate.convertAndSend(isTopic ? new ActiveMQTopic(destination) : new ActiveMQQueue(destination),
                    payload);
            logger.debug("[send] send success, dest {}, payload {}", destination, payload);
        } catch (Exception e) {
            logger.warn("[send] error happens.", e);
        }
    }

    public void send(final String destination, final String payload) {
        send(destination, payload, false);
    }

    public void send(final String destination, final Map<String, String> payload) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, String> entry : payload.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }

        send(destination, obj.toString());
    }
}
