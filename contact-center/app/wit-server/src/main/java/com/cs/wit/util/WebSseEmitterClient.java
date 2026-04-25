/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.util;

import com.cs.wit.proxy.OnlineUserProxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSseEmitterClient {
    private final static Logger logger = LoggerFactory.getLogger(WebSseEmitterClient.class);
    private ConcurrentMap<String, WebIMClient> imClientsMap = new ConcurrentHashMap<>();

    public List<WebIMClient> getClients(String userid) {
        if (StringUtils.isBlank(userid)) {
            logger.warn("[getClients] userid is blank");
            return new ArrayList<>();
        }
        Collection<WebIMClient> values = imClientsMap.values();
        List<WebIMClient> clients = new ArrayList<>();
        for (WebIMClient client : values) {
            if (client != null && userid.equals(client.getUserid())) {
                clients.add(client);
            }
        }
        logger.info("[getClients] get clients for userId {}, size {}", userid, clients.size());
        return clients;
    }

    public int size() {
        return imClientsMap.size();
    }

    public void putClient(String userid, WebIMClient client) {
        if (StringUtils.isBlank(userid) || client == null) {
            logger.warn("[putClient] invalid parameters: userid={}, client={}", userid, client);
            return;
        }
        logger.info("[putClient] userid {} client {}", userid, client.getClient());
        imClientsMap.put(client.getClient(), client);
    }

    public void removeClient(String userid, String client, boolean timeout) throws Exception {
        if (StringUtils.isBlank(userid) || StringUtils.isBlank(client)) {
            logger.warn("[removeClient] invalid parameters: userid={}, client={}", userid, client);
            return;
        }
        logger.info("[removeClient] remove client {} for userid {}", client, userid);
        // Remove from map directly using client ID as key
        imClientsMap.remove(client);
        // Check if user has any remaining clients
        List<WebIMClient> remainingClients = this.getClients(userid);
        if (remainingClients.isEmpty() && timeout) {
            OnlineUserProxy.offline(userid, userid);
            logger.info("[removeClient] set onlineUser {} as offline.", userid);
        }
    }
}
