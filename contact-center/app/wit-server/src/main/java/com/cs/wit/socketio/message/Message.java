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

package com.cs.wit.socketio.message;

import com.cs.compose4j.AbstractContext;
import com.cs.wit.model.AgentService;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AgentUserContacts;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.OnlineUser;
import com.cs.wit.model.SNSAccount;
import com.cs.wit.model.User;
import java.io.Serializable;
import java.util.List;

/**
 * 发送消息的高级封装
 */
public class Message extends AbstractContext {

    public String id;
    // 租户
    private String orgi;
    /**
     * 发送方向：IN 访客给坐席，OUT 坐席给访客
     * NOTE callType应尽早设置
     */
    private String calltype;
    // 消息类型 [必填]
    private String messageType;
    private SNSAccount snsAccount;

    private Serializable channelMessage;

    // 渠道信息
    // 渠道类型
    private String channel;
    // 渠道应用ID
    private String appid;

    private String attachmentid;
    // 是否有坐席，用于为新访客分配坐席的一个flag
    private boolean noagent;

    // 访客坐席会话
    private AgentUser agentUser;
    // 坐席状态
    private AgentStatus agentStatus;
    // 访客会话服务
    private AgentService agentService;
    // 此值倾向于发送给前端，后端接口直接使用agentService传对象
    private String agentserviceid;
    // 访客
    private OnlineUser onlineUser;
    // 坐席
    private User agent;
    // 会话监控人员
    private User supervisor;

    // 访客关联的联系人信息
    // 访客关联的联系人
    private Contacts contact;
    // 会话联系关联信息
    private AgentUserContacts agentUserContacts;

    // 会话ID
    // 会话周期
    private String session;
    // 上下文ID
    private String contextid;
    // 创建时间
    private String createtime;
    // 消息签名
    private String sign;

    // 消息属性
    // 文本
    private String message;
    // 文件名
    private String filename;
    // 文件大小
    private int filesize;

    // boolean 处理结果
    // 该请求是否被正常处理
    private boolean isResolved;

    private List<OtherMessageItem> suggest;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public SNSAccount getSnsAccount() {
        return snsAccount;
    }

    public void setSnsAccount(SNSAccount snsAccount) {
        this.snsAccount = snsAccount;
    }

    public AgentUser getAgentUser() {
        return agentUser;
    }

    public void setAgentUser(AgentUser agentUser) {
        this.agentUser = agentUser;
    }

    public Serializable getChannelMessage() {
        return channelMessage;
    }

    public void setChannelMessage(Serializable channelMessage) {
        this.channelMessage = channelMessage;
    }

    public String getContextid() {
        return contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getAttachmentid() {
        return attachmentid;
    }

    public void setAttachmentid(String attachmentid) {
        this.attachmentid = attachmentid;
    }

    public boolean isNoagent() {
        return noagent;
    }

    public void setNoagent(boolean noagent) {
        this.noagent = noagent;
    }

    public List<OtherMessageItem> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<OtherMessageItem> suggest) {
        this.suggest = suggest;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public AgentService getAgentService() {
        return agentService;
    }

    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    public OnlineUser getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(OnlineUser onlineUser) {
        this.onlineUser = onlineUser;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Contacts getContact() {
        return contact;
    }

    public void setContact(Contacts contact) {
        this.contact = contact;
    }

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public AgentUserContacts getAgentUserContacts() {
        return agentUserContacts;
    }

    public void setAgentUserContacts(AgentUserContacts agentUserContacts) {
        this.agentUserContacts = agentUserContacts;
    }

    public String getAgentserviceid() {
        return agentserviceid;
    }

    public void setAgentserviceid(String agentserviceid) {
        this.agentserviceid = agentserviceid;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }
}
