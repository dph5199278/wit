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

import com.cs.wit.model.Contacts;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Component
@RequiredArgsConstructor
public class ContactsRepositoryImpl implements ContactsEsCommonRepository {
    @NonNull
    private final UserRepository userRes;
    @NonNull
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<Contacts> findByCreaterAndSharesAndOrgi(String creater, String shares, String orgi, boolean includeDeleteData, String q, Pageable page) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder1 = new BoolQueryBuilder();
        boolQueryBuilder1.should(termQuery("creater", creater));
        boolQueryBuilder1.should(termQuery("shares", creater));
        boolQueryBuilder1.should(termQuery("shares", "all"));
        boolQueryBuilder.must(boolQueryBuilder1);
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (includeDeleteData) {
            boolQueryBuilder.must(termQuery("datastatus", true));
        } else {
            boolQueryBuilder.must(termQuery("datastatus", false));
        }
        if (StringUtils.isNotBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        return processQuery(boolQueryBuilder, page);
    }

    @Override
    public Page<Contacts> findByCreaterAndSharesAndOrgi(String creater,
                                                        String shares, String orgi, Date begin, Date end, boolean includeDeleteData,
                                                        BoolQueryBuilder boolQueryBuilder, String q, Pageable page) {
        BoolQueryBuilder boolQueryBuilder1 = new BoolQueryBuilder();
        boolQueryBuilder1.should(termQuery("creater", creater));
        boolQueryBuilder1.should(termQuery("shares", creater));
        boolQueryBuilder1.should(termQuery("shares", "all"));
        boolQueryBuilder.must(boolQueryBuilder1);
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (includeDeleteData) {
            boolQueryBuilder.must(termQuery("datastatus", true));
        } else {
            boolQueryBuilder.must(termQuery("datastatus", false));
        }
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createtime");
        if (begin != null) {
            rangeQuery.from(begin.getTime());
        }
        if (end != null) {
            rangeQuery.to(end.getTime());
        } else {
            rangeQuery.to(new Date().getTime());
        }
        if (begin != null || end != null) {
            boolQueryBuilder.must(rangeQuery);
        }
        if (StringUtils.isNotBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        return processQuery(boolQueryBuilder, page);
    }

    @Override
    public Page<Contacts> findByOrgi(String orgi, boolean includeDeleteData,
                                     String q, Pageable page) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (includeDeleteData) {
            boolQueryBuilder.must(termQuery("datastatus", true));
        } else {
            boolQueryBuilder.must(termQuery("datastatus", false));
        }
        if (StringUtils.isNotBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        return processQuery(boolQueryBuilder, page);
    }

    @Override
    public Page<Contacts> findByCreaterAndSharesAndOrgi(String creater, String shares, String orgi, Date begin, Date end, boolean includeDeleteData, String q, Pageable page) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder1 = new BoolQueryBuilder();
        boolQueryBuilder1.should(termQuery("creater", creater));
        boolQueryBuilder1.should(termQuery("shares", creater));
        boolQueryBuilder1.should(termQuery("shares", "all"));
        boolQueryBuilder.must(boolQueryBuilder1);
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (includeDeleteData) {
            boolQueryBuilder.must(termQuery("datastatus", true));
        } else {
            boolQueryBuilder.must(termQuery("datastatus", false));
        }
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createtime");
        if (begin != null) {
            rangeQuery.from(begin.getTime());
        }
        if (end != null) {
            rangeQuery.to(end.getTime());
        } else {
            rangeQuery.to(new Date().getTime());
        }
        if (begin != null || end != null) {
            boolQueryBuilder.must(rangeQuery);
        }
        if (StringUtils.isNotBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        return processQuery(boolQueryBuilder, page);
    }


    private Page<Contacts> processQuery(BoolQueryBuilder boolQueryBuilder, Pageable page) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(new FieldSortBuilder("creater").unmappedType("boolean").order(SortOrder.DESC)).withSort(new FieldSortBuilder("name").unmappedType("string").order(SortOrder.DESC));

        searchQueryBuilder.withPageable(page);

        Page<Contacts> entCustomerList = null;
        if (elasticsearchRestTemplate.indexOps(Contacts.class).exists()) {
            entCustomerList = elasticsearchRestTemplate.queryForPage(searchQueryBuilder.build(), Contacts.class, elasticsearchRestTemplate.getIndexCoordinatesFor(
                    Contacts.class));
        }
        if (entCustomerList != null && entCustomerList.getContent().size() > 0) {
            List<String> ids = new ArrayList<>();
            for (Contacts contacts : entCustomerList.getContent()) {
                if (contacts.getCreater() != null && ids.size() < 1024) {
                    ids.add(contacts.getCreater());
                }
            }
            List<User> users = userRes.findAllById(ids);
            for (Contacts contacts : entCustomerList.getContent()) {
                for (User user : users) {
                    if (user.getId().equals(contacts.getCreater())) {
                        contacts.setUser(user);
                        break;
                    }
                }
            }
        }
        return entCustomerList;
    }

    @Override
    public Page<Contacts> findByDataAndOrgi(String orgi, String q, Pageable page) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQuery("datastatus", false));
        boolQueryBuilder.must(termQuery("orgi", orgi));
        if (StringUtils.isNotBlank(q)) {
            boolQueryBuilder.must(new QueryStringQueryBuilder(q).defaultOperator(Operator.AND));
        }
        return processQuery(boolQueryBuilder, page);
    }
}
