<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/customer/impsave" enctype="multipart/form-data"  method="post">
		<#if ekind??><input type="hidden" name="ekind" value="${ekind}"></#if>
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">选择导入的数据文件</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">数据文件：</label>
							<div class="layui-input-inline">
								<div class="layui-box layui-upload-button" style="width: 100%;">
									<input type="file" name="cusfile" required lay-verify="required" accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" lay-ext="jpg|png|gif" class="layui-upload-file" onchange="$('#cus_file').html($(this).val());">
									<span class="layui-upload-icon"><i class="layui-icon">&#xe62f;</i></span>
								</div>
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font> (需要导入的客户数据Excel文件，可以是多个Sheet页)
							<a href="/res/template?filename=customer_template.xls"  target="_blank" style="margin-left:20px;">
										<i class="layui-icon">&#xe601;</i>
										下载模板
									</a>
						</div>
					</div>
					
					<div class="layui-form-item" style="height:40px;line-height:40px;">
						<div class="layui-inline">
							<label class="layui-form-label"></label>
							<div class="layui-input-inline" style="width:500px;" id="cus_file">
								
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">客户信息导入说明，导入步骤：</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">第一步：</label>
							<div class="layui-input-inline" style="width:auto;padding-top:9px;">上传客户数据的Excel文件（支持多Sheet页导入）</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">第二步：</label>
							<div class="layui-input-inline" style="width:auto;padding-top:9px;">选择Excel里的列与系统客户的字段对应关系</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">第三步：</label>
							<div class="layui-input-inline" style="width:auto;padding-top:9px;">开始导入客户数据</div>
						</div>
					</div>
					
				</div>
			</div>
			<div class="ukefu-imp-tips">
	                                             自动匹配客户导入字段功能，无需下载模板，系统自动匹配，客户数据直接导入，使用更加方便、快捷、智能。
	        </div>
		</div>

		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即导入</button>
				<button type="reset" class="layui-btn layui-btn-primary">重置</button>
			</div>
		</div>
	</form>
</div>
<script>
	layui.use('upload', function(){
		var target = $(this).attr("data-target");
	});
	layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
</script>