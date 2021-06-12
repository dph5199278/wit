/*
 * Copyright (C) 2018-2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */
package com.cs.wit.controller.api;

import com.cs.wit.basic.MainContext;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.controller.api.request.RestUtils;
import com.cs.wit.model.InviteRecord;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.persistence.repository.InviteRecordRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.util.Menu;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/apps")
public class ApiAppsController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiAppsController.class);

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private InviteRecordRepository inviteRecordRes;

    @Autowired
    private Cache cache;

    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "apps", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body, @Valid String q) {
        logger.info("[operations] body {}, q {}", body, q);
        final JsonObject j = StringUtils.isBlank(body) ? (new JsonObject()) : (new JsonParser()).parse(
                body).getAsJsonObject();

        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "invite":
                    json = invite(request, j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的操作。");
            }
        }

        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);

    }

    /**
     * 邀请访客加入会话
     *
     * @param request
     * @param j
     * @return
     */
    private JsonObject invite(final HttpServletRequest request, final JsonObject j) {
        JsonObject resp = new JsonObject();
        final String orgi = super.getOrgi(request);
        final String agentno = super.getUser(request).getId();

        final String userid = j.get("userid").getAsString();

        logger.info("[invite] agentno {} invite onlineUser {}", agentno, userid);
        OnlineUser onlineUser = OnlineUserProxy.onlineuser(userid, orgi);

        if (onlineUser != null) {
            logger.info("[invite] userid {}, agentno {}, orgi {}", userid, agentno, orgi);
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.INVITE.toString());
            onlineUser.setInvitetimes(onlineUser.getInvitetimes() + 1);
            onlineUserRes.save(onlineUser);

            InviteRecord record = new InviteRecord();
            record.setAgentno(super.getUser(request).getId());
            // 对于OnlineUser, 其userId与id是相同的
            record.setUserid(onlineUser.getUserid());
            record.setAppid(onlineUser.getAppid());
            record.setOrgi(super.getOrgi(request));
            inviteRecordRes.save(record);
            logger.info("[invite] new invite record {} of onlineUser id {} saved.", record.getId(), onlineUser.getId());

            try {
                OnlineUserProxy.sendWebIMClients(onlineUser.getUserid(), "invite:" + agentno);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            } catch (Exception e) {
                logger.error("[invite] error", e);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "online user is offline.");
            }
        } else {
            // 找不到的情况不可能发生，因为坐席看到的Onlineuser信息是从数据库查找到的
            logger.info("[invite] can not find onlineUser {} in database.", userid);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "online user is invalid, not found in db or cache.");
        }

        return resp;
    }
}
