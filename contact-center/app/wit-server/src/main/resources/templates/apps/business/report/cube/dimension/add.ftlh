<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/report/dimension/save" method="post">
		<input type="hidden" name="cubeid" value="${cubeid!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="name"  lay-verify="required" autocomplete="off"
									class="layui-input">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" >维度类型：</label>
							<div class="layui-input-inline" style="width:218px;margin-right:0px;padding-top:9px;">
								<@select "com.dic.dimension.modeltype" "modeltype" modeltype!'' "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" >汇总名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="allmembername"  
									class="layui-input" value="合计">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" >参数：</label>
							<div class="layui-input-inline">
								<input type="text" name="parameters" 
									class="layui-input">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">属性：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="attribue" placeholder="请输入内容" class="layui-textarea" style="resize:none;"></textarea>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">主表外键：</label>
							<div class="layui-input-inline">
								<select lay-ignore class="ukefu-select" name="fkfield" id="fkfield" style="width: 100%">
									<option value="">无关联外键</option>
								 	<#if fkfieldList?? && fkfieldList?size gt 0>
								 	<#list fkfieldList as fkfield>
								 	<#if fkfield?? && fkfield.tb?? && fkfield.tb.tableproperty??>
								 	<#list fkfield.tb.tableproperty as field>
								 	<option value="${field.id!''}">${field.fieldname!''}</option>
								 	</#list>
								 	</#if>
								 	</#list>
								 	</#if> 
								</select>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">关联子表：</label>
							<div class="layui-input-inline">
								<select lay-ignore class="ukefu-select" name="fktable" id="fktable" style="width: 100%">
									<option value="">选择关联子表</option>
								 	<#if fktableList??>
								 	<#list fktableList as fktable>
								 	<option value="${fktable.tb.id!''}">${fktable.tb.name!''}</option>
								 	</#list>
								 	</#if> 
								</select>
							</div>
						</div>
						<div class="layui-inline uckefu-inline" id="fktableiddiv">
							<#include "/apps/business/report/cube/dimension/fktableiddiv.ftlh">
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button type="reset" class="layui-btn layui-btn-primary">重置</button>
			</div>
		</div>
	</form>
</div>

<script>
	//Demo
	$('#fktable').change(function(){
		loadURL('/apps/report/dimension/fktableid?tableid='+$(this).val(), '#fktableiddiv');
	})
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('element', function(){
		var element = layui.element;
	});
</script>