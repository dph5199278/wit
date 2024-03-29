/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

import com.cs.wit.basic.auth.AuthRedisTemplate;
import com.cs.wit.cache.RedisObjectMapper;
import com.cs.wit.cache.RedisSsoKey;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FlushMode;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * maxInactiveIntervalInSeconds: 设置 Session 失效时间，
 * 使用 Redis Session 之后，原 Spring Boot 的 server.session.timeout 属性不再生效。
 * http://www.ityouknow.com/springboot/2016/03/06/spring-boot-redis.html
 * 86400 代表一天
 * maxInactiveIntervalInSeconds = 86400 * 30
 */

@Configuration
@EnableRedisHttpSession
public class WebServerSessionConfigure {

    private final RedisObjectMapper mapper = new RedisObjectMapper();

    @Primary
    @Bean
    public RedisIndexedSessionRepository redisIndexedSessionRepository(@NonNull RedisTemplate<String, Object> sessionRedisTemplate,
        @NonNull ApplicationEventPublisher applicationEventPublisher, @NonNull ObjectProvider<SessionRepositoryCustomizer<RedisIndexedSessionRepository>> sessionRepositoryCustomizers,
        @NonNull RedisProperties redisProperties, @NonNull SessionProperties sessionProperties) {
        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(sessionRedisTemplate);
        repository.setDefaultMaxInactiveInterval(sessionProperties.getTimeout());
        repository.setFlushMode(FlushMode.IMMEDIATE);
        repository.setRedisKeyNamespace(RedisSsoKey.CACHE_SESSIONS);
        //  应用事件分发，设置后才能使session监听生效
        repository.setApplicationEventPublisher(applicationEventPublisher);
        repository.setDatabase(redisProperties.getDatabase());
        sessionRepositoryCustomizers.orderedStream()
                .forEach((sessionRepositoryCustomizer) -> sessionRepositoryCustomizer.customize(repository));
        return repository;
    }

    @Bean
    public RedisTemplate<String, Object> sessionRedisTemplate(
        @NonNull final RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setDefaultSerializer(RedisSerializer.string());
        template.setEnableDefaultSerializer(true);

        final RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(mapper);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 存储AuthToken
     */
    @Bean
    public AuthRedisTemplate authRedisTemplate(
        @NonNull final RedisConnectionFactory factory) {
        return new AuthRedisTemplate(factory, mapper);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
