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

import cn.hutool.v7.core.data.id.IdUtil;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * IdUtils
 *
 * @author Dely
 * @version 1.0
 * @date 2023-05 add
 */
public class IdUtils {

  private static final Random RANDOM = ThreadLocalRandom.current();

  private static final AtomicLong LAST_UUID_V7_TIME = new AtomicLong(0);

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
    return IdUtil.simpleUUID();
  }

  /**
   * @param session 会话id
   * @return 获取无分隔符的会话id
   */
  public static String getContextID(String session) {
    return session.replaceAll("-", "");
  }

  /**
   * Gets uuid 7.
   *
   * @return the uuid 7
   */
  public static UUID getUuid7() {
    long now = Instant.now().toEpochMilli();

    // 低12位预留给序列号
    long base = now << 12;

    while (true) {
      long last = LAST_UUID_V7_TIME.get();
      long candidate = base;

      // 时钟回退或同毫秒内序列溢出
      if (candidate <= last) {
        // 单调递增
        candidate = last + 1;
      }

      if (LAST_UUID_V7_TIME.compareAndSet(last, candidate)) {
        long timestamp = candidate >> 12;

        // 12位序列号
        long seq = candidate & 0xFFF;

        // 62位随机数（保证安全性）
        long random62 = RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL;

        // 时间戳占位0-47
        // 版本号7占位48-51
        // 序列号占位52-63
        long mostSigBits = (timestamp << 16)
            | (0x7L << 12)
            | seq;

        // 变体10占位62-63
        long leastSigBits = (0x2L << 62)
            | random62;

        return new UUID(mostSigBits, leastSigBits);
      }
    }
  }

  /**
   * Gets uuid 7 str.
   *
   * @return the uuid 7 str
   */
  public static String getUuid7Str() {
    return IdUtils.getUUID().replaceAll("-", "");
  }
}
