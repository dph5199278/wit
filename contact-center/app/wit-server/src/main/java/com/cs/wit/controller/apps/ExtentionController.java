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

import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.Extention;
import com.cs.wit.model.PbxHost;
import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.Template;
import com.cs.wit.persistence.repository.AclRepository;
import com.cs.wit.persistence.repository.CallCenterSkillRepository;
import com.cs.wit.persistence.repository.ExtentionRepository;
import com.cs.wit.persistence.repository.PbxHostRepository;
import com.cs.wit.persistence.repository.RouterRulesRepository;
import com.cs.wit.persistence.repository.SipTrunkRepository;
import com.cs.wit.persistence.repository.SkillExtentionRepository;
import com.cs.wit.util.Menu;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/apps/callcenter")
@RequiredArgsConstructor
public class ExtentionController extends Handler {

    @NonNull
    private final PbxHostRepository pbxHostRes;

    @NonNull
    private final ExtentionRepository extentionRes;

    @NonNull
    private final AclRepository aclRes;

    @NonNull
    private final RouterRulesRepository routerRes;

    @NonNull
    private final SkillExtentionRepository skillExtentionRes;

    @NonNull
    private final CallCenterSkillRepository skillRes;

    @NonNull
    private final SipTrunkRepository sipTrunkRes;

    @RequestMapping(value = "/extention")
    @Menu(type = "callcenter", subtype = "extention", access = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String hostname) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/index"));
        List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname);
        PbxHost pbxHost;
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (pbxHostList != null && pbxHostList.size() > 0) {
            pbxHost = pbxHostList.get(0);
            map.addAttribute("pbxHost", pbxHost);
            map.addAttribute("skillGroups", skillRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
            map.addAttribute("extentionList", extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        if (systemConfig != null && systemConfig.isCallcenter()) {
            if (!StringUtils.isBlank(systemConfig.getCc_extention())) {
                Template template = MainUtils.getTemplate(systemConfig.getCc_extention());
                if (template != null) {
                    map.addAttribute("template", template);
                    view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template"));
                }
            }
        }
        return view;
    }

    @RequestMapping(value = "/configuration")
    @Menu(type = "callcenter", subtype = "configuration", access = true)
    public ModelAndView configuration(ModelMap map, HttpServletRequest request, @Valid String hostname, @Valid String key_value) {

        List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname);
        PbxHost pbxHost;
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (pbxHostList != null && pbxHostList.size() > 0) {
            pbxHost = pbxHostList.get(0);
            map.addAttribute("pbxHost", pbxHost);
            map.addAttribute("skillGroups", skillRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
            map.addAttribute("skillExtentionList", skillExtentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
            map.addAttribute("extentionList", extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
            map.addAttribute("aclList", aclRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
            map.addAttribute("sipTrunkList", sipTrunkRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        Template template = null;
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/notfound"));
        if (key_value != null && key_value.equals("callcenter.conf")) {
            view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/callcenter"));
            if (systemConfig != null && systemConfig.isCallcenter()) {
                if (!StringUtils.isBlank(systemConfig.getCc_quene())) {
                    template = MainUtils.getTemplate(systemConfig.getCc_quene());
                }
            }
        } else if (key_value != null && key_value.equals("acl.conf")) {
            view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/acl"));
            if (systemConfig != null && systemConfig.isCallcenter()) {
                if (!StringUtils.isBlank(systemConfig.getCc_acl())) {
                    template = MainUtils.getTemplate(systemConfig.getCc_acl());
                }
            }
        } else if (key_value != null && key_value.equals("ivr.conf")) {
            view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/configure/ivr"));
            if (systemConfig != null && systemConfig.isCallcenter()) {
                if (!StringUtils.isBlank(systemConfig.getCc_ivr())) {
                    template = MainUtils.getTemplate(systemConfig.getCc_ivr());
                }
            }
        }
        if (template != null) {
            map.addAttribute("template", template);
            view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template"));
        }
        return view;
    }

    @RequestMapping(value = "/dialplan")
    @Menu(type = "callcenter", subtype = "dialplan", access = true)
    public ModelAndView dialplan(ModelMap map, HttpServletRequest request, @Valid String hostname) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/dialplan/index"));
        List<PbxHost> pbxHostList = pbxHostRes.findByHostnameOrIpaddr(hostname, hostname);
        PbxHost pbxHost;
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        Template template = null;
        if (pbxHostList != null && pbxHostList.size() > 0) {
            pbxHost = pbxHostList.get(0);
            map.addAttribute("pbxHost", pbxHost);
            map.addAttribute("routerList", routerRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        if (systemConfig != null && systemConfig.isCallcenter()) {
            if (!StringUtils.isBlank(systemConfig.getCc_siptrunk())) {
                template = MainUtils.getTemplate(systemConfig.getCc_router());
            }
            if (template != null) {
                map.addAttribute("template", template);
                view = request(super.createRequestPageTempletResponse("/apps/business/callcenter/template"));
            }
        }

        return view;
    }

    @RequestMapping(value = "/extention/detail")
    @Menu(type = "callcenter", subtype = "extention")
    public ModelAndView detail(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String extno) {
        List<Extention> extentionList = extentionRes.findByExtentionAndOrgi(extno, super.getOrgi(request));
        if (extentionList != null && extentionList.size() == 1) {
            Extention extention = extentionList.get(0);
            if (!StringUtils.isBlank(extention.getHostid())) {
                pbxHostRes.findById(extention.getHostid()).ifPresent(it -> map.addAttribute("pbxhost", it));
            }
            map.addAttribute("extention", extention);
        }
        response.setContentType("Content-type: text/json; charset=utf-8");
        return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/detail"));
    }

    @RequestMapping(value = "/ivr")
    @Menu(type = "callcenter", subtype = "ivr")
    public ModelAndView ivr(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        map.addAttribute("ivrList", extentionRes.findByHostidAndExtypeAndOrgi(hostid, MainContext.ExtentionType.BUSINESS.toString(), super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/ivr"));
    }

}
