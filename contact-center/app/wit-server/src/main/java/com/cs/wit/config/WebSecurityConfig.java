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

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * @author Dely
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        Filter tokenInfoTokenFilterSecurityInterceptor,
        Filter apiTokenFilterSecurityInterceptor,
        HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        // https://github.com/spring-projects/spring-security/issues/13568
        http.addFilterAfter(tokenInfoTokenFilterSecurityInterceptor, BasicAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(mvcMatcherBuilder.pattern("/**"))
                .permitAll()
            )
            .csrf()
            .csrfTokenRequestHandler(new CryptoCsrfTokenRequestAttributeHandler())
            .and()
            .headers().frameOptions().disable().and()
            .addFilterAfter(apiTokenFilterSecurityInterceptor, BasicAuthenticationFilter.class)
            ;
        return http.build();
    }

    @Bean("tokenInfoTokenFilterSecurityInterceptor")
    public Filter tokenInfoTokenFilterSecurityInterceptor() {
        RequestMatcher autoConfig = new AntPathRequestMatcher("/autoconfig/**");
        RequestMatcher configprops = new AntPathRequestMatcher("/configprops/**");
        RequestMatcher beans = new AntPathRequestMatcher("/beans/**");
        RequestMatcher dump = new AntPathRequestMatcher("/dump/**");
        RequestMatcher env = new AntPathRequestMatcher("/env/**");
        RequestMatcher info = new AntPathRequestMatcher("/info/**");
        RequestMatcher mappings = new AntPathRequestMatcher("/mappings/**");
        RequestMatcher trace = new AntPathRequestMatcher("/trace/**");
        RequestMatcher druid = new AntPathRequestMatcher("/druid/**");

        /**
         * Bypass actuator api
         */
//        RequestMatcher health = new AntPathRequestMatcher("/health/**");
//        RequestMatcher metrics = new AntPathRequestMatcher("/metrics/**");
//        return new DelegateRequestMatchingFilter(autoConfig , configprops , beans , dump , env , health , info , mappings , metrics , trace, druid);
        return new DelegateRequestMatchingFilter(autoConfig , configprops , beans , dump , env , mappings , trace, druid);
    }
    
    @Bean("apiTokenFilterSecurityInterceptor")
    public Filter apiTokenFilterSecurityInterceptor() {
        return new ApiRequestMatchingFilter(new AntPathRequestMatcher("/api/**"));
    }
}
