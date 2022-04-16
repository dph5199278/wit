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
package com.cs.wit.activemq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cs.wit.basic.Constants;
import com.cs.wit.cache.Cache;
import com.cs.wit.persistence.repository.BlackListRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 访客黑名单
 */
@Component
@RequiredArgsConstructor
public class BlackListEventSubscription {
    private final static Logger logger = LoggerFactory.getLogger(BlackListEventSubscription.class);

    @NonNull
    private final Cache cache;

    @NonNull
    private final BlackListRepository blackListRes;

    @NonNull
    private final BrokerPublisher brokerPublisher;

    /**
     * 发送移除黑名单的消息
     * @param payload
     */
    public void publish(JsonObject payload, int timeSeconds) {
        brokerPublisher.send(Constants.WEBIM_SOCKETIO_ONLINE_USER_BLACKLIST, payload.toString(), false, timeSeconds);
    }

    /**
     * 拉黑访客到达拉黑时间后，从黑名单中移除
     */
    @JmsListener(destination = Constants.WEBIM_SOCKETIO_ONLINE_USER_BLACKLIST, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        logger.info("[onMessage] payload {}", payload);

        try {
            final JSONObject json = JSON.parseObject(payload);
            final String userId = json.getString("userId");
            final String orgi = json.getString("orgi");

            if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(orgi)) {
                cache.findOneBlackEntityByUserIdAndOrgi(userId, orgi).ifPresent(blackListRes::delete);
            } else {
                logger.warn("[onMessage] error: invalid payload");
            }
        } catch (Exception e) {
            logger.error("[onMessage] error", e);
        }
    }
}
