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
import co.elastic.clients.elasticsearch._types.query_dsl.TermRangeQuery;
import com.cs.wit.basic.MainContext;
import com.cs.wit.model.QuickReply;
import com.cs.wit.util.es.SearchTools;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuickReplyRepositoryImpl implements QuickReplyEsCommonRepository {

    @NonNull
    private final ElasticsearchOperations operations;

    @Override
    public Page<QuickReply> getByOrgiAndCate(String orgi, String cate, String q, Pageable page) {

        Page<QuickReply> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));
        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title", HighlightFieldParameters.builder().withFragmentSize(200).build()));
        searchQueryBuilder.withHighlightQuery(new HighlightQuery(new Highlight(fields), null));
        Query searchQuery = searchQueryBuilder.build().setPageable(page);
        if (operations.indexOps(QuickReply.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)),
                page);
        }
        return pages;
    }

    @Override
    public List<QuickReply> findByOrgiAndCreater(String orgi, String creater, String q) {

        List<QuickReply> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));

        BoolQuery.Builder quickQueryBuilder = QueryBuilders.bool();

        quickQueryBuilder.should(QueryBuilders.term(builder -> builder.field("type").value(MainContext.QuickType.PUB.toString())));

        BoolQuery.Builder priQueryBuilder = QueryBuilders.bool();

        priQueryBuilder.must(QueryBuilders.term(builder -> builder.field("type").value(MainContext.QuickType.PRI.toString())));
        priQueryBuilder.must(QueryBuilders.term(builder -> builder.field("creater").value(creater)));

        quickQueryBuilder.should(priQueryBuilder.build()._toQuery());

        boolQueryBuilder.must(quickQueryBuilder.build()._toQuery());

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));
        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title", HighlightFieldParameters.builder().withFragmentSize(200).build()));
        searchQueryBuilder.withHighlightQuery(new HighlightQuery(new Highlight(fields), null));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(0, 10000));
        if (operations.indexOps(QuickReply.class).exists()) {
            pages = SearchTools.listUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)));
        }
        return pages;
    }


    @Override
    public Page<QuickReply> getByQuicktype(String quicktype, final int p, final int ps) {

        Page<QuickReply> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("type").value(quicktype)));

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));

        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title", HighlightFieldParameters.builder().withFragmentSize(200).build()));
        searchQueryBuilder.withHighlightQuery(new HighlightQuery(new Highlight(fields), null));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(QuickReply.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByCateAndUser(String cate, String q, String user, final int p, final int ps) {

        Page<QuickReply> pages = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withQuery(QueryBuilders.term(builder -> builder.field("creater").value(user)))
            .withSort(Sort.by(Order.desc("top"), Order.desc("updatetime")));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(QuickReply.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByCon(BoolQuery.Builder boolQueryBuilder, final int p, final int ps) {

        Page<QuickReply> pages = null;

        BoolQuery.Builder beginFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("begintime").build()._toQuery())
            .should(QueryBuilders.range().term(new TermRangeQuery.Builder().field("begintime").from(
                String.valueOf(System.currentTimeMillis())).build()).build()._toQuery());
        BoolQuery.Builder endFilter = QueryBuilders.bool().should(QueryBuilders.exists().field("endtime").build()._toQuery())
            .should(QueryBuilders.range().term(new TermRangeQuery.Builder().field("endtime").to(
                String.valueOf(System.currentTimeMillis())).build()).build()._toQuery());

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withFilter(QueryBuilders.bool().must(beginFilter.build()._toQuery()).must(endFilter.build()._toQuery()).build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));

        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(p, ps));
        if (operations.indexOps(QuickReply.class).exists()) {
            pages = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)),
                searchQuery.getPageable());
        }
        return pages;
    }

    @Override
    public Page<QuickReply> getByOrgiAndType(String orgi, String type, String q, Pageable page) {

        Page<QuickReply> list = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("type").value(type)));
        }

        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("createtime")));
        Query searchQuery = searchQueryBuilder.build().setPageable(page);
        if (operations.indexOps(QuickReply.class).exists()) {
            list = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)),
                searchQuery.getPageable());
        }
        return list;
    }

    @Override
    public void deleteByCate(String cate, String orgi) {
        NativeQueryBuilder deleteQuery = new NativeQueryBuilder();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));
        deleteQuery.withQuery(boolQueryBuilder.build()._toQuery());
        operations.delete(DeleteQuery.builder(deleteQuery.build()).build(), QuickReply.class);
    }

    @Override
    public List<QuickReply> getQuickReplyByOrgi(String orgi, String cate, String type, String q) {

        List<QuickReply> list = null;

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));

        if (!StringUtils.isBlank(cate)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("cate").value(cate)));
        }
        if (!StringUtils.isBlank(type)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("type").value(type)));
        }
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }

        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder().withQuery(boolQueryBuilder.build()._toQuery())
            .withSort(Sort.by(Order.desc("top"), Order.desc("updatetime")));
        Query searchQuery = searchQueryBuilder.build().setPageable(PageRequest.of(0, 10000));
        if (operations.indexOps(QuickReply.class).exists()) {
            list = SearchTools.listUnwrapSearchHits(
                operations.search(searchQuery, QuickReply.class,
                    operations.getIndexCoordinatesFor(QuickReply.class)));
        }
        return list;
    }
}
