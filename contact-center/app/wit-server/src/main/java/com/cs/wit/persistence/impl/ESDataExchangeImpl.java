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
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.JsonData;
import com.cs.wit.basic.Constants;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.model.Organ;
import com.cs.wit.model.TableProperties;
import com.cs.wit.model.UKefuCallOutTask;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.UKefuCallOutTaskRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.util.es.UKDataBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository("esdataservice")
public class ESDataExchangeImpl {

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final UKefuCallOutTaskRepository taskRes;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final ElasticsearchClient client;

    @NonNull
    private final ElasticsearchOperations operations;

    public void saveIObject(UKDataBean dataBean) throws IOException {
        if (dataBean.getId() == null) {
            dataBean.setId((String) dataBean.getValues().get("id"));
        }
        client.index(this.saveBulk(dataBean));
    }

    @NonNull
    public IndexRequest saveBulk(UKDataBean dataBean) {
        if (dataBean.getId() == null) {
            dataBean.setId((String) dataBean.getValues().get("id"));
        }
        String type;
        if (!StringUtils.isBlank(dataBean.getType())) {
            type = dataBean.getType();
        } else {
            type = dataBean.getTable().getTablename();
        }

        return new IndexRequest.Builder()
            .index(Constants.SYSTEM_INDEX)
            .id(dataBean.getId())
            .document(processValues(dataBean))
            .build();
    }

    /**
     * 处理数据，包含 自然语言处理算法计算 智能处理字段
     */
    private Map<String, Object> processValues(UKDataBean dataBean) {
        Map<String, Object> values = new HashMap<>();
        if (dataBean.getTable() != null) {
            for (TableProperties tp : dataBean.getTable().getTableproperty()) {
                if (dataBean.getValues().get(tp.getFieldname()) != null) {
                    values.put(tp.getFieldname(), dataBean.getValues().get(tp.getFieldname()));
                } else if ("nlp".equals(tp.getDatatypename()) && dataBean.getValues() != null) {
                    //智能处理， 需要计算过滤HTML内容，自动获取关键词、摘要、实体识别、情感分析、信息指纹 等功能
                    values.put(tp.getFieldname(), dataBean.getValues().get(tp.getFieldname()));
                } else {
                    values.put(tp.getFieldname(), dataBean.getValues().get(tp.getFieldname()));
                }
            }
        } else {
            values.putAll(dataBean.getValues());
        }
        return values;
    }

    public void deleteById(String type, String id) {
        if (!StringUtils.isBlank(type) && !StringUtils.isBlank(id)) {
            final DeleteRequest request = new DeleteRequest.Builder().index(Constants.SYSTEM_INDEX).id(id).build();
            try {
                client.delete(request);
            }
            catch(IOException e) {
                log.error("Error while deleting item request: " + request, e);
            }
        }
    }


