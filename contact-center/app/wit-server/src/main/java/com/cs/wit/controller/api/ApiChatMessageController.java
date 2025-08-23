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
package com.cs.wit.controller.api;

import com.cs.wit.controller.Handler;
import com.cs.wit.persistence.repository.ChatMessageRepository;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.util.Menu;
import com.cs.wit.util.RestResult;
import com.cs.wit.util.RestResultType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取对话内容
 * 获取访客对话的内容
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatmessage")
public class ApiChatMessageController extends Handler {

    @NonNull
    private final ChatMessageRepository chatMessageRes;

    /**
     * 获取访客对话内容
     *
     * @param serviceid AgentServiceID
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "agentuser", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String serviceid) {
        ResponseEntity<RestResult> result;
        if (!StringUtils.isBlank(serviceid)) {
            PageRequest pageRequest = PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime");
            Page<ChatMessage> page = chatMessageRes.findByAgentserviceidAndOrgi(serviceid, super.getUser(request).getOrgi(), pageRequest);
            result = new ResponseEntity<>(new RestResult(RestResultType.OK, page), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(new RestResult(RestResultType.LACKDATA, RestResultType.LACKDATA.getMessage()), HttpStatus.OK);
        }
        return result;
    }
}
