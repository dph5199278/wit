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
package com.cs.wit.persistence.repository;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Util;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataSourceService {

    @NonNull
    private final DataSource dataSource;

    /**
     * @param xml
     * @throws Exception
     */
    public Connection service(String xml) {
        Connection dataSourceObject = null;
        StringBuffer strb = new StringBuffer();
        Util.PropertyList properties = Util.parseConnectString(strb.append("Provider=mondrian;")
                .append(
                        "Catalog=").append(xml).append(";").toString());
        if (properties != null) {
            dataSourceObject = DriverManager.getConnection(properties, null, dataSource);
        }
        return dataSourceObject;
    }
}
