/*
 * Copyright (C) 2024 Dely<https://github.com/dph5199278>, All rights reserved.
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author Dely
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // apiTokenFilterSecurityInterceptor
            .addFilterAfter(
                new ApiRequestMatchingFilter(new AntPathRequestMatcher("/api/**")),
                BasicAuthenticationFilter.class)
            // tokenInfoTokenFilterSecurityInterceptor
            .addFilterAfter(
                DelegateRequestMatchingFilter.builder()
                // autoConfig
                .addRequestMatcher(new AntPathRequestMatcher("/autoconfig/**"))
                // configprops
                .addRequestMatcher(new AntPathRequestMatcher("/configprops/**"))
                // beans
                .addRequestMatcher(new AntPathRequestMatcher("/beans/**"))
                // dump
                .addRequestMatcher(new AntPathRequestMatcher("/dump/**"))
                // env
                .addRequestMatcher(new AntPathRequestMatcher("/env/**"))
                // health(Bypass actuator api)
                //.addRequestMatcher(new AntPathRequestMatcher("/health/**"))
                // info
                //.addRequestMatcher(new AntPathRequestMatcher("/info/**"))
                // mappings
                .addRequestMatcher(new AntPathRequestMatcher("/mappings/**"))
                // metrics(Bypass actuator api)
                //.addRequestMatcher(new AntPathRequestMatcher("/metrics/**"))
                // trace
                .addRequestMatcher(new AntPathRequestMatcher("/trace/**"))
                // druid
                .addRequestMatcher(new AntPathRequestMatcher("/druid/**"))
                .build(),
                ApiRequestMatchingFilter.class)
            // https://github.com/spring-projects/spring-security/issues/13568
            .authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll())
            .csrf(csrf -> csrf.csrfTokenRequestHandler(new CryptoCsrfTokenRequestAttributeHandler()))
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
            .build()
            ;
    }

}
