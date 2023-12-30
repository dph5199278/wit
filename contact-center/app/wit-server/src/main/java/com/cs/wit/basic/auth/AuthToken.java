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

import com.cs.wit.cache.RedisKey;
import com.cs.wit.model.User;
import com.cs.wit.util.SerializeUtil;
import java.time.Duration;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * 认证和授权的API Token管理
 */
@Component
public class AuthToken {

    private final static Logger logger = LoggerFactory.getLogger(AuthToken.class);

    @Value("${spring.session.timeout}")
    private Duration timeout;

    @Autowired
    private AuthRedisTemplate authRedisTemplate;

    private ValueOperations<String, String> redisValOps;

    @PostConstruct
    private void init() {
        redisValOps = authRedisTemplate.opsForValue();
    }

    /**
     * 设置一个KEY的过期时间
     *
     * @param key
     * @param seconds
     */
    private boolean expire(final String key, final Duration seconds) {
        return Boolean.TRUE.equals(authRedisTemplate.expire(key, seconds));
    }


    private String authKey(final String auth) {
        return RedisKey.getLoginUserKey(auth);
    }

    /**********************************
     *  LOGIN USER API TOKEN 相关
     *  认证，授权，登录用户
     **********************************/

    /**
     * @param auth      授权的KEY
     * @param loginUser 已经登录的用户
     */
    public void putUserByAuth(final String auth, final User loginUser) {
        if (StringUtils.isNotBlank(auth) && loginUser != null) {
            redisValOps.set(authKey(auth), SerializeUtil.serialize(loginUser), timeout);
        } else {
            logger.warn("[putLoginUserByAuth] error Invalid params.");
        }
    }


    /**
     * 判断一个Auth是否是有效的
     *
     * @param auth
     * @return
     */
    public boolean existUserByAuth(final String auth) {
        final String key = authKey(auth);
        return authRedisTemplate.hasKey(key) ? (expire(key, timeout) ? true : true) : false;
    }

    /**
     * 根据租户ID和认证Auth获得一个登录用户
     *
     * @param auth
     * @return
     */
    public User findUserByAuth(final String auth) {
        final String key = authKey(auth);
        String serialized = redisValOps.get(key);
        if (StringUtils.isNotBlank(serialized)) {
            expire(key, timeout);
            return SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    /**
     * 登出已经登录的系统用户
     *
     * @param auth
     */
    public void deleteUserByAuth(final String auth) {
        authRedisTemplate.delete(authKey(auth));
    }
}
