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
package com.cs.wit.controller.api.auth;

import com.cs.wit.basic.MainUtils;
import com.cs.wit.basic.auth.AuthToken;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.User;
import com.cs.wit.model.UserRole;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.persistence.repository.UserRoleRepository;
import com.cs.wit.util.Md5Utils;
import com.cs.wit.util.Menu;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账号密码登录
 */
@Slf4j
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class ApiLoginController extends Handler {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final UserRoleRepository userRoleRes;

    @NonNull
    private final AuthToken authToken;

    /**
     * 登录服务，传入登录账号和密码
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "token", access = true)
    public ResponseEntity login(HttpServletResponse response, @Valid String username, @Valid String password) {
        User loginUser = userRepository.findByUsernameAndPassword(username, Md5Utils.doubleMd5(password));
        ResponseEntity entity;
        if (loginUser != null && !StringUtils.isBlank(loginUser.getId())) {
            loginUser.setLogin(true);
            List<UserRole> userRoleList = userRoleRes.findByOrgiAndUser(loginUser.getOrgi(), loginUser);
            if (userRoleList != null && userRoleList.size() > 0) {
                for (UserRole userRole : userRoleList) {
                    loginUser.getRoleList().add(userRole.getRole());
                }
            }
            loginUser.setLastlogintime(new Date());
            if (!StringUtils.isBlank(loginUser.getId())) {
                userRepository.save(loginUser);
            }
            String auth = MainUtils.getUUID();
            authToken.putUserByAuth(auth, loginUser);

            entity = new ResponseEntity<>(auth, HttpStatus.OK);
            response.addCookie(new Cookie("authorization", auth));
        } else {
            entity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return entity;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "token", access = true)
    public ResponseEntity error(HttpServletRequest request) {
        User data = super.getUser(request);
        return new ResponseEntity<>(data, data != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity logout(@RequestHeader(value = "authorization") String authorization) {
        authToken.deleteUserByAuth(authorization);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
