<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/metadata/update">
		<input type="hidden" name="id" value="${metadata.id!''}">
		<div class="layui-form-item">
			<label class="layui-form-label">标题：</label>
			<div class="layui-input-inline">
				<input type="text" name="name" value="${metadata.name!''}"
					required lay-verify="required" placeholder="标题"
					autocomplete="off" class="layui-input">
			</div>
		</div>
		
		<div class="layui-form-item">
			<label class="layui-form-label">选项：</label>
			<div class="layui-input-inline">
				<input type="checkbox" name="fromdb" title="ES数据" lay-skin="primary" lay-filter="es" <#if metadata.fromdb>checked</#if>>
			</div>
		</div>
		
		<div class="layui-form-item es" <#if metadata.fromdb == false>style="display:none;"</#if>>
			<label class="layui-form-label">ES资源：</label>
			<div class="layui-input-inline">
				<@select "com.dic.es.type" "listblocktemplet" metadata.listblocktemplet!'' "lay-ignore required lay-verify='required' "/>
			</div>
		</div>
		
		<div class="layui-form-item es" <#if metadata.fromdb == false>style="display:none;"</#if>>
			<label class="layui-form-label">JAVABean：</label>
			<div class="layui-input-inline">
				<@select "com.dic.db.type" "previewtemplet" metadata.previewtemplet!'' "lay-ignore required lay-verify='required' "/>
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
  form.render(); //更新全部
  form.on("checkbox(es)" , function(data){
		if(data.elem.checked == true){
			$('.es').show();		
		}else{
			$('.es').hide();
		}
	});
  form.verify({
	  repass: function(value){
	    if(value != $('#password').val()){
	      return '两次输入的密码不一致，请确认';
	    }
	  }
	});      
});
</script>