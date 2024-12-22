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
package com.cs.wit.socketio;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.cs.wit.acd.ACDVisitorDispatcher;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.cache.Cache;
import com.cs.wit.mq.SocketioConnEventSubscription;
import com.cs.wit.peer.PeerSyncEntIM;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.AgentStatusRepository;
import com.cs.wit.proxy.AgentProxy;
import com.cs.wit.proxy.AgentSessionProxy;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.socketio.handler.AgentEventHandler;
import com.cs.wit.socketio.handler.EntIMEventHandler;
import com.cs.wit.socketio.handler.IMEventHandler;
import com.cs.wit.util.ip.IPTools;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    public ServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Bean(name = "imNamespace")
    public SocketIONamespace getIMSocketIONameSpace(@NonNull final SocketIOServer server
            , @NonNull final AgentUserProxy agentUserProxy
            , @NonNull final AgentServiceRepository agentServiceRepository
            , @NonNull final ACDVisitorDispatcher acdVisitorDispatcher
            , @NonNull final IPTools ipTools
    ) {
        SocketIONamespace imSocketNameSpace = server.addNamespace(MainContext.NameSpaceEnum.IM.getNamespace());
        imSocketNameSpace.addListeners(new IMEventHandler(server, agentUserProxy, agentServiceRepository, acdVisitorDispatcher, ipTools));
        return imSocketNameSpace;
    }

    @Bean(name = "agentNamespace")
    public SocketIONamespace getAgentSocketIONameSpace(@NonNull final SocketIOServer server
            , @NonNull final AgentUserProxy agentUserProxy
            , @NonNull final SocketioConnEventSubscription socketioConnEventSubscription
            , @NonNull final AgentStatusRepository agentStatusRes
            , @NonNull final AgentProxy agentProxy
            , @NonNull final AgentSessionProxy agentSessionProxy
            , @NonNull final UserProxy userProxy
            , @NonNull final Cache cache) {
        SocketIONamespace agentSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.AGENT.getNamespace());
        agentSocketIONameSpace.addListeners(new AgentEventHandler(server, socketioConnEventSubscription, agentStatusRes, agentUserProxy, agentProxy, agentSessionProxy, userProxy, cache));
        return agentSocketIONameSpace;
    }

    @Bean(name = "entimNamespace")
    public SocketIONamespace getEntIMSocketIONameSpace(@NonNull final SocketIOServer server
            , @NonNull PeerSyncEntIM peerSyncEntIM) {
        SocketIONamespace entIMSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.ENTIM.getNamespace());
        entIMSocketIONameSpace.addListeners(new EntIMEventHandler(server, peerSyncEntIM));
        return entIMSocketIONameSpace;
    }

    @Bean(name = "callCenterNamespace")
    public SocketIONamespace getCallCenterIMSocketIONameSpace(SocketIOServer server) {
        SocketIONamespace callCenterSocketIONameSpace = null;
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            Constructor<?> constructor;
            try {
                constructor = Class.forName(
                        "com.cs.wit.socketio.server.handler.CallCenterEventHandler").getConstructor(
                        SocketIOServer.class);
                callCenterSocketIONameSpace = server.addNamespace(MainContext.NameSpaceEnum.CALLCENTER.getNamespace());
                callCenterSocketIONameSpace.addListeners(constructor.newInstance(server));
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return callCenterSocketIONameSpace;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        // IMServer 启动成功
        MainContext.setIMServerStatus(true);
    }

    @PreDestroy
    public void stop() {
        server.stop();
    }
}  
