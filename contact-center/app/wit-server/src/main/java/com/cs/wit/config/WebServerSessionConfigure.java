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
import com.cs.wit.cache.RedisKey;
import java.time.Duration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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

    /**
     * spring在多长时间后强制使redis中的session失效,默认是1800.(单位/秒)
     */
    @Value("${server.session-timeout}")
    private int maxInactiveIntervalInSeconds;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String pass;

    @Value("${spring.redis.session.db}")
    private int sessionDb;

    @Value("${spring.redis.token.db}")
    private int tokenDb;

    @Value("${spring.redis.timeout}")
    private Duration timeout;

    @Primary
    @Bean
    public RedisIndexedSessionRepository redisIndexedSessionRepository(@NonNull RedisTemplate<Object, Object> sessionRedisTemplate,
                                                                       @NonNull ApplicationEventPublisher applicationEventPublisher,
                                                                       @NonNull ObjectProvider<SessionRepositoryCustomizer<RedisIndexedSessionRepository>> sessionRepositoryCustomizers) {
        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(sessionRedisTemplate);
        repository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        repository.setFlushMode(FlushMode.IMMEDIATE);
        repository.setRedisKeyNamespace(RedisKey.CACHE_SESSIONS);
        //  应用事件分发，设置后才能使session监听生效
        repository.setApplicationEventPublisher(applicationEventPublisher);
        repository.setDatabase(sessionDb);
        sessionRepositoryCustomizers.orderedStream()
                .forEach((sessionRepositoryCustomizer) -> sessionRepositoryCustomizer.customize(repository));
        return repository;
    }

    @Bean
    public RedisTemplate<Object, Object> sessionRedisTemplate() {
        LettuceConnectionFactory factory = createJedisConnectionFactory(sessionDb);
        factory.afterPropertiesSet();

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(factory);
        return template;
    }


    /**
     * 存储AuthToken
     */
    @Bean
    public AuthRedisTemplate authRedisTemplate() {
        LettuceConnectionFactory factory = createJedisConnectionFactory(tokenDb);
        factory.afterPropertiesSet();

        AuthRedisTemplate template = new AuthRedisTemplate();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(factory);
        return template;
    }

    @NonNull
    private LettuceConnectionFactory createJedisConnectionFactory(int tokenDb) {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(host);
        standaloneConfiguration.setDatabase(tokenDb);
        standaloneConfiguration.setPort(port);
        if (StringUtils.isNotBlank(pass)) {
            standaloneConfiguration.setPassword(pass);
        }

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(timeout)
                .shutdownTimeout(timeout)
                .build();
        return new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
