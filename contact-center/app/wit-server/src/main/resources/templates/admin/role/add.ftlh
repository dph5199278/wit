<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/role/save">
	  <div class="layui-form-item">
	    <label class="layui-form-label">角色：</label>
	    <div class="layui-input-inline">
	      <input type="text" name="name" required lay-verify="required_name" placeholder="请输入角色名称" autocomplete="off" class="layui-input">
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
layui.use('form', function(){
  var form = layui.form;
	form.verify({
		required_name: function (value) {
			if (value.length > 50){
				return "角色名称不能超过50字";
			}
		}
	});
});
</script>