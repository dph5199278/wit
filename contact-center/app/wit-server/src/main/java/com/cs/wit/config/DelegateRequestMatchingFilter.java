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
package com.cs.wit.config;

import com.cs.wit.basic.Constants;
import com.cs.wit.model.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class DelegateRequestMatchingFilter implements Filter {
    private final RequestMatcher[] ignoredRequests;

    private DelegateRequestMatchingFilter(RequestMatcher[] matcher) {
        this.ignoredRequests = matcher;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        boolean matchAnyRoles = false;
        for (RequestMatcher anyRequest : ignoredRequests) {
            if (anyRequest.matches(request)) {
                matchAnyRoles = true;
            }
        }
        if (matchAnyRoles) {
            User user = (User) request.getSession().getAttribute(Constants.USER_SESSION_NAME);
            if (user != null && (user.isAdmin())) {
                chain.doFilter(req, resp);
            } else {
                // 重定向到 无权限执行操作的页面
                HttpServletResponse response = (HttpServletResponse) resp;
                response.sendRedirect("/?msg=security");
            }
        } else {
            try {
                chain.doFilter(req, resp);
            } catch (ClientAbortException ex) {
                //Tomcat异常，不做处理
            }
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private List<RequestMatcher> ignoredRequestList = new ArrayList<>();

        /**
         * Add request matcher builder.
         *
         * @param matcher the matcher
         * @return the builder
         */
        public Builder addRequestMatcher(RequestMatcher matcher) {
            ignoredRequestList.add(matcher);
            return this;
        }

        /**
         * Build delegate request matching filter.
         *
         * @return the delegate request matching filter
         */
        public DelegateRequestMatchingFilter build() {
            return new DelegateRequestMatchingFilter(ignoredRequestList.toArray(new RequestMatcher[0]));
        }
    }
}