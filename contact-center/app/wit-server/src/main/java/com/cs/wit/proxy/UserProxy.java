/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */

package com.cs.wit.proxy;

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.model.Organ;
import com.cs.wit.model.OrganUser;
import com.cs.wit.model.Role;
import com.cs.wit.model.RoleAuth;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.OrganUserRepository;
import com.cs.wit.persistence.repository.RoleAuthRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.util.Md5Utils;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * 用户/坐席 常用方法
 */
@Component
@RequiredArgsConstructor
public class UserProxy {
    private final static Logger logger = LoggerFactory.getLogger(UserProxy.class);

    @NonNull
    private final OrganUserRepository organUserRes;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final UserRepository userRes;

    @NonNull
    private final RoleAuthRepository roleAuthRes;

    /**
     * 创建新用户
     * 支持多租户
     */
    public String createNewUser(final User user, final String orgi, final String orgid, final String orgiByTenantshare) {
        String msg;
        msg = validUser(user);
        if (StringUtils.isNotBlank(msg) && !msg.equals("new_user_success")) {
            return msg;
        } else {
            // 此时 msg 是 new_user_success
            // 不支持创建第二个系统管理员
            user.setSuperadmin(false);

            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(Md5Utils.doubleMd5(user.getPassword()));
            }

            user.setOrgi(orgiByTenantshare);
            if (StringUtils.isNotBlank(orgid)) {
                user.setOrgid(orgid);
            } else {
                user.setOrgid(MainContext.SYSTEM_ORGI);
            }
            userRes.save(user);
            OnlineUserProxy.clean(orgi);
        }
        return msg;
    }


    @NonNull
    public User findOne(final String id) {
        return userRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", id)));
    }

    public List<String> findUserIdsInOrgan(final String organ) {
        List<OrganUser> x = organUserRes.findByOrgan(organ);

        if (x.size() == 0) {
            return null;
        }

        List<String> z = new ArrayList<>();
        for (final OrganUser y : x) {
            z.add(y.getUserid());
        }
        return z;
    }

    public List<String> findUserIdsInOrgans(final List<String> organs) {

        List<OrganUser> x = organUserRes.findByOrganIn(organs);

        if (x.size() == 0) return null;

        Set<String> y = new HashSet<>();

        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }

        return new ArrayList<>(y);

    }


    /**
     * 通过坐席ID查找其技能组Map
     */
    public HashMap<String, String> getSkillsMapByAgentno(final String agentno) {

        final User user = userRes.findById(agentno).orElse(null);
        if (user == null) return new HashMap<>();

        attachOrgansPropertiesForUser(user);
        return user.getSkills();
    }

    /**
     * 获得一个用户的直属组织机构
     */
    public List<String> findOrgansByUserid(final String userid) {
        List<OrganUser> x = organUserRes.findByUserid(userid);

        if (x.size() == 0) return null;

        List<String> y = new ArrayList<>();

        for (final OrganUser z : x) {
            y.add(z.getOrgan());
        }

        return y;
    }


    public Page<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users, pageRequest);

    }

    public List<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByAgentAndDatastatusAndIdIn(agent, datastatus, users);
    }

    public List<User> findByOrganInAndDatastatus(
            final List<String> organs,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return userRes.findByDatastatusAndIdIn(datastatus, users);
    }

    public Page<User> findByOrganInAndDatastatusAndUsernameLike(
            final List<String> organs,
            final boolean datastatus,
            final String username,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);
        if (users == null) return null;
        return userRes.findByDatastatusAndUsernameLikeAndIdIn(datastatus, username, users, pageRequest);
    }

    public List<User> findByOrganAndOrgiAndDatastatus(final String organ, final String orgi, final boolean datastatus) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findByOrgiAndDatastatusAndIdIn(orgi, datastatus, users);

    }

    /**
     * 检查用户更新是否合理
     */
    public String validUserUpdate(final User user, final User oldUser) {
        String msg = "edit_user_success";
        User tempUser = userRes.findByUsernameAndDatastatus(user.getUsername(), false);

        if (!StringUtils.equals(user.getUsername(), oldUser.getUsername()) && tempUser != null && (!StringUtils.equals(
                oldUser.getId(), tempUser.getId()))) {
            // 用户名发生变更，并且数据库里有其它用户占用该用户名
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRes.findByEmailAndDatastatus(user.getEmail(), false);
            if (!StringUtils.equals(user.getEmail(), oldUser.getEmail()) && tempUser != null && (!StringUtils.equals(
                    oldUser.getId(), tempUser.getId()))) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRes.findByMobileAndDatastatus(user.getMobile(), false);
            if (!StringUtils.equals(user.getMobile(), oldUser.getMobile()) && tempUser != null && (!StringUtils.equals(
                    oldUser.getId(), tempUser.getId()))) {
                msg = "mobile_exist";
                return msg;
            }
        }

        if (user.isCallcenter() && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            if (StringUtils.isNotBlank(user.getSipaccount())) {
                Optional<User> opt = userRes.findOneBySipaccountAndDatastatus(
                        user.getSipaccount(), false);
                if (opt.isPresent() && (!StringUtils.equals(opt.get().getId(), user.getId()))) {
                    msg = "sip_account_exist";
                }
            }
        }

        return msg;
    }

    /**
     * 从Json中创建User
     */
    public User parseUserFromJson(final JsonObject json) {
        User tempUser = new User();

        // 手机号
        if (json.has("id")) {
            String val = json.get("id").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setId(val);
            }
        }

        // 用户名，用于登录
        if (json.has("username")) {
            String val = json.get("username").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setUsername(val);
            }
        }

        // 姓名
        if (json.has("uname")) {
            String val = json.get("uname").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setUname(val);
            }
        }

        // 邮件
        if (json.has("email")) {
            String val = json.get("email").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setEmail(val);
            }
        }

        // 手机号
        if (json.has("mobile")) {
            String val = json.get("mobile").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setMobile(val);
            }
        }

        // 密码
        if (json.has("password")) {
            String val = json.get("password").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setPassword(val);
            }
        }

        // 是否是坐席
        if (json.has("agent")) {
            String val = json.get("agent").getAsString();
            tempUser.setAgent(StringUtils.isNotBlank(val) && StringUtils.equals("1", val));
        } else {
            tempUser.setAgent(false);
        }

        // 是否是管理员
        if (json.has("admin")) {
            String val = json.get("admin").getAsString();
            if (StringUtils.isNotBlank(val) && StringUtils.equals("1", val)) {
                // 管理员默认就是坐席
                tempUser.setAdmin(true);
                tempUser.setAgent(true);
            } else {
                tempUser.setAdmin(false);
            }
        } else {
            tempUser.setAdmin(false);
        }

        // 是否是呼叫中心
        if (json.has("callcenter")) {
            String val = json.get("callcenter").getAsString();
            tempUser.setCallcenter(StringUtils.isNotBlank(val) && StringUtils.equals("1", val));
        } else {
            tempUser.setCallcenter(false);
        }

        // 是否有SIP电话
        if (json.has("sipAccount")) {
            String val = json.get("sipAccount").getAsString();
            if (StringUtils.isNotBlank(val)) {
                tempUser.setSipaccount(val);
            }
        }

        // 不允许创建系统管理员
        tempUser.setSuperadmin(false);

        return tempUser;
    }

    /**
     * 验证用户数据合法性
     */
    public String validUser(final User user) {
        String msg = "new_user_success";
        User tempUser = userRes.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null) {
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRes.findByEmailAndDatastatus(user.getEmail(), false);
            if (tempUser != null) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRes.findByMobileAndDatastatus(user.getMobile(), false);
            if (tempUser != null) {
                msg = "mobile_exist";
                return msg;
            }
        }

        if (user.isCallcenter() && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            if (StringUtils.isNotBlank(user.getSipaccount())) {
                if (userRes.findOneBySipaccountAndDatastatus(
                        user.getSipaccount(), false).isPresent()) {
                    msg = "sip_account_exist";
                    return msg;
                }
            }
        }

        return msg;
    }


    public List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndOrgan(final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if(null == users
        || users.isEmpty()) {
            return null;
        }

        return userRes.findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(users);

    }

    /**
     * 或取Sips列表
     */
    public List<String> findSipsByOrganAndDatastatusAndOrgi(final String organ, final boolean datastatus, final String orgi) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return userRes.findSipsByDatastatusAndOrgiAndIdIn(datastatus, orgi, users);
    }


    /**
     * 通过租户ID，是否为坐席，是否有效和组织机构查询坐席数
     */
    public long countByOrgiAndAgentAndDatastatusAndOrgan(
            final boolean agent,
            final boolean datastatus,
            final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return 0;

        return userRes.countByAgentAndDatastatusAndIdIn(agent, datastatus, users);

    }

    /**
     * 增加用户的角色信息
     */
    public void attachRolesMap(final User user) {
        // 获取用户的角色权限，进行授权
        List<RoleAuth> roleAuthList = roleAuthRes.findAll((Specification<RoleAuth>) (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();
            if (user.getRoleList() != null && user.getRoleList().size() > 0) {
                for (Role role : user.getRoleList()) {
                    criteria.add(cb.equal(root.get("roleid").as(String.class), role.getId()));
                }
            }
            Predicate[] p = new Predicate[criteria.size()];
            cb.and(cb.equal(root.get("orgi").as(String.class), user.getOrgi()));
            return cb.or(criteria.toArray(p));
        });

        // clear previous auth map values, ensure the changes are token effect in real time.
        user.getRoleAuthMap().clear();
        if (roleAuthList != null) {
            for (RoleAuth roleAuth : roleAuthList) {
                user.getRoleAuthMap().put(roleAuth.getDicvalue(), true);
            }
        }
    }

    /**
     * 获得一个部门及其子部门并添加到User的myorgans中
     */
    public void processAffiliates(final User user, final Organ organ) {
        if (organ == null) {
            return;
        }

        if (user.inAffiliates(organ.getId())) {
            return;
        }

        user.getAffiliates().add(organ.getId());

        // 获得子部门
        List<Organ> y = organRes.findByOrgiAndParent(user.getOrgi(), organ.getId());

        for (Organ x : y) {
            try {
                // 递归调用
                processAffiliates(user, x);
            } catch (Exception e) {
                logger.error("processAffiliates", e);
            }
        }
    }

    /**
     * 获取用户部门以及下级部门
     */
    public void attachOrgansPropertiesForUser(final User user) {
        List<OrganUser> organs = organUserRes.findByUserid(user.getId());
        user.setOrgans(new HashMap<>());
        user.setAffiliates(new HashSet<>());

        final HashMap<String, String> skills = new HashMap<>();

        for (final OrganUser organ : organs) {
            // 添加直属部门到organs
            String id = organ.getOrgan();
            final Organ o = organRes.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Organization %s not found", id)));
            user.getOrgans().put(id, o);
            if (o.isSkill()) {
                skills.put(o.getId(), o.getName());
            }

            // 添加部门及附属部门
            processAffiliates(user, o);
        }

        user.setSkills(skills);
    }
}
