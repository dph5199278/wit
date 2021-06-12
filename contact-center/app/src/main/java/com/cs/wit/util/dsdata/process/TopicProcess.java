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
package com.cs.wit.util.dsdata.process;

import com.cs.wit.basic.MainContext;
import com.cs.wit.model.Topic;
import com.cs.wit.model.TopicItem;
import com.cs.wit.persistence.es.TopicRepository;
import com.cs.wit.persistence.repository.TopicItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TopicProcess implements JPAProcess {

    @NonNull
    private final TopicRepository topicRes;

    @Override
    public void process(Object data) {
        Topic topic = (Topic) data;
        topicRes.save(topic);
        this.process(data, topic.getOrgi());
    }

    /**
     * 只处理 类似问题
     */
    public void process(Object data, String orgi) {
        Topic topic = (Topic) data;
        if (topic.getSilimar() != null && topic.getSilimar().size() > 0) {
            TopicItemRepository topicItemRes = MainContext.getContext().getBean(TopicItemRepository.class);
            List<TopicItem> topicItemList = topicItemRes.findByTopicid(topic.getId());
            if (topicItemList == null) {
                return;
            }
            if (topicItemList.size() > 0) {
                topicItemRes.deleteAll(topicItemList);
            }
            topicItemList.clear();
            for (String item : topic.getSilimar()) {
                TopicItem topicItem = new TopicItem();
                topicItem.setTitle(item);
                topicItem.setTopicid(topic.getId());
                topicItem.setOrgi(topic.getOrgi());
                topicItem.setCreater(topic.getCreater());
                topicItem.setCreatetime(new Date());
                topicItemList.add(topicItem);
            }
            if (topicItemList.size() > 0) {
                topicItemRes.saveAll(topicItemList);
            }
        }
    }

    @Override
    public void end() {

    }
}
