<#include "/public/macro.ftlh">
<form class="layui-form" action="/apps/report/design/values" id="selcubelevel">
	<input type="hidden" name="t" value="${t!''}" id="t">	
	<#if dtype??><input type="hidden" name="dtype" value="${dtype!''}"></#if>
	<input type="hidden" name="tabid" value="${tabid!''}">
	<input type="hidden" name="dsid" value="<#if cube??>${cube.id!''}</#if>" >
	<input type="hidden" name="mid" value="<#if reportModel?? && reportModel.id??>${reportModel.id!''}</#if>" />
	<#if t?? && t=='dim'>
		  <div class="layui-form-item">
		    <label class="layui-form-label">选择维度</label>
		    <div class="layui-input-inline">
			    <select  id="dimensionselect" name="dimension" lay-filter="dimensionselect">
					<option value="">选择维度</option>
				 	<#if cube?? && cube.cube?? && cube.cube.dimension??>
				 	<#list cube.cube.dimension as thisdim>
				 	<option value="${thisdim.id!''}" <#if dim?? && dim == thisdim.id>selected</#if> >${thisdim.name!''}</option>
				 	</#list>
				 	</#if> 
				</select>
		    </div>
		  </div>
		  <div class="layui-form-item" >
			<label class="layui-form-label">维度成员：</label>
			<div class="layui-input-inline">
				<select name="d" id="levelid" lay-verify="required">
					<option value="">请选择维度成员</option>
					<#if cube?? && cube.cube?? && cube.cube.dimension??>
					<#list cube.cube.dimension as thisdim>
					<#if dim?? && dim == thisdim.id &&  thisdim.cubeLevel??>
				 	<#list thisdim.cubeLevel as cubelevel>
				 	<option value="${cubelevel.id!''}" >${cubelevel.name!''}</option>
				 	</#list>
				 	</#if>
				 	</#list>
				 	</#if> 
				</select>
			</div>
		  </div>
  <#elseif t?? && t=='measure'>
  	<div class="layui-form-item">
	    <label class="layui-form-label">选择指标</label>
	    <div class="layui-input-inline">
		    <select   name="m" lay-verify="required">
				<option value="">选择指标</option>
			 	<#if cube?? && cube.cube?? && cube.cube.measure??>
			 	<#list cube.cube.measure as measure>
			 	<option value="${measure.id!''}" >${measure.name!''}</option>
			 	</#list>
			 	</#if> 
			</select>
	    </div>
  	</div>
  <#elseif t?? && t=='filter'>
  	<div class="layui-form-item">
	    <label class="layui-form-label">选择维度</label>
	    <div class="layui-input-inline">
		    <select  id="dimensionselect" name="dimension" lay-filter="dimensionselect">
				<option value="">选择维度</option>
			 	<#if cube?? && cube.cube?? && cube.cube.dimension??>
			 	<#list cube.cube.dimension as thisdim>
			 	<option value="${thisdim.id!''}" <#if dim?? && dim == thisdim.id>selected</#if> >${thisdim.name!''}</option>
			 	</#list>
			 	</#if> 
			</select>
	    </div>
	  </div>
	  <div class="layui-form-item" >
		<label class="layui-form-label">维度成员：</label>
		<div class="layui-input-inline">
			<select name="f" id="levelid" lay-verify="required">
				<option value="">请选择维度成员</option>
				<#if cube?? && cube.cube?? && cube.cube.dimension??>
				<#list cube.cube.dimension as thisdim>
				<#if dim?? && dim == thisdim.id &&  thisdim.cubeLevel??>
			 	<#list thisdim.cubeLevel as cubelevel>
			 	<option value="${cubelevel.id!''}" >${cubelevel.name!''}</option>
			 	</#list>
			 	</#if>
			 	</#list>
			 	</#if> 
			</select>
		</div>
	  </div>
  </#if>
  <div class="layui-form-button" >
		<div class="layui-button-block">
			 <button class="layui-btn layui-btn-normal"   lay-submit>立即提交</button>
		</div>
	</div>
</form>
 
<script>
layui.use('form', function(){
  var form = layui.form;
  form.render();
  <#if t?? && (t=='dim'|| t=='filter') >
  form.on('select(dimensionselect)', function(data){
	  loadURL('/apps/report/design/adddata?<#if dtype??>dtype=${dtype!''}&</#if>tabid=${tabid!''}&t=${t!''}&cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>&dim='+data.value, '#selcubelevel');
	}); 
  </#if>
});
</script>
