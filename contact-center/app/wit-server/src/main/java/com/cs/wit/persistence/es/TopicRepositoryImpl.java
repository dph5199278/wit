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
package com.cs.wit.persistence.es;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.cs.wit.model.Topic;
import com.cs.wit.util.es.SearchTools;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicEsCommonRepository {
    @NonNull
    private final ElasticsearchOperations operations;

    @Override
    public Page<Topic> getTopicByCateAndOrgi(String cate, String orgi, String q, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (!StringUtils.isBlank(cate)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));
        }
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("top"), Order.desc("createtime")));
        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title", HighlightFieldParameters.builder().withFragmentSize(200).build()));
        searchQueryBuilder.withHighlightQuery(new HighlightQuery(new Highlight(fields), null));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(Topic.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, Topic.class,
                    operations.getIndexCoordinatesFor(Topic.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByTopAndOrgi(boolean top, String orgi, String aiid, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("top").value(String.valueOf(top))));
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (!StringUtils.isBlank(aiid)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("aiid").value(aiid)));
        }

        BoolQuery.Builder beginFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("begintime").build()._toQuery())
            .should(QueryBuilders.range().field("begintime").to(
                String.valueOf(System.currentTimeMillis())).build()._toQuery());
        BoolQuery.Builder endFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("endtime").build()._toQuery())
            .should(QueryBuilders.range().field("endtime").from(
                String.valueOf(System.currentTimeMillis())).build()._toQuery());

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withFilter(QueryBuilders.bool().must(beginFilter.build()._toQuery()).must(endFilter.build()._toQuery()).build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));


        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title", HighlightFieldParameters.builder().withFragmentSize(200).build()));
        searchQueryBuilder.withHighlightQuery(new HighlightQuery(new Highlight(fields), null));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(Topic.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, Topic.class,
                    operations.getIndexCoordinatesFor(Topic.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByCateAndUser(String cate, String q, String user, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withQuery(QueryBuilders.term(builder -> builder.field("creater").value(user)))
            .withSort(Sort.by(Order.desc("top"), Order.desc("updatetime")));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(Topic.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, Topic.class,
                    operations.getIndexCoordinatesFor(Topic.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByCon(BoolQuery.Builder boolQueryBuilder, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQuery.Builder beginFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("begintime").build()._toQuery())
            .should(QueryBuilders.range().field("begintime").to(String.valueOf(System.currentTimeMillis())).build()._toQuery());
        BoolQuery.Builder endFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("endtime").build()._toQuery())
            .should(QueryBuilders.range().field("endtime").from(String.valueOf(System.currentTimeMillis())).build()._toQuery());

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withFilter(QueryBuilders.bool().must(beginFilter.build()._toQuery()).must(endFilter.build()._toQuery()).build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(Topic.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, Topic.class,
                    operations.getIndexCoordinatesFor(Topic.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public List<Topic> getTopicByOrgi(String orgi, String type, String q) {

        List<Topic> list = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));

        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(type)));
        }

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("top"), Order.desc("updatetime")));
        Query searchQuery = searchQueryBuilder.build();
        if (operations.indexOps(Topic.class).exists()) {
            list = SearchTools.listUnwrapSearchHits(
                operations.search(searchQuery, Topic.class,
                    operations.getIndexCoordinatesFor(Topic.class)));
        }
        return list;
    }
}
