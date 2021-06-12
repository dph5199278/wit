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
package com.cs.wit.controller.admin.callcenter;

import com.cs.wit.basic.MainContext;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.persistence.repository.BlackListRepository;
import com.cs.wit.util.Menu;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/callcenter")
@RequiredArgsConstructor
public class CallCenterBlackController extends Handler {

    @NonNull
    private final BlackListRepository blackRes;

    @RequestMapping(value = "/black")
    @Menu(type = "callcenter", subtype = "callcenterblack", admin = true)
    public ModelAndView black(ModelMap map, HttpServletRequest request) {
        map.addAttribute("blackList", blackRes.findByOrgi(super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/black/index"));
    }

    @RequestMapping(value = "/black/add")
    @Menu(type = "callcenter", subtype = "black", admin = true)
    public ModelAndView blackadd() {
        return request(super.createRequestPageTempletResponse("/admin/callcenter/black/add"));
    }

    @RequestMapping(value = "/black/save")
    @Menu(type = "callcenter", subtype = "black", admin = true)
    public ModelAndView blacksave(HttpServletRequest request, @Valid String phones) {
        if (!StringUtils.isBlank(phones)) {
            String[] ps = phones.split("[ ,，\t\n]");
            for (String ph : ps) {
                if (ph.length() >= 3) {
                    int count = blackRes.countByPhoneAndOrgi(ph.trim(), super.getOrgi(request));
                    if (count == 0) {
                        BlackEntity be = new BlackEntity();
                        be.setPhone(ph.trim());
                        be.setChannel(MainContext.ChannelType.PHONE.toString());
                        be.setOrgi(super.getOrgi(request));
                        be.setCreater(super.getUser(request).getId());
                        blackRes.save(be);
                    }
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/black.html"));
    }

    @RequestMapping(value = "/black/edit")
    @Menu(type = "callcenter", subtype = "black", admin = true)
    public ModelAndView blackedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("black", blackRes.findByIdAndOrgi(id, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/black/edit"));
    }

    @RequestMapping(value = "/black/update")
    @Menu(type = "callcenter", subtype = "black", admin = true)
    public ModelAndView pbxhostupdate(HttpServletRequest request, @Valid BlackEntity black) {
        if (!StringUtils.isBlank(black.getId())) {
            BlackEntity oldBlack = blackRes.findByIdAndOrgi(black.getId(), super.getOrgi(request));
            if (oldBlack != null) {
                oldBlack.setPhone(black.getPhone());
                oldBlack.setChannel(MainContext.ChannelType.PHONE.toString());
                oldBlack.setOrgi(super.getOrgi(request));
                blackRes.save(oldBlack);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/black.html"));
    }

    @RequestMapping(value = "/black/delete")
    @Menu(type = "callcenter", subtype = "black", admin = true)
    public ModelAndView blackdelete(@Valid String id) {
        if (!StringUtils.isBlank(id)) {
            blackRes.deleteById(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/black.html"));
    }
}
