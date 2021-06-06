/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

import com.cs.wit.model.CallOutDialplan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallOutDialplanRepository extends JpaRepository<CallOutDialplan, String> {
    boolean existsById(String id);

    /**
     * Updating Entities with Update Query in Spring Data JPA
     * https://codingexplained.com/coding/java/spring-framework/updating-entities-with-update-query-spring-data-jpa
     * @param id
     * @return
     */
    @Query("UPDATE CallOutDialplan t set t.executed = t.executed + 1 WHERE t.id = :id")
    int increExecuted(@Param("id") String id);

    List<CallOutDialplan> findByStatusAndIsarchive(String status, boolean isarchive);

    Page<CallOutDialplan> findAllByIsarchiveNot(boolean isarchive, Pageable pageRequest);

    Page<CallOutDialplan> findByIsarchive(boolean isarchive, Pageable pageRequest);
}
