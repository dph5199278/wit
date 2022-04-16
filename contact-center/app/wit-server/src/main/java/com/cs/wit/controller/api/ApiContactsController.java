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

import com.cs.wit.basic.MainContext;
import com.cs.wit.controller.Handler;
import com.cs.wit.controller.api.request.RestUtils;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.User;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.proxy.AgentUserProxy;
import com.cs.wit.proxy.ContactsProxy;
import com.cs.wit.util.Menu;
import com.cs.wit.util.RestResult;
import com.cs.wit.util.RestResultType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联系人服务
 * 联系人管理功能
 */
@Slf4j
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ApiContactsController extends Handler {

    @NonNull
    private final ContactsRepository contactsRepository;

    @NonNull
    private final ContactsProxy contactsProxy;

    @NonNull
    private final AgentUserProxy agentUserProxy;

    /**
     * 返回用户列表，支持分页，分页参数为 p=1&ps=50，默认分页尺寸为 20条每页
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String creater, @Valid String q) {
        Page<Contacts> contactsList;
        if (!StringUtils.isBlank(creater)) {
            User user = super.getUser(request);
            contactsList = contactsRepository.findByCreaterAndSharesAndOrgi(user.getId(), user.getId(),
                    super.getOrgi(request), false, q,
                    PageRequest.of(
                            super.getP(request),
                            super.getPs(request)));
        } else {
            contactsList = contactsRepository.findByOrgi(super.getOrgi(request), false, q,
                    PageRequest.of(super.getP(request), super.getPs(request)));
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, contactsList), HttpStatus.OK);
    }

    /**
     * 新增或修改用户用户 ，在修改用户信息的时候，如果用户 密码未改变，请设置为 NULL
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid Contacts contacts) {
        if (contacts != null && !StringUtils.isBlank(contacts.getName())) {

            contacts.setOrgi(super.getOrgi(request));
            contacts.setCreater(super.getUser(request).getId());
            contacts.setUsername(super.getUser(request).getUsername());
            contacts.setCreatetime(new Date());
            contacts.setUpdatetime(new Date());

            contactsRepository.save(contacts);
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }

    /**
     * 删除用户，只提供 按照用户ID删除 ， 并且，不能删除系统管理员
     * 删除联系人，联系人删除是逻辑删除，将 datastatus字段标记为 true，即已删除
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> delete(@Valid String id) {
        RestResult result = new RestResult(RestResultType.OK);
        if (!StringUtils.isBlank(id)) {
            contactsRepository.findById(id).ifPresent(contacts -> {
                //系统管理员， 不允许 使用 接口删除
                contacts.setDatastatus(true);
                contactsRepository.save(contacts);
            });
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 联系人页面，客户点击页面时，判断是否有能触达的通道
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<String> operations(
            final HttpServletRequest request,
            @RequestBody final String body) {
        final JsonObject j = JsonParser.parseString(body).getAsJsonObject();
        log.info("[chatbot] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        final User logined = super.getUser(request);

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "approach":
                    // 查找立即触达的渠道
                    json = approach(j, logined);
                    break;
                case "proactive":
                    // 与联系开始聊天
                    json = proactive(j, logined);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * 主动与联系人聊天
     */
    private JsonObject proactive(final JsonObject payload, User logined) {
        JsonObject resp = new JsonObject();

        final String channels = payload.has("channels") ? payload.get("channels").getAsString() : null;
        final String contactid = payload.has("contactid") ? payload.get("contactid").getAsString() : null;

        if (StringUtils.isBlank(channels) || StringUtils.isBlank(contactid)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
            return resp;
        }

        try {
            AgentUser agentUser = agentUserProxy.figureAgentUserBeforeChatWithContactInfo(channels, contactid, logined);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            JsonObject data = new JsonObject();
            data.addProperty("agentuserid", agentUser.getId());
            resp.add(RestUtils.RESP_KEY_DATA, data);
        } catch (CSKefuException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Can not create agent user.");
            return resp;
        }

        return resp;
    }

    /**
     * 根据联系人信息查找立即触达的渠道
     */
    private JsonObject approach(final JsonObject payload, final User logined) {
        JsonObject resp = new JsonObject();

        if (!payload.has("contactsid")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid params.");
            return resp;
        }

        final String contactsid = payload.get("contactsid").getAsString();
        Optional<Contacts> contactOpt = contactsRepository.findOneById(contactsid).filter(
                p -> !p.isDatastatus());

        if (contactOpt.isPresent()) {
            List<MainContext.ChannelType> channles;
            try {
                channles = contactsProxy.liveApproachChannelsByContactid(
                        logined, contactsid, contactsProxy.isSkypeSetup(logined.getOrgi()));
                if (channles.size() > 0) {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                    JsonArray data = new JsonArray();
                    for (final MainContext.ChannelType e : channles) {
                        data.add(e.toString());
                    }
                    resp.add(RestUtils.RESP_KEY_DATA, data);
                } else {
                    resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    resp.addProperty(RestUtils.RESP_KEY_ERROR, "No available channel to approach contact.");
                }
            } catch (CSKefuException e) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "Contact not found.");
            }
        } else {
            // can not find contact, may is deleted.
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Not found contact.");
        }

        return resp;
    }
}
