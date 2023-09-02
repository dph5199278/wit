/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

public class StringToDateConverter implements Converter<String, Date> {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_SLASH      = "yyyy/MM/dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT_SLASH = "yyyy/MM/dd";

    /** 
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public Date convert(@Nullable String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        source = source.trim();
        try {
            if (source.contains("-")) {
                return new SimpleDateFormat(source.contains(":")
                        ? DATE_FORMAT
                        : SHORT_DATE_FORMAT)
                        .parse(source);
            }
            else if(source.contains("/")) {
                return new SimpleDateFormat(source.contains(":")
                        ? DATE_FORMAT_SLASH
                        : SHORT_DATE_FORMAT_SLASH)
                        .parse(source);
            }
            else if (source.matches("^\\d+$")) {
                return new Date(Long.parseLong(source));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date callOutFail", source), e);
        }
        throw new RuntimeException(String.format("parser %s to Date callOutFail", source));
    }

}