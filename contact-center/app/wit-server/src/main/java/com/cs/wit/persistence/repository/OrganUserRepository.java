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

import com.cs.wit.model.OrganUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrganUserRepository extends JpaRepository<OrganUser, String>
{
	
	List<OrganUser> findByOrgi(final String orgi);

	OrganUser findByUseridAndOrgan(final String userid, final String organ);

	List<OrganUser> findByUserid(final String userid);

	List<OrganUser> findByUseridAndOrgi(final String userid, final String orgi);

	List<OrganUser> findByOrgan(final String organ);

	List<OrganUser> findByOrganIn(final List<String> organs);

	List<OrganUser> findByOrganAndOrgi(final String organ, final String orgi);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM OrganUser o WHERE o.userid = :userid AND o.organ = :organ")
	void deleteOrganUserByUseridAndOrgan(@Param("userid") final String userid,
										 @Param("organ") final String organ);
}

