<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/topic/save" method="post">
		<#if type??><input type="hidden" name="type" value="${type!''}"></#if>
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">知识信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">标题：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<input type="text" name="title" autocomplete="off" class="layui-input" required="required" lay-verify="required" maxlength="255">
								
							</div>
						</div>
						<div class="layui-inline">
							<label style="color:red;">* 必填项</label>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">有效期开始：</label>
							<div class="layui-input-inline">
								<input type="text" name="begintime" id="begintime" readOnly="readOnly" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">有效期结束：</label>
							<div class="layui-input-inline">
								<input type="text" id="endtime" name="endtime" autocomplete="off" class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">类似问题：</label>
							<div class="layui-input-inline" style="width: 664px;" id="mlt">
								<div style="margin-bottom:5px;position: relative;" class="mlt_item">
									<input type="text" name="silimar" autocomplete="off" value="" class="layui-input" maxlength="255" style="width:600px;">
									<span style="position: absolute;right:0px;top:0px;">
										<a href="javascript:void(0)" onclick="$('#mlt').append($(this).closest('.mlt_item').prop('outerHTML'));">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#xe608;</i></label>
										</a>
										<a href="javascript:void(0)" onclick="if($('.mlt_item').length > 1){$(this).closest('.mlt_item').remove();}else{top.layer.alert('类似问题最少需要保留一个，如果不需要，请删除内容');}" class="ukefu-group-remove">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#x1006;</i></label>
										</a>
									</span>
								</div>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">热点问题：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="top" value="1" title="是（出现在访客对话页面右侧热门问题）"> 
									<input type="radio" name="top" value="0" title="否" checked>
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">知识内容</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">回复内容：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="content" id="content" placeholder="请输入内容" class="layui-textarea" style="height:160px;resize:none;"></textarea>
							</div>
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
	layui.use('element', function(){
		var element = layui.element;
	});
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('laydate', function() {
		var laydate = layui.laydate;

		var date = {
			min : '1950-01-01 00:00:00',
			istoday : false
		};

		date.elem = document.getElementById('begintime');
		laydate.render(date);

		date.elem = document.getElementById('endtime');
		laydate.render(date);

	});
	KindEditor.create('#content', {
		themeType : 'simple',
		width:"100%",
		resizeType:0,
		uploadJson : "/res/image/upload",
		items : ['fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
				 'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
				 'insertunorderedlist', '|', 'emoticons', 'image', 'link'],
        allowFileManager : false,
		fontsize:16
	});
</script>