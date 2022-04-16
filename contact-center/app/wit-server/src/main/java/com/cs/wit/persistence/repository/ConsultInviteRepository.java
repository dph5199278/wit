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

import com.cs.wit.model.CousultInvite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsultInviteRepository
  extends JpaRepository<CousultInvite, String>
{
  @Query(value = "SELECT * from uk_consult_invite WHERE snsaccountid = ?1 AND orgi = ?2 LIMIT 1", nativeQuery = true)
  CousultInvite findBySnsaccountidAndOrgi(@Param("snsaccountid") String Snsaccountid, @Param("orgi") String orgi);

  List<CousultInvite> findByOrgi(String orgi);
}


