<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/admin/sms/save" method="post">
		<div class="layui-form-item" style="margin-top:10px;">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">短信网关名称：</label>
				<div class="layui-input-inline">
					<input type="text" name="name" required lay-verify="required" autocomplete="off"
						class="layui-input" >
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item" style="margin-top:10px;">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">短信网关地址：</label>
				<div class="layui-input-inline">
					<input type="text" name="url" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item" style="margin-top:10px;">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">短信网关类型：</label>
				<div class="layui-input-inline">
					<@select "com.dic.sms.type" "smstype" "" "lay-verify=required" ""/>
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">签名：</label>
				<div class="layui-input-inline">
					<input type="text" name="sign" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">模板ID：</label>
				<div class="layui-input-inline">
					<input type="text" name="tpcode" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">AppKey：</label>
				<div class="layui-input-inline">
					<input type="text" name="appkey" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">AppSec：</label>
				<div class="layui-input-inline">
					<input type="password" name="appsec" required lay-verify="required" autocomplete="off"
						class="layui-input">
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
	            </div>
			</div>
		</div>
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">其他参数：</label>
				<div class="layui-input-inline">
					<textarea name="moreparam" placeholder="请输入参数" class="layui-textarea"></textarea>
				</div>
				<div class="layui-form-mid layui-word-aux">
	            	<font color="red">*</font> 
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