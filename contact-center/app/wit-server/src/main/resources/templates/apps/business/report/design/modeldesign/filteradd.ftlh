<#include "/public/macro.ftlh">
<form class="layui-form uk-layui-form" action="/apps/report/design/filtersave" >
	<input type="hidden" name="modelid"   value="${mid!''}">
	<input type="hidden" name="cubeid"   value="${cube.id!''}">
	<div class="layui-row">
		<div class="layui-col-md6">
			<div class="layui-form-item">
				<label class="layui-form-label">主表</label>
				<div class="layui-input-inline">
					<div class="layui-form-mid layui-word-aux">${table.tablename}</div>
					<input type="hidden" name="tableid"  lay-verify="required" autocomplete="off"
					class="layui-input" value="${table.id!''}">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">关联表</label>
				<div class="layui-input-inline">
					<select class="ukefu-select" name="fktableid" id="fktableid"  lay-filter="fktableid" lay-verify="required">
						<option value="">请选择</option>
					 	<#if fktableList??>
					 	<#list fktableList as table>
					 	<option value="${table.id!''}" >${table.tablename!''}</option>
					 	</#list>
					 	</#if> 
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">过滤字段</label>
				<div class="layui-input-inline">
					<select class="ukefu-select ukefu-field" name="filterfieldid" id="filterfieldid" lay-verify="required" >
						<option value="">请选择</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">显示名称</label>
			    <div class="layui-input-inline">
				    <input type="text" name="title"  lay-verify="required" autocomplete="off"
					class="layui-input" value="${reportFilter.title!''}">
			    </div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">选择方式</label>
			    <div class="layui-input-inline">
				    <@select "com.dic.filter.modeltype" "modeltype" reportFilter.modeltype!'' "lay-verify=required lay-filter=modeltype " />
			    </div>
			</div>
			<div class="layui-form-item cascadeid_div" <#if !(reportFilter?? && reportFilter.modeltype == 'sigsel' && reportFilter.convalue =='auto') >style="display:none;"</#if>>
			    <label class="layui-form-label">是否字典项</label>
			    <div class="layui-input-inline">
					<input type="radio" name="isdic"  lay-filter="isdic" value="1" title="是" <#if reportFilter?? && reportFilter.isdic>checked</#if>  > 
					<input type="radio" name="isdic" lay-filter="isdic"  value="0" title="否"   <#if !reportFilter?? || (reportFilter?? && !reportFilter.isdic)>checked</#if> >
				</div>
			</div>
			<div class="layui-form-item cascadeid_div" <#if !(reportFilter?? && reportFilter.modeltype == 'sigsel' && reportFilter.convalue =='auto') >style="display:none;"</#if>>
				<label class="layui-form-label">KEY字段</label>
				<div class="layui-input-inline">
					<select class="ukefu-select ukefu-field" name="keyfield" id="keyfield"  >
						<option value="">请选择</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">条件类型</label>
			    <div class="layui-input-inline">
				    <@select "com.dic.filter.valuefiltertype" "valuefiltertype" reportFilter.valuefiltertype!'' "required lay-verify=required lay-filter=valuefiltertype " />
			    </div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">默认值</label>
			    <div class="layui-input-inline" id="rangevalue" <#if  !reportFilter?? || (reportFilter?? && reportFilter.valuefiltertype != 'range')>style="display:none;"</#if> >
				    <div class="layui-input-inline" style="width: 70px;">
				      <input type="text" name="startvalue" style="width: 70px;" placeholder=""  class="layui-input" value="${reportFilter.startvalue!''}">
				    </div>
				    <div class="layui-form-mid">~</div>
				    <div class="layui-input-inline" style="width: 70px;">
				      <input type="text" name="endvalue" style="width: 70px;" placeholder=""  class="layui-input" value="${reportFilter.endvalue!''}">
				    </div>
			    </div>
			    <div class="layui-input-inline" id="comparevalue" <#if reportFilter?? && reportFilter.valuefiltertype == 'range'>style="display:none;"</#if>>
				    <input type="text" name="defaultvalue"  
					class="layui-input" value="${reportFilter.defaultvalue!''}">
			    </div>	
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">必填项</label>
			    <div class="layui-input-inline">
					<input type="radio" name="mustvalue" value="mustvalue" title="是" <#if reportFilter?? && reportFilter.mustvalue == 'mustvalue'>checked</#if>  > 
					<input type="radio" name="mustvalue" value="" title="否"   <#if !reportFilter?? || (reportFilter?? && reportFilter.mustvalue != 'mustvalue')>checked</#if> >
				</div>
			</div>
			<div class="layui-form-item cascadeid_div" id="cascadeid_div" <#if !(reportFilter?? && reportFilter.modeltype == 'sigsel' && reportFilter.convalue =='auto') >style="display:none;"</#if>>
			    <label class="layui-form-label">级联</label>
			    <div class="layui-input-inline">
			    	<select name="cascadeid" lay-filter="cascadeid">
			    		<option value="" <#if reportFilter.cascadeid?? >selected='selected'</#if>></option>
			    		<#if reportModel?? && reportModel.filters??>
			    			<#list reportModel.filters as filter>
			    				<#if !(filter.id == reportFilter.id)>
			    					<option value="${filter.id!''}" <#if filter.id == reportFilter.cascadeid>selected='selected'</#if> >${filter.title!''}</option>
			    				</#if>
			    			</#list>
			    		</#if>
			    	</select>
				</div>
			</div>
			<div class="layui-form-item cascadeid_div" id="tb_div" <#if !(reportFilter?? && reportFilter.modeltype == 'sigsel' && reportFilter.convalue =='auto') >style="display:none;"</#if>>
			    <label class="layui-form-label">级联字段</label>
			    <div class="layui-input-inline" id="tb_select">
			    	<#include "/apps/business/report/design/modeldesign/fktableiddiv.ftlh"/>
				</div>
			</div>
		</div>
		<div class="layui-col-md6">
			<div class="layui-form-item">
				<label class="layui-form-label">映射字段</label>
				<div class="layui-input-inline">
					<select class="ukefu-select" name="fieldid" id="fieldid" lay-verify="required">
						<option value="">请选择</option>
					 	<#if fieldList??>
					 	<#list fieldList as field>
					 	<option value="${field.id!''}" >${field.fieldname!''}（${field.name!''}）</option>
					 	</#list>
					 	</#if> 
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">关联字段</label>
				<div class="layui-input-inline">
					<select class="ukefu-select ukefu-field" name="fkfieldid" id="fkfieldid" lay-verify="required" >
						<option value="">请选择</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">&nbsp;</label>
				<div class="layui-input-inline">
				</div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">参考代码</label>
			    <div class="layui-input-inline">
				    <input type="text" name="code" 
					class="layui-input" value="${reportFilter.code!''}">
			    </div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">取值方式</label>
			    <div class="layui-input-inline">
					 <@select "com.dic.filter.convalue" "convalue" reportFilter.convalue!'' "required lay-verify=required lay-filter=convalue " />
			    </div>
			</div>
			<div class="layui-form-item" id="sysDic"  <#if !reportFilter?? || (reportFilter?? && !reportFilter.isdic)>style="display:none;"</#if> >
			    <label class="layui-form-label">字典类型</label>
			    <div class="layui-input-inline">
					<select name="diccode" >
						<option value="">请选择字典项</option>
						<#if sysdicList??>
						<#list sysdicList as sysDic>
						<option value="${sysDic.code!''}" <#if reportFilter?? && reportFilter.diccode == sysDic.code>selected="selected"</#if>>${sysDic.name}</option>
						</#list>
						</#if>
					</select>
				</div>
			</div>
			<div class="layui-form-item cascadeid_div" <#if !(reportFilter?? && reportFilter.modeltype == 'sigsel' && reportFilter.convalue =='auto') >style="display:none;"</#if>>
				<label class="layui-form-label">VALUE字段</label>
				<div class="layui-input-inline">
					<select class="ukefu-select ukefu-field" name="valuefield" id="valuefield"  >
						<option value="">请选择</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">比较方式</label>
			    <div class="layui-input-inline">
					 <@select "com.dic.filter.comparetype" "comparetype" reportFilter.comparetype!'' "required lay-verify=required " />
			    </div>
			</div>
			<div class="layui-form-item" id="filter_formatstr" <#if !reportFilter??  || (reportFilter?? && reportFilter.modeltype !='date') >style="display:none"</#if>>
			    <label class="layui-form-label">日历格式</label>
			    <div class="layui-input-inline">
					 <input type="text" name="formatstr"  
					class="layui-input" value="${reportFilter.formatstr!'yyyy-MM-dd'}">
			    </div>
			</div>
		</div>
	</div>
	<input type="hidden" name="id" value="${reportFilter.id!''}">	
  <div class="layui-form-button" >
		<div class="layui-button-block">
			 <button class="layui-btn layui-btn-normal"   lay-submit>立即提交</button>
		</div>
	</div>
