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

import com.cs.wit.activemq.AgentAuditSubscription;
import com.cs.wit.basic.MainContext;
import com.cs.wit.cache.Cache;
import com.cs.wit.exception.CSKefuCacheException;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserAudit;
import com.cs.wit.util.SerializeUtil;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 会话监控常用方法
 */
@Component
public class AgentAuditProxy {

    private final static Logger logger = LoggerFactory.getLogger(AgentAuditProxy.class);

    @Autowired
    private AgentAuditSubscription agentAuditSubscription;

    @Autowired
    private Cache cache;

    @Lazy
    @Autowired
    private AgentUserProxy agentUserProxy;

    /**
     * 更新agentuser 监控人员列表
     *
     * @param agentUser
     */
    public AgentUserAudit updateAgentUserAudits(final AgentUser agentUser) {
        try {
            // get interests
            HashMap<String, String> subscribers = agentUserProxy.getAgentUserSubscribers(
                    agentUser.getOrgi(), agentUser);
            AgentUserAudit audit = new AgentUserAudit(agentUser.getOrgi(), agentUser.getId(), subscribers);
            cache.putAgentUserAuditByOrgi(agentUser.getOrgi(), audit);
            return audit;
        } catch (CSKefuCacheException e) {
            logger.error("[updateAgentUserAudits] exception", e);
        }
        return null;
    }

    /**
     * 使用MQ，异步且支持分布式
     *
     * @param agentUser
     * @param data
     * @param event
     */
    public void publishMessage(final AgentUser agentUser, Serializable data, final MainContext.MessageType event) {
        JsonObject json = new JsonObject();
        json.addProperty("orgi", agentUser.getOrgi());
        json.addProperty("data", SerializeUtil.serialize(data));
        json.addProperty("agentUserId", agentUser.getId());
        json.addProperty("event", event.toString());
        // 发送或者接收的对应的坐席的ID
        json.addProperty("agentno", agentUser.getAgentno());
        agentAuditSubscription.publish(json);
    }
}
