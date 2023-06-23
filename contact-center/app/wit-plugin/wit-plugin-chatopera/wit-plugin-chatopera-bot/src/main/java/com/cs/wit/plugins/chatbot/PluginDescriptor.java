/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.plugins.chatbot;

import com.cs.wit.basic.plugins.IPluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 定义Plugin存在
 */
public class PluginDescriptor implements IPluginDescriptor {
    private final static Logger logger = LoggerFactory.getLogger(PluginDescriptor.class);
    public final static String PLUGIN_NAME = "chatbot";


    /**
     * 获取消息服务的Bean的名字
     * 当该方法存在时，加载到消息处理的调用栈 PeerSyncIM 中
     *
     * @return
     */
    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public String getIOEventHandler() {
        return ChatbotEventHandler.class.getName();
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        Map<String, String> env = new HashMap<>();
        env.put(ChatbotConstants.BOT_PROVIDER, "https://bot.chatopera.com");
        env.put(ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.9");
        env.put(ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.3");
        return env;
    }
}