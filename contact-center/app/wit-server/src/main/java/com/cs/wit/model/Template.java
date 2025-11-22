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

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author iceworld
 *
 */
@Entity
@Table(name = "uk_templet")
public class Template implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1946579239823440392L;
	private String id ;
	private String name ;
	//修改用处，变更为 SysDic 的 code
	private String code ;
	private String userid ;					
	private String groupid ;			
	private String description ;
	//邮件头
	private String templettitle;
	private String templettext ;
	//List OR Preview
	private String templettype ;
	private Date createtime = new Date();
	private String orgi ;
	//模板图标
	private String iconstr;
	//模板说明内容
	private String memo ;
	//分组id
	private String typeid;
	//列数
	private int layoutcols ;
	//样例数据
	private String datatype ;
	//报表类型
	private String charttype ;
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTemplettext() {
		return templettext;
	}
	public void setTemplettext(String templettext) {
		this.templettext = templettext;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getTemplettype() {
		return templettype;
	}
	public void setTemplettype(String templettype) {
		this.templettype = templettype;
	}
	@Transient
	public String getTitle(){
		return this.groupid ;
	}
	public String getIconstr() {
		return iconstr;
	}
	public void setIconstr(String iconstr) {
		this.iconstr = iconstr;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getTemplettitle() {
		return templettitle;
	}
	public void setTemplettitle(String templettitle) {
		this.templettitle = templettitle;
	}
	public int getLayoutcols() {
		return layoutcols;
	}
	public void setLayoutcols(int layoutcols) {
		this.layoutcols = layoutcols;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getCharttype() {
		return charttype;
	}
	public void setCharttype(String charttype) {
		this.charttype = charttype;
	}
	
}