    public UKDataBean getIObjectByPK(UKDataBean dataBean) throws IOException {
        if (dataBean.getTable() != null) {
            GetResponse<String> getResponse = client
                    .get(new GetRequest.Builder().index(Constants.SYSTEM_INDEX)
                        .id(dataBean.getId()).build(), String.class);
            Map<String, Object> map = new HashMap<>(getResponse.fields().size());
            for(Map.Entry<String, JsonData> entry : getResponse.fields().entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
            dataBean.setValues(map);
            dataBean.setType(dataBean.getTable().getTablename());
        } else {
            dataBean.setValues(new HashMap<>());
        }

        return processDate(dataBean);
    }

    public UKDataBean getIObjectByPK(String type, String id) throws IOException {
        UKDataBean dataBean = new UKDataBean();
        if (!StringUtils.isBlank(type)) {
            GetResponse<String> getResponse = client
                    .get(new GetRequest.Builder().index(Constants.SYSTEM_INDEX)
                        .id(id).build(), String.class);
            Map<String, Object> map = new HashMap<>(getResponse.fields().size());
            for(Map.Entry<String, JsonData> entry : getResponse.fields().entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
            dataBean.setValues(map);
            dataBean.setType(type);
        } else {
            dataBean.setValues(new HashMap<>());
        }
        return dataBean;
    }

    /**
     *
     */
    public PageImpl<UKDataBean> findPageResult(BoolQuery.Builder queryBuilder, MetadataTable metadata, Pageable page, boolean loadRef) throws IOException {
        return findAllPageResult(queryBuilder, metadata, page, loadRef, metadata != null ? metadata.getTablename() : null);
    }

    /**
     *
     */
    public PageImpl<UKDataBean> findAllPageResult(BoolQuery.Builder queryBuilder, MetadataTable metadata, Pageable page, boolean loadRef, String types) throws IOException {

        NativeQuery query = NativeQuery.builder()
            .withQuery(queryBuilder.build()._toQuery())
            .withPageable(page)
            .build();

        SearchHits<String> response = operations.search(query, String.class, IndexCoordinates.of(Constants.SYSTEM_INDEX));
        List<String> users = new ArrayList<>();
        List<String> organs = new ArrayList<>();
        List<String> taskList = new ArrayList<>();
        // List<String> batchList = new ArrayList<>();
        // List<String> activityList = new ArrayList<>();
        List<UKDataBean> dataBeanList = new ArrayList<>();
        for (SearchHit<String> hit : response.getSearchHits()) {
            UKDataBean temp = new UKDataBean();
            temp.setType(types);
            temp.setTable(metadata);
            Map<String, Object> map = new HashMap<>(hit.getHighlightFields());
            temp.setValues(map);
            temp.setId((String) temp.getValues().get("id"));
            dataBeanList.add(processDate(temp));


            if (loadRef) {
                if (!StringUtils.isBlank((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT))) {
                    users.add((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT));
                }
                if (!StringUtils.isBlank((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_ASSUSER))) {
                    users.add((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_ASSUSER));
                }
                if (!StringUtils.isBlank((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_DIS_ORGAN))) {
                    organs.add((String) temp.getValues().get(Constants.CSKEFU_SYSTEM_DIS_ORGAN));
                }
                if (!StringUtils.isBlank((String) temp.getValues().get("taskid"))) {
                    taskList.add((String) temp.getValues().get("taskid"));
                }
                // if (!StringUtils.isBlank((String) temp.getValues().get("batid"))) {
                //     batchList.add((String) temp.getValues().get("batid"));
                // }
                // if (!StringUtils.isBlank((String) temp.getValues().get("actid"))) {
                //     activityList.add((String) temp.getValues().get("actid"));
                // }
            }
        }
        if (loadRef) {
            if (users.size() > 0) {
                List<User> userList = userRes.findAllById(users);
                for (UKDataBean dataBean : dataBeanList) {
                    String userid = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT);
                    if (!StringUtils.isBlank(userid)) {
                        for (User user : userList) {
                            if (user.getId().equals(userid)) {
                                dataBean.setUser(user);
                                break;
                            }
                        }
                    }
                    String assuer = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_ASSUSER);
                    if (!StringUtils.isBlank(assuer)) {
                        for (User user : userList) {
                            if (user.getId().equals(assuer)) {
                                dataBean.setAssuser(user);
                                break;
                            }
                        }
                    }
                }
            }
            if (organs.size() > 0) {
                List<Organ> organList = organRes.findAllById(organs);
                for (UKDataBean dataBean : dataBeanList) {
                    String organid = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_ORGAN);
                    if (!StringUtils.isBlank(organid)) {
                        for (Organ organ : organList) {
                            if (organ.getId().equals(organid)) {
                                dataBean.setOrgan(organ);
                                break;
                            }
                        }
                    }
                }
            }
            if (taskList.size() > 0) {
                List<UKefuCallOutTask> ukefuCallOutTaskList = taskRes.findAllById(taskList);
                for (UKDataBean dataBean : dataBeanList) {
                    String taskid = (String) dataBean.getValues().get("taskid");
                    if (!StringUtils.isBlank(taskid)) {
                        for (UKefuCallOutTask task : ukefuCallOutTaskList) {
                            if (task.getId().equals(taskid)) {
                                dataBean.setTask(task);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return new PageImpl<>(dataBeanList, page, response.getTotalHits());
    }


    /**
     *
     */
    public PageImpl<UKDataBean> findAllPageAggResult(BoolQuery.Builder queryBuilder, String aggField, Pageable page, boolean loadRef, String types) throws IOException {

        int size = page.getPageSize() * (page.getPageNumber() + 1);

        Aggregation aggregition = new Aggregation.Builder()
            .terms(AggregationBuilders.terms().field(aggField).size(size).build())
            .aggregations("apstatus", AggregationBuilders.terms(aggBuilder -> aggBuilder.field("apstatus")))
            .aggregations("callstatus", AggregationBuilders.terms(aggBuilder -> aggBuilder.field("callstatus")))
            .build();

        NativeQuery query = NativeQuery.builder()
            .withAggregation(aggField, aggregition)
            .withQuery(queryBuilder.build()._toQuery())
            .withPageable(page)
            .build();
        SearchHits<String> response = operations.search(query, String.class, IndexCoordinates.of(Constants.SYSTEM_INDEX));
        List<String> users = new ArrayList<>();
        List<String> organs = new ArrayList<>();
        List<String> taskList = new ArrayList<>();
        // List<String> batchList = new ArrayList<>();
        // List<String> activityList = new ArrayList<>();
        List<UKDataBean> dataBeanList = new ArrayList<>();

        if(response.hasAggregations()) {
            if(response.getAggregations().aggregations() instanceof ElasticsearchAggregations aggregations) {
                ElasticsearchAggregation aggregation = aggregations.get(aggField);
                if(null != aggregation) {
                    org.springframework.data.elasticsearch.client.elc.Aggregation agg = aggregation.aggregation();
                    if (loadRef) {
                        if (Constants.CSKEFU_SYSTEM_DIS_AGENT.equals(aggField)) {
                            users.add(agg.getName());
                        }
                        if (Constants.CSKEFU_SYSTEM_DIS_ORGAN.equals(aggField)) {
                            organs.add(agg.getName());
                        }
                        if ("taskid".equals(aggField)) {
                            taskList.add(agg.getName());
                        }
                    }
                    if (agg.getAggregate().isSterms() && null != agg.getAggregate().sterms().buckets()) {
                        if (agg.getAggregate().sterms().buckets().isArray()) {
                            dataBeanList.addAll(
                                agg.getAggregate().sterms().buckets().array()
                                    .stream()
                                    .map(entry -> {
                                        UKDataBean dataBean = new UKDataBean();
                                        dataBean.getValues()
                                            .put("id", entry.key().stringValue());
                                        dataBean.getValues()
                                            .put(aggField, entry.key().stringValue());
                                        dataBean.setId(agg.getName());
                                        dataBean.setType(aggField);
                                        dataBean.getValues().put("total", entry.docCount());

                                        for (Map.Entry<String, Aggregate> temp : entry.aggregations()
                                            .entrySet()) {
                                            if (temp.getValue().isSterms()) {
                                                StringTermsAggregate agg2 = temp.getValue()
                                                    .sterms();
                                                if (agg2.buckets().isArray()) {
                                                    for (StringTermsBucket entry2 : agg2.buckets()
                                                        .array()) {
                                                        dataBean.getValues().put(
                                                            temp.getKey() + "." + entry2.key()
                                                                .stringValue(),
                                                            entry2.docCount());
                                                    }
                                                }
                                            }
                                        }

                                        return dataBean;
                                    }).toList()
                            );
                        }
                    }
                }
            }
        }

        if (loadRef) {
            if (users.size() > 0) {
                List<User> userList = userRes.findAllById(users);
                for (UKDataBean dataBean : dataBeanList) {
                    String userid = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_AGENT);
                    if (!StringUtils.isBlank(userid)) {
                        for (User user : userList) {
                            if (user.getId().equals(userid)) {
                                dataBean.setUser(user);
                                break;
                            }
                        }
                    }
                }
            }
            if (organs.size() > 0) {
                List<Organ> organList = organRes.findAllById(organs);
                for (UKDataBean dataBean : dataBeanList) {
                    String organid = (String) dataBean.getValues().get(Constants.CSKEFU_SYSTEM_DIS_ORGAN);
                    if (!StringUtils.isBlank(organid)) {
                        for (Organ organ : organList) {
                            if (organ.getId().equals(organid)) {
                                dataBean.setOrgan(organ);
                                break;
                            }
                        }
                    }
                }
            }
            if (taskList.size() > 0) {
                List<UKefuCallOutTask> ukefuCallOutTaskList = taskRes.findAllById(taskList);
                for (UKDataBean dataBean : dataBeanList) {
                    String taskid = (String) dataBean.getValues().get("taskid");
                    if (!StringUtils.isBlank(taskid)) {
                        for (UKefuCallOutTask task : ukefuCallOutTaskList) {
                            if (task.getId().equals(taskid)) {
                                dataBean.setTask(task);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return new PageImpl<>(dataBeanList, page, response.getTotalHits());
    }


    /**
     *
     */
    public UKDataBean processDate(UKDataBean dataBean) {
        return dataBean;
    }
}
