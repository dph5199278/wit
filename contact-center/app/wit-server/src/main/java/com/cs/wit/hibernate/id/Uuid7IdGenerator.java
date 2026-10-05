/*
 * Copyright (C) 2026 Dely<https://github.com/dph5199278>, All rights reserved.
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

package com.cs.wit.hibernate.id;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

import java.util.EnumSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.id.uuid.UuidValueGenerator;

/**
 * Uuid7IdGenerator
 *
 * @author Dely
 * @version 1.0
 * @date 2026-03 add
 */
public class Uuid7IdGenerator implements BeforeExecutionGenerator {
  private final UuidValueGenerator generator;

  public Uuid7IdGenerator() {
    generator = new UuidVersionSevenStrategy();
  }

  /**
   * @return {@link EventTypeSets#INSERT_ONLY}
   */
  @Override
  public EnumSet<EventType> getEventTypes() {
    return INSERT_ONLY;
  }

  @Override
  public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
    return generator.generateUuid( session ).toString().replaceAll("-", "");
  }
}
