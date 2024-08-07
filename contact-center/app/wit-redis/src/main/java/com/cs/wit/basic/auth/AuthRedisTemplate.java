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
package com.cs.wit.basic.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 存储Auth Token的Redis连接
 */
public class AuthRedisTemplate extends RedisTemplate<String, String> {
    public AuthRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        this.setEnableDefaultSerializer(true);
        this.setDefaultSerializer(RedisSerializer.string());

        final RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(mapper);
        this.setValueSerializer(serializer);
        this.setHashValueSerializer(serializer);

        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    @NotNull
    @Override
    protected RedisConnection preProcessConnection(@NotNull RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }
}
