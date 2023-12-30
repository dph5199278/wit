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

import com.cs.wit.model.EntCustomer;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.util.es.SearchTools;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EntCustomerRepositoryImpl implements EntCustomerEsCommonRepository {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final ElasticsearchOperations operations;

    @Override
    public Page<EntCustomer> findByCreaterAndSharesAndOrgi(String creater, String shares, String orgi, boolean includeDeleteData, String q, Pageable page) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        BoolQuery.Builder boolQueryBuilder1 = QueryBuilders.bool();
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("creater").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value("all")));
        boolQueryBuilder.must(boolQueryBuilder1.build()._toQuery());
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (includeDeleteData) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.TRUE.toString())));
        } else {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.FALSE.toString())));
        }
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        return processQuery(boolQueryBuilder, page);
    }

    @Override
    public Page<EntCustomer> findByCreaterAndSharesAndOrgi(String creater,
                                                           String shares, String orgi, Date begin, Date end, boolean includeDeleteData,
                                                           BoolQuery.Builder boolQueryBuilder, String q, Pageable page) {
        BoolQuery.Builder boolQueryBuilder1 = QueryBuilders.bool();
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("creater").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value("all")));
        boolQueryBuilder.must(boolQueryBuilder1.build()._toQuery());
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (includeDeleteData) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.TRUE.toString())));
        } else {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.FALSE.toString())));
        }
        RangeQuery.Builder rangeQuery = QueryBuilders.range().field("createtime");
        if (begin != null) {
            rangeQuery.from(String.valueOf(begin.getTime()));
        }
        if (end != null) {
            rangeQuery.to(String.valueOf(end.getTime()));
        } else {
            rangeQuery.to(String.valueOf(System.currentTimeMillis()));
        }
        if (begin != null || end != null) {
            boolQueryBuilder.must(rangeQuery.build()._toQuery());
        }
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        return processQuery(boolQueryBuilder, page);
    }

    @Override
    public Page<EntCustomer> findByCreaterAndSharesAndOrgi(String creater, String shares, String orgi, Date begin, Date end, boolean includeDeleteData, String q, Pageable page) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        BoolQuery.Builder boolQueryBuilder1 = QueryBuilders.bool();
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("creater").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value(creater)));
        boolQueryBuilder1.should(QueryBuilders.term(builder -> builder.field("shares").value("all")));
        boolQueryBuilder.must(boolQueryBuilder1.build()._toQuery());
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        if (includeDeleteData) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.TRUE.toString())));
        } else {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.FALSE.toString())));
        }
        RangeQuery.Builder rangeQuery = QueryBuilders.range().field("createtime");
        if (begin != null) {
            rangeQuery.from(DATE_FORMAT.format(begin));
        }
        if (end != null) {
            rangeQuery.to(DATE_FORMAT.format(end));
        } else {
            rangeQuery.to(DATE_FORMAT.format(new Date()));
        }
        if (begin != null || end != null) {
            boolQueryBuilder.must(rangeQuery.build()._toQuery());
        }
        if (!StringUtils.isBlank(q)) {
            boolQueryBuilder.must(QueryBuilders.queryString().query(q).defaultOperator(Operator.And).build()._toQuery());
        }
        return processQuery(boolQueryBuilder, page);
    }


    private Page<EntCustomer> processQuery(BoolQuery.Builder boolQueryBuilder, Pageable page) {
        NativeQueryBuilder searchQueryBuilder = new NativeQueryBuilder()
            .withQuery(boolQueryBuilder.build()._toQuery());

        page = PageRequest.of(page.getPageNumber(), page.getPageSize(),
            Sort.by(Order.desc("creater"), Order.desc("name")).and(page.getSort()));

        searchQueryBuilder.withPageable(page);

        Page<EntCustomer> entCustomerList = null;
        if (operations.indexOps(EntCustomer.class).exists()) {
            final NativeQuery searchQuery = searchQueryBuilder.build();
            entCustomerList = SearchTools.pageUnwrapSearchHits(
                operations.search(searchQuery, EntCustomer.class,
                    operations.getIndexCoordinatesFor(EntCustomer.class)),
                page);
        }
        if (entCustomerList != null && entCustomerList.getContent().size() > 0) {
            List<String> ids = new ArrayList<>();
            for (EntCustomer entCustomer : entCustomerList.getContent()) {
                if (entCustomer.getCreater() != null && ids.size() < 1024) {
                    ids.add(entCustomer.getCreater());
                }
            }
            List<User> users = userRes.findAllById(ids);
            for (EntCustomer entCustomer : entCustomerList.getContent()) {
                for (User user : users) {
                    if (user.getId().equals(entCustomer.getCreater())) {
                        entCustomer.setUser(user);
                        break;
                    }
                }
            }
        }
        return entCustomerList;
    }
}
