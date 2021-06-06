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

import com.cs.wit.basic.MainContext;
import com.cs.wit.model.QuickReply;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Component
@RequiredArgsConstructor
public class QuickReplyRepositoryImpl implements QuickReplyEsCommonRepository {
    @NonNull
    private final UKResultMapper ukResultMapper;

    @NonNull
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<QuickReply> getByOrgiAndCate(String orgi, String cate, String q, Pageable page) {

        Page<QuickReply> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("cate", cate));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(page);
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, QuickReply.class, ukResultMapper);
        }
        return pages;
    }

    @Override
    public List<QuickReply> findByOrgiAndCreater(String orgi, String creater, String q) {

        List<QuickReply> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));

        BoolQueryBuilder quickQueryBuilder = QueryBuilders.boolQuery();

        quickQueryBuilder.should(termQuery("type", MainContext.QuickType.PUB.toString()));

        BoolQueryBuilder priQueryBuilder = QueryBuilders.boolQuery();

        priQueryBuilder.must(termQuery("type", MainContext.QuickType.PRI.toString()));
        priQueryBuilder.must(termQuery("creater", creater));

        quickQueryBuilder.should(priQueryBuilder);

        boolQueryBuilder.must(quickQueryBuilder);

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(0, 10000));
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            pages = elasticsearchRestTemplate.queryForList(searchQuery, QuickReply.class);
        }
        return pages;
    }


    @Override
    public Page<QuickReply> getByQuicktype(String quicktype, final int p, final int ps) {

        Page<QuickReply> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("type", quicktype));

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));

        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title").fragmentSize(200));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, QuickReply.class, ukResultMapper);
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByCateAndUser(String cate, String q, String user, final int p, final int ps) {

        Page<QuickReply> pages = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("cate", cate));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withQuery(termQuery("creater", user)).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, QuickReply.class, ukResultMapper);
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByCon(BoolQueryBuilder boolQueryBuilder, final int p, final int ps) {

        Page<QuickReply> pages = null;

        QueryBuilder beginFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("begintime")).should(QueryBuilders.rangeQuery("begintime").from(new Date().getTime()));
        QueryBuilder endFilter = QueryBuilders.boolQuery().should(QueryBuilders.existsQuery("endtime")).should(QueryBuilders.rangeQuery("endtime").to(new Date().getTime()));

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withFilter(QueryBuilders.boolQuery().must(beginFilter).must(endFilter)).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));

        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            pages = elasticsearchRestTemplate.queryForPage(searchQuery, QuickReply.class);
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByOrgiAndType(String orgi, String type, String q, Pageable page) {

        Page<QuickReply> list = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(termQuery("type", type));
        }

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("createtime").unmappedType("date").order(SortOrder.DESC));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(page);
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            list = elasticsearchRestTemplate.queryForPage(searchQuery, QuickReply.class);
        }
        return list;
    }

    @Override
    public void deleteByCate(String cate, String orgi) {
        DeleteQuery deleteQuery = new DeleteQuery();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));
        boolQueryBuilder.must(termQuery("cate", cate));
        deleteQuery.setQuery(boolQueryBuilder);
        elasticsearchRestTemplate.delete(deleteQuery);
    }

    @Override
    public List<QuickReply> getQuickReplyByOrgi(String orgi, String cate, String type, String q) {

        List<QuickReply> list = null;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));

        if (!StringUtils.isBlank(cate)) {
            boolQueryBuilder.must(termQuery("cate", cate));
        }
        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(termQuery("type", type));
        }
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("top").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("updatetime").unmappedType("date").order(SortOrder.DESC));
        SearchQuery searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(0, 10000));
        if (elasticsearchRestTemplate.indexExists(QuickReply.class)) {
            list = elasticsearchRestTemplate.queryForList(searchQuery, QuickReply.class);
        }
        return list;
    }
}
