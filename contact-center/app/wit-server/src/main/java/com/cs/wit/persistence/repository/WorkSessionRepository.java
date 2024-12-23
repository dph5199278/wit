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

import com.cs.wit.model.WorkSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSessionRepository extends JpaRepository<WorkSession, String> {
	
	WorkSession findByIdAndOrgi(String id, String orgi);
	
	Page<WorkSession> findByAgentAndOrgi(String agent, String orgi, Pageable paramPageable);

	List<WorkSession> findByOrgi(String orgi) ;

	List<WorkSession> findByOrgiAndClientid(String orgi, String clientid) ;
	
	//分页按条件查询
	Page<WorkSession> findAll(Specification<WorkSession> spec, Pageable pageable);
	
	int countByAgentAndDatestrAndOrgi(String agent, String datestr, String orgi) ;
}
