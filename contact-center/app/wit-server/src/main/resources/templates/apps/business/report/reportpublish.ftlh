<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/report/design/reportpublished" method="post">
		<input type="hidden" name="reportid" value="${reportid!''}">
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">发布选项：</label>
					<div class="layui-input-inline">
						<input type="radio" name="isRecover" value="yes" title="覆盖最新版本" checked> 
						<input type="radio" name="isRecover" value="no" title="创建新版本" >
						<input type="radio" name="isRecover" value="del" title="删除之前版本">
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
</script>
