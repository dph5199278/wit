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

package com.cs.wit.basic;

import cn.hutool.core.util.StrUtil;

/**
 * ServiceEnumTypeContext
 *
 * @author Dely
 * @version 1.0
 * @date 2023-05 add
 */
public class ServiceEnumTypeContext {

  public enum CallType {
    IN("呼入", 1),
    OUT("呼出", 2),
    SYSTEM("系统", 3),
    INVITE("邀请", 4);

    private final String name;
    private final int index;

    CallType(final String name, final int index) {
      this.name = name;
      this.index = index;
    }

    public String toLetters() {
      return super.toString().toLowerCase();
    }

    public static CallType toValue(final String str) {
      for (final CallType item : values()) {
        if (StrUtil.equals(item.toString(), str)) {
          return item;
        }
      }
      throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
      return this.name;
    }

  }

  public enum CallServiceStatus {
    INQUENE("就绪", 1),
    RING("振铃", 2),            //振铃
    INCALL("应答", 3),            //应答
    BRIDGE("桥接", 4),            //桥接
    HOLD("已挂起", 5),            //已挂起
    HANGUP("已挂机", 6),           //已挂机
    OFFLINE("离线", 7);            //离线

    private final String name;
    private final int index;

    CallServiceStatus(final String name, final int index) {
      this.name = name;
      this.index = index;
    }


    public String toLetters() {
      return super.toString().toLowerCase();
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

}
