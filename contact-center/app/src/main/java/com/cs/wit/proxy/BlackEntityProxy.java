/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.cs.wit.proxy;

import com.cs.wit.cache.Cache;
import com.cs.wit.model.AgentService;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class BlackEntityProxy {

    @NonNull
    private final BlackListRepository blackListRes;

    @NonNull
    private final Cache cache;

    @NonNull
    private final AgentServiceRepository agentServiceRes;


    /**
     * 更新或创建黑名单记录
     */
    public void updateOrCreateBlackEntity(
            final BlackEntity pre,
            final User owner,
            final String userid,
            final String orgi,
            final String agentserviceid) {
        final BlackEntity blackEntityUpdated = cache.findOneBlackEntityByUserIdAndOrgi(
                userid, orgi).orElseGet(
                () -> {
                    BlackEntity p = new BlackEntity();
                    p.setUserid(userid);
                    p.setOrgi(orgi);
                    p.setCreater(owner.getId());
                    return p;
                });

        blackEntityUpdated.setAgentid(owner.getId());
        blackEntityUpdated.setAgentserviceid(agentserviceid);
        blackEntityUpdated.setControltime(pre.getControltime());

        if (StringUtils.isNotBlank(pre.getDescription())) {
            blackEntityUpdated.setDescription(pre.getDescription());
        }

        if (blackEntityUpdated.getControltime() > 0) {
            blackEntityUpdated.setEndtime(
                    new Date(System.currentTimeMillis() + pre.getControltime() * 3600 * 1000L));
        }

        AgentService agentService = agentServiceRes.findByIdAndOrgi(agentserviceid, orgi);
        if (agentService != null) {
            blackEntityUpdated.setChannel(agentService.getChannel());
            blackEntityUpdated.setAgentuser(agentService.getUsername());
            blackEntityUpdated.setSessionid(agentService.getSessionid());
            if (agentService.getSessiontimes() != 0) {
                blackEntityUpdated.setChattime((int) agentService.getSessiontimes());
            } else {
                blackEntityUpdated.setChattime((int) (System.currentTimeMillis() - agentService.getServicetime().getTime()));
            }
        }

        blackListRes.save(blackEntityUpdated);

    }
}
