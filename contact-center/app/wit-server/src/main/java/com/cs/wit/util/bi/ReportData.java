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
package com.cs.wit.util.bi;

import com.cs.wit.util.bi.model.Level;
import com.cs.wit.util.bi.model.RequestData;
import com.cs.wit.util.bi.model.ValueData;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.nodes.PathQueryNode.QueryText;


public interface ReportData extends Serializable{
	Level getRow() ;
	Level getCol() ;
	void setRow(Level level) ;
	List<List<ValueData>> getData() ;
	String getViewData() ;
	void setPageSize(int pageSize);
	int getPageSize() ;
	int getPage();
	void setPage(int page) ;
	void setViewData(String viewData) ;
	void exchangeColRow() ;	//行列转换
	void merge(ReportData data) ;
	Date getDate() ;
	void setDate(Date createtime) ;
	void setException(Exception ex) ;
	Exception getException ();
	RequestData getRequestData();
	void setRequestData(RequestData data);
	
	QueryText getQueryText() ;
	void setQueryText(QueryText queryText) ;
	
	void setTotal(long total) ;
	long getTotal() ;
	
	Map<String , Object> getOptions() ;
	
	void setOptions(Map<String , Object> options);
	
	void setQueryTime(long queryTime) ;
	
	long getQueryTime() ;
}
