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
package com.cs.wit.interceptor;

import com.cs.wit.acd.ACDServiceRouter;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.config.MessagingServerConfigure;
import com.cs.wit.model.Dict;
import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.User;
import com.cs.wit.proxy.AgentSessionProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.util.Menu;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class UserInterceptorHandler implements AsyncHandlerInterceptor {

    private static UserProxy userProxy;
    private static AgentSessionProxy agentSessionProxy;
    private static Integer webIMPort;

    private static Integer getWebIMPort() {
        if (webIMPort == null) {
            webIMPort = MainContext.getContext().getBean(MessagingServerConfigure.class).getWebIMPort();
        }
        return webIMPort;
    }

    private static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }

    private static AgentSessionProxy getAgentSessionProxy() {
        if (agentSessionProxy == null) {
            agentSessionProxy = MainContext.getContext().getBean(AgentSessionProxy.class);
        }
        return agentSessionProxy;
    }

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        boolean filter = false;

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Menu menu = handlerMethod.getMethod().getAnnotation(Menu.class);
            User user = (User) request.getSession(true).getAttribute(Constants.USER_SESSION_NAME);
            if (user != null || (menu != null && menu.access()) || handlerMethod.getBean() instanceof BasicErrorController) {
                filter = true;
                if (user != null && StringUtils.isNotBlank(user.getId())) {

                    /*
                     * 每次刷新用户的组织机构、角色和权限
                     * TODO 此处代码执行频率高，但是并不是每次都要执行，存在很多冗余
                     * 待用更好的方法实现
                     */
                    getUserProxy().attachOrgansPropertiesForUser(user);
                    getUserProxy().attachRolesMap(user);

                    request.getSession(true).setAttribute(Constants.USER_SESSION_NAME, user);
                }
            }

            if (!filter) {
                if (StringUtils.isNotBlank(request.getParameter("msg"))) {
                    response.sendRedirect("/login?msg=" + request.getParameter("msg"));
                } else {
                    response.sendRedirect("/login");
                }
            }
        } else {
            filter = true;
        }
        return filter;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                           @Nullable ModelAndView view) {
        final User user = (User) request.getSession().getAttribute(Constants.USER_SESSION_NAME);
        final String infoace = (String) request.getSession().getAttribute(
                Constants.CSKEFU_SYSTEM_INFOACQ);        //进入信息采集模式
        final SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (view != null) {
            if (user != null) {
                view.addObject("user", user);

                if (systemConfig != null && systemConfig.isEnablessl()) {
                    view.addObject("schema", "https");
                    if (request.getServerPort() == 80) {
                        view.addObject("port", 443);
                    } else {
                        view.addObject("port", request.getServerPort());
                    }
                } else {
                    view.addObject("schema", request.getScheme());
                    view.addObject("port", request.getServerPort());
                }
                view.addObject("hostname", request.getServerName());

                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Menu menu = handlerMethod.getMethod().getAnnotation(Menu.class);
                if (menu != null) {
                    view.addObject("subtype", menu.subtype());
                    view.addObject("maintype", menu.type());
                    view.addObject("typename", menu.name());
                }
                view.addObject("orgi", user.getOrgi());
            }
            if (StringUtils.isNotBlank(infoace)) {
                view.addObject("infoace", infoace);        //进入信息采集模式
            }
            view.addObject("webimport", getWebIMPort());
            view.addObject("sessionid", MainUtils.getContextID(request.getSession().getId()));

            view.addObject("models", MainContext.getModules());

            if (user != null) {
                view.addObject(
                        "agentStatusReport",
                        ACDServiceRouter.getAcdWorkMonitor().getAgentReport(user.getOrgi()));
            }
            /*
             * WebIM共享用户
             */
            User imUser = (User) request.getSession().getAttribute(Constants.IM_USER_SESSION_NAME);
            if (imUser == null) {
                imUser = new User();
                imUser.setUsername(Constants.GUEST_USER);
                imUser.setId(MainUtils.getContextID(request.getSession(true).getId()));
                imUser.setSessionid(imUser.getId());
                view.addObject("imuser", imUser);
            }

            if (request.getParameter("msg") != null) {
                view.addObject("msg", request.getParameter("msg"));
            }

            view.addObject("uKeFuDic", Dict.getInstance());    //处理系统 字典数据 ， 通过 字典code 获取

            view.addObject(
                    "uKeFuSecField", MainContext.getCache().findOneSystemByIdAndOrgi(
                            Constants.CSKEFU_SYSTEM_SECFIELD,
                            MainContext.SYSTEM_ORGI));    //处理系统 需要隐藏号码的字段， 启动的时候加载

            if (systemConfig != null) {
                view.addObject("systemConfig", systemConfig);
            } else {
                view.addObject("systemConfig", new SystemConfig());
            }
            view.addObject("tagTypeList", Dict.getInstance().getDic("com.dic.tag.type"));

            view.addObject("advTypeList", Dict.getInstance().getDic("com.dic.adv.type"));
            view.addObject("ip", request.getRemoteAddr());
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
    }

}
