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

package com.cs.wit.model;

import com.cs.wit.basic.MainUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 *
 */
@Document(indexName = "kbs_topic")
@Entity
@Table(name = "uk_kbs_topic")
@org.hibernate.annotations.Proxy(lazy = false)
public class KbsTopic extends ESBean implements java.io.Serializable , UKAgg{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */

	private String id  = MainUtils.getUUID();

	private String sessionid ;

	//标题
	private String title ;
	//内容
	private String content ;
	//微信渠道回复
	private String weixin;
	//邮件渠道回复
	private String email ;
	//短信回复
	private String sms ;
	//语音播报回复
	private String tts ;

	//问题价格
	private float price ;
	//关键词
	private String keyword ;
	//摘要
	private String summary ;
	//标签
	private String tags ;
	//是否匿名提问
	private boolean anonymous ;
	//逻辑删除
	private boolean datastatus ;

	//是否已经审批通过
	private boolean approval ;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd HH:mm:ss.SSS")
	//有效期开始
	private Date begintime ;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd HH:mm:ss.SSS")
	//有效期结束
	private Date endtime ;

	//是否置顶
	private boolean top ;
	//是否精华
	private boolean essence ;
	//是否已采纳最佳答案
	private boolean accept ;
	//结贴
	private boolean finish;

	//回答数量
	private int answers;

	@Column(name = "sviews")
	//阅读数量
	private int views;
	//关注数量
	private int followers;
	//收藏数量
	private int collections;
	//评论数量
	private int comments;
	//是否移动端提问
	private boolean frommobile;
	//	状态
	private String status;
	//知识分类
	private String tptype;
	//知识 栏目
	private String cate;
	//附件
	private String attachment;

	private String username;
	private String orgi;
	private String creater;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd HH:mm:ss.SSS")
	private Date createtime = new Date();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd HH:mm:ss.SSS")
	private Date updatetime = new Date();
	private String memo;
	private String organ;

	private int rowcount ;
	private String key;


	private User user;


	/**
	 * @return the id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")
	public String getId() {
		return id;
	}

	@Transient
	public String getSessionid() {
		return sessionid;
	}


	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getTptype() {
		return tptype;
	}

	public void setTptype(String tptype) {
		this.tptype = tptype;
	}

	public int getAnswers() {
		return answers;
	}

	public void setAnswers(int answers) {
		this.answers = answers;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public int getCollections() {
		return collections;
	}

	public void setCollections(int collections) {
		this.collections = collections;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public boolean isFrommobile() {
		return frommobile;
	}

	public void setFrommobile(boolean frommobile) {
		this.frommobile = frommobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public boolean isEssence() {
		return essence;
	}

	public void setEssence(boolean essence) {
		this.essence = essence;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	@Transient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getBegintime() {
		return begintime;
	}

	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getTts() {
		return tts;
	}

	public void setTts(String tts) {
		this.tts = tts;
	}
	@Transient
	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	@Transient
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public boolean isDatastatus() {
		return datastatus;
	}

	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}

	public boolean isApproval() {
		return approval;
	}

	public void setApproval(boolean approval) {
		this.approval = approval;
	}
}
