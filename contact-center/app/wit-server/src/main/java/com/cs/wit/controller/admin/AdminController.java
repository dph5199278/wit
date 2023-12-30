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
package com.cs.wit.controller.admin;

import com.cs.wit.acd.ACDWorkMonitor;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.SysDic;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.persistence.repository.SysDicRepository;
import com.cs.wit.persistence.repository.UserEventRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.util.Menu;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class AdminController extends Handler {

    @NonNull
    private final ACDWorkMonitor acdWorkMonitor;

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final OnlineUserRepository onlineUserRes;

    @NonNull
    private final UserEventRepository userEventRes;

    @NonNull
    private final SysDicRepository sysDicRes;

    @NonNull
    private final Cache cache;

    @RequestMapping("/admin")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
        User user = super.getUser(request);
        view.addObject("agentStatusReport", acdWorkMonitor.getAgentReport(user.getOrgi()));
        view.addObject("agentStatus", cache.findOneAgentStatusByAgentnoAndOrig(user.getId(), user.getOrgi()));
        return view;
    }

    private void aggValues(ModelMap map, HttpServletRequest request) {
        String orgi = super.getOrgi(request);
        map.put("onlineUserCache", cache.getOnlineUserSizeByOrgi(orgi));
        map.put("onlineUserClients", OnlineUserProxy.webIMClients.size());
        map.put("chatClients", NettyClients.getInstance().size());
        map.put("systemCaches", cache.getSystemSizeByOrgi(MainContext.SYSTEM_ORGI));

        map.put("agentReport", acdWorkMonitor.getAgentReport(orgi));
        map.put("webIMReport", MainUtils.getWebIMReport(userEventRes.findByOrgiAndCreatetimeRange(super.getOrgi(request), MainUtils.getStartTime(), MainUtils.getEndTime())));

        map.put("agents", getAgent(request).size());

        map.put("webIMInvite", MainUtils.getWebIMInviteStatus(onlineUserRes.findByOrgiAndStatus(super.getOrgi(request), MainContext.OnlineUserStatusEnum.ONLINE.toString())));

        map.put("inviteResult", MainUtils.getWebIMInviteResult(onlineUserRes.findByOrgiAndAgentnoAndCreatetimeRange(super.getOrgi(request), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime())));

        map.put("agentUserCount", onlineUserRes.countByAgentForAgentUser(super.getOrgi(request), MainContext.AgentUserStatusEnum.INSERVICE.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("agentServicesCount", onlineUserRes.countByAgentForAgentUser(super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("agentServicesAvg", onlineUserRes.countByAgentForAvagTime(super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("webInviteReport", MainUtils.getWebIMInviteAgg(onlineUserRes.findByOrgiAndCreatetimeRange(super.getOrgi(request), MainContext.ChannelType.WEBIM.toString(), MainUtils.getLast30Day(), MainUtils.getEndTime())));

        map.put("agentConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForAgent(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime())));

        map.put("clentConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForClient(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime(), MainContext.ChannelType.WEBIM.toString())));

        map.put("browserConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForBrowser(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime(), MainContext.ChannelType.WEBIM.toString())));
    }

    private List<User> getAgent(HttpServletRequest request) {
        //获取当前产品or租户坐席数
        List<User> userList;
        if (super.isEnabletneant()) {
            userList = userRes.findByOrgidAndAgentAndDatastatus(super.getOrgid(request), true, false);
        } else {
            userList = userRes.findByOrgiAndAgentAndDatastatus(super.getOrgi(request), true, false);
        }
        return userList.isEmpty() ? new ArrayList<>() : userList;
    }

    @RequestMapping("/admin/content")
    @Menu(type = "admin", subtype = "content")
    public ModelAndView content(ModelMap map, HttpServletRequest request) {
        aggValues(map, request);
        return request(super.createAdminTempletResponse("/admin/content"));
    	/*if(super.getUser(request).isSuperuser()) {
    		aggValues(map, request);
        	return request(super.createAdminTempletResponse("/admin/content"));
    	}else {
    		return request(super.createAdminTempletResponse("/admin/user/index"));
    	}*/
    }

    @RequestMapping("/admin/auth/infoacq")
    @Menu(type = "admin", subtype = "infoacq", admin = true)
    public ModelAndView infoacq(HttpServletRequest request) {
        String inacq = (String) request.getSession().getAttribute(Constants.CSKEFU_SYSTEM_INFOACQ);
        if (StringUtils.isNotBlank(inacq)) {
            request.getSession().removeAttribute(Constants.CSKEFU_SYSTEM_INFOACQ);
        } else {
            request.getSession().setAttribute(Constants.CSKEFU_SYSTEM_INFOACQ, "true");
        }
        return request(super.createRequestPageTempletResponse("redirect:/"));
    }

    @RequestMapping("/admin/auth/event")
    @Menu(type = "admin", subtype = "authevent")
    public ModelAndView authevent(ModelMap map, @Valid String title, @Valid String url, @Valid String iconstr, @Valid String icontext) {
        map.addAttribute("title", title);
        map.addAttribute("url", url);
        if (StringUtils.isNotBlank(iconstr) && StringUtils.isNotBlank(icontext)) {
            map.addAttribute("iconstr", iconstr.replaceAll(icontext, "&#x" + MainUtils.string2HexString(icontext) + ";"));
        }
        return request(super.createRequestPageTempletResponse("/admin/system/auth/exchange"));
    }

    @RequestMapping("/admin/auth/save")
    @Menu(type = "admin", subtype = "authsave")
    public ModelAndView authsave(HttpServletRequest request, @Valid SysDic dic) {
        SysDic sysDic = sysDicRes.findByCode(Constants.CSKEFU_SYSTEM_AUTH_DIC);
        boolean newdic = false;
        if (sysDic != null && StringUtils.isNotBlank(dic.getName())) {
            if (StringUtils.isNotBlank(dic.getParentid())) {
                if (dic.getParentid().equals("0")) {
                    dic.setParentid(sysDic.getId());
                    newdic = true;
                } else {
                    List<SysDic> dicList = sysDicRes.findByDicid(sysDic.getId());
                    for (SysDic temp : dicList) {
                        if (temp.getCode().equals(dic.getParentid()) || temp.getName().equals(dic.getParentid())) {
                            dic.setParentid(temp.getId());
                            newdic = true;
                        }
                    }
                }
            }
            if (newdic) {
                dic.setCreater(super.getUser(request).getId());
                dic.setCreatetime(new Date());
                dic.setCtype("auth");
                dic.setDicid(sysDic.getId());
                sysDicRes.save(dic);
            }
        }
        return request(super.createRequestPageTempletResponse("/public/success"));
    }

}