</form>
 
<script>
layui.use('form', function(){
  var form = layui.form;
  form.on('select(fktableid)', function(data){
	  loadURL('/apps/report/design/gettableid?tableid='+data.value, '.ukefu-field');
	  setTimeout(function(){
		  form.render();
	  },500)
  });
  form.on('radio(isdic)', function(data){
	  if(data.value == '1'){
		  $("#sysDic").show();
	  }else{
		  $("#sysDic").hide(); 
	  }
	  
	});  
  form.on('select(modeltype)', function(data){
	  if(data.value == 'date'){
		  $("#filter_formatstr").show();
	  }else{
		  $("#filter_formatstr").hide(); 
	  }
	  if(data.value == 'sigsel' && $("#convalue").val() == 'auto'){
		  $(".cascadeid_div").show();
	  }else{
		  $(".cascadeid_div").hide(); 
	  }
	}); 
  form.on('select(convalue)', function(data){
	  if(data.value == 'auto' && $("#modeltype").val() == 'sigsel'){
		  $(".cascadeid_div").show();
	  }else{
		  $(".cascadeid_div").hide(); 
	  }
	}); 
  form.on('select(valuefiltertype)', function(data){
	  if(data.value == 'range'){
		  $("#rangevalue").show();
		  $("#comparevalue").hide();
	  }else{
		  $("#rangevalue").hide(); 
		  $("#comparevalue").show();
	  }
	});
  form.on('select(cascadeid)', function(data){
	  loadURL('/apps/report/design/fktableid?fid=${reportFilter.id!''}&fkId='+data.value, '#tb_select');
	  form.render();
  });
  form.render();
  
});
</script>
