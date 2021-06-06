/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.cs.wit.proxy;

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.model.*;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.persistence.es.QuickReplyRepository;
import com.cs.wit.persistence.interfaces.DataExchangeInterface;
import com.cs.wit.persistence.repository.*;
import com.cs.wit.util.mobile.MobileAddress;
import com.cs.wit.util.mobile.MobileNumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentServiceProxy {

    @NonNull
    private final AgentServiceRepository agentServiceRes;

    @NonNull
    private final AgentUserContactsRepository agentUserContactsRes;

    @NonNull
    private final SNSAccountRepository snsAccountRes;

    @NonNull
    private final WeiXinUserRepository weiXinUserRes;

    @NonNull
    private final OnlineUserRepository onlineUserRes;

    @NonNull
    private final PbxHostRepository pbxHostRes;

    @NonNull
    private final StatusEventRepository statusEventRes;

    @NonNull
    private final ServiceSummaryRepository serviceSummaryRes;

    @NonNull
    private final TagRepository tagRes;

    @NonNull
    private final QuickTypeRepository quickTypeRes;

    @NonNull
    private final QuickReplyRepository quickReplyRes;

    @NonNull
    private final TagRelationRepository tagRelationRes;

    @NonNull
    private final ChatMessageRepository chatMessageRepository;

    @NonNull
    private final ContactsRepository contactsRes;

    /**
     * 关联关系
     */
    public void processRelaData(
            final String agentno,
            final String orgi,
            final AgentService agentService,
            final ModelMap map) {
        Sort defaultSort;
        defaultSort = Sort.by(Sort.Direction.DESC, "servicetime");
        map.addAttribute(
                "agentServiceList",
                agentServiceRes.findByUseridAndOrgiAndStatus(
                        agentService.getUserid(),
                        orgi,
                        MainContext.AgentUserStatusEnum.END.toString(),
                        defaultSort
                )
        );

        if (StringUtils.isNotBlank(agentService.getAppid())) {
            map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(agentService.getAppid(), orgi));
        }
        agentUserContactsRes.findOneByUseridAndOrgi(
                agentService.getUserid(), agentService.getOrgi()).ifPresent(p -> {
            if (MainContext.hasModule(Constants.CSKEFU_MODULE_CONTACTS) && StringUtils.isNotBlank(
                    p.getContactsid())) {
                contactsRes.findOneById(p.getContactsid()).ifPresent(k -> map.addAttribute("contacts", k));
            }
            if (MainContext.hasModule(Constants.CSKEFU_MODULE_WORKORDERS) && StringUtils.isNotBlank(
                    p.getContactsid())) {
                DataExchangeInterface dataExchange = (DataExchangeInterface) MainContext.getContext().getBean(
                        "workorders");
                //noinspection ConstantConditions
                if (dataExchange != null) {
                    map.addAttribute(
                            "workOrdersList",
                            dataExchange.getListDataByIdAndOrgi(p.getContactsid(), agentno, orgi));
                }
                map.addAttribute("contactsid", p.getContactsid());
            }
        });
    }


    /**
     * 增加不同渠道的信息
     *
     * @param logined 登录的用户
     */
    public void attacheChannelInfo(
            final ModelAndView view,
            final AgentUser agentUser,
            final AgentService agentService,
            final User logined) {
        if (MainContext.ChannelType.WEIXIN.toString().equals(agentUser.getChannel())) {
            List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenidAndOrgi(
                    agentUser.getUserid(), logined.getOrgi());
            if (weiXinUserList.size() > 0) {
                WeiXinUser weiXinUser = weiXinUserList.get(0);
                view.addObject("weiXinUser", weiXinUser);
            }
        } else if (MainContext.ChannelType.WEBIM.toString().equals(agentUser.getChannel())) {
            Optional<OnlineUser> optional = onlineUserRes.findById(agentUser.getUserid());
            if (optional.isPresent()) {
                OnlineUser onlineUser = optional.get();
                if (StringUtils.equals(
                        MainContext.OnlineUserStatusEnum.OFFLINE.toString(), onlineUser.getStatus())) {
                    onlineUser.setBetweentime(
                            (int) (onlineUser.getUpdatetime().getTime() - onlineUser.getLogintime().getTime()));
                } else {
                    onlineUser.setBetweentime((int) (System.currentTimeMillis() - onlineUser.getLogintime().getTime()));
                }
                view.addObject("onlineUser", onlineUser);
            }
        } else if (MainContext.ChannelType.PHONE.toString().equals(agentUser.getChannel())) {
            if (agentService != null && StringUtils.isNotBlank(agentService.getOwner())) {
                statusEventRes.findById(agentService.getOwner())
                        .ifPresent(statusEvent -> {
                            if (StringUtils.isNotBlank(statusEvent.getHostid())) {
                                pbxHostRes.findById(statusEvent.getHostid())
                                        .ifPresent(it -> view.addObject("pbxHost", it));
                            }
                            view.addObject("statusEvent", statusEvent);
                        });
                MobileAddress ma = MobileNumberUtils.getAddress(agentUser.getPhone());
                view.addObject("mobileAddress", ma);
            }
        }
    }


    /**
     * 组装AgentUser的相关信息并封装在ModelView中
     */
    public void bundleDialogRequiredDataInView(
            final ModelAndView view,
            final ModelMap map,
            final AgentUser agentUser,
            final String orgi,
            final User logined) {
        view.addObject("curagentuser", agentUser);

        CousultInvite invite = OnlineUserProxy.consult(agentUser.getAppid(), agentUser.getOrgi());
        if (invite != null) {
            view.addObject("aisuggest", invite.isAisuggest());
            view.addObject("ccaAisuggest", invite.isAisuggest());
        }

        // 客服设置
        if (StringUtils.isNotBlank(agentUser.getAppid())) {
            view.addObject("inviteData", OnlineUserProxy.consult(agentUser.getAppid(), orgi));
            // 服务小结
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                List<AgentServiceSummary> summarizes = serviceSummaryRes.findByAgentserviceidAndOrgi(
                        agentUser.getAgentserviceid(), orgi);
                if (summarizes.size() > 0) {
                    view.addObject("summary", summarizes.get(0));
                }
            }

            // 对话消息
            view.addObject(
                    "agentUserMessageList",
                    chatMessageRepository.findByUsessionAndOrgi(agentUser.getUserid(), logined.getOrgi(),
                            PageRequest.of(0, 20, Sort.Direction.DESC,
                                    "updatetime")));

            // 坐席服务记录
            AgentService agentService = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                Optional<AgentService> optional = agentServiceRes.findById(agentUser.getAgentserviceid());
                if (optional.isPresent()) {
                    agentService = optional.get();
                    view.addObject("curAgentService", agentService);
                }
                /*
                 * 获取关联数据
                 */
                if (agentService != null) {
                    processRelaData(logined.getId(), orgi, agentService, map);
                }
            }


            // 渠道信息
            attacheChannelInfo(view, agentUser, agentService, logined);

            // 标签，快捷回复等
            view.addObject("serviceCount", agentServiceRes
                    .countByUseridAndOrgiAndStatus(agentUser
                                    .getUserid(), logined.getOrgi(),
                            MainContext.AgentUserStatusEnum.END.toString()));
            view.addObject("tagRelationList", tagRelationRes.findByUserid(agentUser.getUserid()));
        }

        view.addObject("tags", tagRes.findByOrgiAndTagtype(logined.getOrgi(), MainContext.ModelType.USER.toString()));
        view.addObject("quickReplyList", quickReplyRes.findByOrgiAndCreater(logined.getOrgi(), logined.getId(), null));
        List<QuickType> quickTypeList = quickTypeRes.findByOrgiAndQuicktype(
                logined.getOrgi(), MainContext.QuickType.PUB.toString());
        List<QuickType> priQuickTypeList = quickTypeRes.findByOrgiAndQuicktypeAndCreater(
                logined.getOrgi(),
                MainContext.QuickType.PRI.toString(),
                logined.getId());
        quickTypeList.addAll(priQuickTypeList);
        view.addObject("pubQuickTypeList", quickTypeList);
    }
}
