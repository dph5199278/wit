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
package com.cs.wit.util.es;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import co.elastic.clients.elasticsearch._types.query_dsl.TermRangeQuery;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.model.FormFilterItem;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.persistence.impl.ESDataExchangeImpl;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;

/**
 * The type Search tools.
 */
public class SearchTools {

    public static PageImpl<UKDataBean> search(String orgi, List<FormFilterItem> itemList, MetadataTable metadataTable, boolean loadRef, int p, int ps) throws IOException {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));

        BoolQuery.Builder orBuilder = QueryBuilders.bool();
        int orNums = 0;
        for (FormFilterItem formFilterItem : itemList) {
            Query tempQueryBuilder = null;
            if ("q".equals(formFilterItem.getField())) {
                tempQueryBuilder = QueryBuilders.queryString().query(formFilterItem.getValue()).defaultOperator(Operator.And).build()._toQuery();
            } else {
                switch (formFilterItem.getCond()) {
                    case "01":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).from(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "02":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).from(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "03":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).to(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "04":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).to(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "05":
                        tempQueryBuilder = QueryBuilders.term(builder -> builder.field(formFilterItem.getField()).value(formFilterItem.getValue()));
                        break;
                    case "06":
                        tempQueryBuilder = QueryBuilders.term(builder -> builder.field(formFilterItem.getField()).value(formFilterItem.getValue()));
                        break;
                    case "07":
                        tempQueryBuilder = QueryBuilders.queryString().query(formFilterItem.getValue()).fields(formFilterItem.getField()).defaultOperator(Operator.And).build()._toQuery();
                        break;
                    default:
                        break;
                }
            }
            if ("AND".equalsIgnoreCase(formFilterItem.getComp())) {
                if ("06".equals(formFilterItem.getCond())) {
                    queryBuilder.mustNot(tempQueryBuilder);
                } else {
                    queryBuilder.must(tempQueryBuilder);
                }
            } else {
                orNums++;
                if ("06".equals(formFilterItem.getCond())) {
                    orBuilder.mustNot(tempQueryBuilder);
                } else {
                    orBuilder.should(tempQueryBuilder);
                }
            }
        }
        if (orNums > 0) {
            queryBuilder.must(orBuilder.build()._toQuery());
        }

        return search(queryBuilder, metadataTable, loadRef, p, ps);
    }

    public static PageImpl<UKDataBean> dissearch(String orgi, List<FormFilterItem> itemList, MetadataTable metadataTable, int p, int ps) throws IOException {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("status").value(MainContext.NamesDisStatusType.NOT.toString())));
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("validresult").value("valid")));

        BoolQuery.Builder orBuilder = QueryBuilders.bool();
        int orNums = 0;
        for (FormFilterItem formFilterItem : itemList) {
            Query tempQueryBuilder = null;
            if ("q".equals(formFilterItem.getField())) {
                tempQueryBuilder = QueryBuilders.queryString().query(formFilterItem.getValue()).defaultOperator(Operator.And).build()._toQuery();
            } else {
                switch (formFilterItem.getCond()) {
                    case "01":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).from(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "02":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).from(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "03":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).to(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "04":
                        tempQueryBuilder = QueryBuilders.range().term(new TermRangeQuery.Builder().field(formFilterItem.getField()).to(formFilterItem.getValue()).build()).build()._toQuery();
                        break;
                    case "05":
                        tempQueryBuilder = QueryBuilders.term(builder -> builder.field(formFilterItem.getField()).value(formFilterItem.getValue()));
                        break;
                    case "06":
                        tempQueryBuilder = QueryBuilders.term(builder -> builder.field(formFilterItem.getField()).value(formFilterItem.getValue()));
                        break;
                    case "07":
                        tempQueryBuilder = QueryBuilders.queryString().query(formFilterItem.getValue()).fields(formFilterItem.getField()).defaultOperator(Operator.And).build()._toQuery();
                        break;
                    default:
                        break;
                }
            }
            if ("AND".equalsIgnoreCase(formFilterItem.getComp())) {
                if ("06".equals(formFilterItem.getCond())) {
                    queryBuilder.mustNot(tempQueryBuilder);
                } else {
                    queryBuilder.must(tempQueryBuilder);
                }
            } else {
                orNums++;
                if ("06".equals(formFilterItem.getCond())) {
                    orBuilder.mustNot(tempQueryBuilder);
                } else {
                    orBuilder.should(tempQueryBuilder);
                }
            }
        }
        if (orNums > 0) {
            queryBuilder.must(orBuilder.build()._toQuery());
        }
        return search(queryBuilder, metadataTable, false, p, ps);
    }

    public static PageImpl<UKDataBean> recoversearch(String orgi, String cmd, String id, MetadataTable metadataTable, int p, int ps) throws IOException {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        queryBuilder.mustNot(QueryBuilders.term(builder -> builder.field("status").value(MainContext.NamesDisStatusType.NOT.toString())));
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("validresult").value("valid")));

        switch (cmd) {
            case "actid":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("actid").value(id)));
                break;
            case "batid":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("batid").value(id)));
                break;
            case "taskid":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("taskid").value(id)));
                break;
            case "filterid":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("filterid").value(id)));
                break;
            case "agent":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field(Constants.CSKEFU_SYSTEM_DIS_AGENT).value(id)));
                break;
            case "skill":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field(Constants.CSKEFU_SYSTEM_DIS_ORGAN).value(id)));
                break;
            case "taskskill":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("taskid").value(id)))
                    .must(QueryBuilders.term(builder -> builder.field("status").value(MainContext.NamesDisStatusType.DISAGENT.toString())));
                break;
            case "filterskill":
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("filterid").value(id)))
                    .must(QueryBuilders.term(builder -> builder.field("status").value(MainContext.NamesDisStatusType.DISAGENT.toString())));
                break;
            default:
                //必须传入一个进来;
                queryBuilder.must(QueryBuilders.term(builder -> builder.field("actid").value("NOT_EXIST_KEY")));
        }

        return search(queryBuilder, metadataTable, false, p, ps);
    }


    /**
     *
     */
    public static PageImpl<UKDataBean> namesearch(String orgi, String phonenum) throws IOException {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("orgi").value(orgi)));
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("validresult").value("valid")));
        queryBuilder.must(QueryBuilders.term(builder -> builder.field("status").value(MainContext.NamesDisStatusType.DISAGENT.toString())));
        StringBuilder strb = new StringBuilder();
        if (!StringUtils.isBlank(phonenum)) {
            strb.append(phonenum);
            if (phonenum.startsWith("0")) {
                strb.append(" ").append(phonenum.substring(1));
            }
        } else {
            strb.append(Constants.CSKEFU_SYSTEM_NO_DAT);
        }
        queryBuilder.must(QueryBuilders.queryString().query(strb.toString()).defaultOperator(Operator.Or).build()._toQuery());
        return search(queryBuilder, 0, 1);
    }

    /**
     *
     */
    public static PageImpl<UKDataBean> search(BoolQuery.Builder queryBuilder, int p, int ps) throws IOException {
        return search(queryBuilder, null, true, p, ps);
    }

    /**
     *
     */
    private static PageImpl<UKDataBean> search(BoolQuery.Builder queryBuilder, MetadataTable metadataTable, boolean loadRef, int p, int ps) throws IOException {
        ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
        return esDataExchange.findPageResult(queryBuilder, metadataTable, PageRequest.of(p, ps, Sort.Direction.ASC, "createtime"), loadRef);
    }

    /**
     *
     */
    public static PageImpl<UKDataBean> aggregation(BoolQuery.Builder queryBuilder, String aggField, boolean loadRef, int p, int ps) throws IOException {
        ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
        return esDataExchange.findAllPageAggResult(queryBuilder, aggField, PageRequest.of(p, ps, Sort.Direction.ASC, "createtime"), loadRef, null);
    }

    /**
     *
     */
    public static UKDataBean get(UKDataBean dataBean) throws IOException {
        ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
        return esDataExchange.getIObjectByPK(dataBean);
    }

    /**
     *
     */
    public static UKDataBean get(String type, String id) throws IOException {
        ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
        return esDataExchange.getIObjectByPK(type, id);
    }

    /**
     *
     */
    public static void save(UKDataBean dataBean) {
        ESDataExchangeImpl esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
        try {
            esDataExchange.saveIObject(dataBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Page unwrap search hits page.
     *
     * @param <T>        the type parameter
     * @param searchHits the search hits
     * @param pageable   the pageable
     * @return the page
     */
    public static <T> Page<T> pageUnwrapSearchHits(SearchHits<T> searchHits, Pageable pageable) {
        return (Page<T>) SearchHitSupport.unwrapSearchHits(SearchHitSupport.searchPageFor(searchHits, pageable));
    }

    /**
     * List unwrap search hits list.
     *
     * @param <T>        the type parameter
     * @param searchHits the search hits
     * @return the list
     */
    public static <T> List<T> listUnwrapSearchHits(SearchHits<T> searchHits) {
        return (List<T>) SearchHitSupport.unwrapSearchHits(searchHits);
    }
}
