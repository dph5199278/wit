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
package com.cs.wit.util.mobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MobileNumberTools {
    private final Map<String, MobileAddress> MOBILE_ADDRESS_MAP = new HashMap<>();

    @NonNull
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void setup() {
        try {
            final Resource resource = applicationContext.getResource(
                "classpath:/config/mobile.data");
            log.info("init with file [{}]", resource.getURL());
            if (resource.exists()) {
                try (final BufferedReader bf = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    String data;
                    while ((data = bf.readLine()) != null) {
                        String[] group = data.split("[\t ]");
                        MobileAddress address = null;
                        if (group.length == 5) {
                            address = new MobileAddress(group[0], group[1], group[2], group[3],
                                group[4]);
                        } else if (group.length == 4) {
                            address = new MobileAddress(group[0], group[1], group[2], group[2],
                                group[3]);
                        }
                        if (address != null) {
                            MOBILE_ADDRESS_MAP.putIfAbsent(address.getCode(), address);
                            MOBILE_ADDRESS_MAP.putIfAbsent(address.getAreacode(), address);
                        }
                    }
                    log.info("inited successfully, map size [{}]", MOBILE_ADDRESS_MAP.size());
                }
            }
        }
        catch (Exception e) {
            log.error("Application Startup Error", e);
            if(applicationContext instanceof AnnotationConfigServletWebServerApplicationContext) {
                ((AnnotationConfigServletWebServerApplicationContext)applicationContext).close();
            }
        }
    }

    /**
     * 根据呼入号码 找到对应 城市 , 需要传入的号码是 手机号 或者 固话号码，位数为 11位
     */
    public Optional<MobileAddress> getAddress(String phoneNumber) {
        if(null != phoneNumber
            && !"".equals(phoneNumber.trim())
            && phoneNumber.length() > 10) {
            if(phoneNumber.startsWith("0")) {
                return Optional.ofNullable(MOBILE_ADDRESS_MAP.get(phoneNumber.substring(0, 4)));
            }
            if (phoneNumber.startsWith("1")) {
                return Optional.ofNullable(MOBILE_ADDRESS_MAP.get(phoneNumber.substring(0, 7)));
            }
        }
        return Optional.empty();
    }
}
