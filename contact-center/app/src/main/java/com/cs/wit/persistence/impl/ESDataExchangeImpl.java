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

import com.cs.wit.basic.Constants;
import com.cs.wit.model.*;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.UKefuCallOutTaskRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.util.es.UKDataBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

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
    private final RestHighLevelClient client;

    public void saveIObject(UKDataBean dataBean) throws IOException {
        if (dataBean.getId() == null) {
            dataBean.setId((String) dataBean.getValues().get("id"));
        }
        client.index(this.saveBulk(dataBean), RequestOptions.DEFAULT);
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
        return new IndexRequest(Constants.SYSTEM_INDEX,
                type, dataBean.getId()).source(processValues(dataBean));
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
                } else if (tp.getDatatypename().equals("nlp") && dataBean.getValues() != null) {
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
            final DeleteRequest request = new DeleteRequest(Constants.SYSTEM_INDEX, type, id);
            try {
                client.delete(request, RequestOptions.DEFAULT);
            }
            catch(IOException e) {
                log.error("Error while deleting item request: " + request, e);
            }
        }
    }


    public UKDataBean getIObjectByPK(UKDataBean dataBean) throws IOException {
        if (dataBean.getTable() != null) {
            GetResponse getResponse = client
                    .get(new GetRequest(Constants.SYSTEM_INDEX,
                            dataBean.getTable().getTablename(), dataBean.getId()), RequestOptions.DEFAULT);
            dataBean.setValues(getResponse.getSource());
            dataBean.setType(getResponse.getType());
        } else {
            dataBean.setValues(new HashMap<>());
        }

        return processDate(dataBean);
    }

    public UKDataBean getIObjectByPK(String type, String id) throws IOException {
        UKDataBean dataBean = new UKDataBean();
        if (!StringUtils.isBlank(type)) {
            GetResponse getResponse = client
                    .get(new GetRequest(Constants.SYSTEM_INDEX,
                            type, id), RequestOptions.DEFAULT);
            dataBean.setValues(getResponse.getSource());
            dataBean.setType(getResponse.getType());
        } else {
            dataBean.setValues(new HashMap<>());
        }
        return dataBean;
    }

    /**
     *
     */
    public PageImpl<UKDataBean> findPageResult(QueryBuilder query, MetadataTable metadata, Pageable page, boolean loadRef) throws IOException {
        return findAllPageResult(query, metadata, page, loadRef, metadata != null ? metadata.getTablename() : null);
    }

    /**
     *
     */
    public PageImpl<UKDataBean> findAllPageResult(QueryBuilder query, MetadataTable metadata, Pageable page, boolean loadRef, String types) throws IOException {

        SearchSourceBuilder source = new SearchSourceBuilder();
        int start = page.getPageSize() * page.getPageNumber();
        source.from(start).size(page.getPageSize()).query(query);

        for (Order order : page.getSort()) {
            source.sort(new FieldSortBuilder(order.getProperty()).unmappedType(order.getProperty().equals("createtime") ? "long" : "string").order(order.isDescending() ? SortOrder.DESC : SortOrder.ASC));
        }

        SearchRequest request = new SearchRequest(new String[]{Constants.SYSTEM_INDEX}, source);
        if (!StringUtils.isBlank(types)) {
            request.types(types);
        }

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        List<String> users = new ArrayList<>();
        List<String> organs = new ArrayList<>();
        List<String> taskList = new ArrayList<>();
        // List<String> batchList = new ArrayList<>();
        // List<String> activityList = new ArrayList<>();
        List<UKDataBean> dataBeanList = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            UKDataBean temp = new UKDataBean();
            temp.setType(hit.getType());
            temp.setTable(metadata);
            temp.setValues(hit.getSourceAsMap());
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
        return new PageImpl<>(dataBeanList, page, Optional.ofNullable(response.getHits().getTotalHits())
                                                                .map(hits -> (int)hits.value)
                                                                .orElse(0));
    }


    /**
     *
     */
    public PageImpl<UKDataBean> findAllPageAggResult(QueryBuilder query, String aggField, Pageable page, boolean loadRef, String types) throws IOException {

        int size = page.getPageSize() * (page.getPageNumber() + 1);

        AggregationBuilder aggregition = AggregationBuilders.terms(aggField).field(aggField).size(size);
        aggregition.subAggregation(AggregationBuilders.terms("apstatus").field("apstatus"));
        aggregition.subAggregation(AggregationBuilders.terms("callstatus").field("callstatus"));

        SearchSourceBuilder source = new SearchSourceBuilder().aggregation(aggregition).query(query);
        SearchRequest request = new SearchRequest(new String[]{Constants.SYSTEM_INDEX}, source);
        if (!StringUtils.isBlank(types)) {
            request.types(types);
        }
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        List<String> users = new ArrayList<>();
        List<String> organs = new ArrayList<>();
        List<String> taskList = new ArrayList<>();
        // List<String> batchList = new ArrayList<>();
        // List<String> activityList = new ArrayList<>();
        List<UKDataBean> dataBeanList = new ArrayList<>();

        if (response.getAggregations().get(aggField) instanceof Terms) {
            Terms agg = response.getAggregations().get(aggField);
            if (agg != null) {
                if (loadRef) {
                    if (aggField.equals(Constants.CSKEFU_SYSTEM_DIS_AGENT)) {
                        users.add(agg.getName());
                    }
                    if (aggField.equals(Constants.CSKEFU_SYSTEM_DIS_ORGAN)) {
                        organs.add(agg.getName());
                    }
                    if (aggField.equals("taskid")) {
                        taskList.add(agg.getName());
                    }
                    // if (aggField.equals("batid")) {
                    //     batchList.add(agg.getName());
                    // }
                    // if (aggField.equals("actid")) {
                    //     activityList.add(agg.getName());
                    // }
                }
                if (agg.getBuckets() != null && agg.getBuckets().size() > 0) {
                    for (Terms.Bucket entry : agg.getBuckets()) {
                        UKDataBean dataBean = new UKDataBean();
                        dataBean.getValues().put("id", entry.getKeyAsString());
                        dataBean.getValues().put(aggField, entry.getKeyAsString());
                        dataBean.setId(agg.getName());
                        dataBean.setType(aggField);
                        dataBean.getValues().put("total", entry.getDocCount());

                        for (Aggregation temp : entry.getAggregations()) {
                            if (temp instanceof StringTerms) {
                                StringTerms agg2 = (StringTerms) temp;
                                for (Terms.Bucket entry2 : agg2.getBuckets()) {
                                    dataBean.getValues().put(temp.getName() + "." + entry2.getKeyAsString(), entry2.getDocCount());
                                }
                            }
                        }
                        dataBeanList.add(dataBean);
                    }
                }
            }
        } else {
            response.getAggregations().get(aggField);//			InternalDateHistogram agg = response.getAggregations().get(aggField) ;
//			long total = response.getHits().getTotalHits() ;
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
        return new PageImpl<>(dataBeanList, page, Optional.ofNullable(response.getHits().getTotalHits())
                                                          .map(hits -> (int)hits.value)
                                                          .orElse(0));
    }


    /**
     *
     */
    public UKDataBean processDate(UKDataBean dataBean) {
        return dataBean;
    }
}
