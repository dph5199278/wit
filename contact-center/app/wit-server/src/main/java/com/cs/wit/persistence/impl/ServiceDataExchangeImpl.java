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
package com.cs.wit.persistence.impl;

import com.cs.wit.model.Tag;
import com.cs.wit.persistence.interfaces.DataExchangeInterface;
import com.cs.wit.persistence.repository.TagRepository;
import java.io.Serializable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service("servicedata")
@RequiredArgsConstructor
public class ServiceDataExchangeImpl implements DataExchangeInterface {
    @NonNull
    private final TagRepository tagRes;

    public String getDataByIdAndOrgi(String id, String orgi) {
        Tag tag = tagRes.findByOrgiAndId(orgi, id);
        return tag != null ? tag.getTag() : id;
    }

    @Override
    public List<Serializable> getListDataByIdAndOrgi(String id, String creater, String orgi) {
        return null;
    }

    public void process(Object data, String orgi) {

    }
}
