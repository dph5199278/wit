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

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModuleContext
 *
 * @author Dely
 * @version 1.0
 * @date 2023-05 add
 */
public class ModuleContext {

  private final static Logger logger = LoggerFactory.getLogger(ModuleContext.class);

  private static final Set<String> modules = new HashSet<>();
  static {
    enableModule("report");
  }

  /**
   * 开启模块
   *
   * @param moduleName
   */
  public static void enableModule(final String moduleName) {
    logger.info("[module] enable module {}", moduleName);
    modules.add(StringUtils.lowerCase(moduleName));
  }

  public static boolean hasModule(final String moduleName) {
    return modules.contains(StringUtils.lowerCase(moduleName));
  }

  public static void removeModule(final String moduleName) {
    modules.remove(moduleName);
  }

  /**
   * 获得Model
   *
   * @return
   */
  public static Set<String> getModules() {
    return modules;
  }

}
