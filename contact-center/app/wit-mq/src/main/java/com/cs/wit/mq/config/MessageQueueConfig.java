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

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * The type Message Queue Config.
 *
 * @author Dely
 * @version 1.0
 * @date 2024-08 add
 */
@EnableJms
@Configuration
public class MessageQueueConfig {

  /**
   * topic mode ListenerContainer
   *
   * @param connectionFactory the connection factory
   * @return the jms listener container factory
   */
  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerTopic(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
    return DefaultJmsListenerContainerFactoryBuilder.builder()
        .withConnectionFactory(connectionFactory)
        .withPubSubDomain(true)
        .build();
  }

  /**
   * queue mode ListenerContainer
   *
   * @param connectionFactory the connection factory
   * @return the jms listener container factory
   */
  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerQueue(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
    return DefaultJmsListenerContainerFactoryBuilder.builder()
        .withConnectionFactory(connectionFactory)
        .withPubSubDomain(false)
        .build();
  }
}
