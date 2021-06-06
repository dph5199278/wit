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

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.PbxHost;
import com.cs.wit.persistence.interfaces.CallCenterInterface;
import com.cs.wit.persistence.repository.ExtentionRepository;
import com.cs.wit.persistence.repository.PbxHostRepository;
import com.cs.wit.persistence.repository.ServiceAiRepository;
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
import java.util.List;

@Controller
@RequestMapping("/admin/callcenter")
@RequiredArgsConstructor
public class CallCenterResourceController extends Handler {

    @NonNull
    private final PbxHostRepository pbxHostRes;

    @NonNull
    private final ExtentionRepository extentionRes;

    @NonNull
    private final ServiceAiRepository serviceAiRes;

    @RequestMapping(value = "/resource")
    @Menu(type = "callcenter", subtype = "callcenter", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        map.addAttribute("pbxHostList", pbxHostList);

        map.put("serviceAiList", serviceAiRes.findByOrgi(super.getOrgi(request)));

        PbxHost pbxHost;
        if (pbxHostList.size() > 0) {
            map.addAttribute("pbxHost", pbxHost = getPbxHost(pbxHostList, hostid));
            map.addAttribute("extentionList",
                    extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        return request(super.createAdminTempletResponse("/admin/callcenter/resource/index"));
    }

    @RequestMapping(value = "/resource/config")
    @Menu(type = "callcenter", subtype = "callcenter", admin = true)
    public ModelAndView config(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        map.addAttribute("pbxHostList", pbxHostList);
        PbxHost pbxHost;
        if (pbxHostList.size() > 0) {
            map.addAttribute("pbxHost", pbxHost = getPbxHost(pbxHostList, hostid));
            map.addAttribute("extentionList",
                    extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/admin/callcenter/resource/config"));
    }

    @RequestMapping(value = "/resource/save")
    @Menu(type = "callcenter", subtype = "callcenter", admin = true)
    public ModelAndView save(HttpServletRequest request, @Valid PbxHost pbxHost) throws Exception {
        PbxHost tempPbxHost = pbxHostRes.findByIdAndOrgi(pbxHost.getId(), super.getOrgi(request));
        if (tempPbxHost != null) {
            pbxHost.setCreater(tempPbxHost.getCreater());
            pbxHost.setCreatetime(tempPbxHost.getCreatetime());
            if (StringUtils.isBlank(pbxHost.getPassword())) {
                pbxHost.setPassword(tempPbxHost.getPassword());
            }
            pbxHost.setOrgi(super.getOrgi(request));
            pbxHostRes.save(pbxHost);

            if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
                CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean(
                        "callcenter");
                callCenterImpl.init(pbxHost);
            }
        }
        return request(super.createRequestPageTempletResponse(
                "redirect:/admin/callcenter/resource.html?hostid=" + pbxHost.getId()));
    }

    @RequestMapping(value = "/resource/pbxhost")
    @Menu(type = "callcenter", subtype = "callcenter", admin = true)
    public ModelAndView resourcepbx(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        map.addAttribute("pbxHostList", pbxHostList);
        PbxHost pbxHost;
        if (pbxHostList.size() > 0) {
            map.addAttribute("pbxHost", pbxHost = getPbxHost(pbxHostList, hostid));
            map.addAttribute("extentionList",
                    extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        return request(super.createAdminTempletResponse("/admin/callcenter/resource/pbxhost"));
    }

    private PbxHost getPbxHost(List<PbxHost> pbxHostList, String hostid) {
        PbxHost pbxHost = pbxHostList.get(0);
        if (!StringUtils.isBlank(hostid)) {
            for (PbxHost pbx : pbxHostList) {
                if (pbx.getId().equals(hostid)) {
                    pbxHost = pbx;
                    break;
                }
            }
        }
        return pbxHost;
    }

}
