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

import com.cs.wit.basic.Constants;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.SipTrunk;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.ExtentionRepository;
import com.cs.wit.persistence.repository.SipTrunkRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.CallcenterOutboundProxy;
import com.cs.wit.util.CallCenterUtils;
import com.cs.wit.util.Menu;
import com.cs.wit.util.es.SearchTools;
import com.cs.wit.util.es.UKDataBean;
import com.cs.wit.util.freeswitch.model.CallCenterAgent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/apps/callcenter")
@RequiredArgsConstructor
public class SipTrunkController extends Handler {

    @NonNull
    private final ExtentionRepository extentionRes;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final SipTrunkRepository sipTrunkRes;

    @NonNull
    private final Cache cache;

    @RequestMapping(value = "/siptrunk")
    @Menu(type = "callcenter", subtype = "extention", access = true)
    public ModelAndView detail(ModelMap map, HttpServletResponse response, @Valid String extno) {
        SipTrunk sipTrunk = CallCenterUtils.siptrunk(extno, sipTrunkRes, extentionRes);
        map.addAttribute("siptrunk", sipTrunk);
        response.setContentType("text/plain; charset=utf-8");
        return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/siptrunk"));
    }

    @RequestMapping(value = "/agent")
    @Menu(type = "callcenter", subtype = "agent", access = true)
    public ModelAndView agent(ModelMap map, HttpServletResponse response, @Valid String ani, @Valid String sip) throws IOException {
        SipTrunk sipTrunk = CallCenterUtils.siptrunk(sip, sipTrunkRes);
        map.addAttribute("siptrunk", sipTrunk);
        String agent = null;
        response.setContentType("Content-type: text/plain; charset=utf-8");
        if (sipTrunk != null) {
            PageImpl<UKDataBean> dataBeanList = SearchTools.namesearch(sipTrunk.getOrgi(), ani);
            if (dataBeanList != null && dataBeanList.getContent().size() > 0) {
                UKDataBean dataBean = dataBeanList.getContent().get(0);
                if (dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT) != null) {
                    String disagent = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT);
                    /*
                     * 找到了 坐席
                     */
                    CallCenterAgent callCenterAgent = cache.findOneCallCenterAgentByIdAndOrgi(disagent, sipTrunk.getOrgi());
                    if (callCenterAgent != null) {
                        /*
                         * 坐席在线
                         */
                        agent = callCenterAgent.getExtno();
                    } else if (sipTrunk.isEnablecallagent()) {//坐席不在线
                        Optional<User> optional = userRepository.findById(disagent);
                        if (optional.isPresent()) {
                            User user = optional.get();
                            if (!StringUtils.isBlank(user.getMobile())) {
                                agent = user.getMobile();
                            }
                        }
                    }
                } else {
                    /*
                     * 名单未分配 ， 转给网关进来的任何一个坐席 ， 从当前登录系统的 坐席中选取一个
                     */
                    List<CallCenterAgent> agentList = CallcenterOutboundProxy.service(sipTrunk.getId());
                    if (agentList.size() > 0) {
                        CallCenterAgent callCenterAgent = agentList.get(0);
                        agent = callCenterAgent.getExtno();
                    } else if (!StringUtils.isBlank(sipTrunk.getNotready())) {
                        agent = sipTrunk.getNotready();
                    }
                }
            }
            if (StringUtils.isBlank(agent) && !StringUtils.isBlank(sipTrunk.getNoname())) {
                /*
                 * 未找到名单，从 SIPTrunk里选取一个 转移号码
                 */
                agent = sipTrunk.getNoname();
            }
        }
        map.addAttribute("agent", agent);

        return request(super.createRequestPageTempletResponse("/apps/business/callcenter/extention/agent"));
    }
}
