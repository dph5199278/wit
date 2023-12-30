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
package com.cs.wit.controller;

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.basic.Viewport;
import com.cs.wit.basic.auth.AuthToken;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.api.QueryParams;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.StreamingFile;
import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.Tenant;
import com.cs.wit.model.User;
import com.cs.wit.persistence.blob.JpaBlobHelper;
import com.cs.wit.persistence.repository.StreamingFileRepository;
import com.cs.wit.persistence.repository.TenantRepository;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@SessionAttributes
@NoArgsConstructor
public class Handler {
    public final static int PAGE_SIZE_BG = 1;
    public final static int PAGE_SIZE_TW = 20;
    public final static int PAGE_SIZE_HA = 100;
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    @Autowired
    private TenantRepository tenantRes;
    @Autowired
    private JpaBlobHelper jpaBlobHelper;
    @Autowired
    private StreamingFileRepository streamingFileRes;
    @Autowired
    private Cache cache;
    @Autowired
    private AuthToken authToken;
    private long startTime = System.currentTimeMillis();

    public User getUser(HttpServletRequest request) {
        User user = (User) request.getSession(true).getAttribute(Constants.USER_SESSION_NAME);
        if (user == null) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.isBlank(authorization) && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("authorization")) {
                        authorization = cookie.getValue();
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(authorization)) {
                user = authToken.findUserByAuth(authorization);
            }
            if (user == null) {
                user = new User();
                user.setId(MainUtils.getContextID(request.getSession().getId()));
                user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                user.setOrgi(MainContext.SYSTEM_ORGI);
                user.setSessionid(user.getId());
            }
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    /**
     * 构建ElasticSearch基于部门查询的Filter
     */
    public boolean esOrganFilter(final HttpServletRequest request) throws CSKefuException {
        // 组合部门条件
        User u = getUser(request);
        if (u == null) {
            throw new CSKefuException("[esOrganFilter] 未能获取到登录用户。");
        } else if (u.isAdmin()) {
            // 管理员, 查看任何数据
            return true;
            // } else {
            // 用户在部门中，通过部门过滤数据
//            String[] values = u.getAffiliates().toArray(new String[u.getAffiliates().size()]);
//            boolQueryBuilder.filter(termsQuery("organ", values));
            // 不对contacts进行过滤，普通用户也可以查看该租户的任何数据
//            return true;
        }
        return true;
    }

