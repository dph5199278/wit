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
package com.cs.wit.controller.resource;

import com.cs.wit.basic.Constants;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.util.CallCenterUtils;
import com.cs.wit.util.Menu;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class CallAgentResourceController extends Handler {

    @NonNull
    private final UserRepository userRes;

    @RequestMapping("/res/agent")
    @Menu(type = "res", subtype = "agent")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String q) {
        if (q == null) {
            q = "";
        }
        final String search = q;
        final String orgi = super.getOrgi(request);
        final List<String> organList = CallCenterUtils.getExistOrgan(super.getUser(request));
        map.put("owneruserList", userRes.findAll((Specification<User>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            In<Object> in = cb.in(root.get("organ"));
            list.add(cb.equal(root.get("orgi").as(String.class), orgi));
            list.add(cb.or(cb.like(root.get("username").as(String.class), "%" + search + "%"), cb.like(root.get("uname").as(String.class), "%" + search + "%")));

            if (organList.size() > 0) {
                for (String id : organList) {
                    in.value(id);
                }
            } else {
                in.value(Constants.CSKEFU_SYSTEM_NO_DAT);
            }
            list.add(in);

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }));
        return request(super.createRequestPageTempletResponse("/public/agent"));
    }
}
