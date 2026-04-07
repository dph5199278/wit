
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
package com.cs.wit.util.ip;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class IPTest {

    /**
     * IPV4 test
     */
    @Test
    public void ipv4() {
        IP ip = ip("157.0.45.35");
        System.out.println("IPV4: ");
        System.out.println(ip.getCountry());
        System.out.println(ip.getProvince());
        System.out.println(ip.getCity());
        System.out.println(ip.getIsp());
        System.out.println(ip.getRegion());
    }

    /**
     * IPV6 test
     */
    @Test
    public void ipv6() {
        IP ip = ip("2408:843c:4600::1");
        System.out.println("IPV6: ");
        System.out.println(ip.getCountry());
        System.out.println(ip.getProvince());
        System.out.println(ip.getCity());
        System.out.println(ip.getIsp());
        System.out.println(ip.getRegion());
    }

    private IP ip(String ip) {
        IPTools tools = new IPTools(new AnnotationConfigApplicationContext());
        tools.setup();
        return tools.findGeography(ip);
    }
}
