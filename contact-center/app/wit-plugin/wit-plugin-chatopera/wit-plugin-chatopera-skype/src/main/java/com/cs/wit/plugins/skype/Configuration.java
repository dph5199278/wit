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

package com.cs.wit.plugins.skype;

import com.cs.compose4j.AbstractContext;
import com.cs.compose4j.Functional;
import com.cs.compose4j.Middleware;
import com.cs.wit.basic.ModuleContext;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * Configuration
 *
 * @author Dely
 * @version 1.0
 * @date 2023-06 add
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "cskefu.plugins.skype", name = "enable", havingValue = "true")
public class Configuration {

  @PostConstruct
  public void setup() {
    // skype模块
    ModuleContext.enableModule(PluginDescriptor.PLUGIN_NAME);
  }

  @Order(250)
  @Bean
  public Middleware skypeChannelMessager() {
    return new Middleware() {
      @Override
      public void apply(AbstractContext var1, Functional var2) {

      }
    };
  }
}
