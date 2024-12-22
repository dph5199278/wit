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

import com.cs.wit.basic.Constants;
import com.cs.wit.model.ColumnProperties;
import com.cs.wit.util.IdUtils;
import com.cs.wit.util.bi.CubeReportData;
import com.cs.wit.util.bi.model.FirstTitle;
import com.cs.wit.util.bi.model.Level;
import com.cs.wit.util.bi.model.ValueData;
import com.cs.wit.util.template.FreemarkerUtils;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mondrian.olap.Axis;
import mondrian.olap.Cell;
import mondrian.olap.Connection;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Result;
import mondrian.rolap.RolapCubeLevel;
import mondrian.rolap.RolapLevel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class CubeService {
	private DataSourceService dataSource ;
	
	private static final String SCHEMA_DATA_PATH = "WEB-INF/data/mdx/";
	private File schemaFile = null;
	
	
	public CubeService(String xml , String path , DataSourceService dataSource , Map<String,Object> requestValues) throws IOException, TemplateException {
		this(xml, path, dataSource, requestValues, false);
	}
	
	public CubeService(String xml , String path , DataSourceService dataSource , Map<String,Object> requestValues,boolean isContentStr) throws IOException, TemplateException {
		this.dataSource = dataSource ;

		this.schemaFile = new File(Optional
				.of(new File(path , "mdx"))
				.stream().peek(File::mkdirs)
				.findAny()
				.get(),
				IdUtils.getUUID() + ".xml");

		String template = "";
		if(isContentStr) {
			// 为模板内容直接解析为字符串
			template = FreemarkerUtils.getTemplate(xml, requestValues);
		}else {
			// 为模板路径，先加载内容再解析为字符串
			StringWriter writer = new StringWriter();
			IOUtils.copy(Objects.requireNonNull(
					CubeService.class.getClassLoader().getResourceAsStream(SCHEMA_DATA_PATH + xml)), writer, StandardCharsets.UTF_8);
			template = FreemarkerUtils.getTemplate(writer.toString(), requestValues);
		}

		// 使用系统默认编码输出到临时文件
		FileUtils.write(schemaFile, template, StandardCharsets.UTF_8);
	}
	
	public CubeReportData execute(String mdx) throws Exception {
		return execute(mdx , null) ;
	}

	public CubeReportData execute(String mdx,List<ColumnProperties> cols) throws Exception {
		Connection connection = null ;
		CubeReportData cubeReportData = new CubeReportData();
		try{
			connection = dataSource.service(schemaFile.getAbsolutePath());
			Result result = connection
					.execute(connection.parseQuery(mdx));
			Axis[] axises = result.getAxes();
			cubeReportData.setData(new ArrayList<>());
			for (int i = 0; i < axises.length; i++) {
				if (i == 0) {
					cubeReportData.setCol(createTitle(axises[i], i , cols));
				} else {
					cubeReportData.setRow(createTitle(axises[i], i , cols));
				}
			}

			if(Optional.of(cubeReportData)
					.map(CubeReportData::getRow)
					.isEmpty()) {
				Level rowLevel = Optional.of(
					Optional.of(new Level("合计","row", null , 0))
						.stream()
						.collect(Collectors.toList())
				)
				.map(title -> {
					List<List<Level>> list = new ArrayList<>();
					list.add(title);
					return list;
				})
				.map(title -> {
					Level level = new Level("root","row", null , 0);
					level.setTitle(title);
					return level;
				})
				.get();

				cubeReportData.setRow(rowLevel);
			}
			// 获取行数据
			getRowData(axises, axises.length - 1, new int[axises.length], result, cubeReportData.getData(), 0 , null , cubeReportData , cols);
			// 处理合计行
			processSum(cubeReportData.getRow(), cubeReportData.getData() , cubeReportData.getRow());
			// 处理合计列
			processSum(cubeReportData.getCol(), cubeReportData.getData() , cubeReportData.getCol());
			// 清除行标题
			cubeReportData.getRow().setTitle(new ArrayList<>()) ;
			// 处理标题
			processTitle(cubeReportData.getRow() , cubeReportData.getRow());
			
		}
		finally{
			if(connection!=null){
				connection.close();
			}
			if(schemaFile.exists()){
				schemaFile.delete();
			}
		}
		return cubeReportData ;
	}
	
	@SuppressWarnings("rawtypes")
	public Level createTitle(Axis axis, int index,List<ColumnProperties> cols) {
		Level level = new Level("root", index == 0 ? "col" : "row" , null , 0);
		Map<String, Map> valueMap = new HashMap<String, Map>();
		List<Position> posList = axis.getPositions();
		List<String> valueStr = new ArrayList<String>();
		List<FirstTitle> firstTitle = new ArrayList<FirstTitle>();
		for (Position pos : posList) {
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < pos.size(); i++) {
				Member member = pos.get(i);
				RolapLevel cubeLevel = (RolapLevel) member.getLevel();
				int n = 0;
				if(member.getLevel() instanceof RolapCubeLevel && !cubeLevel.getName().contains("All")){
					if(level.getFirstTitle()==null){
						level.setFirstTitle(firstTitle);
						FirstTitle first = new FirstTitle();
						first.setName(cubeLevel.getName());
						first.setLevel(cubeLevel.getUniqueName()) ;
						addFirstTitle(level.getFirstTitle(), -1 , first) ;
						while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null
								&& !cubeLevel.getName().contains("All")){
							n++;
							FirstTitle first2 = new FirstTitle();
							first2.setName(cubeLevel.getName());
							first2.setLevel(cubeLevel.getUniqueName()) ;
							if(level.getFirstTitle().size()>firstTitle.size()-i){
								addFirstTitle(level.getFirstTitle(),  firstTitle.size()-n, first2) ;
							}else{
								addFirstTitle(level.getFirstTitle() , 0 , first2) ;
							}
						}
					}else{
						boolean isHave = false;
						for(FirstTitle fr : level.getFirstTitle()){
							if(fr.getLevel().equals(cubeLevel.getUniqueName())){
								isHave = true;
								break;
							}
						}
						if(!isHave){
							FirstTitle first = new FirstTitle();
							first.setName(cubeLevel.getName());
							first.setLevel(cubeLevel.getUniqueName()) ;
							addFirstTitle(level.getFirstTitle(), -1 , first ) ;
							while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null &&
									!cubeLevel.getName().contains("All")){
								n++;
								FirstTitle first2 = new FirstTitle();
								first2.setName(cubeLevel.getName());
								first2.setLevel(cubeLevel.getUniqueName()) ;
								if(level.getFirstTitle().size()>firstTitle.size()-i){
									addFirstTitle(level.getFirstTitle() , firstTitle.size()-n, first2) ;
								}else{
									addFirstTitle(level.getFirstTitle() ,0,  first2) ;
								}
								
							}
						}
					}
				}else if(member.getLevel() instanceof RolapLevel && cubeLevel.getName().equals("MeasuresLevel")){	//指标列
					if(level.getFirstTitle()==null){
						level.setFirstTitle(firstTitle);
						FirstTitle first = new FirstTitle();
						first.setName(Constants.CUBE_TITLE_MEASURE);
						first.setLevel(cubeLevel.getUniqueName()) ;
						addFirstTitle(level.getFirstTitle(), -1 , first) ;
						while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null &&
								!cubeLevel.getName().contains("All")){
							n++;
							FirstTitle first2 = new FirstTitle();
							first2.setName(cubeLevel.getName());
							first2.setLevel(cubeLevel.getUniqueName()) ;
							if(level.getFirstTitle().size()>firstTitle.size()-i){
								addFirstTitle(level.getFirstTitle(),  firstTitle.size()-n, first2) ;
							}else{
								addFirstTitle(level.getFirstTitle() , 0 , first2) ;
							}
						}
					}else{
						boolean isHave = false;
						for(FirstTitle fr : level.getFirstTitle()){
							if(fr.getLevel().equals(cubeLevel.getUniqueName())){
								isHave = true;
								break;
							}
						}
						if(!isHave){
							FirstTitle first = new FirstTitle();
							first.setName(Constants.CUBE_TITLE_MEASURE);
							first.setLevel(cubeLevel.getUniqueName()) ;
							addFirstTitle(level.getFirstTitle(), -1 , first ) ;
							while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null &&
									!cubeLevel.getName().contains("All")){
								n++;
								FirstTitle first2 = new FirstTitle();
								first2.setName(cubeLevel.getName());
								first2.setLevel(cubeLevel.getUniqueName()) ;
								if(level.getFirstTitle().size()>firstTitle.size()-i){
									addFirstTitle(level.getFirstTitle() , firstTitle.size()-n, first2) ;
								}else{
									addFirstTitle(level.getFirstTitle() ,0,  first2) ;
								}
								
							}
						}
					}
				}
				
		
				if (strb.length() > 0) {
					strb.append("l__HHHH-A-HHHH__l");
				}
				strb.append(member.getUniqueName().substring(member.getUniqueName().indexOf(".")+1).replaceAll("\\.\\[\\]", "______R3_SPACE").replaceAll("\\]\\.\\[", "l__HHHH-A-HHHH__l").replaceAll("[\\]\\[]", ""));
			}
			if(cols!=null) {
				for(ColumnProperties col : cols) {
					if(strb.toString().equals(col.getDataname())) {
						strb = new StringBuilder() ;
						strb.append(col.getTitle()) ;
					}
				}
			}
			//替换掉所有的#null为空字符串
			valueStr.add(strb.toString().replace("#null", " "));
		}
		int depth = 0 ;
		for (int inx = 0 ; inx< valueStr.size() ; inx++) {
			String value = valueStr.get(inx) ;
			Level currentlevel = level;
			String[] levels = value.replaceAll("Measures______", "").split("l__HHHH-A-HHHH__l");
			if(levels.length>depth){
				depth = levels.length-1 ;
			}
			for (int i = 0 ; i < levels.length; i++) {
				boolean found = false;
				if (currentlevel.getChilderen() == null) {
					currentlevel.setChilderen(new ArrayList<Level>());
				}
				if(!levels[i].equals("R3_SPACE")){
					for (Level lv : currentlevel.getChilderen()) {
						if (levels[i].equals(lv.getName())) {
							currentlevel = lv;
							found = true;
							break;
						}
					}
					if (!found) {
						currentlevel.getChilderen().add(currentlevel = new Level(levels[i], level.getLeveltype() , currentlevel , levels.length - i , inx));
						if(i == levels.length-1){
							if(currentlevel.getChilderen()==null){
								currentlevel.setChilderen(new ArrayList<Level>()) ;
							}
							currentlevel.setIndex(inx) ;
							currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
						}
						if(level.getFirstTitle()!=null && level.getFirstTitle().size()>i){
							currentlevel.setDimname(level.getFirstTitle().get(i).getName()) ;
						}
					}else{
						if(i == levels.length-1){
							if(currentlevel.getChilderen()==null){
								currentlevel.setChilderen(new ArrayList<Level>()) ;
							}
							currentlevel.setIndex(inx) ;
							currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
						}
					}
				}else{
					currentlevel.getChilderen().add(currentlevel = new Level("", level.getLeveltype() , currentlevel , levels.length - i , inx));
					if(i == levels.length-1){
						if(currentlevel.getChilderen()==null){
							currentlevel.setChilderen(new ArrayList<Level>()) ;
						}
						currentlevel.setIndex(inx) ;
						currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
					}
					if(level.getFirstTitle()!=null && level.getFirstTitle().size()>i){
						currentlevel.setDimname(level.getFirstTitle().get(i).getName()) ;
					}
				}
			}
		}

		iterator(valueMap, level, level.getLeveltype());
		level.setDepth(depth) ;
		for(Level temp : level.getChilderen()){
			temp.setParent(level) ;
		}
		sumRowspanColspan(level);
		//格式化
		level.init() ;
		return level;
	}
	
	/**
	 * 
	 * @param firstTitleList
	 * @param title
	 */
	private void addFirstTitle(List<FirstTitle> firstTitleList , int index , FirstTitle title){
		if(firstTitleList
				.stream()
				.noneMatch(firstTitle -> firstTitle.getLevel().equals(title.getLevel()))) {
			if(index<0){
				firstTitleList.add(title) ;
			}else{
				firstTitleList.add(index , title) ;
			}
		}
	}
	
	/**
	 * 
	 * @param axes
	 * @param axis
	 * @param pos
	 * @param result
	 * @param dataList
	 * @param rowno
	 */
	private void getRowData(Axis[] axes, int axis, int[] pos, Result result, List<List<ValueData>> dataList, int rowno , Position position , CubeReportData cubeData , List<ColumnProperties> cols) {
		if (axis < 0) {
			if (dataList.size() <= rowno || dataList.get(rowno) == null) {
				dataList.add(new ArrayList<>());
			}
			Cell cell = result.getCell(pos);
			ValueData valueData  = new ValueData(cell.getValue(), cell.getFormattedValue(), null  , cell.canDrillThrough(), cell.getDrillThroughSQL(true) , position!=null && position.size()>0 ? position.get(position.size()-1).getName():"" , cell.getCachedFormatString() , cols) ;
			int rows = 0;
			valueData.setRow(getParentLevel(cubeData.getRow(), rowno, rows));
			dataList.get(rowno).add(valueData);
		} else {
			List<Position> positions = axes[axis].getPositions();
			for (int i = 0, size = positions.size(); i < size; i++) {
				Position posit = positions.get(i);
				pos[axis] = i;
				if (axis == 0) {
					rowno = axis + 1 < pos.length ? pos[axis + 1] : 0;
				}
				getRowData(axes, axis - 1, pos, result, dataList, rowno , posit , cubeData , cols);
			}
		}
	}
	
	private Level getParentLevel(Level level , int rowno , int rows){
		Optional<List<Level>> childOpt = Optional.ofNullable(level)
				.map(Level::getChilderen);
		if(childOpt.isPresent()) {
			List<Level> child = childOpt.get();
			for(Level lv : child){
				rows = rows + lv.getRowspan() ;
				if(rows==rowno){
					while(level.getChilderen()!=null && level.getChilderen().size()>0){
						level = level.getChilderen().get(level.getChilderen().size()-1) ;
					}
				}
				if(rows>rowno){
					rows = rows - lv.getRowspan() ;
					return getParentLevel(lv , rowno , rows) ;
				}
			}
		}
		return level ;
	}

	/**
	 * 处理 合计字段
	 * @param level
	 */
	private void processSum(Level level , List<List<ValueData>> valueDataList , Level root){
		for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
			Level child = level.getChilderen().get(i) ;
//			child.setIndex(i) ;
			if(child.getName().equals("R3_TOTAL") && !child.isTotal() && level.getIndex() < valueDataList.size()){
				child.setName("合计");
				child.setTotal(true) ;
				child.setFirst(i==0) ;
				if(level.getChilderen().size()>1 && level.getParent()!=null){
					child.setValueData(valueDataList.get(level.getIndex())) ;
					if(level.getParent().getChilderen().size()>0){
//						for(int inx = 0 ; inx < level.getParent().getChilderen().size() ; inx++){
//							if(level.getParent().getChilderen().get(inx).getName().equals(child.getParent().getName())){
////								child.setParent(child.getParent().getParent()) ;
////								level.getParent().getChilderen().add(inx+1 , child);
//								break ;
//							}
//						}
						child.setDepth(getDepth(child)) ;
						child.setColspan(root.getFirstTitle().size() - child.getDepth()) ;
//						level.getChilderen().remove(i--) ;
					}
					
				}else{
					if(valueDataList.size()>child.getIndex()){
						level.setValueData(valueDataList.get(level.getIndex())) ;
					}
					level.setChilderen(null) ;
				}
			}else{
				processSum(child , valueDataList , root) ;
			}
		}
	}
	private int getDepth(Level level){
		int depth = 0 ;
		while((level = level.getParent())!=null){
			depth++ ;
		}
		return depth - 1 ;
	}
	
	/*
	 * 
	 */
	private void processTitle(Level level , Level root){
		for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
			Level child = level.getChilderen().get(i) ;
			int depth = getDepth(child) ;
			if(depth>=0){
				if(root.getTitle().size()<=depth){
					root.getTitle().add(new ArrayList<>()) ;
				}
				Level tempLevel = new Level(child.getName() , child.getNameValue() , child.getLeveltype() , child.getRowspan() , child.getColspan() , child.getValueData() , child.isTotal() , child.isFirst()) ;
				tempLevel.setParent(child) ;
				root.getTitle().get(depth).add(tempLevel)  ;
			}
			if((child.getNameValue().equals("R3_TOTAL") && root.getFirstTitle()!=null && (depth+1) < root.getFirstTitle().size()) || (child.getChilderen()==null && root.getFirstTitle()!=null && (depth+1) < root.getFirstTitle().size())){
				child.setChilderen(new ArrayList<>()) ;
				child.setColspan(root.getFirstTitle().size() - depth);
				if(root.getTitle()!=null && root.getTitle().size()>depth){
					for(Level title : root.getTitle().get(depth)){
						if(title.getName().equals(child.getName())){
							title.setColspan(child.getColspan()) ;
						}
					}
				}
				Level tempLevel = new Level("TOTAL_TEMP" , "R3_TOTAL" , child.getLeveltype() ,child.getRowspan() ,child.getColspan(), child.getValueData() , child.isTotal() , child.isFirst() , child.getDepth()+1) ;
				tempLevel.setParent(child) ;
				child.getChilderen().add(tempLevel) ;
				child.setValue(null) ;
			}
			processTitle(child , root);
		}
	}
	/**
	 * 处理行列合并单元格
	 * @param level
	 */
	private void sumRowspanColspan(Level level) {
		if(level.getChilderen()!=null){
			for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
				Level lv = level.getChilderen().get(i) ;
				if(lv.getColspan()==0 && lv.getRowspan()==0){
					sumRowspanColspan(lv);
				}
				if(level.getLeveltype().equals("col")){
					level.setColspan(level.getColspan()+lv.getColspan()) ;
				}else{
					level.setRowspan(level.getRowspan()+lv.getRowspan()) ;
				}
			}
		}else{
			if(level.getLeveltype().equals("col")){
				level.setColspan(1) ;
			}else{
				level.setRowspan(1) ;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void iterator(Map<String, Map> value, Level level, String leveltype) {
		Iterator<String> iterator = value.keySet().iterator();
		if(level.getChilderen() == null) {
			level.setChilderen(new ArrayList<>());
		}
		while(iterator.hasNext()) {
			String name = iterator.next();
			Level sublevel = new Level(name, leveltype , level , level.getDepth()-1);
			level.getChilderen().add(sublevel);
			if (value.get(name) != null) {
				iterator(value.get(name), sublevel, leveltype);
			}
		}
	}
}
