<div class="uk-layui-form">
	<form class="layui-form"  style="margin-top:10px;" action="/apps/tenant/update"  enctype="multipart/form-data"  method="post">
	<input type="hidden" name="id" value="${tenant.id!''}">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">名称：</label>
				<div class="layui-input-inline">
					<input type="text" name="tenantname" required lay-verify="required" autocomplete="off"
						class="layui-input" value="${tenant.tenantname!''}" maxlength="10">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">logo：</label>
				<div class="layui-input-inline">
					<div class="box-item">
						<div class="layui-box layui-upload-button" style="position:absolute;">
							<input type="file" name="tenantpic" accept="image/gif, image/jpeg, image/png" lay-ext="jpg|png|gif"  class="layui-upload-file" onchange="$('#logo_img').html($(this).val());">
							<span class="layui-upload-icon"><i class="layui-icon"></i>上传图片</span>
						</div>
						<span id="logo_img" style="position:absolute;left: 150px;"></span>
					</div>
				</div>
			</div>
		</div>
		<#if tenant?? && tenant.tenantlogo??>
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label" style="width:150px;"></label>
					<div class="layui-input-inline">
						<span class="ukefu-logo">
						<img src="<#if tenant?? && tenant.tenantlogo??>/res/image?id=${tenant.tenantlogo?url}</#if>" style="height:80px;">
					</span>
					</div>
				</div>
			</div>
		</#if>
		<#if isShowSkillGroups?? && isShowSkillGroups==true>
			<div class="layui-form-item">
				<label class="layui-form-label" style="width:150px;">选择技能组：</label>
			    <div class="layui-input-block" style="padding-top:0px;">
			    	<#if skillGroups??>
				    	<#list skillGroups as skill>
				    	<div style="padding-left:170px;"><input type="checkbox" name="skills" value="${skill.id!''}" title="${skill.name!''}" lay-skin="primary" <#if orgiSkillRelList??><#list orgiSkillRelList as rel><#if rel.skillid?? && rel.skillid == skill.id>checked="checked"</#if></#list></#if>></div>
				    	</#list>
			    	<#else>
			    	<div><i>无技能组信息</i></div>	
			    	</#if>
			    </div>
			</div>
		</#if>
		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button type="reset" class="layui-btn layui-btn-primary">重置</button>
			</div>
		</div>
	</form>
</div>
<script>
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('element', function(){
		var element = layui.element;
	});
</script>
