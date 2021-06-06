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
import com.cs.wit.model.Organ;
import com.cs.wit.model.OrgiSkillRel;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.OrgiSkillRelRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.util.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/res")
@RequiredArgsConstructor
public class UsersResourceController extends Handler {
    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final OrgiSkillRelRepository orgiSkillRelService;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final UserProxy userProxy;

    @RequestMapping("/users")
    @Menu(type = "res", subtype = "users")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String q) {
        if (q == null) {
            q = "";
        }
        map.addAttribute("usersList", getUsers(request, q));
        return request(super.createRequestPageTempletResponse("/public/users"));
    }

    @RequestMapping("/bpm/users")
    @Menu(type = "res", subtype = "users")
    public ModelAndView bpmusers(ModelMap map, HttpServletRequest request, @Valid String q) {
        if (q == null) {
            q = "";
        }
        map.addAttribute("usersList", getUsers(request, q));
        return request(super.createRequestPageTempletResponse("/public/bpmusers"));
    }

    @RequestMapping("/bpm/organ")
    @Menu(type = "res", subtype = "users")
    public ModelAndView organ(ModelMap map, HttpServletRequest request, @Valid String ids) {
        map.addAttribute("organList", getOrgans(request));
        map.addAttribute("usersList", getUsers(request));
        map.addAttribute("ids", ids);
        return request(super.createRequestPageTempletResponse("/public/organ"));
    }

    private List<User> getUsers(HttpServletRequest request) {
        List<User> list;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            list = userProxy.findByOrganInAndDatastatus(organIdList, false);
        } else {
            list = userRes.findByOrgiAndDatastatus(super.getOrgi(request), false);
        }
        return list;
    }

    /**
     * 获取当前产品下人员信息
     */
    private Page<User> getUsers(HttpServletRequest request, String q) {
        if (q == null) {
            q = "";
        }
        Page<User> list;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            list = userProxy.findByOrganInAndDatastatusAndUsernameLike(organIdList, false, "%" + q + "%", PageRequest.of(0, 10));
        } else {
            list = userRes.findByDatastatusAndOrgiAndOrgidAndUsernameLike(false, super.getOrgi(request), super.getOrgid(request), "%" + q + "%", PageRequest.of(0, 10));
        }
        return list;
    }

    /**
     * 获取当前产品下 技能组 组织信息
     */
    private List<Organ> getOrgans(HttpServletRequest request) {
        List<Organ> list;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            list = organRes.findByIdInAndSkill(organIdList, true);
        } else {
            list = organRes.findByOrgiAndSkillAndOrgid(super.getOrgi(request), true, super.getOrgid(request));
        }
        return list;
    }
}
