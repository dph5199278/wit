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

package com.cs.wit.mq.broker;

import jakarta.jms.Destination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.lang.NonNull;

/**
 * 202209:Add by Dely<br/>
 * QueueBrokerPublisher
 *
 * @author Dely
 * @version 1.0
 * @date 2022-09 add
 */
@RequiredArgsConstructor
@Slf4j
public class QueueBrokerPublisher implements BrokerPublisher {

  @NonNull
  private final JmsTemplate jmsTemplate;

  @NonNull
  @Override
  public Logger getLogger() {
    return log;
  }

  @NonNull
  @Override
  public JmsTemplate getJmsTemplate() {
    return jmsTemplate;
  }

  @NonNull
  @Override
  public Destination buildDestination(String destination) {
    return new ActiveMQQueue(destination);
  }
}
