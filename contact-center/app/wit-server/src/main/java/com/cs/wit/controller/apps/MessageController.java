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

package com.cs.wit.controller.apps;

import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.User;
import com.cs.wit.util.Menu;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController extends Handler{

    @NonNull
    private final Cache cache;
	
    @RequestMapping("/ping")
    @Menu(type = "message" , subtype = "ping" , admin= true)
    public ModelAndView ping(ModelMap map , HttpServletRequest request) {

        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();

        // 每次ping,确认存活
        cache.putConnectAlive(orgi, logined.getId());

        return request(super.createRequestPageTempletResponse("/apps/message/ping"));
    }
}