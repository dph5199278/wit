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

import com.cs.wit.model.Topic;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Component
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicEsCommonRepository {
    @NonNull
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<Topic> getTopicByCateAndOrgi(String cate, String orgi, String q, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isBlank(cate)) {
            boolQueryBuilder.must(termQuery("cate", cate));
        }
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexOps(Topic.class).exists()) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, Topic.class, elasticsearchRestTemplate.getIndexCoordinatesFor(Topic.class));
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByTopAndOrgi(boolean top, String orgi, String aiid, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("top", top));
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (!StringUtils.isBlank(aiid)) {
            boolQueryBuilder.must(termQuery("aiid", aiid));
        }

        QueryBuilder beginFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("begintime")).should(QueryBuilders.rangeQuery("begintime").to(new Date().getTime()));
        QueryBuilder endFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("endtime")).should(QueryBuilders.rangeQuery("endtime").from(new Date().getTime()));

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withFilter(QueryBuilders.boolQuery().must(beginFilter).must(endFilter)).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));


        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexOps(Topic.class).exists()) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, Topic.class, elasticsearchRestTemplate.getIndexCoordinatesFor(Topic.class));
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByCateAndUser(String cate, String q, String user, final int p, final int ps) {

        Page<Topic> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("cate", cate));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withQuery(termQuery("creater", user)).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexOps(Topic.class).exists()) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, Topic.class, elasticsearchRestTemplate.getIndexCoordinatesFor(Topic.class));
        }
        return pages;
    }

    @Override
    public Page<Topic> getTopicByCon(BoolQueryBuilder boolQueryBuilder, final int p, final int ps) {

        Page<Topic> pages = null;

        QueryBuilder beginFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("begintime")).should(QueryBuilders.rangeQuery("begintime").to(new Date().getTime()));
        QueryBuilder endFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("endtime")).should(QueryBuilders.rangeQuery("endtime").from(new Date().getTime()));

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withFilter(QueryBuilders.boolQuery().must(beginFilter).must(endFilter)).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexOps(Topic.class).exists()) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery,  Topic.class, elasticsearchRestTemplate.getIndexCoordinatesFor(Topic.class));
        }
        return pages;
    }

    @Override
    public List<Topic> getTopicByOrgi(String orgi, String type, String q) {

        List<Topic> list = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));

        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(termQuery("cate", type));
        }

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
        Query searchQuery = searchQueryBuilder.build();
        if (elasticsearchRestTemplate.indexOps(Topic.class).exists()) {
            list = elasticsearchRestTemplate.queryForList(searchQuery,  Topic.class, elasticsearchRestTemplate.getIndexCoordinatesFor(Topic.class));
        }
        return list;
    }
}
