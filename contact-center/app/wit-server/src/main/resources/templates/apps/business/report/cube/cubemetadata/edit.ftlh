<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/report/cube/metadata/update" method="post">
		<input type="hidden" name="id" value="${cubeMetadata.id!''}">
		<input type="hidden" name="cubeid" value="${cubeMetadata.cubeid!''}">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">数据表名称：</label>
				<div class="layui-form-mid">
					<font>${cubeMetadata.tb.name!''}</font>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">数据表类型：</label>
				<div class="layui-input-block">
					<input type="radio" name="mtype" value="0" title="主表" <#if cubeMetadata.mtype?? && cubeMetadata.mtype == '0'>checked</#if>> 
					<input type="radio" name="mtype" value="1" title="从表" <#if cubeMetadata.mtype?? && cubeMetadata.mtype == '1'>checked</#if>>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" >数据表别称：</label>
				<div class="layui-input-inline">
					<input type="text" name="namealias"  
						class="layui-input" value="${cubeMetadata.namealias}">
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">WHERE条件：</label>
				<div class="layui-input-inline" style="width: 664px;">
					<textarea name="parameters" placeholder="" class="layui-textarea" style="resize:none;" >${cubeMetadata.parameters}</textarea>
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
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('element', function(){
		var element = layui.element;
	});
</script>