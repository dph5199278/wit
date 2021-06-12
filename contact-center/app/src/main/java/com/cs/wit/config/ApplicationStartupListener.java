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

import com.cs.wit.basic.MainContext;
import com.cs.wit.model.Favorites;
import com.cs.wit.model.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @NonNull
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (!elasticsearchRestTemplate.indexExists(WorkOrders.class)) {
            elasticsearchRestTemplate.createIndex(WorkOrders.class);
        }
        if (!elasticsearchRestTemplate.indexExists(Favorites.class)) {
            elasticsearchRestTemplate.createIndex(Favorites.class);
        }
        try {
            elasticsearchRestTemplate.getMapping(WorkOrders.class);
        } catch (ElasticsearchException e) {
            elasticsearchRestTemplate.putMapping(Favorites.class);
            elasticsearchRestTemplate.putMapping(WorkOrders.class);
        }
        MainContext.setTemplet(elasticsearchRestTemplate);
    }
}
