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
package com.cs.wit.socketio.client;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NettyClient {

    /**
     * 已连接的客户端数
     * @return
     */
    int size();

    /**
     * 获取当前所有已连接的客户端
     * @return
     */
    List<SocketIOClient> getAllClients();

    /**
     * 获取map形式的所有已连接的客户端
     * @return
     */
    Map<String, Collection<SocketIOClient>> asMap();

    /**
     * 获取缓存中的客户端
     * @param key
     * @return
     */
    List<SocketIOClient> getClients(String key);

    /**
     * 将连接的客户端放入缓存
     * @param key
     * @param client
     */
    void putClient(String key, SocketIOClient client);

    /**
     * 返回该KEY剩余的连接客户端的数量
     * @param key
     * @param id
     * @return
     */
    int removeClient(String key, String id);

    /**
     * 删除当前连接key的所有缓存客户端
     * @param key
     */
    void removeAll(final String key);
}
