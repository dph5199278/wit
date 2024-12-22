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
package com.cs.wit.proxy;

import com.cs.wit.acd.ACDServiceRouter;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.model.AiConfig;
import com.cs.wit.model.AreaType;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.model.KnowledgeType;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.OnlineUserHis;
import com.cs.wit.model.Organ;
import com.cs.wit.model.OrgiSkillRel;
import com.cs.wit.model.SceneType;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.Template;
import com.cs.wit.model.Tenant;
import com.cs.wit.model.Topic;
import com.cs.wit.model.User;
import com.cs.wit.model.UserTraceHistory;
import com.cs.wit.persistence.interfaces.DataExchangeInterface;
import com.cs.wit.persistence.repository.ConsultInviteRepository;
import com.cs.wit.persistence.repository.OnlineUserHisRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.OrgiSkillRelRepository;
import com.cs.wit.persistence.repository.TenantRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.persistence.repository.UserTraceRepository;
import com.cs.wit.socketio.message.OtherMessageItem;
import com.cs.wit.util.HttpClientUtil;
import com.cs.wit.util.OnlineUserUtils;
import com.cs.wit.util.WebIMClient;
import com.cs.wit.util.WebSseEmitterClient;
import com.cs.wit.util.ip.IP;
import com.cs.wit.util.ip.IPTools;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.dromara.hutool.http.useragent.UserAgent;
import org.dromara.hutool.http.useragent.UserAgentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public class OnlineUserProxy {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserProxy.class);
    public static WebSseEmitterClient webIMClients = new WebSseEmitterClient();
    public static ObjectMapper objectMapper = new ObjectMapper();

    private static OnlineUserRepository onlineUserRes;
    private static UserRepository userRes;
    private static Cache cache;
    private static ConsultInviteRepository consultInviteRes;
    private static OnlineUserHisRepository onlineUserHisRes;
    private static UserTraceRepository userTraceRes;
    private static OrgiSkillRelRepository orgiSkillRelRes;
    private static UserProxy userProxy;

    /**
     *
     */
    @Nullable
    public static CousultInvite consult(final String snsid, final String orgi) {
//        logger.info("[consult] snsid {}, orgi {}", snsid, orgi);
        CousultInvite consultInvite = MainContext.getCache().findOneConsultInviteBySnsidAndOrgi(snsid, orgi);
        if (consultInvite == null) {
            consultInvite = getConsultInviteRes().findBySnsaccountidAndOrgi(snsid, orgi);
            if (consultInvite != null) {
                getCache().putConsultInviteByOrgi(orgi, consultInvite);
            }
        }
        return consultInvite;
    }


    /**
     * 在Cache中查询OnlineUser，或者从数据库中根据UserId，Orgi和Invite查询
     */
    public static OnlineUser onlineuser(String userid, String orgi) {
        // 从Cache中查找
        OnlineUser onlineUser = getCache().findOneOnlineUserByUserIdAndOrgi(userid, orgi);

        if (onlineUser == null) {
            logger.info(
                    "[onlineuser] !!! fail to resolve user {} with both cache and database, maybe this user is first presents.",
                    userid);
        }

        return onlineUser;
    }


    /**
     *
     */
    public static List<Organ> organ(
            String orgi, final IP ipdata,
            final CousultInvite invite, boolean isJudgeShare) {
        String origOrig = orgi;
        boolean isShare = false;
        if (isJudgeShare) {
            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
                orgi = MainContext.SYSTEM_ORGI;
                isShare = true;
            }
        }
        List<Organ> skillGroups = getCache().findOneSystemByIdAndOrgi(Constants.CACHE_SKILL + origOrig, origOrig);
        if (skillGroups == null) {
            OrganRepository service = MainContext.getContext().getBean(OrganRepository.class);
            skillGroups = service.findByOrgiAndSkill(orgi, true);
            // 租户共享时 查出该租住要显的绑定的技能组
            if (isShare && !(StringUtils.equals(
                    MainContext.SYSTEM_ORGI, (invite == null ? origOrig : invite.getOrgi())))) {
                OrgiSkillRelRepository orgiSkillRelService = MainContext.getContext().getBean(
                        OrgiSkillRelRepository.class);
                List<OrgiSkillRel> orgiSkillRelList;
                orgiSkillRelList = orgiSkillRelService.findByOrgi((invite == null ? origOrig : invite.getOrgi()));
                List<Organ> skillTempList = new ArrayList<>();
                if (!orgiSkillRelList.isEmpty()) {
                    for (Organ organ : skillGroups) {
                        for (OrgiSkillRel rel : orgiSkillRelList) {
                            if (organ.getId().equals(rel.getSkillid())) {
                                skillTempList.add(organ);
                            }
                        }
                    }
                }
                skillGroups = skillTempList;
            }

            if (skillGroups.size() > 0) {
                getCache().putSystemListByIdAndOrgi(Constants.CACHE_SKILL + origOrig, origOrig, skillGroups);
            }
        }

        if (ipdata == null && invite == null) {
            return skillGroups;
        }

        List<Organ> regOrganList = new ArrayList<>();
        if (ipdata == null) {
            return regOrganList;
        }
        for (Organ organ : skillGroups) {
            if (StringUtils.isNotBlank(organ.getArea())) {
                if (organ.getArea().contains(ipdata.getProvince()) || organ.getArea().contains(ipdata.getCity())) {
                    regOrganList.add(organ);
                }
            } else {
                regOrganList.add(organ);
            }
        }
        return regOrganList;
    }


    /**
     *
     */
    public static List<Organ> organ(String orgi, boolean isJudgeShare) {
        return organ(orgi, null, null, isJudgeShare);
    }

    private static List<AreaType> getAreaTypeList(String area, List<AreaType> areaTypeList) {
        List<AreaType> atList = new ArrayList<>();
        if (areaTypeList != null && areaTypeList.size() > 0) {
            for (AreaType areaType : areaTypeList) {
                if (StringUtils.isNotBlank(area) && area.contains(areaType.getId())) {
                    atList.add(areaType);
                }
            }
        }
        return atList;
    }

    public static List<Topic> topic(List<KnowledgeType> topicTypeList, List<Topic> topicList) {
        List<Topic> tempTopicList = new ArrayList<>();
        if (topicList != null) {
            for (Topic topic : topicList) {
                if (StringUtils.isBlank(topic.getCate()) || Constants.DEFAULT_TYPE.equals(
                        topic.getCate()) || getTopicType(topic.getCate(), topicTypeList) != null) {
                    tempTopicList.add(topic);
                }
            }
        }
        return tempTopicList;
    }

    /**
     * 根据热点知识找到 非空的 分类
     */
    public static List<KnowledgeType> filterTopicType(List<KnowledgeType> topicTypeList, List<Topic> topicList) {
        List<KnowledgeType> tempTopicTypeList = new ArrayList<>();
        if (topicTypeList != null) {
            for (KnowledgeType knowledgeType : topicTypeList) {
                boolean hasTopic = false;
                for (Topic topic : topicList) {
                    if (knowledgeType.getId().equals(topic.getCate())) {
                        hasTopic = true;
                        break;
                    }
                }
                if (hasTopic) {
                    tempTopicTypeList.add(knowledgeType);
                }
            }
        }
        return tempTopicTypeList;
    }

    /**
     * 找到知识点对应的 分类
     */
    private static KnowledgeType getTopicType(String cate, List<KnowledgeType> topicTypeList) {
        KnowledgeType kt = null;
        for (KnowledgeType knowledgeType : topicTypeList) {
            if (knowledgeType.getId().equals(cate)) {
                kt = knowledgeType;
                break;
            }
        }
        return kt;
    }


    /**
     *
     */
    public static List<User> agents(String orgi, boolean isJudgeShare) {
        String origOrig = orgi;
        boolean isShare = false;
        if (isJudgeShare) {
            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
                orgi = MainContext.SYSTEM_ORGI;
                isShare = true;
            }
        }
        List<User> agentList = getCache().findOneSystemByIdAndOrgi(Constants.CACHE_AGENT + origOrig, origOrig);
        List<User> agentTempList;
        if (agentList == null) {
            agentList = getUserRes().findByOrgiAndAgentAndDatastatus(orgi, true, false);
            agentTempList = new ArrayList<>();
            // 共享的话 查出绑定的组织
            if (isShare) {
                List<OrgiSkillRel> orgiSkillRelList = getOrgiSkillRelRes().findByOrgi(origOrig);
                if (!orgiSkillRelList.isEmpty()) {
                    for (User user : agentList) {
                        // TODO 此处的查询处理比较多，应使用缓存
                        // 一个用户可隶属于多个组织
                        getUserProxy().attachOrgansPropertiesForUser(user);
                        for (OrgiSkillRel rel : orgiSkillRelList) {
                            if (user.getOrgans().size() > 0 && user.inAffiliates(rel.getSkillid())) {
                                agentTempList.add(user);
                            }
                        }
                    }
                }
                agentList = agentTempList;
            }
            if (agentList.size() > 0) {
                getCache().putSystemListByIdAndOrgi(Constants.CACHE_AGENT + origOrig, origOrig, agentList);
            }
        }
        return agentList;
    }


    public static void clean(final String orgi) {
        // 共享 查出机构下所有产品
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
            TenantRepository tenantRes = MainContext.getContext().getBean(TenantRepository.class);
            Tenant tenant = tenantRes.findById(orgi).orElse(null);
            if (tenant != null) {
                List<Tenant> tenants = tenantRes.findByOrgid(tenant.getOrgid());
                if (!tenants.isEmpty()) {
                    for (Tenant t : tenants) {
                        String orgiT = t.getId();
                        getCache().deleteSystembyIdAndOrgi(Constants.CACHE_SKILL + orgiT, orgiT);
                        getCache().deleteSystembyIdAndOrgi(Constants.CACHE_AGENT + orgiT, orgiT);
                    }
                }
            }
        } else {
            getCache().deleteSystembyIdAndOrgi(Constants.CACHE_SKILL + orgi, orgi);
            getCache().deleteSystembyIdAndOrgi(Constants.CACHE_AGENT + orgi, orgi);
        }
    }


    /**
     * 创建OnlineUser并上线
     * 根据user判断追踪，在浏览器里，用fingerprint2生成的ID作为唯一标识
     */
    public static OnlineUser online(
            final IPTools ipTools,
            final User user,
            final String orgi,
            final String sessionid,
            final String optype,
            final HttpServletRequest request,
            final String channel,
            final String appid,
            final Contacts contacts,
            final CousultInvite invite) {
//        logger.info(
//                "[online] user {}, orgi {}, sessionid {}, optype {}, channel {}", user.getId(), orgi, sessionid, optype,
//                channel);
        OnlineUser onlineUser = null;
        final Date now = new Date();
        if (invite != null) {
            // resolve user from cache or db.
            onlineUser = onlineuser(user.getId(), orgi);

            if (onlineUser == null) {
                logger.info("[online] create new online user.");

                final String userAgentString = request.getHeader("User-Agent");
                final UserAgent client = UserAgentUtil.parse(userAgentString);

                onlineUser = new OnlineUser();
                onlineUser.setId(user.getId());
                onlineUser.setCreater(user.getId());
                onlineUser.setUsername(user.getUsername());
                onlineUser.setCreatetime(now);
                onlineUser.setUpdatetime(now);
                onlineUser.setUpdateuser(user.getUsername());
                onlineUser.setSessionid(sessionid);

                if (contacts != null) {
                    onlineUser.setContactsid(contacts.getId());
                }

                onlineUser.setOrgi(orgi);
                onlineUser.setChannel(channel);

                // 从Server session信息中查找该用户相关的历史信息
                String cookie = getCookie(request, "R3GUESTUSEKEY");
                if ((StringUtils.isBlank(cookie))
                        || (StringUtils.equals(user.getSessionid(), cookie))) {
                    onlineUser.setOlduser("0");
                } else {
                    // 之前有session的访客
                    onlineUser.setOlduser("1");
                }
                onlineUser.setMobile(client.isMobile() ? "1" : "0");

                // onlineUser.setSource(user.getId());

                String url = request.getHeader("referer");
                onlineUser.setUrl(url);
                if (StringUtils.isNotBlank(url)) {
                    try {
                        URL referer = new URL(url);
                        onlineUser.setSource(referer.getHost());
                    } catch (MalformedURLException e) {
                        logger.info("[online] error when parsing URL", e);
                    }
                }
                onlineUser.setAppid(appid);
                onlineUser.setUserid(user.getId());
                onlineUser.setUsername(user.getUsername());

                if (StringUtils.isNotBlank(request.getParameter("title"))) {
                    String title = request.getParameter("title");
                    if (title.length() > 255) {
                        onlineUser.setTitle(title.substring(0, 255));
                    } else {
                        onlineUser.setTitle(title);
                    }
                }

                onlineUser.setLogintime(now);

                // 地理信息
                String ip = MainUtils.getIpAddr(request);
                onlineUser.setIp(ip);
                IP ipdata = ipTools.findGeography(ip);
                onlineUser.setCountry(ipdata.getCountry());
                onlineUser.setProvince(ipdata.getProvince());
                onlineUser.setCity(ipdata.getCity());
                onlineUser.setIsp(ipdata.getIsp());
                onlineUser.setRegion(ipdata.toString() + "（"
                        + ip + "）");

                onlineUser.setDatestr(new SimpleDateFormat("yyyMMdd")
                        .format(now));

                onlineUser.setHostname(ip);
                onlineUser.setSessionid(sessionid);
                onlineUser.setOptype(optype);
                onlineUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());

                // 浏览器信息
                onlineUser.setOpersystem(client.getOs().getName());
                onlineUser.setBrowser(client.getBrowser().getName());
                onlineUser.setUseragent(userAgentString);

                logger.info("[online] new online user is created but not persisted.");
            } else {
                // 从DB或缓存找到OnlineUser
                // 刷新创建时间
                onlineUser.setCreatetime(now);
                if ((StringUtils.isNotBlank(onlineUser.getSessionid()) && !StringUtils.equals(
                        onlineUser.getSessionid(), sessionid)) ||
                        !StringUtils.equals(
                                MainContext.OnlineUserStatusEnum.ONLINE.toString(), onlineUser.getStatus())) {
                    // 当新的session与从DB或缓存查找的session不一致时，或者当数据库或缓存的OnlineUser状态不是ONLINE时
                    // 代表该用户登录了新的Session或从离线变为上线！

                    // 设置用户到上线
                    onlineUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());
                    // 设置渠道
                    onlineUser.setChannel(channel);
                    onlineUser.setAppid(appid);
                    // 刷新更新时间
                    onlineUser.setUpdatetime(now);
                    if (StringUtils.isNotBlank(onlineUser.getSessionid()) && !StringUtils.equals(
                            onlineUser.getSessionid(), sessionid)) {
                        onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                        // 设置新的session信息
                        onlineUser.setSessionid(sessionid);
                        // 设置更新时间
                        onlineUser.setLogintime(now);
                        // 重置邀请次数
                        onlineUser.setInvitetimes(0);
                    }
                }

                // 处理联系人关联信息
                if (contacts != null) {
                    // 当关联到联系人
                    if (StringUtils.isNotBlank(contacts.getId()) && StringUtils.isNotBlank(
                            contacts.getName()) && (StringUtils.isBlank(
                            onlineUser.getContactsid()) || !contacts.getName().equals(onlineUser.getUsername()))) {
                        if (StringUtils.isBlank(onlineUser.getContactsid())) {
                            onlineUser.setContactsid(contacts.getId());
                        }
                        if (!contacts.getName().equals(onlineUser.getUsername())) {
                            onlineUser.setUsername(contacts.getName());
                        }
                        onlineUser.setUpdatetime(now);
                    }
                }

                if (StringUtils.isBlank(onlineUser.getUsername()) && StringUtils.isNotBlank(user.getUsername())) {
                    onlineUser.setUseragent(user.getUsername());
                    onlineUser.setUpdatetime(now);
                }
            }

            if (invite.isRecordhis() && StringUtils.isNotBlank(request.getParameter("traceid"))) {
                UserTraceHistory trace = new UserTraceHistory();
                trace.setId(request.getParameter("traceid"));
                trace.setTitle(request.getParameter("title"));
                trace.setUrl(request.getParameter("url"));
                trace.setOrgi(invite.getOrgi());
                trace.setUpdatetime(new Date());
                trace.setUsername(onlineUser.getUsername());
                getUserTraceRes().save(trace);
            }

            // 完成获取及更新OnlineUser, 将信息加入缓存
            if (StringUtils.isNotBlank(onlineUser.getUserid())) {
//                logger.info(
//                        "[online] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
//                        onlineUser.getStatus(), onlineUser.getInvitestatus());
                // 存储到缓存及数据库
                getOnlineUserRes().save(onlineUser);
            }
        }
        return onlineUser;
    }

    /**
     *
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie data = null;
        if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(key)) {
                    data = cookie;
                    break;
                }
            }
        }
        return data != null ? data.getValue() : null;
    }

    /**
     *
     */
    public static void offline(String user, String orgi) {
        if (MainContext.getContext() != null) {
            OnlineUser onlineUser = getCache().findOneOnlineUserByUserIdAndOrgi(user, orgi);
            if (onlineUser != null) {
                onlineUser.setStatus(MainContext.OnlineUserStatusEnum.OFFLINE.toString());
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                onlineUser.setBetweentime((int) (new Date().getTime() - onlineUser.getLogintime().getTime()));
                onlineUser.setUpdatetime(new Date());
                getOnlineUserRes().save(onlineUser);

                final OnlineUserHis his = getOnlineUserHisRes().findOneBySessionidAndOrgi(
                        onlineUser.getSessionid(), onlineUser.getOrgi()).orElseGet(OnlineUserHis::new);
                MainUtils.copyProperties(onlineUser, his);
                his.setDataid(onlineUser.getId());
                getOnlineUserHisRes().save(his);
            }
            getCache().deleteOnlineUserByIdAndOrgi(user, orgi);
        }
    }

    /**
     * 设置onlineUser为离线
     */
    public static void offline(OnlineUser onlineUser) {
        if (onlineUser != null) {
            offline(onlineUser.getId(), onlineUser.getOrgi());
        }
    }

    /**
     *
     */
    public static void refuseInvite(final String user) {
        OnlineUser onlineUser = getOnlineUserRes().findById(user).orElse(null);
        if (onlineUser != null) {
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.REFUSE.toString());
            onlineUser.setRefusetimes(onlineUser.getRefusetimes() + 1);
            getOnlineUserRes().save(onlineUser);
        }
    }

    public static String unescape(String src) {
        StringBuilder tmp = new StringBuilder();
        try {
            tmp.append(java.net.URLDecoder.decode(src, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tmp.toString();
    }

    public static String getKeyword(String url) {
        Map<String, String[]> values = new HashMap<>();
        try {
            OnlineUserUtils.parseParameters(values, url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder strb = new StringBuilder();
        String[] data = values.get("q");
        if (data != null) {
            for (String v : data) {
                strb.append(v);
            }
        }
        return strb.toString();
    }

    public static String getSource(String url) {
        String source = "0";
        try {
            URL addr = new URL(url);
            source = addr.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * 发送邀请
     */
    public static void sendWebIMClients(String userid, String msg) throws Exception {
//        logger.info("[sendWebIMClients] userId {}, msg {}", userid, msg);
        List<WebIMClient> clients = OnlineUserProxy.webIMClients.getClients(userid);

        if (clients != null && clients.size() > 0) {
            for (WebIMClient client : clients) {
                try {
                    client.getSse().send(SseEmitter.event().reconnectTime(0).data(msg));
//                    logger.info("[sendWebIMClients] sent done with client {}", client.getClient());
                } catch (Exception ex) {
                    // 一些连接断开在服务器端没有清除
//                    logger.info("[sendWebIMClients] lost connection", ex);
                    // cleanup connections hold in server side
                    OnlineUserProxy.webIMClients.removeClient(userid, client.getClient(), false);
                } finally {
                    client.getSse().complete();
                }
            }
        }
    }

    public static void resetHotTopic(DataExchangeInterface dataExchange, String orgi, String aiid) {
        getCache().deleteSystembyIdAndOrgi("xiaoeTopic", orgi);
        cacheHotTopic(dataExchange, orgi, aiid);
    }

    @SuppressWarnings("unchecked")
    public static List<Topic> cacheHotTopic(DataExchangeInterface dataExchange, String orgi, String aiid) {
        List<Topic> topicList;
        if ((topicList = getCache().findOneSystemListByIdAndOrgi("xiaoeTopic", orgi)) == null) {
            topicList = (List<Topic>) dataExchange.getListDataByIdAndOrgi(aiid, null, orgi);
            getCache().putSystemListByIdAndOrgi("xiaoeTopic", orgi, topicList);
        }
        return topicList;
    }

    public static void resetHotTopicType(DataExchangeInterface dataExchange, String orgi, String aiid) {
        if (getCache().existSystemByIdAndOrgi("xiaoeTopicType" + "." + orgi, orgi)) {
            getCache().deleteSystembyIdAndOrgi("xiaoeTopicType" + "." + orgi, orgi);
        }
        cacheHotTopicType(dataExchange, orgi, aiid);
    }

    @SuppressWarnings("unchecked")
    public static List<KnowledgeType> cacheHotTopicType(DataExchangeInterface dataExchange, String orgi, String aiid) {
        List<KnowledgeType> topicTypeList;
        if ((topicTypeList = getCache().findOneSystemListByIdAndOrgi("xiaoeTopicType" + "." + orgi, orgi)) == null) {
            topicTypeList = (List<KnowledgeType>) dataExchange.getListDataByIdAndOrgi(aiid, null, orgi);
            getCache().putSystemListByIdAndOrgi("xiaoeTopicType" + "." + orgi, orgi, topicTypeList);
        }
        return topicTypeList;
    }

    @SuppressWarnings("unchecked")
    public static List<SceneType> cacheSceneType(DataExchangeInterface dataExchange, String orgi) {
        List<SceneType> sceneTypeList;
        if ((sceneTypeList = getCache().findOneSystemListByIdAndOrgi("xiaoeSceneType", orgi)) == null) {
            sceneTypeList = (List<SceneType>) dataExchange.getListDataByIdAndOrgi(null, null, orgi);
            getCache().putSystemListByIdAndOrgi("xiaoeSceneType", orgi, sceneTypeList);
        }
        return sceneTypeList;
    }

    public static boolean filterSceneType(String cate, String orgi, IP ipdata) {
        boolean result = false;
        List<SceneType> sceneTypeList = cacheSceneType(
                (DataExchangeInterface) MainContext.getContext().getBean("scenetype"), orgi);
        List<AreaType> areaTypeList = getCache().findOneSystemListByIdAndOrgi(
                Constants.CSKEFU_SYSTEM_AREA, MainContext.SYSTEM_ORGI);
        if (sceneTypeList != null && cate != null && !Constants.DEFAULT_TYPE.equals(cate)) {
            for (SceneType sceneType : sceneTypeList) {
                if (cate.equals(sceneType.getId())) {
                    if (StringUtils.isNotBlank(sceneType.getArea())) {
                        if (ipdata != null) {
                            //找到技能组配置的地区信息
                            List<AreaType> atList = getAreaTypeList(
                                    sceneType.getArea(), areaTypeList);
                            for (AreaType areaType : atList) {
                                if (areaType.getArea().contains(ipdata.getProvince()) || areaType.getArea().contains(ipdata.getCity())) {
                                    result = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        result = true;
                    }
                }
                if (result) {
                    break;
                }
            }
        } else {
            result = true;
        }
        return result;
    }

    public static List<OtherMessageItem> search(String q, String orgi, User user) throws IOException, TemplateException {
        List<OtherMessageItem> otherMessageItemList = null;
        String param = "";
        SessionConfig sessionConfig = ACDServiceRouter.getAcdPolicyService().initSessionConfig(
                orgi);
        if (StringUtils.isNotBlank(sessionConfig.getOqrsearchurl())) {
            Template templet = MainUtils.getTemplate(sessionConfig.getOqrsearchinput());
            Map<String, Object> values = new HashMap<>();
            values.put("q", q);
            values.put("user", user);
            param = MainUtils.getTemplet(templet.getTemplettext(), values);
        }
        String result = HttpClientUtil.doPost(sessionConfig.getOqrsearchurl(), param), text = null;
        if (StringUtils.isNotBlank(result) && StringUtils.isNotBlank(
                sessionConfig.getOqrsearchoutput()) && !result.equals("error")) {
            Template templet = MainUtils.getTemplate(sessionConfig.getOqrsearchoutput());
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonData = objectMapper.readValue(result, Map.class);
            Map<String, Object> values = new HashMap<>();
            values.put("q", q);
            values.put("user", user);
            values.put("data", jsonData);
            text = MainUtils.getTemplet(templet.getTemplettext(), values);
        }
        if (StringUtils.isNotBlank(text)) {
            JavaType javaType = getCollectionType(ArrayList.class, OtherMessageItem.class);
            otherMessageItemList = objectMapper.readValue(text, javaType);
        }
        return otherMessageItemList;
    }

    public static OtherMessageItem suggestdetail(AiConfig aiCofig, String id, User user) throws IOException, TemplateException {
        OtherMessageItem otherMessageItem = null;
        String param = "";
        if (StringUtils.isNotBlank(aiCofig.getOqrdetailinput())) {
            Template templet = MainUtils.getTemplate(aiCofig.getOqrdetailinput());
            Map<String, Object> values = new HashMap<>();
            values.put("id", id);
            values.put("user", user);
            param = MainUtils.getTemplet(templet.getTemplettext(), values);
        }
        if (StringUtils.isNotBlank(aiCofig.getOqrdetailurl())) {
            String result = HttpClientUtil.doPost(aiCofig.getOqrdetailurl(), param), text = null;
            if (StringUtils.isNotBlank(aiCofig.getOqrdetailoutput()) && !result.equals("error")) {
                Template templet = MainUtils.getTemplate(aiCofig.getOqrdetailoutput());
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonData = objectMapper.readValue(result, Map.class);
                Map<String, Object> values = new HashMap<>();
                values.put("id", id);
                values.put("user", user);
                values.put("data", jsonData);
                text = MainUtils.getTemplet(templet.getTemplettext(), values);
            }
            if (StringUtils.isNotBlank(text)) {
                otherMessageItem = objectMapper.readValue(text, OtherMessageItem.class);
            }
        }
        return otherMessageItem;
    }

    public static OtherMessageItem detail(String id, String orgi, User user) throws IOException, TemplateException {
        OtherMessageItem otherMessageItem = null;
        String param = "";
        SessionConfig sessionConfig = ACDServiceRouter.getAcdPolicyService().initSessionConfig(
                orgi);
        if (StringUtils.isNotBlank(sessionConfig.getOqrdetailinput())) {
            Template templet = MainUtils.getTemplate(sessionConfig.getOqrdetailinput());
            Map<String, Object> values = new HashMap<>();
            values.put("id", id);
            values.put("user", user);
            param = MainUtils.getTemplet(templet.getTemplettext(), values);
        }
        if (StringUtils.isNotBlank(sessionConfig.getOqrdetailurl())) {
            String result = HttpClientUtil.doPost(sessionConfig.getOqrdetailurl(), param), text = null;
            if (StringUtils.isNotBlank(sessionConfig.getOqrdetailoutput()) && !result.equals("error")) {
                Template templet = MainUtils.getTemplate(sessionConfig.getOqrdetailoutput());
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonData = objectMapper.readValue(result, Map.class);
                Map<String, Object> values = new HashMap<>();
                values.put("id", id);
                values.put("user", user);
                values.put("data", jsonData);
                text = MainUtils.getTemplet(templet.getTemplettext(), values);
            }
            if (StringUtils.isNotBlank(text)) {
                otherMessageItem = objectMapper.readValue(text, OtherMessageItem.class);
            }
        }
        return otherMessageItem;
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


    /**
     * 创建Skype联系人的onlineUser记录
     */
    public static OnlineUser createNewOnlineUserWithContactAndChannel(final Contacts contact, final User logined, final String channel) {
        final Date now = new Date();
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setId(MainUtils.getUUID());
        onlineUser.setUserid(onlineUser.getId());
        onlineUser.setLogintime(now);
        onlineUser.setUpdateuser(logined.getId());
        onlineUser.setContactsid(contact.getId());
        onlineUser.setUsername(contact.getName());
        onlineUser.setChannel(channel);
        onlineUser.setCity(contact.getCity());
        onlineUser.setOrgi(logined.getOrgi());
        onlineUser.setCreater(logined.getId());

        logger.info(
                "[createNewOnlineUserWithContactAndChannel] onlineUser id {}, userId {}", onlineUser.getId(),
                onlineUser.getUserid());
        // TODO 此处没有创建 onlineUser 的 appid
        getOnlineUserRes().save(onlineUser);
        return onlineUser;

    }


    private static OnlineUserRepository getOnlineUserRes() {
        if (onlineUserRes == null) {
            onlineUserRes = MainContext.getContext().getBean(OnlineUserRepository.class);
        }
        return onlineUserRes;

    }

    private static Cache getCache() {
        if (cache == null) {
            cache = MainContext.getCache();
        }

        return cache;
    }

    private static ConsultInviteRepository getConsultInviteRes() {
        if (consultInviteRes == null) {
            consultInviteRes = MainContext.getContext().getBean(ConsultInviteRepository.class);
        }
        return consultInviteRes;
    }

    private static OnlineUserHisRepository getOnlineUserHisRes() {
        if (onlineUserHisRes == null) {
            onlineUserHisRes = MainContext.getContext().getBean(OnlineUserHisRepository.class);
        }
        return onlineUserHisRes;
    }

    private static UserTraceRepository getUserTraceRes() {
        if (userTraceRes == null) {
            userTraceRes = MainContext.getContext().getBean(UserTraceRepository.class);
        }
        return userTraceRes;
    }

    private static UserRepository getUserRes() {
        if (userRes == null) {
            userRes = MainContext.getContext().getBean(UserRepository.class);
        }
        return userRes;
    }

    private static OrgiSkillRelRepository getOrgiSkillRelRes() {
        if (orgiSkillRelRes == null) {
            orgiSkillRelRes = MainContext.getContext().getBean(OrgiSkillRelRepository.class);
        }
        return orgiSkillRelRes;
    }

    public static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }
}