    /**
     * 创建或从HTTP会话中查找到访客的User对象，该对象不在数据库中，属于临时会话。
     * 这个User很可能是打开一个WebIM访客聊天控件，随机生成用户名，之后和Contact关联
     * 这个用户可能关联一个OnlineUser，如果开始给TA分配坐席
     */
    public User getIMUser(HttpServletRequest request, String userid, String nickname) {
        User user = (User) request.getSession(true).getAttribute(Constants.IM_USER_SESSION_NAME);
        if (user == null) {
            user = new User();
            if (StringUtils.isNotBlank(userid)) {
                user.setId(userid);
            } else {
                user.setId(MainUtils.getContextID(request.getSession().getId()));
            }
            if (StringUtils.isNotBlank(nickname)) {
                user.setUsername(nickname);
            } else {
                Map<String, String> sessionMessage = cache.findOneSystemMapByIdAndOrgi(
                        request.getSession().getId(), MainContext.SYSTEM_ORGI);
                if (sessionMessage != null) {
                    String struname = sessionMessage.get("username");
                    String strcname = sessionMessage.get("company_name");

                    user.setUsername(struname + "@" + strcname);
                } else {
                    user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                }
            }
            user.setSessionid(user.getId());
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    public User getIMUser(HttpServletRequest request, String userid, String nickname, String sessionid) {
        User user = (User) request.getSession(true).getAttribute(Constants.IM_USER_SESSION_NAME);
        if (user == null) {
            user = new User();
            if (StringUtils.isNotBlank(userid)) {
                user.setId(userid);
            } else {
                user.setId(MainUtils.getContextID(request.getSession().getId()));
            }
            if (StringUtils.isNotBlank(nickname)) {
                user.setUsername(nickname);
            } else {
                Map<String, String> sessionMessage = cache.findOneSystemMapByIdAndOrgi(
                        sessionid, MainContext.SYSTEM_ORGI);
                if (sessionMessage != null) {
                    String struname = sessionMessage.get("username");
                    String strcname = sessionMessage.get("company_name");

                    user.setUsername(struname + "@" + strcname);
                } else {
                    user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                }
            }
            user.setSessionid(user.getId());
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    public void setUser(HttpServletRequest request, User user) {
        request.getSession(true).removeAttribute(Constants.USER_SESSION_NAME);
        request.getSession(true).setAttribute(Constants.USER_SESSION_NAME, user);
    }


    /**
     * 创建系统监控的 模板页面
     */
    public Viewport createAdminTempletResponse(String page) {
        return new Viewport("/admin/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     */
    public Viewport createAppsTempletResponse(String page) {
        return new Viewport("/apps/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     */
    public Viewport createEntIMTempletResponse(final String page) {
        return new Viewport("/apps/entim/include/tpl", page);
    }

    public Viewport createRequestPageTempletResponse(final String page) {
        return new Viewport(page);
    }

    /**
     *
     */
    public ModelAndView request(Viewport data) {
        return new ModelAndView(data.getTemplet() != null ? data.getTemplet() : data.getPage(), "data", data);
    }

    public int getP(HttpServletRequest request) {
        int page = 0;
        String p = request.getParameter("p");
        if (StringUtils.isNotBlank(p) && p.matches("[\\d]*")) {
            page = Integer.parseInt(p);
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(HttpServletRequest request) {
        int pagesize = PAGE_SIZE_TW;
        String ps = request.getParameter("ps");
        if (StringUtils.isNotBlank(ps) && ps.matches("[\\d]*")) {
            pagesize = Integer.parseInt(ps);
        }
        return pagesize;
    }

    public int getP(QueryParams params) {
        int page = 0;
        if (params != null && StringUtils.isNotBlank(params.getP()) && params.getP().matches("[\\d]*")) {
            page = Integer.parseInt(params.getP());
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(QueryParams params) {
        int pagesize = PAGE_SIZE_TW;
        if (params != null && StringUtils.isNotBlank(params.getPs()) && params.getPs().matches("[\\d]*")) {
            pagesize = Integer.parseInt(params.getPs());
        }
        return pagesize;
    }


    public String getOrgi(HttpServletRequest request) {
        return getUser(request).getOrgi();
    }

    /**
     * 机构id
     */
    public String getOrgid(HttpServletRequest request) {
        User u = getUser(request);
        return u.getOrgid();
    }

    public Tenant getTenant(HttpServletRequest request) {
        String id = getOrgi(request);
        return tenantRes.findById(id).orElse(null);
    }

    /**
     * 根据是否租户共享获取orgi
     */
    public String getOrgiByTenantshare(HttpServletRequest request) {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
            User user = this.getUser(request);
            return user.getOrgid();
        }
        return getOrgi(request);
    }

    /**
     * 判断是否租户共享
     */
    public boolean isTenantshare() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        return systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare();
    }

    /**
     * 判断是否多租户
     */
    public boolean isEnabletneant() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        return systemConfig != null && systemConfig.isEnabletneant();
    }

    /**
     * 判断是否多租户
     */
    public boolean isTenantconsole() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        return systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantconsole();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 使用Blob保存文件
     */
    public String saveImageFileWithMultipart(MultipartFile multipart) throws IOException {
        StreamingFile sf = new StreamingFile();
        final String fileid = MainUtils.getUUID();
        sf.setId(fileid);
        sf.setMime(multipart.getContentType());
        sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
        sf.setName(multipart.getOriginalFilename());
        streamingFileRes.save(sf);
        return fileid;
    }


}
