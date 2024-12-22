/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
import com.cs.wit.controller.api.request.RestUtils;
import com.cs.wit.model.Tag;
import com.cs.wit.persistence.repository.TagRepository;
import com.cs.wit.util.Menu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签
 * 管理标签
 */
@RestController
@RequestMapping("/api/repo/tags")
@RequiredArgsConstructor
public class ApiTagsController extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(ApiTagsController.class);

    @NonNull
    private final TagRepository tagRes;

    /**
     * 获取标签
     */
    private JsonObject fetch(final JsonObject j, final HttpServletRequest request) {
        JsonObject resp = new JsonObject();
        String tagType = null;

        if (j.has("tagtype"))
            tagType = j.get("tagtype").getAsString();

        Page<Tag> records = tagRes.findByOrgiAndTagtype(j.get("orgi").getAsString(), tagType,
                PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));

        JsonArray ja = new JsonArray();

        for (Tag t : records) {
            JsonObject o = new JsonObject();
            o.addProperty("id", t.getId());
            o.addProperty("name", t.getTag());
            o.addProperty("type", t.getTagtype());
            o.addProperty("icon", t.getIcon());
            o.addProperty("color", t.getColor());
            ja.add(o);
        }

        resp.add("data", ja);
        // 每页条数
        resp.addProperty("size", records.getSize());
        // 当前页
        resp.addProperty("number", records.getNumber());
        // 所有页
        resp.addProperty("totalPage", records.getTotalPages());
        // 所有检索结果数量
        resp.addProperty("totalElements", records.getTotalElements());
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);

        return resp;
    }


    /**
     * 联系人标签
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "tags", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) {
        final JsonObject j = JsonParser.parseString(body).getAsJsonObject();
        logger.info("[contact tags] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        j.addProperty("creater", super.getUser(request).getId());
        j.addProperty("orgi", super.getUser(request).getOrgi());

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            if ("fetch".equals(StringUtils.lowerCase(j.get("ops").getAsString()))) {
                json = fetch(j, request);
            } else {
                json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                json.addProperty(RestUtils.RESP_KEY_ERROR, "不支持的操作。");
            }
        }
        return new ResponseEntity<>(json.toString(), headers, HttpStatus.OK);
    }

}
