/*
 * Copyright (C) 2023 Dely. <https://github.com/dph5199278>
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

package com.cs.wit.plugins.chatbot;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.cs.wit.basic.ModuleContext;
import com.cs.wit.basic.plugins.PluginRegistry;
import com.cs.wit.basic.plugins.PluginsLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Configuration
 *
 * @author Dely
 * @version 1.0
 * @date 2023-06 add
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "cskefu.plugins.chatbot", name = "enable", havingValue = "true")
public class Configuration {

  @PostConstruct
  public void setup() {
    // 聊天机器人模块
    ModuleContext.enableModule(PluginDescriptor.PLUGIN_NAME);
  }

  @Bean(name = "chatbotNamespace")
  public SocketIONamespace getChatbotSocketIONameSpace(SocketIOServer server) {
    SocketIONamespace chatbotSocketIONameSpace = null;
    Constructor<?> constructor;
    try {
      constructor = Class.forName(
          PluginsLoader.getIOEventHandler(PluginRegistry.PLUGIN_ENTRY_CHATBOT)).getConstructor(
          SocketIOServer.class);
      chatbotSocketIONameSpace = server.addNamespace("/im/chatbot");
      chatbotSocketIONameSpace.addListeners(constructor.newInstance(server));
    } catch (NoSuchMethodException | SecurityException
        | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return chatbotSocketIONameSpace;

  }
}
