/*
 * Copyright (C) 2024 Dely<https://github.com/dph5199278>, All rights reserved.
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

package com.cs.wit.mq.config;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.lang.Nullable;

/**
 * DefaultJmsListenerContainerFactoryBuilder
 *
 * @author Dely
 * @version 1.0
 * @date 2024 -08 add
 */
public class DefaultJmsListenerContainerFactoryBuilder {

  @Nullable
  private ConnectionFactory connectionFactory;

  @Nullable
  private Boolean pubSubDomain;

  private DefaultJmsListenerContainerFactoryBuilder() {}

  /**
   * Builder default jms listener container factory builder.
   *
   * @return the default jms listener container factory builder
   */
  public static DefaultJmsListenerContainerFactoryBuilder builder() {
    return new DefaultJmsListenerContainerFactoryBuilder();
  }

  /**
   * Set the ConnectionFactory to use for obtaining JMS {@link Connection Connections}.
   *
   * @param connectionFactory the connection factory
   * @return the default jms listener container factory builder
   */
  public DefaultJmsListenerContainerFactoryBuilder withConnectionFactory(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    return this;
  }

  /**
   * Configure the destination accessor with knowledge of the JMS domain used. Default is
   * Point-to-Point (Queues).
   * <p>This setting primarily indicates what type of destination to resolve
   * if dynamic destinations are enabled.
   *
   * @param pubSubDomain "true" for the Publish/Subscribe domain ({@link jakarta.jms.Topic Topics}),
   *                     "false" for the Point-to-Point domain ({@link jakarta.jms.Queue Queues})
   * @return the default jms listener container factory builder
   */
  public DefaultJmsListenerContainerFactoryBuilder withPubSubDomain(Boolean pubSubDomain) {
    this.pubSubDomain = pubSubDomain;
    return this;
  }

  /**
   * Build default jms listener container factory.
   *
   * @return the default jms listener container factory
   */
  public DefaultJmsListenerContainerFactory build() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    if(null != this.connectionFactory) {
      factory.setConnectionFactory(this.connectionFactory);
    }
    if(null != this.pubSubDomain) {
      factory.setPubSubDomain(this.pubSubDomain);
    }
    return factory;
  }
}
