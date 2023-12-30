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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.cs.wit.basic.MainContext;
import com.cs.wit.util.dsdata.process.JPAProcess;
import com.cs.wit.util.es.UKDataBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.lang.NonNull;

public class DataBatProcess implements JPAProcess {
    @NonNull
    private final List<BulkOperation> requestOperations;
    @NonNull
    private final ESDataExchangeImpl esDataExchangeImpl;

    public DataBatProcess(@NonNull ESDataExchangeImpl esDataExchangeImpl) {
        this.esDataExchangeImpl = esDataExchangeImpl;
        this.requestOperations = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void process(Object data) {
        if (data instanceof UKDataBean) {
            UKDataBean dataBean = (UKDataBean) data;
            try {
                requestOperations.add(BulkOperation.of(op -> op.index(idx -> {
                    IndexRequest indexRequest = esDataExchangeImpl.saveBulk(dataBean);
                    return idx.index(indexRequest.index())
                        .id(indexRequest.index())
                        .document(indexRequest.document());
                })));
                if (requestOperations.size() % 1000 == 0) {
                    end();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void end() throws IOException {
        final BulkRequest request = new BulkRequest.Builder().operations(new ArrayList<>(requestOperations)).build();
        requestOperations.clear();
        MainContext.getContext().getBean(ElasticsearchClient.class).bulk(request);
    }
}
