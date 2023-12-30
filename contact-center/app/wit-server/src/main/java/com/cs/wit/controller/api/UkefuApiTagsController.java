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
import com.cs.wit.persistence.repository.TagRepository;
import com.cs.wit.util.Menu;
import com.cs.wit.util.RestResult;
import com.cs.wit.util.RestResultType;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签功能
 * 获取分类标签
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class UkefuApiTagsController extends Handler {

    @NonNull
    private final TagRepository tagRes;

    /**
     * 按照分类获取标签列表
     * 按照分类获取标签列表，Type 参数类型来自于 枚举，可选值目前有三个 ： user  workorders summary
     *
     * @param type 类型
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "tags", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String type) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, tagRes.findByOrgiAndTagtype(super.getOrgi(request), !StringUtils.isBlank(type) ? type : MainContext.ModelType.USER.toString())), HttpStatus.OK);
    }
}
