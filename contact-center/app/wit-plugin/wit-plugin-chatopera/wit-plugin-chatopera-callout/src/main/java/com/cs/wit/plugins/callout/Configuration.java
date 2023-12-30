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

package com.cs.wit.plugins.callout;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.cs.wit.basic.ModuleContext;
import com.cs.wit.basic.plugins.PluginRegistry;
import com.cs.wit.basic.plugins.PluginsLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration
 *
 * @author Dely
 * @version 1.0
 * @date 2023-06 add
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "cskefu.plugins.callout", name = "enable", havingValue = "true")
public class Configuration {

  private final static Logger logger = LoggerFactory.getLogger(Configuration.class);

  @PostConstruct
  public void setup() {
    // 外呼模块
    ModuleContext.enableModule(PluginDescriptor.PLUGIN_NAME);
  }

  /**
   * 外呼线程池
   * @return
   */
  @Bean(name = "callOutTaskExecutor")
  public ThreadPoolTaskExecutor callout() {
    ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
    // 线程池维护线程的最少数量
    poolTaskExecutor.setCorePoolSize(7);
    // 线程池维护线程的最大数量
    poolTaskExecutor.setMaxPoolSize(100);
    // 线程池所使用的缓冲队列
    poolTaskExecutor.setQueueCapacity(200);
    // 线程池维护线程所允许的空闲时间
    poolTaskExecutor.setKeepAliveSeconds(30000);
    poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    poolTaskExecutor.setThreadNamePrefix("cs-callout-");

    return poolTaskExecutor;
  }

  @Bean(name = "calloutNamespace")
  public SocketIONamespace getCalloutIMSocketIONameSpace(SocketIOServer server) {
    SocketIONamespace calloutSocketIONameSpace = null;
    Constructor<?> constructor;
    try {
      constructor = Class.forName(
          PluginsLoader.getIOEventHandler(PluginRegistry.PLUGIN_ENTRY_CALLOUT)).getConstructor(
          SocketIOServer.class);
      calloutSocketIONameSpace = server.addNamespace("/callout/exchange");
      calloutSocketIONameSpace.addListeners(constructor.newInstance(server));
    } catch (NoSuchMethodException | SecurityException
        | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      logger.error("[calloutNamespace] error", e);
    }
    return calloutSocketIONameSpace;
  }
}
