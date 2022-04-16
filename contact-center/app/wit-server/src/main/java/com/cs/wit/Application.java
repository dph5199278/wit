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
import com.cs.wit.basic.plugins.PluginRegistry;
import com.cs.wit.config.AppCtxRefreshEventListener;
import com.cs.wit.util.ClassHelper;
import com.cs.wit.util.mobile.MobileNumberUtils;
import java.io.IOException;
import javax.servlet.MultipartConfigElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
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

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

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

        /*
         * 插件组
         */
        // 外呼模块

        if (ClassHelper.isClassExistByFullName(
                PluginRegistry.PLUGIN_ENTRY_CALLOUT)) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CALLOUT);
        }

        // skype模块
        if (ClassHelper.isClassExistByFullName(
                PluginRegistry.PLUGIN_ENTRY_SKYPE)) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_SKYPE);
        }

        // 聊天机器人模块
        if (ClassHelper.isClassExistByFullName(PluginRegistry.PLUGIN_ENTRY_CHATBOT)) {
            MainContext.enableModule(Constants.CSKEFU_MODULE_CHATBOT);
        }
    }

    @Value("${web.upload-path}")
    private String uploadDir;
    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize multipartMaxUpload;
    @Value("${spring.servlet.multipart.max-request-size}")
    private DataSize multipartMaxRequest;

    /**
     * Init local resources
     */
    protected static void init() {
        try {
            MobileNumberUtils.init();
        } catch (IOException e) {
            logger.error("Application Startup Error", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Application.init();

        /*
         该APP中加载多个配置文件
         http://roufid.com/load-multiple-configuration-files-different-directories-spring-boot/
         */
        SpringApplication app = new SpringApplicationBuilder(Application.class)
                .properties("spring.config.name:application,git")
                .build();

        app.setBannerMode(Banner.Mode.CONSOLE);
        app.setAddCommandLineProperties(false);
        app.addListeners(new AppCtxRefreshEventListener());
        MainContext.setApplicationContext(app.run(args));
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
