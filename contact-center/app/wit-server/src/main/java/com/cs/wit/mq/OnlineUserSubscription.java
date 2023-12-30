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

import com.cs.wit.basic.Constants;
import com.cs.wit.mq.broker.BrokerPublisher;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.util.SerializeUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * IM OnlineUser
 */
@Component
public class OnlineUserSubscription {

    private final static Logger logger = LoggerFactory.getLogger(OnlineUserSubscription.class);

    @Resource(name = "topicBrokerPublisher")
    private BrokerPublisher brokerPublisher;

    @PostConstruct
    public void setup() {
        logger.info("Subscription is setup successfully.");
    }

    /**
     * Publish Message
     *
     * @param j
     */
    public void publish(final JsonObject j) {
        brokerPublisher.send(Constants.INSTANT_MESSAGING_MQ_TOPIC_ONLINEUSER, j.toString());
    }

    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_TOPIC_ONLINEUSER, containerFactory = "jmsListenerContainerTopic")
    public void onMessage(final String payload){
        logger.info("[onMessage] payload {}", payload);
        JsonObject j = JsonParser.parseString(payload).getAsJsonObject();
        logger.debug("[instant messaging] message body {}", j.toString());
        try {
            NettyClients.getInstance().publishIMEventMessage(j.get("id").getAsString(),
                    j.get("event").getAsString(),
                    SerializeUtil.deserialize(j.get("data").getAsString()));
        } catch (Exception e) {
            logger.error("onMessage", e);
        }
    }

}
