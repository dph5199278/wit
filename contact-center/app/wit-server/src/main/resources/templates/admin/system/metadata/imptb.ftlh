<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/metadata/imptbsave">
		<input type="hidden" name="role" value="${role.id!''}">
		<div class="layui-form-item">
			<div class="layui-input-inline">
				<select multiple = "multiple" name="tables" class="ukefu-form-multiple">
					<#if tablesList??>
					<#list tablesList as table>
						<option value="${table.name}">${table.name!''}</option>
					</#list>
					</#if>
				</select>
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
