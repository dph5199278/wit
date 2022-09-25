/*
 * Copyright (c) 2022 Dely. <https://github.com/dph5199278>
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

import com.cs.wit.mq.broker.BrokerPublisher;
import com.cs.wit.mq.broker.QueueBrokerPublisher;
import com.cs.wit.mq.broker.TopicBrokerPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

/**
 * 202209:Add by Dely<br/>
 * BrokerConfig
 *
 * @author Dely
 * @version 1.0
 * @date 2022 -09 add
 */
@Configuration
@Slf4j
public class BrokerConfig {

  /**
   * Topic broker publisher broker publisher.
   *
   * @param jmsTemplate the jms template
   * @return the broker publisher
   */
  @Bean
  public BrokerPublisher topicBrokerPublisher(final JmsTemplate jmsTemplate) {
    BrokerPublisher publisher = new TopicBrokerPublisher(jmsTemplate);
    log.info("[TopicPublisher] setup successfully.");
    return publisher;
  }

  /**
   * Queue broker publisher broker publisher.
   *
   * @param jmsTemplate the jms template
   * @return the broker publisher
   */
  @Bean
  public BrokerPublisher queueBrokerPublisher(final JmsTemplate jmsTemplate) {
    BrokerPublisher publisher = new QueueBrokerPublisher(jmsTemplate);
    log.info("[QueuePublisher] setup successfully.");
    return publisher;
  }
}
