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

import com.cs.wit.util.IdUtils;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.uuid.UuidValueGenerator;

/**
 * UuidVersionSevenStrategy
 *
 * @author Dely
 * @version 1.0
 * @date 2026-03 add
 */
public class UuidVersionSevenStrategy implements UuidValueGenerator {

  @Override
  public UUID generateUuid(SharedSessionContractImplementor session) {
    return IdUtils.getUuid7();
  }
}
