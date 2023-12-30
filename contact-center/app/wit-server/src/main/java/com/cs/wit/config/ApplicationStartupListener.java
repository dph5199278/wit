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

import com.cs.wit.model.ContactNotes;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.EntCustomer;
import com.cs.wit.model.Favorites;
import com.cs.wit.model.KbsTopic;
import com.cs.wit.model.KbsTopicComment;
import com.cs.wit.model.OrdersComment;
import com.cs.wit.model.PublishedReport;
import com.cs.wit.model.QuickReply;
import com.cs.wit.model.Report;
import com.cs.wit.model.Topic;
import com.cs.wit.model.WorkOrders;
import com.cs.wit.socketio.message.ChatMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @NonNull
    private final ElasticsearchOperations operations;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        Optional.of(operations.indexOps(ContactNotes.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(Contacts.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(EntCustomer.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(Favorites.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(KbsTopic.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(KbsTopicComment.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(OrdersComment.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(PublishedReport.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(QuickReply.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(Report.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(Topic.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(WorkOrders.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
        Optional.of(operations.indexOps(ChatMessage.class))
                .filter(indexOps -> !indexOps.exists())
                .ifPresent(indexOps -> {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping());
                });
    }
}
