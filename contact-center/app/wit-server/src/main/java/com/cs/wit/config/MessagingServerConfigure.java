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

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.cs.wit.basic.TextEncryptor;
import com.cs.wit.exception.InstantMessagingExceptionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class MessagingServerConfigure {
    @Value("${uk.im.server.host}")
    private String host;

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${cs.im.server.ssl.port}")
    private Integer sslPort;

    @Value("${web.upload-path}")
    private String path;

    @Value("${uk.im.server.threads:100}")
    private int threads;

    @Autowired
    private TextEncryptor encryptor;

    @Bean(name = "webimport")
    public Integer getWebIMPort() {
        if (sslPort != null) {
            return sslPort;
        } else {
            return port;
        }
    }

    @Bean
    public SocketIOServer socketIOServer() throws NoSuchAlgorithmException, IOException {
        Configuration config = new Configuration();
        // port
        config.setPort(port);
        // worker threads
        config.setWorkerThreads(threads);
        // anyone to authorization
        config.setAuthorizationListener(data -> new AuthorizationResult(true));
        // use epoll by os is linux
        config.setUseLinuxNativeEpoll("Linux".equalsIgnoreCase(System.getProperty("os.name")));
        // exception listener
        config.setExceptionListener(new InstantMessagingExceptionListener());
        // store factory
		    //config.setStoreFactory(new HazelcastStoreFactory());

        final SocketConfig socketConfig = config.getSocketConfig();
        // reuse address
        socketConfig.setReuseAddress(true);
        // close delay time
        socketConfig.setSoLinger(0);
        // disable nagle(send now)
        socketConfig.setTcpNoDelay(true);
        // TCP keep alive
        socketConfig.setTcpKeepAlive(true);

        // SSL
        File sslFile = new File(path, "ssl/https.properties");
        if (sslFile.exists()) {
            Properties sslProperties = new Properties();
            try (FileInputStream in = new FileInputStream(sslFile)) {
                sslProperties.load(in);
            }
            final String keyStore = sslProperties.getProperty("key-store");
            final String keyStorePassword = sslProperties.getProperty("key-store-password");
            if (StringUtils.isNotBlank(keyStore)
                && StringUtils.isNotBlank(keyStorePassword)) {
                config.setKeyStorePassword(encryptor.decryption(keyStorePassword));
                InputStream stream = new FileInputStream(
                        new File(path, "ssl/" + keyStore));
                config.setKeyStore(stream);
            }
        }

        // build socket
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}  