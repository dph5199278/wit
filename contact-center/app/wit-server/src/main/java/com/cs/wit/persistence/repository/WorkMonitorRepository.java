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

import com.cs.wit.model.WorkMonitor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkMonitorRepository extends JpaRepository<WorkMonitor, String> {
	
	WorkMonitor findByIdAndOrgi(String id, String orgi);
	
	Page<WorkMonitor> findByAgentAndOrgi(String agent, String orgi, Pageable paramPageable);

	List<WorkMonitor> findByOrgi(String orgi) ;
	
	int countByAgentAndDatestrAndStatusAndOrgi(String agent, String datestr, String status, String orgi) ;

	List<WorkMonitor> findByOrgiAndAgentAndDatestrAndFirsttime(String orgi, String agent, String datestr, boolean firsttime) ;
	
	//分页按条件查询
	Page<WorkMonitor> findAll(Specification<WorkMonitor> spec, Pageable pageable);
	
	
	
}
