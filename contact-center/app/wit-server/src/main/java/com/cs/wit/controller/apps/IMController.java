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

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.acd.ACDWorkMonitor;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.basic.TextEncryptor;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.AgentReport;
import com.cs.wit.model.AgentServiceSatis;
import com.cs.wit.model.AgentUserContacts;
import com.cs.wit.model.AttachmentFile;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.model.Chatbot;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.model.Dict;
import com.cs.wit.model.InviteRecord;
import com.cs.wit.model.LeaveMsg;
import com.cs.wit.model.SNSAccount;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.StreamingFile;
import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.UploadStatus;
import com.cs.wit.model.User;
import com.cs.wit.model.UserHistory;
import com.cs.wit.persistence.blob.JpaBlobHelper;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.AgentServiceSatisRepository;
import com.cs.wit.persistence.repository.AgentUserContactsRepository;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.persistence.repository.AttachmentRepository;
import com.cs.wit.persistence.repository.ChatMessageRepository;
import com.cs.wit.persistence.repository.ChatbotRepository;
import com.cs.wit.persistence.repository.ConsultInviteRepository;
import com.cs.wit.persistence.repository.InviteRecordRepository;
import com.cs.wit.persistence.repository.LeaveMsgRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.persistence.repository.SNSAccountRepository;
import com.cs.wit.persistence.repository.StreamingFileRepository;
import com.cs.wit.persistence.repository.UserHistoryRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.socketio.util.RichMediaUtils;
import com.cs.wit.util.Md5Utils;
import com.cs.wit.util.Menu;
import com.cs.wit.util.PinYinTools;
import com.cs.wit.util.StreamingFileUtil;
import com.cs.wit.util.WebIMClient;
import com.cs.wit.util.ip.IP;
import com.cs.wit.util.ip.IPTools;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/im")
@EnableAsync
@RequiredArgsConstructor
public class IMController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(IMController.class);

    @NonNull
    private final ACDWorkMonitor acdWorkMonitor;

    @NonNull
    private final ACDPolicyService acdPolicyService;

    @NonNull
    private final OnlineUserRepository onlineUserRes;
    @NonNull
    private final StreamingFileRepository streamingFileRepository;
    @NonNull
    private final JpaBlobHelper jpaBlobHelper;
    @NonNull
    private final ConsultInviteRepository inviteRepository;
    @NonNull
    private final ChatMessageRepository chatMessageRes;
    @NonNull
    private final AgentServiceSatisRepository agentServiceSatisRes;
    @NonNull
    private final AgentServiceRepository agentServiceRepository;
    @NonNull
    private final InviteRecordRepository inviteRecordRes;
    @NonNull
    private final LeaveMsgRepository leaveMsgRes;
    @NonNull
    private final AgentUserRepository agentUserRepository;
    @NonNull
    private final AttachmentRepository attachementRes;
    @NonNull
    private final ContactsRepository contactsRes;
    @NonNull
    private final AgentUserContactsRepository agentUserContactsRes;
    @NonNull
    private final SNSAccountRepository snsAccountRepository;
    @NonNull
    private final SNSAccountRepository snsAccountRes;
    @NonNull
    private final UserHistoryRepository userHistoryRes;
    @NonNull
    private final ChatbotRepository chatbotRes;
    @NonNull
    private final Cache cache;
    @NonNull
    private final TextEncryptor encryptor;
    @Value("${uk.im.server.port}")
    private Integer port;
    @Value("${cs.im.server.ssl.port}")
    private Integer sslPort;
    @Value("${web.upload-path}")
    private String path;
    @Value("${cskefu.settings.webim.visitor-separate}")
    private Boolean channelWebIMVisitorSeparate;

    @NonNull
    private final IPTools ipTools;

    @PostConstruct
    private void init() {
    }

    /**
     * 在客户或第三方网页内，写入聊天控件
     */
    @RequestMapping("/{id}")
    @Menu(type = "im", subtype = "point", access = true)
    public ModelAndView point(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid String userid,
            @Valid String title,
            @Valid String aiid) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/point"));
        view.addObject("channelVisitorSeparate", channelWebIMVisitorSeparate);

        final String sessionid = MainUtils.getContextID(request.getSession().getId());
        logger.info("[point] session snsid {}, session {}", id, sessionid);

        if (StringUtils.isNotBlank(id)) {
            boolean webimexist = false;
            view.addObject("hostname", request.getServerName());
            logger.info("[point] new website is : {}", request.getServerName());
            SNSAccount SnsAccountList = snsAccountRes.findBySnsidAndOrgi(id, super.getUser(request).getOrgi());
            if (SnsAccountList != null) {
                webimexist = true;
            }
            view.addObject("webimexist", webimexist);

            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnablessl()) {
                view.addObject("schema", "https");
                if (request.getServerPort() == 80) {
                    view.addObject("port", 443);
                } else {
                    view.addObject("port", request.getServerPort());
                }
            } else {
                view.addObject("schema", request.getScheme());
                String header = request.getHeader("X-Forwarded-Proto");
                if (header != null) {
                    view.addObject("schema", header);
                }
                view.addObject("port", request.getServerPort());
            }

            final UserAgent client = UserAgentUtil.parse(request.getHeader("User-Agent"));

            view.addObject("appid", id);
            view.addObject("client", MainUtils.getUUID());
            view.addObject("sessionid", sessionid);
            view.addObject("ip", Md5Utils.doubleMd5(request.getRemoteAddr()));
            view.addObject("mobile", client.isMobile());

            CousultInvite invite = OnlineUserProxy.consult(id, MainContext.SYSTEM_ORGI);
            if (invite != null) {
                logger.info("[point] find CousultInvite {}", invite.getId());
                view.addObject("inviteData", invite);
                view.addObject("orgi", invite.getOrgi());
                view.addObject("appid", id);

                if (StringUtils.isNotBlank(aiid)) {
                    view.addObject("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    view.addObject("aiid", invite.getAiid());
                }

                // 记录用户行为日志
                // 每次有一个新网页加载出聊天控件，都会生成一个userHistory
                UserHistory userHistory = new UserHistory();
                String url = request.getHeader("referer");
                if (StringUtils.isNotBlank(url)) {
                    if (url.length() > 255) {
                        userHistory.setUrl(url.substring(0, 255));
                    } else {
                        userHistory.setUrl(url);
                    }
                    userHistory.setReferer(userHistory.getUrl());
                }
                userHistory.setParam(MainUtils.getParameter(request));
                userHistory.setMaintype("send");
                userHistory.setSubtype("point");
                userHistory.setName("online");
                userHistory.setAdmin(false);
                userHistory.setAccessnum(true);
                userHistory.setModel(MainContext.ChannelType.WEBIM.toString());

                final User imUser = super.getIMUser(request, userid, null);
                if (imUser != null) {
                    userHistory.setCreater(imUser.getId());
                    userHistory.setUsername(imUser.getUsername());
                    userHistory.setOrgi(MainContext.SYSTEM_ORGI);
                }

                if (StringUtils.isNotBlank(title)) {
                    if (title.length() > 255) {
                        userHistory.setTitle(title.substring(0, 255));
                    } else {
                        userHistory.setTitle(title);
                    }
                }

                userHistory.setOrgi(invite.getOrgi());
                userHistory.setAppid(id);
                userHistory.setSessionid(sessionid);

                String ip = MainUtils.getIpAddr(request);
                userHistory.setHostname(ip);
                userHistory.setIp(ip);
                IP ipdata = ipTools.findGeography(ip);
                userHistory.setCountry(ipdata.getCountry());
                userHistory.setProvince(ipdata.getProvince());
                userHistory.setCity(ipdata.getCity());
                userHistory.setIsp(ipdata.getIsp());

                userHistory.setOstype(client.getOs().getName());
                userHistory.setBrowser(client.getBrowser().getName());
                userHistory.setMobile(client.isMobile() ? "1" : "0");

                if (invite.isSkill() && !invite.isConsult_skill_fixed()) { // 展示所有技能组
                    /*
                     * 查询 技能组 ， 缓存？
                     */
                    view.addObject("skillGroups", OnlineUserProxy.organ(MainContext.SYSTEM_ORGI, ipdata, invite, true));
                    /*
                     * 查询坐席 ， 缓存？
                     */
                    view.addObject("agentList", OnlineUserProxy.agents(MainContext.SYSTEM_ORGI, true));
                }

                view.addObject("traceid", userHistory.getId());

                // 用户的浏览历史会有很大的数据量，目前强制开启
                userHistoryRes.save(userHistory);

                view.addObject(
                        "pointAd",
                        MainUtils.getPointAdv(MainContext.AdPosEnum.POINT.toString(), MainContext.SYSTEM_ORGI));
                view.addObject(
                        "inviteAd",
                        MainUtils.getPointAdv(MainContext.AdPosEnum.INVITE.toString(), MainContext.SYSTEM_ORGI));
            } else {
                logger.info("[point] invite id {}, orgi {} not found", id, MainContext.SYSTEM_ORGI);
            }
        }

        return view;
    }

    private void createContacts(
            final String gid,
            final String uid,
            final String cid,
            final String sid,
            final String username,
            final String company_name,
            final String system_name) {
        if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
            Contacts data = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(uid, sid, cid, false);
            if (data == null) {
                data = new Contacts();
                data.setCreater(gid);
                data.setOrgi(MainContext.SYSTEM_ORGI);
                data.setWluid(uid);
                data.setWlusername(username);
                data.setWlcid(cid);
                data.setWlcompany_name(company_name);
                data.setWlsid(sid);
                data.setWlsystem_name(system_name);
                data.setName(username + '@' + company_name);
                data.setShares("all");

                data.setPinyin(PinYinTools.getInstance().getFirstPinYin(username));
                contactsRes.save(data);
            }
        }
    }

    @ResponseBody
    @RequestMapping("/chatoperainit")
    @Menu(type = "im", subtype = "chatoperainit")
    public String chatoperaInit(
            HttpServletRequest request,
            String userid,
            String uid,
            String username,
            String cid,
            String company_name,
            String sid,
            String system_name,
            Boolean whitelist_mode,
            @RequestParam String sessionid) {
        final User logined = super.getUser(request);

        request.getSession().setAttribute("Sessionuid", uid);

        Map<String, String> sessionMessage = new HashMap<>();
        sessionMessage.put("username", username);
        sessionMessage.put("cid", cid);
        sessionMessage.put("company_name", company_name);
        sessionMessage.put("sid", sid);
        sessionMessage.put("Sessionsystem_name", system_name);
        sessionMessage.put("sessionid", sessionid);
        sessionMessage.put("uid", uid);
        cache.putSystemMapByIdAndOrgi(sessionid, MainContext.SYSTEM_ORGI, sessionMessage);

        onlineUserRes.findById(userid)
                .ifPresent(onlineUser -> {

                    String updateusername;
                    updateusername = username + "@" + company_name;
                    onlineUser.setUsername(updateusername);
                    onlineUser.setUpdateuser(updateusername);
                    onlineUser.setUpdatetime(new Date());
                    onlineUserRes.save(onlineUser);
                });

        Contacts usc = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(uid, sid, cid, false);
        if (usc != null) {
            return "usc";
        } else {
            if (!whitelist_mode) {
                createContacts(logined.getId(), uid, cid, sid, username, company_name, system_name);
            }
        }

        return "ok";
    }


    @RequestMapping("/{id}/userlist")
    @Menu(type = "im", subtype = "inlist", access = true)
    public void inlist(HttpServletResponse response, @PathVariable String id, @Valid String userid) throws IOException {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        if (StringUtils.isNotBlank(userid)) {
            BlackEntity black = cache.findOneSystemByIdAndOrgi(userid, MainContext.SYSTEM_ORGI);
            if ((black != null && (black.getEndtime() == null || black.getEndtime().after(new Date())))) {
                response.getWriter().write("in");
            }
        }
    }

    /**
     * 延时获取用户端浏览器的跟踪ID
     */
    @RequestMapping("/online")
    @Menu(type = "im", subtype = "online", access = true)
    public SseEmitter callable(
            HttpServletRequest request,
            final @Valid String orgi,
            final @Valid String sessionid,
            @Valid String appid,
            final @Valid String userid,
            @Valid String sign,
            final @Valid String client,
            final @Valid String traceid) throws InterruptedException {
//        logger.info(
//                "[online] user {}, orgi {}, traceid {}, appid {}, session {}", userid, orgi, traceid, appid, sessionid);
        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, orgi);
        if (blackOpt.isPresent() && (blackOpt.get().getEndtime() == null || blackOpt.get().getEndtime().after(
                new Date()))) {
            logger.info("[online] online user {} is in black list.", userid);
            // 该访客被拉黑
            return null;
        }

        final SseEmitter emitter = new SseEmitter(30000L);
        if (StringUtils.isNotBlank(userid)) {
            emitter.onCompletion(() -> {
                try {
                    OnlineUserProxy.webIMClients.removeClient(userid, client, false); // 执行了 邀请/再次邀请后终端的
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            emitter.onTimeout(() -> {
                try {
                    emitter.complete();
                    OnlineUserProxy.webIMClients.removeClient(userid, client, true); // 正常的超时断开
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            CousultInvite invite = OnlineUserProxy.consult(appid, orgi);

            // TODO 该contacts的识别并不准确，因为不能关联
//                if (invite != null && invite.isTraceuser()) {
//                    contacts = OnlineUserProxy.OnlineUserProxy.processContacts(orgi, contacts, appid, userid);
//                }
//
//                if (StringUtils.isNotBlank(sign)) {
//                    OnlineUserProxy.online(
//                            super.getIMUser(request, sign, contacts != null ? contacts.getName() : null, sessionid),
//                            orgi,
//                            sessionid,
//                            MainContext.OnlineUserType.WEBIM.toString(),
//                            request,
//                            MainContext.ChannelType.WEBIM.toString(),
//                            appid,
//                            contacts,
//                            invite);
            // END 取消关联contacts

            if (StringUtils.isNotBlank(sign)) {
                OnlineUserProxy.online(
                        ipTools,
                        super.getIMUser(request, sign, null, sessionid),
                        orgi,
                        sessionid,
                        MainContext.OnlineUserType.WEBIM.toString(),
                        request,
                        MainContext.ChannelType.WEBIM.toString(),
                        appid,
                        null,
                        invite);
            }
            OnlineUserProxy.webIMClients.putClient(userid, new WebIMClient(userid, client, emitter, traceid));
            Thread.sleep(500);
        }

        return emitter;
    }

    /**
     * 访客与客服聊天小窗口
     * <p>
     * 此处返回给访客新的页面：根据访客/坐席/机器人的情况进行判断
     * 如果此处返回的是人工服务，那么此处并不寻找服务的坐席信息，而是在返回的页面中查找
     */
    @RequestMapping("/index")
    @Menu(type = "im", subtype = "index", access = true)
    public ModelAndView index(
            ModelMap map,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid final String orgi,
            @Valid final String aiid,
//                              @Valid String uid,
            @Valid final String traceid,
            @Valid final String exchange,
            @Valid final String title,
            @Valid final String url,
            @Valid final String mobile,
            @Valid final String ai,
            @Valid final String client,
            @Valid final String type,
            @Valid final String appid,
            @Valid final String userid,
            @Valid final String sessionid,
            @Valid final String skill,
            @Valid final String agent,
            @Valid Contacts contacts,
            @Valid final String product,
            @Valid final String description,
            @Valid final String imgurl,
            @Valid final String pid,
            @Valid final String purl,
            @Valid final boolean isInvite) throws Exception {
        logger.info(
                "[index] orgi {}, skill {}, agent {}, traceid {}, isInvite {}, exchange {}", orgi, skill, agent,
                traceid, isInvite, exchange);
        Map<String, String> sessionMessageObj = cache.findOneSystemMapByIdAndOrgi(sessionid, orgi);

        if (sessionMessageObj != null) {
            request.getSession().setAttribute("Sessionusername", sessionMessageObj.get("username"));
            request.getSession().setAttribute("Sessioncid", sessionMessageObj.get("cid"));
            request.getSession().setAttribute("Sessioncompany_name", sessionMessageObj.get("company_name"));
            request.getSession().setAttribute("Sessionsid", sessionMessageObj.get("sid"));
            request.getSession().setAttribute("Sessionsystem_name", sessionMessageObj.get("system_name"));
            request.getSession().setAttribute("sessionid", sessionMessageObj.get("sessionid"));
            request.getSession().setAttribute("Sessionuid", sessionMessageObj.get("uid"));
        }

        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/index"));
        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, MainContext.SYSTEM_ORGI);
        if (StringUtils.isNotBlank(
                appid) && ((!blackOpt.isPresent()) || (blackOpt.get().getEndtime() != null && blackOpt.get().getEndtime().before(
                new Date())))) {
            CousultInvite invite = OnlineUserProxy.consult(appid, orgi);
            String randomUserId; // 随机生成OnlineUser的用户名，使用了浏览器指纹做唯一性KEY
            if (StringUtils.isNotBlank(userid)) {
                randomUserId = MainUtils.genIDByKey(userid);
            } else {
                randomUserId = MainUtils.genIDByKey(sessionid);
            }
            String nickname;

            if (sessionMessageObj != null) {
                //noinspection rawtypes
                nickname = ((Map) sessionMessageObj).get("username") + "@" + ((Map) sessionMessageObj).get(
                        "company_name");
            } else if (request.getSession().getAttribute("Sessionusername") != null) {
                String struname = (String) request.getSession().getAttribute("Sessionusername");
                String strcname = (String) request.getSession().getAttribute("Sessioncompany_name");
                nickname = struname + "@" + strcname;
            } else {
                nickname = "Guest_" + "@" + randomUserId;
            }

            view.addObject("nickname", nickname);

            boolean consult = true;                //是否已收集用户信息
            SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);

            // 强制开启满意调查问卷
            sessionConfig.setSatisfaction(true);

            map.addAttribute("sessionConfig", sessionConfig);
            map.addAttribute("hostname", request.getServerName());

            if (sslPort != null) {
                map.addAttribute("port", sslPort);
            } else {
                map.addAttribute("port", port);
            }

            map.addAttribute("appid", appid);
            map.addAttribute("userid", userid);
            map.addAttribute("schema", request.getScheme());
            map.addAttribute("sessionid", sessionid);
            map.addAttribute("isInvite", isInvite);


            view.addObject("product", product);
            view.addObject("description", description);
            view.addObject("imgurl", imgurl);
            view.addObject("pid", pid);
            view.addObject("purl", purl);

            map.addAttribute("ip", Md5Utils.doubleMd5(request.getRemoteAddr()));

            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("traceid", traceid);
            }
            if (StringUtils.isNotBlank(exchange)) {
                map.addAttribute("exchange", exchange);
            }
            if (StringUtils.isNotBlank(title)) {
                map.addAttribute("title", title);
            }
            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("url", url);
            }

            map.addAttribute("cskefuport", request.getServerPort());

            /*
             * 先检查 invite不为空
             */
            if (invite != null) {
                logger.info("[index] invite id {}, orgi {}", invite.getId(), invite.getOrgi());
                map.addAttribute("orgi", invite.getOrgi());
                map.addAttribute("inviteData", invite);

                if (StringUtils.isNotBlank(aiid)) {
                    map.addAttribute("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    map.addAttribute("aiid", invite.getAiid());
                }

                AgentReport report;
                if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 绑定技能组
                    report = acdWorkMonitor.getAgentReport(invite.getConsult_skill_fixed_id(), invite.getOrgi());
                } else {
                    report = acdWorkMonitor.getAgentReport(invite.getOrgi());
                }

                boolean isLeavemsg = false;
                if (report.getAgents() == 0 ||
                        (sessionConfig.isHourcheck() &&
                                !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours()) &&
                                invite.isLeavemessage())) {
                    // 没有坐席在线，进入留言
                    isLeavemsg = true;
                    boolean isInWorkingHours = MainUtils.isInWorkingHours(sessionConfig.getWorkinghours());
                    map.addAttribute("isInWorkingHours", isInWorkingHours);
                    view = request(super.createRequestPageTempletResponse("/apps/im/leavemsg"));
                } else if (invite.isConsult_info()) {    //启用了信息收集，从Request获取， 或从 Cookies 里去
                    // 验证 OnlineUser 信息
                    if (contacts != null && StringUtils.isNotBlank(
                            contacts.getName())) {    //contacts用于传递信息，并不和 联系人表发生 关联，contacts信息传递给 Socket.IO，然后赋值给 AgentUser，最终赋值给 AgentService永久存储
                        //存入 Cookies
                        if (invite.isConsult_info_cookies()) {
                            Cookie name = new Cookie(
                                    "name", encryptor.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                            response.addCookie(name);
                            name.setMaxAge(3600);
                            if (StringUtils.isNotBlank(contacts.getPhone())) {
                                Cookie phonecookie = new Cookie(
                                        "phone", encryptor.encryption(URLEncoder.encode(contacts.getPhone(), "UTF-8")));
                                phonecookie.setMaxAge(3600);
                                response.addCookie(phonecookie);
                            }
                            if (StringUtils.isNotBlank(contacts.getEmail())) {
                                Cookie email = new Cookie(
                                        "email", encryptor.encryption(URLEncoder.encode(contacts.getEmail(), "UTF-8")));
                                email.setMaxAge(3600);
                                response.addCookie(email);
                            }

                            if (StringUtils.isNotBlank(contacts.getSkypeid())) {
                                Cookie skypeid = new Cookie(
                                        "skypeid", encryptor.encryption(
                                        URLEncoder.encode(contacts.getSkypeid(), "UTF-8")));
                                skypeid.setMaxAge(3600);
                                response.addCookie(skypeid);
                            }


                            if (StringUtils.isNotBlank(contacts.getMemo())) {
                                Cookie memo = new Cookie(
                                        "memo", encryptor.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                                memo.setMaxAge(3600);
                                response.addCookie(memo);
                            }
                        }
                    } else {
                        //从 Cookies里尝试读取
                        if (invite.isConsult_info_cookies()) {
                            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
                            contacts = new Contacts();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if (cookie != null && StringUtils.isNotBlank(
                                            cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
                                        if (cookie.getName().equals("name")) {
                                            contacts.setName(URLDecoder.decode(
                                                    encryptor.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("phone")) {
                                            contacts.setPhone(URLDecoder.decode(
                                                    encryptor.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("email")) {
                                            contacts.setEmail(URLDecoder.decode(
                                                    encryptor.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("memo")) {
                                            contacts.setMemo(URLDecoder.decode(
                                                    encryptor.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("skypeid")) {
                                            contacts.setSkypeid(
                                                    URLDecoder.decode(
                                                            encryptor.decryption(cookie.getValue()),
                                                            "UTF-8"));
                                        }
                                    }
                                }
                            }
                        }
                        if (contacts != null && StringUtils.isBlank(contacts.getName())) {
                            consult = false;
                            view = request(super.createRequestPageTempletResponse("/apps/im/collecting"));
                        }
                    }
                } else {
                    // TODO 该contacts的识别并不准确，因为不能关联
//                    contacts = OnlineUserProxy.processContacts(invite.getOrgi(), contacts, appid, userid);
                    String uid = (String) request.getSession().getAttribute("Sessionuid");
                    String sid = (String) request.getSession().getAttribute("Sessionsid");
                    String cid = (String) request.getSession().getAttribute("Sessioncid");

                    if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
                        Contacts contacts1 = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(
                                uid, sid, cid, false);
                        if (contacts1 != null) {
                            agentUserRepository.findOneByUseridAndOrgi(userid, orgi).ifPresent(p -> {
                                // 关联AgentService的联系人
                                if (StringUtils.isNotBlank(p.getAgentserviceid())) {
                                    agentServiceRepository.findById(p.getAgentserviceid())
                                            .ifPresent(it -> {
                                                it.setContactsid(contacts1.getId());
                                                agentServiceRepository.save(it);
                                            });
                                }

                                // 关联AgentUserContact的联系人
                                // NOTE: 如果该userid已经有了关联的Contact则忽略，继续使用之前的
                                Optional<AgentUserContacts> agentUserContactsOpt = agentUserContactsRes.findOneByUseridAndOrgi(
                                        userid, orgi);
                                if (!agentUserContactsOpt.isPresent()) {
                                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                                    agentUserContacts.setOrgi(orgi);
                                    agentUserContacts.setAppid(appid);
                                    agentUserContacts.setChannel(p.getChannel());
                                    agentUserContacts.setContactsid(contacts1.getId());
                                    agentUserContacts.setUserid(userid);
                                    agentUserContacts.setUsername(
                                            (String) request.getSession().getAttribute("Sessionusername"));
                                    agentUserContacts.setCreater(super.getUser(request).getId());
                                    agentUserContacts.setCreatetime(new Date());
                                    agentUserContactsRes.save(agentUserContacts);
                                }
                            });
                        }
                    }
                }

                if (StringUtils.isNotBlank(client)) {
                    map.addAttribute("client", client);
                }

                if (StringUtils.isNotBlank(skill)) {
                    map.addAttribute("skill", skill);
                }

                if (StringUtils.isNotBlank(agent)) {
                    map.addAttribute("agent", agent);
                }

                map.addAttribute("contacts", contacts);

                if (StringUtils.isNotBlank(type)) {
                    map.addAttribute("type", type);
                }
                IP ipdata = ipTools.findGeography(MainUtils.getIpAddr(request));
                map.addAttribute("skillGroups", OnlineUserProxy.organ(invite.getOrgi(), ipdata, invite, true));

                if (consult) {
                    if (contacts != null && StringUtils.isNotBlank(contacts.getName())) {
                        nickname = contacts.getName();
                    }

                    map.addAttribute("username", nickname);
                    boolean isChatbotAgentFirst = false;
                    boolean isEnableExchangeAgentType = false;

                    // 是否使用机器人客服
                    if (invite.isAi() && MainContext.hasModule(Constants.CSKEFU_MODULE_CHATBOT)) {
                        // 查找机器人
                        Optional<Chatbot> optional = chatbotRes.findById(invite.getAiid());
                        if (optional.isPresent()) {
                            Chatbot bot = optional.get();
                            // 判断是否接受访客切换坐席类型
                            isEnableExchangeAgentType = !StringUtils.equals(
                                    bot.getWorkmode(), Constants.CHATBOT_CHATBOT_ONLY);

                            // 判断是否机器人客服优先
                            if (((StringUtils.equals(
                                    ai, "true")) || (invite.isAifirst() && ai == null))) {
                                isChatbotAgentFirst = true;
                            }
                        }
                    }

                    final UserAgent ua = UserAgentUtil.parse(request.getHeader("User-Agent"));

                    map.addAttribute("exchange", isEnableExchangeAgentType);

                    if (isChatbotAgentFirst) {
                        // 机器人坐席
                        HashMap<String, String> chatbotConfig = new HashMap<>();
                        chatbotConfig.put("botname", invite.getAiname());
                        chatbotConfig.put("botid", invite.getAiid());
                        chatbotConfig.put("botwelcome", invite.getAimsg());
                        chatbotConfig.put("botfirst", Boolean.toString(invite.isAifirst()));
                        chatbotConfig.put("isai", Boolean.toString(invite.isAi()));

                        map.addAttribute("chatbotConfig", chatbotConfig);
                        view = request(super.createRequestPageTempletResponse("/apps/im/chatbot/index"));
                        if (ua.isMobile()
                            || StringUtils.isNotBlank(mobile)) {
                            view = request(super.createRequestPageTempletResponse(
                                    "/apps/im/chatbot/mobile"));        // 智能机器人 移动端
                        }
                    } else {
                        // 维持人工坐席的设定，检查是否进入留言
                        if (!isLeavemsg && (ua.isMobile()
                            || StringUtils.isNotBlank(mobile))) {
                            view = request(
                                    super.createRequestPageTempletResponse("/apps/im/mobile"));    // WebIM移动端。再次点选技能组？
                        }
                    }

                    map.addAttribute(
                            "chatMessageList", chatMessageRes.findByUsessionAndOrgi(userid, orgi, PageRequest.of(0, 20,
                                    Direction.DESC,
                                    "updatetime")));
                }
                view.addObject("commentList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC));
                view.addObject("commentItemList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_ITEM_DIC));
                view.addObject("welcomeAd", MainUtils.getPointAdv(MainContext.AdPosEnum.WELCOME.toString(), orgi));
                view.addObject("imageAd", MainUtils.getPointAdv(MainContext.AdPosEnum.IMAGE.toString(), orgi));

                // 确定"接受邀请"被处理后，通知浏览器关闭弹出窗口
                OnlineUserProxy.sendWebIMClients(userid, "accept");

                // 更新InviteRecord
                logger.info("[index] update inviteRecord for user {}", userid);
                final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
                Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
                        userid, orgi,
                        MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
                        threshold, PageRequest.of(0, 1, Direction.DESC, "createtime"));
                List<InviteRecord> content = inviteRecords.getContent();
                if (content.size() > 0) {
                    final InviteRecord record = content.get(0);
                    record.setUpdatetime(new Date());
                    record.setTraceid(traceid);
                    record.setTitle(title);
                    record.setUrl(url);
                    record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
                    record.setResult(MainContext.OnlineUserInviteStatus.ACCEPT.toString());
                    logger.info("[index] re-save inviteRecord id {}", record.getId());
                    inviteRecordRes.save(record);
                }

            } else {
                logger.info("[index] can not invite for appid {}, orgi {}", appid, orgi);
            }
        }

        logger.info("[index] return view");
        return view;
    }

    @RequestMapping("/text/{appid}")
    @Menu(type = "im", subtype = "index", access = true)
    public ModelAndView text(
            HttpServletRequest request,
            @PathVariable String appid,
            @Valid String traceid,
            @Valid String aiid,
            @Valid String exchange,
            @Valid String title,
            @Valid String url,
            @Valid String skill,
            @Valid String id,
            @Valid String userid,
            @Valid String agent,
            @Valid String name,
            @Valid String email,
            @Valid String phone,
            @Valid String ai,
            @Valid String orgi,
            @Valid String product,
            @Valid String description,
            @Valid String imgurl,
            @Valid String pid,
            @Valid String purl) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/text"));
        CousultInvite invite = OnlineUserProxy.consult(
                appid, StringUtils.isBlank(orgi) ? MainContext.SYSTEM_ORGI : orgi);

        view.addObject("hostname", request.getServerName());
        view.addObject("port", request.getServerPort());
        view.addObject("schema", request.getScheme());
        view.addObject("appid", appid);
        view.addObject("channelVisitorSeparate", channelWebIMVisitorSeparate);
        view.addObject("ip", Md5Utils.doubleMd5(request.getRemoteAddr()));

        if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 添加技能组ID
            // 忽略前端传入的技能组ID
            view.addObject("skill", invite.getConsult_skill_fixed_id());
        } else if (StringUtils.isNotBlank(skill)) {
            view.addObject("skill", skill);
        }

        if (StringUtils.isNotBlank(agent)) {
            view.addObject("agent", agent);
        }

        view.addObject("client", MainUtils.getUUID());
        view.addObject("sessionid", request.getSession().getId());

        view.addObject("id", id);
        if (StringUtils.isNotBlank(ai)) {
            view.addObject("ai", ai);
        }
        if (StringUtils.isNotBlank(exchange)) {
            view.addObject("exchange", exchange);
        }

        view.addObject("name", name);
        view.addObject("email", email);
        view.addObject("phone", phone);
        view.addObject("userid", userid);

        view.addObject("product", product);
        view.addObject("description", description);
        view.addObject("imgurl", imgurl);
        view.addObject("pid", pid);
        view.addObject("purl", purl);

        if (StringUtils.isNotBlank(traceid)) {
            view.addObject("traceid", traceid);
        }
        if (StringUtils.isNotBlank(title)) {
            view.addObject("title", title);
        }
        if (StringUtils.isNotBlank(traceid)) {
            view.addObject("url", url);
        }

        view.addObject("inviteData", invite);
        view.addObject("orgi", invite.getOrgi());
        view.addObject("appid", appid);

        if (StringUtils.isNotBlank(aiid)) {
            view.addObject("aiid", aiid);
        } else if (StringUtils.isNotBlank(invite.getAiid())) {
            view.addObject("aiid", invite.getAiid());
        }

        return view;
    }

    @RequestMapping("/leavemsg/save")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView leavemsgsave(@Valid String appid, @Valid LeaveMsg msg) {
        if (StringUtils.isNotBlank(appid)) {
            snsAccountRepository.findBySnsid(appid).ifPresent(p -> {
                CousultInvite invite = inviteRepository.findBySnsaccountidAndOrgi(appid, MainContext.SYSTEM_ORGI);
                // TODO 增加策略防止恶意刷消息
                //  List<LeaveMsg> msgList = leaveMsgRes.findByOrgiAndUserid(invite.getOrgi(), msg.getUserid());
                // if(msg!=null && msgList.size() == 0){
                if (msg != null) {
                    msg.setOrgi(invite.getOrgi());
                    msg.setChannel(p);
                    msg.setSnsId(appid);
                    leaveMsgRes.save(msg);
                }
            });
        }
        return request(super.createRequestPageTempletResponse("/apps/im/leavemsgsave"));
    }

    @RequestMapping("/refuse")
    @Menu(type = "im", subtype = "refuse", access = true)
    public void refuse(@Valid String orgi, @Valid String userid) {
        OnlineUserProxy.refuseInvite(userid);
        final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
        Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
                userid,
                orgi,
                MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
                threshold,
                PageRequest.of(
                        0,
                        1,
                        Direction.DESC,
                        "createtime"));
        List<InviteRecord> content = inviteRecords.getContent();
        if (content.size() > 0) {
            InviteRecord record = content.get(0);
            record.setUpdatetime(new Date());
            record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
            record.setResult(MainContext.OnlineUserInviteStatus.REFUSE.toString());
            inviteRecordRes.save(record);
        }
    }

    @RequestMapping("/satis")
    @Menu(type = "im", subtype = "satis", access = true)
    public void satis(@Valid AgentServiceSatis satis) {
        if (satis != null && StringUtils.isNotBlank(satis.getId())) {
            int count = agentServiceSatisRes.countById(satis.getId());
            if (count == 1) {
                if (StringUtils.isNotBlank(satis.getSatiscomment()) && satis.getSatiscomment().length() > 255) {
                    satis.setSatiscomment(satis.getSatiscomment().substring(0, 255));
                }
                satis.setSatisfaction(true);
                satis.setSatistime(new Date());
                agentServiceSatisRes.save(satis);
            }
        }
    }

    @RequestMapping("/image/upload")
    @Menu(type = "im", subtype = "image", access = true)
    public ModelAndView upload(
            ModelMap map, HttpServletRequest request,
            @RequestParam(value = "imgFile", required = false) MultipartFile multipart,
            @Valid String channel,
            @Valid String userid,
            @Valid String username,
            @Valid String appid,
            @Valid String orgi,
            @Valid String paste) throws IOException {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/upload"));
        final User logined = super.getUser(request);

        UploadStatus upload;
        String fileName;
//        String multipartLast = null;
//        if ( multipart != null && multipart.getOriginalFilename() != null ){
//            Number multipartLenght = multipart.getOriginalFilename().split("\\.").length - 1;
//            multipartLast = multipart.getOriginalFilename().split("\\.")[ multipartLenght.intValue()];
//        }

//        if (multipart != null &&
//                multipartLast != null
//                && multipart.getOriginalFilename().lastIndexOf(".") > 0
//                && StringUtils.isNotBlank(userid)) {
//            if(     multipartLast.equals("jpeg") || multipartLast.equals("jpg") || multipartLast.equals("bmp")
//                    || multipartLast.equals("png")  ){
        if (multipart != null
                && multipart.getOriginalFilename() != null
                && multipart.getOriginalFilename().lastIndexOf(".") > 0
                && StringUtils.isNotBlank(userid)) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                uploadDir.mkdirs();
            }


            String fileid = MainUtils.getUUID();
            StreamingFile sf = new StreamingFile();
            sf.setId(fileid);
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            if (multipart.getContentType() != null
                    && multipart.getContentType().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
                // 检查文件格式
                String invalid = StreamingFileUtil.getInstance().validate(
                        Constants.ATTACHMENT_TYPE_IMAGE, multipart.getOriginalFilename());
                if (invalid == null) {
                    fileName = "upload/" + fileid + "_original";
                    File imageFile = new File(path, fileName);
                    FileCopyUtils.copy(multipart.getBytes(), imageFile);
                    String thumbnailsFileName = "upload/" + fileid;
                    File thumbnail = new File(path, thumbnailsFileName);
                    MainUtils.processImage(thumbnail, imageFile);

                    //  存储数据库
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
                    streamingFileRepository.save(sf);
                    String fileUrl = "/res/image.html?id=" + fileid;
                    upload = new UploadStatus("0", fileUrl);

                    if (paste == null) {
                        if (StringUtils.isNotBlank(channel)) {
                            RichMediaUtils.uploadImageWithChannel(
                                    fileUrl, fileid, (int) multipart.getSize(), multipart.getName(), channel, userid,
                                    username, appid, orgi);
                        } else {
                            RichMediaUtils.uploadImage(
                                    fileUrl, fileid, (int) multipart.getSize(), multipart.getName(), userid);
                        }
                    }
                } else {
                    upload = new UploadStatus(invalid);
                }
            } else {
                String invalid = StreamingFileUtil.getInstance().validate(
                        Constants.ATTACHMENT_TYPE_FILE, multipart.getOriginalFilename());
                if (invalid == null) {
                    // 存储数据库
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    streamingFileRepository.save(sf);

                    // 存储到本地硬盘
                    String id = processAttachmentFile(multipart,
                            fileid, logined.getOrgi(), logined.getId());
                    upload = new UploadStatus("0", "/res/file.html?id=" + id);
                    String file = "/res/file.html?id=" + id;

                    File tempFile = new File(multipart.getOriginalFilename());
                    if (StringUtils.isNotBlank(channel)) {
                        RichMediaUtils.uploadFileWithChannel(
                                file, (int) multipart.getSize(), tempFile.getName(), channel, userid, username, appid,
                                orgi, id);
                    } else {
                        RichMediaUtils.uploadFile(file, (int) multipart.getSize(), tempFile.getName(), userid, id);
                    }
                } else {
                    upload = new UploadStatus(invalid);
                }
            }
        } else {
            upload = new UploadStatus("请选择文件");
        }
//        }else {
//                upload = new UploadStatus("请上传格式为jpg，png，jpeg，bmp类型图片");
//            }
//        } else {
//            upload = new UploadStatus("请上传格式为jpg，png，jpeg，bmp类型图片");
//        }
        map.addAttribute("upload", upload);
        return view;
    }


    private String processAttachmentFile(
            final MultipartFile file,
            final String fileid,
            final String orgi,
            final String creator) throws IOException {
        String id = null;

        if (file.getSize() > 0) {            //文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
            AttachmentFile attachmentFile = new AttachmentFile();
            attachmentFile.setCreater(creator);
            attachmentFile.setOrgi(orgi);
            attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
            attachmentFile.setFilelength((int) file.getSize());
            if (file.getContentType() != null && file.getContentType().length() > 255) {
                attachmentFile.setFiletype(file.getContentType().substring(0, 255));
            } else {
                attachmentFile.setFiletype(file.getContentType());
            }
            String originalFilename = URLDecoder.decode(Objects.requireNonNull(file.getOriginalFilename()), "utf-8");
            File uploadFile = new File(originalFilename);
            if (uploadFile.getName().length() > 255) {
                attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
            } else {
                attachmentFile.setTitle(uploadFile.getName());
            }
            if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().contains("image")) {
                attachmentFile.setImage(true);
            }
            attachmentFile.setFileid(fileid);
            attachementRes.save(attachmentFile);
            FileUtils.writeByteArrayToFile(new File(path, "upload/" + fileid), file.getBytes());
            id = attachmentFile.getId();
        }
        return id;
    }
}
