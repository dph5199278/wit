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
package com.cs.wit.cache;

import com.cs.wit.aspect.AgentUserAspect;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.exception.CSKefuCacheException;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserAudit;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.model.CousultInvite;
import com.cs.wit.model.JobDetail;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.SysDic;
import com.cs.wit.persistence.repository.AgentUserRepository;
import com.cs.wit.persistence.repository.OnlineUserRepository;
import com.cs.wit.util.SerializeUtil;
import com.cs.wit.util.freeswitch.model.CallCenterAgent;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Cache {

    final static private Logger logger = LoggerFactory.getLogger(Cache.class);

    @NonNull
    private final OnlineUserRepository onlineUserRes;

    @NonNull
    private final AgentUserRepository agentUserRes;

    @NonNull
    private final RedisCommand redisCommand;

    /**
     * Inline??????
     */
    private static Map<String, AgentStatus> convertFromStringToAgentStatus(final Map<String, String> map) {
        Map<String, AgentStatus> result = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            AgentStatus obj = SerializeUtil.deserialize(entry.getValue());
            result.put(entry.getKey(), obj);
        }
        return result;
    }

    private static OnlineUser convertFromStringToOnlineUser(final String serialized) {
        return SerializeUtil.deserialize(serialized);
    }

    /**
     * ???????????????????????????
     *
     * @param orgi ??????
     */
    public Map<String, AgentStatus> getAgentStatusReadyByOrig(final String orgi) {
        Map<String, String> agentStatuses = redisCommand.getHash(RedisKey.getAgentStatusReadyHashKey(orgi));
        return convertFromStringToAgentStatus(agentStatuses);
    }

    /**
     * ????????????ID???ORGI??????????????????????????????
     */
    public Optional<AgentUser> findOneAgentUserByUserIdAndOrgi(final String userId, final String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userId)) {
            // ???????????????
            return Optional.ofNullable(SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), userId)) {
            // ?????????
            return Optional.ofNullable(SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserInServHashKey(orgi), userId)));
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(orgi), userId)) {
            // ????????????
            return Optional.ofNullable(SerializeUtil.deserialize(
                    redisCommand.getHashKV(RedisKey.getAgentUserEndHashKey(orgi), userId)));
        } else {
            // ????????????????????????????????????????????????
            return agentUserRes.findOneByUseridAndOrgi(userId, orgi);
        }
    }

    /**
     * ??????????????????????????????
     */
    public Map<String, AgentUser> getAgentUsersInQueByOrgi(final String orgi) {
        Map<String, String> agentUsers = redisCommand.getHash(RedisKey.getAgentUserInQueHashKey(orgi));
        Map<String, AgentUser> map = new HashMap<>();
        for (final Map.Entry<String, String> entry : agentUsers.entrySet()) {
            final AgentUser obj = SerializeUtil.deserialize(entry.getValue());
            map.put(Objects.requireNonNull(obj).getId(), obj);
        }
        return map;
    }

    /**
     * ?????????ID???????????????????????????
     * TODO ?????????????????????????????????????????????
     */
    public void deleteAgentUserInservByAgentUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), userid);
    }

    /**
     * ?????????ID????????????????????????
     */
    public void deleteAgentUserInqueByAgentUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), userid);
    }

    /**
     * ???????????????????????????
     *
     * @param agentno ??????ID
     * @param orgi    ??????ID
     */
    public AgentStatus findOneAgentStatusByAgentnoAndOrig(final String agentno, final String orgi) {
        String status = getAgentStatusStatus(agentno, orgi);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] agentno {}, status {}", agentno, status);

        // ????????????????????????????????????????????????????????????
        if (StringUtils.equals(status, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            return null;
        }

        String val = redisCommand.getHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, status), agentno);
        AgentStatus result = SerializeUtil.deserialize(val);
        logger.debug("[findOneAgentStatusByAgentnoAndOrig] result: username {}", Objects.requireNonNull(result).getUsername());
        return result;
    }

    /**
     * ??????????????????
     */
    public void putAgentStatusByOrgi(AgentStatus agentStatus, String orgi) {
        String pre = getAgentStatusStatus(agentStatus.getAgentno(), orgi); // ???????????????

        if (StringUtils.equals(pre, MainContext.AgentStatusEnum.OFFLINE.toString())) {
            // ??????????????????????????????
            if ((!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString()))) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(orgi, agentStatus.getStatus()),
                        agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
            }
        } else {
            // ?????????????????????????????????????????????
            if (StringUtils.equals(pre, agentStatus.getStatus())) {
                redisCommand.setHashKV(
                        RedisKey.getAgentStatusHashKeyByStatusStr(orgi, pre), agentStatus.getAgentno(),
                        SerializeUtil.serialize(agentStatus));
            } else {
                // ??????????????????????????????????????????
                redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, pre), agentStatus.getAgentno());
                if (!StringUtils.equals(agentStatus.getStatus(), MainContext.AgentStatusEnum.OFFLINE.toString())) {
                    redisCommand.setHashKV(
                            RedisKey.getAgentStatusHashKeyByStatusStr(orgi, agentStatus.getStatus()),
                            agentStatus.getAgentno(), SerializeUtil.serialize(agentStatus));
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public Map<String, AgentStatus> findAllReadyAgentStatusByOrgi(final String orgi) {
        List<String> keys = new ArrayList<>();
        keys.add(RedisKey.getAgentStatusReadyHashKey(orgi));

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }

    /**
     * ???????????????????????????????????????
     */
    public Map<String, AgentStatus> findAllAgentStatusByOrgi(final String orgi) {
        List<String> keys = new ArrayList<>();
        // TODO ????????????????????????
        keys.add(RedisKey.getAgentStatusReadyHashKey(orgi));
        keys.add(RedisKey.getAgentStatusNotReadyHashKey(orgi));

        Map<String, String> map = redisCommand.getAllMembersInMultiHash(keys);
        return convertFromStringToAgentStatus(map);
    }

    /**
     * Delete Agent Status
     */
    public void deleteAgentStatusByAgentnoAndOrgi(final String agentno, final String orgi) {
        String status = getAgentStatusStatus(agentno, orgi);
        if (!StringUtils.equals(MainContext.AgentStatusEnum.OFFLINE.toString(), status)) {
            redisCommand.delHashKV(RedisKey.getAgentStatusHashKeyByStatusStr(orgi, status), agentno);
        }
    }

    /**
     * ??????????????????????????? agentStatus.status
     * ?????????????????????
     */
    private String getAgentStatusStatus(final String agentno, final String orgi) {
        // ????????????????????????????????????READY??????BUSY???????????????
        if (redisCommand.hasHashKV(RedisKey.getAgentStatusReadyHashKey(orgi), agentno)) {
            return MainContext.AgentStatusEnum.READY.toString();
        } else if (redisCommand.hasHashKV(RedisKey.getAgentStatusNotReadyHashKey(orgi), agentno)) {
            return MainContext.AgentStatusEnum.NOTREADY.toString();
        } else {
            return MainContext.AgentStatusEnum.OFFLINE.toString();
        }
    }

    /**
     * ??????????????????????????????
     */
    public List<AgentStatus> getAgentStatusBySkillAndOrgi(final String skill, final String orgi) {
        Map<String, AgentStatus> map = findAllAgentStatusByOrgi(orgi);
        List<AgentStatus> agentList = new ArrayList<>();

        for (Map.Entry<String, AgentStatus> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(skill)) {
                if (entry.getValue().getSkills() != null &&
                        entry.getValue().getSkills().containsKey(skill)) {
                    agentList.add(entry.getValue());
                }
            } else {
                agentList.add(entry.getValue());
            }
        }
        return agentList;
    }


    //**************************
    //* AgentUser??????
    //**************************

    /**
     * ??????????????????????????????????????????
     */
    public int getAgentStatusReadySizeByOrgi(final String orgi) {
        return Math.toIntExact(redisCommand.getHashSize(RedisKey.getAgentStatusReadyHashKey(orgi)));
    }

    /**
     * ??????????????????????????????
     * TODO ????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????
     * ????????????????????????"??????"????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param agentUser ?????????agentUser?????????
     */
    @AgentUserAspect.LinkAgentUser
    public void putAgentUserByOrgi(AgentUser agentUser, String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid())) {
            // ?????????
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INSERVICE.toString())) {
                // ???????????????
                redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid());
            }
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid())) {
            // ????????????
            if (!StringUtils.equals(
                    agentUser.getStatus(),
                    MainContext.AgentUserStatusEnum.INQUENE.toString())) {
                // ???????????????
                redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid());
            }
        }

        // ?????????????????????????????????END???agentUser????????????????????????????????????
        if (!StringUtils.equals(agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            redisCommand.setHashKV(
                    RedisKey.getAgentUserHashKeyByStatusStr(orgi, agentUser.getStatus()), agentUser.getUserid(),
                    SerializeUtil.serialize(agentUser));
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    public List<AgentUser> findInservAgentUsersByAgentnoAndOrgi(final String agentno, final String orgi) {
        logger.info("[findInservAgentUsersByAgentnoAndOrgi] agentno {}, orgi {}", agentno, orgi);
        List<AgentUser> result = new ArrayList<>();
        List<String> ids = redisCommand.getSet(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi));
        if (ids.size() == 0) { // no inserv agentUser
            return result;
        } else {
            result = agentUserRes.findAllByUserids(ids);
        }

        return result;
    }

    /**
     * ??????????????????????????????????????????
     */
    public int getInservAgentUsersSizeByAgentnoAndOrgi(final String agentno, final String orgi) {
        return Math.toIntExact(redisCommand.getSetSize(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi)));
    }

    /**
     * ?????????????????????????????????
     */
    public int getInservAgentUsersSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getAgentUserInServHashKey(orgi));
    }

    /**
     * ?????????????????????????????????
     */
    public int getInqueAgentUsersSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getAgentUserInQueHashKey(orgi));
    }

    /**
     * Delete agentUser
     */
    @AgentUserAspect.LinkAgentUser
    public void deleteAgentUserByUserIdAndOrgi(final AgentUser agentUser, String orgi) {
        if (redisCommand.hasHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid())) {
            // ???????????????
            redisCommand.delHashKV(RedisKey.getAgentUserInQueHashKey(orgi), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserInServHashKey(orgi), agentUser.getUserid());
        } else if (redisCommand.hasHashKV(RedisKey.getAgentUserEndHashKey(orgi), agentUser.getUserid())) {
            redisCommand.delHashKV(RedisKey.getAgentUserEndHashKey(orgi), agentUser.getUserid());
        }
        // TODO ?????????????????????????????????
    }

    /***************************
     * CousultInvite ??????
     ***************************/
    public void putConsultInviteByOrgi(final String orgi, final CousultInvite cousultInvite) {
        redisCommand.setHashKV(
                RedisKey.getConsultInvitesByOrgi(orgi), cousultInvite.getSnsaccountid(),
                SerializeUtil.serialize(cousultInvite));
    }

    public CousultInvite findOneConsultInviteBySnsidAndOrgi(final String snsid, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getConsultInvitesByOrgi(orgi), snsid);
        if (StringUtils.isBlank(serialized)) {
            return null;
        } else {
            return (CousultInvite) SerializeUtil.deserialize(serialized);
        }
    }


    /****************************
     *  OnlineUser??????
     ****************************/

    public void deleteConsultInviteBySnsidAndOrgi(final String snsid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getConsultInvitesByOrgi(orgi), snsid);
    }

    /**
     * ?????? onlineUser
     */
    public void putOnlineUserByOrgi(final OnlineUser onlineUser, final String orgi) {
        // ??????onlineUser???id ??? onlineUser userId??????
        redisCommand.setHashKV(
                RedisKey.getOnlineUserHashKey(orgi), onlineUser.getId(), SerializeUtil.serialize(onlineUser));
    }

    /**
     * ?????? onlineUser
     */
    public OnlineUser findOneOnlineUserByUserIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getOnlineUserHashKey(orgi), id);
        if (StringUtils.isBlank(serialized)) {
            // query with MySQL
            return onlineUserRes.findOneByUseridAndOrgi(id, orgi);
        } else {
            return convertFromStringToOnlineUser(serialized);
        }
    }

    /**
     * ?????? onlineUser
     */
    public void deleteOnlineUserByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getOnlineUserHashKey(orgi), id);
    }

    /**
     * ????????????ID?????????????????????????????????
     */
    public int getOnlineUserSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getOnlineUserHashKey(orgi));
    }


    /**
     * ??????????????????????????????????????????????????????
     */
    public void deleteOnlineUserIdFromAgentStatusByUseridAndAgentnoAndOrgi(final String userid, final String agentno, final String orgi) {
        redisCommand.removeSetVal(RedisKey.getInServAgentUsersByAgentnoAndOrgi(agentno, orgi), userid);
    }

    private Map<String, OnlineUser> convertFromStringToOnlineUsers(final Map<String, String> map) {
        Map<String, OnlineUser> result = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            OnlineUser x = SerializeUtil.deserialize(entry.getValue());
            result.put(entry.getKey(), x);
        }
        return result;
    }


    //******************************
    //* Callcenter Agent ??????
    //******************************

    /**
     * ??????CallCenterAgent
     */
    public void putCallCenterAgentByIdAndOrgi(final String id, final String orgi, final CallCenterAgent agent) {
        redisCommand.setHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id, SerializeUtil.serialize(agent));
    }

    /**
     * ??????ID?????????ID??????CallCenterAgent
     */
    public CallCenterAgent findOneCallCenterAgentByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            return (CallCenterAgent) SerializeUtil.deserialize(serialized);
        } else {
            return null;
        }
    }

    /**
     * ??????CallCenterAgent
     */
    public void deleteCallCenterAgentByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi), id);
    }


    /**
     * ????????????ID???????????????CallCenterAgent
     */
    public Map<String, CallCenterAgent> findAllCallCenterAgentsByOrgi(final String orgi) {
        Map<String, String> map = redisCommand.getHash(RedisKey.getCallCenterAgentHashKeyByOrgi(orgi));
        Map<String, CallCenterAgent> result = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey(), SerializeUtil.deserialize(entry.getValue()));
        }

        return result;
    }

    /**
     * ???????????????
     */
    // ????????????????????????????????????
    public void putBlackEntityByOrgi(final BlackEntity blackEntity, final String orgi) {
        redisCommand.setHashKV(
                RedisKey.getBlackEntityKeyByOrgi(orgi), blackEntity.getUserid(), SerializeUtil.serialize(blackEntity));
    }

    // ?????????????????????????????????????????????
    public Optional<BlackEntity> findOneBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        String ser = redisCommand.getHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
        if (StringUtils.isBlank(ser)) {
            return Optional.empty();
        }

        return Optional.ofNullable(SerializeUtil.deserialize(ser));
    }

    // ????????????????????????????????????
    public void deleteBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        redisCommand.delHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
    }

    // ?????????????????????????????????????????????
    public boolean existBlackEntityByUserIdAndOrgi(final String userid, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getBlackEntityKeyByOrgi(orgi), userid);
    }

    // ????????????ID??????????????????????????????
    public Map<String, BlackEntity> findAllBlackEntityByOrgi(final String orgi) {
        Map<String, BlackEntity> result = new HashMap<>();
        for (Map.Entry<String, String> entry : redisCommand.getHash(
                RedisKey.getBlackEntityKeyByOrgi(orgi)).entrySet()) {
            result.put(entry.getKey(), SerializeUtil.deserialize(entry.getValue()));
        }
        return result;
    }


    /*****************************
     * Job ??????
     *****************************/
    public void putJobByIdAndOrgi(final String jobId, final String orgi, final JobDetail job) {
        redisCommand.setHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId, SerializeUtil.serialize(job));
    }

    public JobDetail findOneJobByIdAndOrgi(final String jobId, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);

        if (StringUtils.isNotBlank(serialized)) {
            return (JobDetail) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public boolean existJobByIdAndOrgi(final String jobId, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);
    }

    public void deleteJobByJobIdAndOrgi(final String jobId, final String orgi) {
        redisCommand.delHashKV(RedisKey.getJobHashKeyByOrgi(orgi), jobId);
    }

    /**
     * ??????????????????
     */
    // ???????????????
    public void putSysDicByOrgi(final String id, final String orgi, final SysDic sysDic) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id, SerializeUtil.serialize(sysDic));
    }

    // ????????????????????????????????????
    public void eraseSysDicByOrgi(final String orgi) {
        redisCommand.delete(RedisKey.getSysDicHashKeyByOrgi(orgi));
    }

    // ??????????????????
    public void putSysDicByOrgi(final String code, final String orgi, final List<SysDic> sysDics) {
        redisCommand.setHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code, SerializeUtil.serialize(sysDics));
    }

    // ???????????????????????????
    public List<SysDic> getSysDicItemsByCodeAndOrgi(final String code, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code);
        if (serialized != null) {
            //noinspection unchecked
            return (List<SysDic>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // ??????????????????
    public SysDic findOneSysDicByCodeAndOrgi(final String code, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), code);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // ????????????
    public SysDic findOneSysDicByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);

        if (StringUtils.isBlank(serialized)) {
            return null;
        }

        return (SysDic) SerializeUtil.deserialize(serialized);
    }

    // ????????????
    public void putSysDicByOrgi(List<SysDic> vals, final String orgi) {
        Map<String, String> map = new HashMap<>();
        for (final SysDic dic : vals) {
            map.put(dic.getId(), SerializeUtil.serialize(dic));
        }
        redisCommand.hmset(RedisKey.getSysDicHashKeyByOrgi(orgi), map);
    }

    public void deleteSysDicByIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);
    }

    public boolean existSysDicByIdAndOrgi(final String id, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getSysDicHashKeyByOrgi(orgi), id);
    }

    /**
     * System ??????
     */
    public <T extends Serializable> void putSystemByIdAndOrgi(final String id, final String orgi, final T obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public <T extends Serializable> void putSystemListByIdAndOrgi(final String id, final String orgi, final List<T> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public <TK, TV extends Serializable> void putSystemMapByIdAndOrgi(final String id, final String orgi, final Map<TK, TV> obj) {
        redisCommand.setHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id, SerializeUtil.serialize(obj));
    }

    public boolean existSystemByIdAndOrgi(final String id, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
    }

    public void deleteSystembyIdAndOrgi(final String id, final String orgi) {
        redisCommand.delHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
    }

    public <T extends Serializable> T findOneSystemByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            //noinspection unchecked
            return (T) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <T extends Serializable> List<T> findOneSystemListByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            //noinspection unchecked
            return (List<T>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public <TK, TV extends Serializable> Map<TK, TV> findOneSystemMapByIdAndOrgi(final String id, final String orgi) {
        String serialized = redisCommand.getHashKV(RedisKey.getSystemHashKeyByOrgi(orgi), id);
        if (StringUtils.isNotBlank(serialized)) {
            //noinspection unchecked
            return (Map<TK, TV>) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    // ????????????cache???????????????
    public int getSystemSizeByOrgi(final String orgi) {
        return redisCommand.getHashSize(RedisKey.getSystemHashKeyByOrgi(orgi));
    }

    /**************************
     * Session Config ??????
     **************************/

    public void putSessionConfigByOrgi(final SessionConfig sessionConfig, final String orgi) {
        redisCommand.put(RedisKey.getSessionConfig(orgi), SerializeUtil.serialize(sessionConfig));
    }

    public SessionConfig findOneSessionConfigByOrgi(final String orgi) {
        String serialized = redisCommand.get(RedisKey.getSessionConfig(orgi));
        if (StringUtils.isNotBlank(serialized)) {
            return (SessionConfig) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    public void deleteSessionConfigByOrgi(final String orgi) {
        redisCommand.delete(RedisKey.getSessionConfig(orgi));
    }

    public boolean existSessionConfigByOrgi(final String orgi) {
        return redisCommand.exists(RedisKey.getSessionConfig(orgi));
    }

    public void putSessionConfigListByOrgi(final List<SessionConfig> lis, final String orgi) {
        redisCommand.put(RedisKey.getSessionConfigList(orgi), SerializeUtil.serialize(lis));
    }

    public List<SessionConfig> findOneSessionConfigListByOrgi(final String orgi) {
        String serialized = redisCommand.get(RedisKey.getSessionConfigList(orgi));
        if (StringUtils.isNotBlank(serialized)) {
            //noinspection unchecked
            return (List<SessionConfig>) SerializeUtil.deserialize(serialized);
        }

        return null;
    }

    public void deleteSessionConfigListByOrgi(final String orgi) {
        redisCommand.delete(RedisKey.getSessionConfigList(orgi));
    }

    public boolean existSessionConfigListByOrgi(final String orgi) {
        return redisCommand.exists(RedisKey.getSessionConfigList(orgi));
    }

    /******************************************
     * Customer Chats Audit ??????
     ******************************************/
    public void putAgentUserAuditByOrgi(final String orgi, final AgentUserAudit audit) throws CSKefuCacheException {
        if (StringUtils.isBlank(audit.getAgentUserId())) {
            throw new CSKefuCacheException("agentUserId is required.");
        }
        redisCommand.setHashKV(
                RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), audit.getAgentUserId(), SerializeUtil.serialize(audit));
    }

    public void deleteAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        redisCommand.delHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
    }

    public Optional<AgentUserAudit> findOneAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        logger.info("[findOneAgentUserAuditByOrgiAndId] orgi {}, agentUserId {}", orgi, agentUserId);
        String serialized = redisCommand.getHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
        if (StringUtils.isBlank(serialized)) {
            return Optional.empty();
        }
        return Optional.ofNullable(SerializeUtil.deserialize(serialized));
    }

    public boolean existAgentUserAuditByOrgiAndId(final String orgi, final String agentUserId) {
        return redisCommand.hasHashKV(RedisKey.getCustomerChatsAuditKeyByOrgi(orgi), agentUserId);
    }


    //******************************************
    //* User Session ??????
    //******************************************

    /**
     * ??????user???session???????????????????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public void putUserSessionByAgentnoAndSessionIdAndOrgi(final String agentno, final String sessionId, final String orgi) {
        redisCommand.setHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno, sessionId);
    }

    public boolean existUserSessionByAgentnoAndOrgi(final String agentno, final String orgi) {
        return redisCommand.hasHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

    public String findOneSessionIdByAgentnoAndOrgi(final String agentno, final String orgi) {
        return redisCommand.getHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

    public void deleteUserSessionByAgentnoAndOrgi(final String agentno, final String orgi) {
        redisCommand.delHashKV(RedisKey.getUserSessionKeyByOrgi(orgi), agentno);
    }

    public void putConnectAlive(String orgi, String userid) {
        redisCommand.put(MessageFormat.format("ALIVE::{0}::{1}", orgi, userid), "ALIVE", Duration.ofSeconds(Constants.WEBIM_SOCKETIO_AGENT_ONLINE_THRESHOLD));
    }

    public boolean existConnectAlive(String orgi, String userid) {
        return redisCommand.exists(MessageFormat.format("ALIVE::{0}::{1}", orgi, userid));
    }
}
