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

package com.cs.wit.util;

import java.util.Objects;
import java.util.UUID;

/**
 * IdUtils
 *
 * @author Dely
 * @version 1.0
 * @date 2023-05 add
 */
public class IdUtils {

  /**
   * @return 返回用UUID去自定义加密的值
   */
  public static String genID() {
    return genIDByKey(getUUID());
  }

  /**
   * @param key 原始值
   * @return 返回经过自定义加密的值
   */
  public static String genIDByKey(String key) {
    return Objects.requireNonNull(Base62.encode(key)).toLowerCase();
  }

  /**
   * @return 获取无分隔符的UUID
   */
  public static String getUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * @param session 会话id
   * @return 获取无分隔符的会话id
   */
  public static String getContextID(String session) {
    return session.replaceAll("-", "");
  }

}
