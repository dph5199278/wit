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
package com.cs.wit.controller.admin.channel;

import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.model.Organ;
import com.cs.wit.model.OrgiSkillRel;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.ConsultInviteRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.OrgiSkillRelRepository;
import com.cs.wit.persistence.repository.SNSAccountRepository;
import com.cs.wit.persistence.repository.ServiceAiRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.util.Menu;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/webim")
@RequiredArgsConstructor
public class WebIMController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(WebIMController.class);

    @NonNull
    private final ConsultInviteRepository inviteRes;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final ServiceAiRepository serviceAiRes;

    @NonNull
    private final OrgiSkillRelRepository orgiSkillRelService;

    @NonNull
    private final SNSAccountRepository snsAccountRes;

    @NonNull
    private final Cache cache;

    @NonNull
    private final UserProxy userProxy;

    @RequestMapping("/index")
    @Menu(type = "app", subtype = "app", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid, super.getOrgi(request));

        if (coultInvite != null) {
            logger.info("[index] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
            map.addAttribute("skillGroups", getSkillGroups(request));
            map.addAttribute("agentList", getUsers(request));
            map.addAttribute("import", request.getServerPort());
            map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));
        }
        return request(super.createAdminTempletResponse("/admin/webim/index"));
    }

    /**
     *
     */
    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "app", admin = true)
    public ModelAndView save(HttpServletRequest request,
                             @Valid CousultInvite inviteData,
                             @RequestParam(value = "webimlogo", required = false) MultipartFile webimlogo,
                             @RequestParam(value = "agentheadimg", required = false) MultipartFile agentheadimg) throws IOException {
        logger.info("[save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", inviteData.getSnsaccountid(), inviteData.isAi(), inviteData.isAifirst(), inviteData.getAiname(), inviteData.getAisuccesstip(), inviteData.getAiid());

        if (StringUtils.isNotBlank(inviteData.getSnsaccountid())) {
            CousultInvite tempData = inviteRes.findBySnsaccountidAndOrgi(inviteData.getSnsaccountid(), super.getOrgi(request));
            if (tempData != null) {
                tempData.setConsult_vsitorbtn_model(inviteData.getConsult_vsitorbtn_model());
                tempData.setConsult_vsitorbtn_color(inviteData.getConsult_vsitorbtn_color());
                tempData.setConsult_vsitorbtn_position(inviteData.getConsult_vsitorbtn_position());
                tempData.setConsult_vsitorbtn_content(inviteData.getConsult_vsitorbtn_content());
                tempData.setConsult_vsitorbtn_display(inviteData.getConsult_vsitorbtn_display());
                tempData.setConsult_dialog_color(inviteData.getConsult_dialog_color());
                inviteData = tempData;
            }
        } else {
            inviteData.setSnsaccountid(super.getUser(request).getId());
        }
        inviteData.setOrgi(super.getOrgi(request));
        // 网页品牌标识
        if (webimlogo != null) {
            String originalFilename = webimlogo.getOriginalFilename();
            if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                inviteData.setConsult_dialog_logo(super.saveImageFileWithMultipart(webimlogo));
            }
        }

        // 网页坐席头像
        if (agentheadimg != null) {
            String originalFilename = agentheadimg.getOriginalFilename();
            if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                inviteData.setConsult_dialog_headimg(super.saveImageFileWithMultipart(agentheadimg));
            }
        }
        inviteRes.save(inviteData);
        cache.putConsultInviteByOrgi(inviteData.getOrgi(), inviteData);
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/index.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/profile")
    @Menu(type = "app", subtype = "profile", admin = true)
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid, super.getOrgi(request));

        if (coultInvite != null) {
            logger.info("[profile] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
            map.addAttribute("skillGroups", getSkillGroups(request));
        }
        map.addAttribute("import", request.getServerPort());
        map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));

        map.put("serviceAiList", serviceAiRes.findByOrgi(super.getOrgi(request)));
        return request(super.createAdminTempletResponse("/admin/webim/profile"));
    }

    @RequestMapping("/profile/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveprofile(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "dialogad", required = false) MultipartFile dialogad) throws IOException {
        final String orgi = super.getOrgi(request);

        if (inviteData != null && StringUtils.isNotBlank(inviteData.getId())) {
            logger.info("[profile/save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}, traceUser {}",
                    inviteData.getSnsaccountid(),
                    inviteData.isAi(),
                    inviteData.isAifirst(),
                    inviteData.getAiname(),
                    inviteData.getAisuccesstip(),
                    inviteData.getAiid(),
                    inviteData.isTraceuser());
            // 从Cache及DB加载consult
            CousultInvite tempInviteData = OnlineUserProxy.consult(inviteData.getSnsaccountid(), orgi);

            if (tempInviteData != null) {
                tempInviteData.setDialog_name(inviteData.getDialog_name());
                tempInviteData.setDialog_address(inviteData.getDialog_address());
                tempInviteData.setDialog_phone(inviteData.getDialog_phone());
                tempInviteData.setDialog_mail(inviteData.getDialog_mail());
                tempInviteData.setDialog_introduction(inviteData.getDialog_introduction());
                tempInviteData.setDialog_message(inviteData.getDialog_message());
                tempInviteData.setLeavemessage(inviteData.isLeavemessage());
                tempInviteData.setLvmopentype(inviteData.getLvmopentype());
                tempInviteData.setLvmname(inviteData.isLvmname());
                tempInviteData.setLvmphone(inviteData.isLvmphone());
                tempInviteData.setLvmemail(inviteData.isLvmemail());
                tempInviteData.setLvmaddress(inviteData.isLvmaddress());
                tempInviteData.setLvmqq(inviteData.isLvmqq());

                tempInviteData.setConsult_skill_fixed(inviteData.isConsult_skill_fixed());
                tempInviteData.setConsult_skill_fixed_id(inviteData.getConsult_skill_fixed_id());
                tempInviteData.setSkill(inviteData.isSkill());
                tempInviteData.setConsult_skill_title(inviteData.getConsult_skill_title());
                tempInviteData.setConsult_skill_msg(inviteData.getConsult_skill_msg());
                tempInviteData.setConsult_skill_bottomtitle(inviteData.getConsult_skill_bottomtitle());
                tempInviteData.setConsult_skill_maxagent(inviteData.getConsult_skill_maxagent());
                tempInviteData.setConsult_skill_numbers(inviteData.getConsult_skill_numbers());
                tempInviteData.setConsult_skill_agent(inviteData.isConsult_skill_agent());

                tempInviteData.setOnlyareaskill(inviteData.isOnlyareaskill());
                tempInviteData.setAreaskilltipmsg(inviteData.getAreaskilltipmsg());

                tempInviteData.setConsult_info(inviteData.isConsult_info());
                tempInviteData.setConsult_info_email(inviteData.isConsult_info_email());
                tempInviteData.setConsult_info_name(inviteData.isConsult_info_name());
                tempInviteData.setConsult_info_phone(inviteData.isConsult_info_phone());
                tempInviteData.setConsult_info_resion(inviteData.isConsult_info_resion());
                tempInviteData.setConsult_info_message(inviteData.getConsult_info_message());
                tempInviteData.setConsult_info_cookies(inviteData.isConsult_info_cookies());

                tempInviteData.setRecordhis(inviteData.isRecordhis());
                tempInviteData.setTraceuser(inviteData.isTraceuser());

                tempInviteData.setAi(inviteData.isAi());
                tempInviteData.setAifirst(inviteData.isAifirst());
                tempInviteData.setAimsg(inviteData.getAimsg());
                tempInviteData.setAisuccesstip(inviteData.getAisuccesstip());
                tempInviteData.setAiname(inviteData.getAiname());
                tempInviteData.setAiid(inviteData.getAiid());


                tempInviteData.setMaxwordsnum(inviteData.getMaxwordsnum());
                tempInviteData.setCtrlenter(inviteData.isCtrlenter());
                tempInviteData.setWhitelist_mode(inviteData.isWhitelist_mode());

                if (dialogad != null && StringUtils.isNotBlank(dialogad.getName()) && dialogad.getBytes().length > 0) {
                    tempInviteData.setDialog_ad(super.saveImageFileWithMultipart(dialogad));
                }
                // 保存到DB
                inviteRes.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            if (inviteData != null) {
                inviteRes.save(inviteData);
            }
        }
        if (inviteData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite data not found");
        }
        cache.putConsultInviteByOrgi(orgi, inviteData);
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/profile.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/invote")
    @Menu(type = "app", subtype = "invote", admin = true)
    public ModelAndView invote(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid, super.getOrgi(request));

        if (coultInvite != null) {
            logger.info("[invote] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
        }
        map.addAttribute("import", request.getServerPort());
        map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));
        return request(super.createAdminTempletResponse("/admin/webim/invote"));
    }

    @RequestMapping("/invote/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveinvote(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "invotebg", required = false) MultipartFile invotebg) throws IOException {
        CousultInvite tempInviteData;

        if (inviteData != null && StringUtils.isNotBlank(inviteData.getId())) {
            logger.info("[invote/save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", inviteData.getSnsaccountid(), inviteData.isAi(), inviteData.isAifirst(), inviteData.getAiname(), inviteData.getAisuccesstip(), inviteData.getAiid());
            tempInviteData = OnlineUserProxy.consult(inviteData.getSnsaccountid(), super.getOrgi(request));
            if (tempInviteData != null) {
                tempInviteData.setConsult_invite_enable(inviteData.isConsult_invite_enable());
                tempInviteData.setConsult_invite_content(inviteData.getConsult_invite_content());
                tempInviteData.setConsult_invite_accept(inviteData.getConsult_invite_accept());
                tempInviteData.setConsult_invite_later(inviteData.getConsult_invite_later());
                tempInviteData.setConsult_invite_delay(inviteData.getConsult_invite_delay());

                tempInviteData.setConsult_invite_color(inviteData.getConsult_invite_color());

                if (invotebg != null && StringUtils.isNotBlank(invotebg.getName()) && invotebg.getBytes().length > 0) {
                    tempInviteData.setConsult_invite_bg(super.saveImageFileWithMultipart(invotebg));
                }
                inviteRes.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            if (inviteData != null) {
                inviteRes.save(inviteData);
            }
        }

        if (inviteData == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite data not found");
        }

        cache.putConsultInviteByOrgi(inviteData.getOrgi(), inviteData);
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/invote.html?snsid=" + inviteData.getSnsaccountid()));
    }

    /**
     * 获取当前登录者组织下的技能组列表
     */
    private List<Organ> getSkillGroups(HttpServletRequest request) {
        List<Organ> skillgroups = new ArrayList<>();
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            skillgroups = organRes.findAllById(organIdList);
        } else {
            List<Organ> allgroups = organRes.findByOrgiAndOrgid(super.getOrgi(request), super.getOrgid(request));
            for (Organ o : allgroups) {
                if (o.isSkill()) {
                    skillgroups.add(o);
                }
            }
        }
        return skillgroups;
    }

    /**
     * 获取当前产品下人员信息
     */
    private List<User> getUsers(HttpServletRequest request) {
        List<User> userList;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            userList = userProxy.findByOrganInAndAgentAndDatastatus(organIdList, true, false);
        } else {
            userList = userRes.findByOrgiAndAgentAndDatastatus(super.getOrgi(request), true, false);
        }
        return userList;
    }
}
