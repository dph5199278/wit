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
package com.cs.wit;

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.config.AppCtxRefreshEventListener;
import javax.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.unit.DataSize;

@SpringBootApplication
@EnableJpaRepositories("com.cs.wit.persistence.repository")
@EnableElasticsearchRepositories("com.cs.wit.persistence.es")
@EnableTransactionManagement
public class Application {

    /*
     * 加载模块
     */
    static {
        // CRM模块
        MainContext.enableModule(Constants.CSKEFU_MODULE_CONTACTS);

        // 会话监控模块 Customer Chats Audit
        MainContext.enableModule(Constants.CSKEFU_MODULE_CCA);

        // 企业聊天模块
        MainContext.enableModule(Constants.CSKEFU_MODULE_ENTIM);
    }

    @Value("${web.upload-path}")
    private String uploadDir;
    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize multipartMaxUpload;
    @Value("${spring.servlet.multipart.max-request-size}")
    private DataSize multipartMaxRequest;

    public static void main(String[] args) {
        /*
         该APP中加载多个配置文件
         http://roufid.com/load-multiple-configuration-files-different-directories-spring-boot/
         */
        MainContext.setApplicationContext(new SpringApplicationBuilder(Application.class)
            .addCommandLineProperties(false)
            .listeners(new AppCtxRefreshEventListener())
            .run(args));
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(multipartMaxUpload); //KB,MB
        factory.setMaxRequestSize(multipartMaxRequest);
        factory.setLocation(uploadDir);
        return factory.createMultipartConfig();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return container -> {
            ErrorPage error = new ErrorPage("/error.html");
            container.addErrorPages(error);
        };
    }
}
