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
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.Organ;
import com.cs.wit.model.OrgiSkillRel;
import com.cs.wit.model.Tenant;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.OrganizationRepository;
import com.cs.wit.persistence.repository.OrgiSkillRelRepository;
import com.cs.wit.persistence.repository.TenantRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.util.Menu;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/apps/tenant")
@RequiredArgsConstructor
public class TenantController extends Handler {

    @NonNull
    private final TenantRepository tenantRes;

    @NonNull
    private final OrgiSkillRelRepository orgiSkillRelRes;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final OrganizationRepository organizationRes;

    @NonNull
    private final AgentUserRepository agentUserRepository;

    @NonNull
    private final Cache cache;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView index(
            ModelMap map, final HttpServletRequest request,
            final @Valid String msg,
            final @Valid String currentorgi,
            final @Valid String currentname) throws IOException {
        if (super.isEnabletneant()) {
            // 系统管理员开启多租户访问
            if (super.getUser(request).isAdmin()) {
                map.addAttribute("tenantList", tenantRes.findByOrgid(super.getOrgid(request)));
            } else {
                List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelRes.findBySkillidIn(
                        new ArrayList<>((super.getUser(request)).getAffiliates()));
                List<Tenant> tenantList;
                if (!orgiSkillRelList.isEmpty()) {
                    tenantList = new ArrayList<>();
                    for (OrgiSkillRel orgiSkillRel : orgiSkillRelList) {
                        tenantRes.findById(orgiSkillRel.getOrgi())
                                .ifPresent(tenantList::add);
                    }
                } else {
                    tenantList = null;
                }
                map.addAttribute("tenantList", tenantList);
            }
        } else {
            map.addAttribute("tenantList", tenantRes.findById(super.getOrgi(request)));
        }
        map.addAttribute("organization", organizationRes.findById(super.getUser(request).getOrgid()).orElse(null));
        map.addAttribute("msg", msg);
        map.addAttribute("currentorgi", currentorgi);
        if (currentname != null) {
            map.addAttribute("currentname", URLDecoder.decode(currentname, "UTF-8"));
        }
        return request(super.createRequestPageTempletResponse("/apps/tenant/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        if (super.isTenantshare()) {
            map.addAttribute("isShowSkillGroups", true);
            List<Organ> organList = organRes.findByOrgiAndOrgid(
                    super.getOrgiByTenantshare(request), super.getOrgid(request));
            map.addAttribute("skillGroups", organList);
        }
        return request(super.createRequestPageTempletResponse("/apps/tenant/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView save(HttpServletRequest request, @Valid Tenant tenant, @RequestParam(value = "tenantpic", required = false) MultipartFile tenantpic, @Valid String skills) throws IOException {
        Tenant tenanttemp = tenantRes.findByOrgidAndTenantname(super.getOrgid(request), tenant.getTenantname());
        if (tenanttemp != null) {
            return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=tenantexist"));
        }
        tenantRes.save(tenant);
        if (tenantpic != null && tenantpic.getOriginalFilename() != null && tenantpic.getOriginalFilename().lastIndexOf(".") > 0) {
            File logoDir = new File(path, "tenantpic");
            if (!logoDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                logoDir.mkdirs();
            }
            String fileName = "tenantpic/" + tenant.getId() + tenantpic.getOriginalFilename().substring(
                    tenantpic.getOriginalFilename().lastIndexOf("."));
            FileCopyUtils.copy(tenantpic.getBytes(), new File(path, fileName));
            tenant.setTenantlogo(fileName);
        }
        String tenantid = tenant.getId();
        List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelRes.findByOrgi(tenantid);
        orgiSkillRelRes.deleteAll(orgiSkillRelList);
        if (!StringUtils.isBlank(skills)) {
            String[] skillsarray = skills.split(",");
            for (String skill : skillsarray) {
                OrgiSkillRel rel = new OrgiSkillRel();
                rel.setOrgi(tenant.getId());
                rel.setSkillid(skill);
                rel.setCreater(super.getUser(request).getId());
                rel.setCreatetime(new Date());
                orgiSkillRelRes.save(rel);
            }
        }
        if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
            tenant.setOrgid(super.getUser(request).getOrgid());
        } else {
            tenant.setOrgid(MainContext.SYSTEM_ORGI);
        }
        tenantRes.save(tenant);
        OnlineUserProxy.clean(tenantid);
        return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }

    @RequestMapping("/edit")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (super.isTenantshare()) {
            map.addAttribute("isShowSkillGroups", true);
            List<Organ> organList = organRes.findByOrgiAndOrgid(
                    super.getOrgiByTenantshare(request), super.getOrgid(request));
            map.addAttribute("skillGroups", organList);
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelRes.findByOrgi(id);
            map.addAttribute("orgiSkillRelList", orgiSkillRelList);
        }
        map.addAttribute("tenant", tenantRes.findById(id));
        return request(super.createRequestPageTempletResponse("/apps/tenant/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "apps", subtype = "tenant", admin = true)
    public ModelAndView update(HttpServletRequest request, @NonNull @Valid Tenant tenant, @RequestParam(value = "tenantpic", required = false) MultipartFile tenantpic, @Valid String skills) throws IOException {
        Tenant temp = optionalById(tenant.getId());
        Tenant tenanttemp = tenantRes.findByOrgidAndTenantname(super.getOrgid(request), tenant.getTenantname());
        if (temp != null && tenanttemp != null && !temp.getId().equals(tenanttemp.getId())) {
            return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=tenantexist"));
        }
        if (temp != null) {
            tenant.setCreatetime(temp.getCreatetime());
        }
        if (tenantpic != null && tenantpic.getOriginalFilename() != null && tenantpic.getOriginalFilename().lastIndexOf(".") > 0) {
            File logoDir = new File(path, "tenantpic");
            if (!logoDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                logoDir.mkdirs();
            }
            String fileName = "tenantpic/" + tenant.getId() + tenantpic.getOriginalFilename().substring(
                    tenantpic.getOriginalFilename().lastIndexOf("."));
            FileCopyUtils.copy(tenantpic.getBytes(), new File(path, fileName));
            tenant.setTenantlogo(fileName);
        } else {
            if (temp != null) {
                tenant.setTenantlogo(temp.getTenantlogo());
            }
        }
        if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
            tenant.setOrgid(super.getUser(request).getOrgid());
        } else {
            tenant.setOrgid(MainContext.SYSTEM_ORGI);
        }
        tenantRes.save(tenant);
        List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelRes.findByOrgi(tenant.getId());
        orgiSkillRelRes.deleteAll(orgiSkillRelList);
        if (!StringUtils.isBlank(skills)) {
            String[] skillsarray = skills.split(",");
            for (String skill : skillsarray) {
                OrgiSkillRel rel = new OrgiSkillRel();
                rel.setOrgi(tenant.getId());
                rel.setSkillid(skill);
                rel.setCreater(super.getUser(request).getId());
                rel.setCreatetime(new Date());
                orgiSkillRelRes.save(rel);
            }
        }
        OnlineUserProxy.clean(tenant.getId());
        return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }

    @RequestMapping("/delete")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView delete(@Valid Tenant tenant) {
        if (tenant != null) {
            tenantRes.deleteById(tenant.getId());
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }

    @RequestMapping("/canswitch")
    @Menu(type = "apps", subtype = "tenant")
    public ModelAndView canswitch(HttpServletRequest request, @Valid Tenant tenant) throws UnsupportedEncodingException {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                (super.getUser(request)).getId(), super.getOrgi(request));
        if (agentStatus == null && cache.getInservAgentUsersSizeByAgentnoAndOrgi(
                super.getUser(request).getId(), super.getOrgi(request)) == 0) {
            Tenant temp = optionalById(tenant.getId());
            if (temp != null) {
                super.getUser(request).setOrgi(temp.getId());
            }
            return view;
        }
        if (agentStatus != null) {
            if (tenant.getId().equals(agentStatus.getOrgi())) {
                Tenant temp = optionalById(tenant.getId());
                if (temp != null) {
                    super.getUser(request).setOrgi(temp.getId());
                }
                return view;
            } else {
                Tenant temp = optionalById(agentStatus.getOrgi());
                return request(super.createRequestPageTempletResponse(
                        "redirect:/apps/tenant/index.html?msg=t0" + "&currentorgi=" + agentStatus.getOrgi() + "&currentname=" + URLEncoder.encode(
                                temp != null ? temp.getTenantname() : "", "UTF-8")));
            }
        }
        AgentUser agentUser = agentUserRepository.findOneByAgentnoAndStatusAndOrgi(
                super.getUser(request).getId(), MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                super.getOrgi(request));
        if (agentUser != null) {
            if (tenant.getId().equals(agentUser.getOrgi())) {
                Tenant temp = optionalById(tenant.getId());
                if (temp != null) {
                    super.getUser(request).setOrgi(temp.getId());
                }
                return view;
            } else {
                Tenant temp = optionalById(agentUser.getOrgi());
                return request(super.createRequestPageTempletResponse(
                        "redirect:/apps/tenant/index.html?msg=t0" + "&currentorgi=" + agentUser.getOrgi() + "&currentname=" + URLEncoder.encode(
                                temp != null ? temp.getTenantname() : "", "UTF-8")));
            }

        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=t0"));
    }

    @Nullable
    private Tenant optionalById(@NonNull String id) {
        return tenantRes.findById(id)
                .orElse(null);
    }
}
