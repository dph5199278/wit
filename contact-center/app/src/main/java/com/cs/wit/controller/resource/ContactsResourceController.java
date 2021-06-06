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

import com.cs.wit.controller.Handler;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.util.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ContactsResourceController extends Handler {

    @NonNull
    private final ContactsRepository contactsRes;

    @RequestMapping("/res/contacts")
    @Menu(type = "res", subtype = "contacts")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String q) {
        if (q == null) {
            q = "";
        }
        map.addAttribute("contactsList", contactsRes.findByCreaterAndSharesAndOrgi(super.getUser(request).getId(), super.getUser(request).getId(), super.getOrgi(request), false, q, PageRequest.of(0, 10)));
        return request(super.createRequestPageTempletResponse("/public/contacts"));
    }
}
