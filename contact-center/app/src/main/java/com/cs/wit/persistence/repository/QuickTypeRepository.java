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

import com.cs.wit.model.QuickType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuickTypeRepository extends JpaRepository<QuickType, String> {

	QuickType findByIdAndOrgi(String id, String orgi);

	int countByNameAndOrgi(String name, String orgi);

	/**
	 * 获取所有的公共分类
	 */
    List<QuickType> findByOrgiAndQuicktype(String orgi, String quicktype) ;

	/**
	 * 获取个人分类
	 */
    List<QuickType> findByOrgiAndQuicktypeAndCreater(String orgi, String quicktype, String creater) ;

	int countByOrgiAndNameAndParentid(String orgi, String name, String parentid) ;

	QuickType findByOrgiAndName(String orgi, String name);

}
