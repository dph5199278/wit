<div class="uk-layui-form">
	<form class="layui-form" action="/admin/im/save" method="post">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">网站名称：</label>
				<div class="layui-input-inline">
					<input type="text" name="name" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            	网站名称
	            </div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">网站地址：</label>
				<div class="layui-input-inline">
					<input type="text" name="baseURL" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		
		<div class="ukefu-imp-tips" style="margin-top:0px;padding: 0;width: 548px;text-align: center">
		     网站信息填写后会根据网站的URL生成网站标识，URL中不包含http或https
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