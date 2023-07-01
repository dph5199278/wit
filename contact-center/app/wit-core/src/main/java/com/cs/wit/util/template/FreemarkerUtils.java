/*
 * Copyright (C) 2023 Dely<https://github.com/dph5199278>, All rights reserved.
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

package com.cs.wit.util.template;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

/**
 * FreemarkerUtils
 *
 * @author Dely
 * @version 1.0
 * @date 2023-07 add
 */
public class FreemarkerUtils {

  /**
   * Gets template.
   *
   * @param template the template
   * @param values   the values
   * @return the template
   * @throws IOException       the io exception
   * @throws TemplateException the template exception
   */
  public static String getTemplate(String template, Map<String, Object> values) throws IOException, TemplateException {
    String retValue = template;
    if (template != null && template.contains("$")) {
      Configuration cfg = Optional.of(new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS))
          .stream()
          .peek(config -> config.setDefaultEncoding("UTF-8"))
          .peek(config -> config.setTemplateLoader(new TempletLoader(template)))
          .findAny()
          .get();

      StringWriter writer = new StringWriter();
      cfg.getTemplate("").process(values, writer);
      retValue = writer.toString();
    }
    return retValue;
  }

}
