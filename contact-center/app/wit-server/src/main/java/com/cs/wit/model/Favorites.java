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

package com.cs.wit.model;

import com.cs.wit.basic.MainUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "favorites", createIndex = false)
@Entity
@Table(name = "uk_favorites")
@org.hibernate.annotations.Proxy(lazy = false)
public class Favorites implements java.io.Serializable {
    private static final long serialVersionUID = 8667838872697390231L;
    private String id = MainUtils.getUUID();
    private String name;
    private String code = "true";
    private String title;
    private String model;
    @Field(type = FieldType.Text, store = true)
    // @Parent(type = "uk_workorders")
    private String orderid;
    private WorkOrders workOrders;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "8uuuu-MM-dd HH:mm:ss.SSS")
    private Date createtime = new Date();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "8uuuu-MM-dd HH:mm:ss.SSS")
    private Date updatetime = new Date();
    private String creater;
    private String username;
    private String orgi;

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "assigned")
    public String getId() {
        return id;
    }

    @Transient
    public WorkOrders getWorkOrders() {
        return workOrders;
    }
}
