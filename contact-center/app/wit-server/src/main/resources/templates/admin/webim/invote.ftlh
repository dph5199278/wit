<div class="row" style="border-bottom: 10px solid #EFEFEF;padding:10px;">
	<div class="col-lg-8">
		<div class="ukefu-measure">
			<div class="ukefu-bt"> 
				<div	class="layui-icon ukewo-btn ukefu-content-ind"><img src="/images/webim.png" style="width:60px;height:60px;"></div>
				<div class="ukefu-bt-text">
					<div class="ukefu-bt-text-title"
						style="font-weight:400;font-size:24px;border-bottom:1px solid #dedede;">
							${snsAccount.name!''}
							<span style="font-size:15px;color:#AAAAAA;">接入网站创建时间：${snsAccount.createtime!?string("yyyy-MM-dd HH:mm:ss")}</span>
					</div>
					<div class="ukefu-bt-text-content" style="">${snsAccount.name!''}</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="ukefu-tab-title" style="margin-left: 0px;">
	<ul class="tab-title">
		<li <#if subtype == 'webim'>class="layui-this"</#if>><a href="/admin/webim/index?snsid=${snsAccount.snsid!''}">基本设置</a></li>
		<li <#if subtype == 'invote'>class="layui-this"</#if>><a href="/admin/webim/invote?snsid=${snsAccount.snsid!''}">邀请设置</a></li>
		<li <#if subtype == 'profile'>class="layui-this"</#if>><a href="/admin/webim/profile?snsid=${snsAccount.snsid!''}">客服信息</a></li>
	</ul>
</div>
<div class="layui-tab">
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
		<form method="post" key="set-mine" enctype="multipart/form-data" action="/admin/webim/invote/save" class="layui-form">
			<#if inviteData??>
				<input type="hidden" name="id" value="${inviteData.id!''}">
				<input type="hidden" name="snsaccountid" value="${inviteData.snsaccountid!''}">
				<#if inviteData.ai??>
					<input type="hidden" name="ai" value="${inviteData.ai?then('true','false')}">
				<#else>
					<input type="hidden" name="ai" value="false">
				</#if>
				<#if inviteData.aifirst??>
					<input type="hidden" name="aifirst" value="${inviteData.aifirst?then('true','false')}">
				<#else>
					<input type="hidden" name="aifirst" value="false">
				</#if>
	            <input type="hidden" name="aisuccesstip" value="${inviteData.aisuccesstip!''}">
			    <input type="hidden" name="ainame" value="${inviteData.ainame!''}">
			    <input type="hidden" name="aiid" value="${inviteData.aiid!''}">
            </#if>
			<div class="row">
				<div class="col-lg-12">
					<div class="ukefu-customer-div setting-wrapper">
						<div class="box default-box">
							<div class="box-header">
								<h3 class="box-title">邀请框设置</h3>
							</div>
							<div class="box-body ukefu-im-theme">
								<div class="row">
									<div class="col-lg-7">
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">1、启用访客邀请功能</div>
											<div class="box-item">
												<div class="row">
													<div class="col-lg-8">
														<p>访客自动弹出邀请框，可以设置邀请文字和邀请按钮</p>
														<p style="color:#888888;font-size:13px;margin-top:10px;">默认启用访客邀请功能</p>
													</div>
													<div class="col-lg-4" style="text-align:right;">
														<input type="checkbox" title="启用" name="consult_invite_enable" value="1" <#if inviteData.consult_invite_enable>checked="checked"</#if>>
													</div>
												</div>
											</div>
										</div>
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">2、邀请提示文字</div>
											<div class="box-item">
												<div class="row">
													<div class="col-lg-12">
														<textarea name=consult_invite_content autocomplete="off" class="layui-input" onkeyup="$('#ukefu-cousult-invite-content').text($(this).val())" resize="false" style="height:90px;line-height:22px;resize: none;">${inviteData.consult_invite_content!'欢迎来到本网站，请问有什么可以帮您？'}</textarea>
													</div>
												</div>
											</div>
										</div>
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">3、选择邀请框背景</div>
											<div class="box-item">
												<input type="hidden" name="consult_invite_color" id="consult_invite_color" value="<#if inviteData??>${inviteData.consult_invite_color!'1'}<#else>1</#if>"> 
												<div class="item-cnt ukefu-im-ping-color" style="display: block;">
													<div class="ukefu-im-item theme1 <#if !(inviteData?? && inviteData.consult_invite_color??) || (inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '1')>ukefu-im-checked</#if>"
														data-class="theme1" data-value="1">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
													<div class="ukefu-im-item theme2 <#if inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '2'>ukefu-im-checked</#if>" data-class="theme2" data-value="2">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
													<div class="ukefu-im-item theme3 <#if inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '3'>ukefu-im-checked</#if>" data-class="theme3" data-value="3">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
													<div class="ukefu-im-item theme4 <#if inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '4'>ukefu-im-checked</#if>" data-class="theme4" data-value="4">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
													<div class="ukefu-im-item theme5 <#if inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '5'>ukefu-im-checked</#if>" data-class="theme5" data-value="5">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
													<div class="ukefu-im-item theme6 <#if inviteData?? && inviteData.consult_invite_color?? && inviteData.consult_invite_color == '6'>ukefu-im-checked</#if>" data-class="theme6" data-value="6">
														<i
															class="layui-icon ukefu-im-check">&#xe618;</i>
													</div>
												</div>
											</div>
										</div>
										
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">4、显示“现在咨询”按钮</div>
											<div class="box-item">
												<div class="row">
													<div class="col-lg-8">
														<p>启用访客邀请功能，在弹出的邀请框里显示“接受邀请”的按钮</p>
														<p style="color:#888888;font-size:13px;margin-top:10px;">默认提示文本是：现在咨询</p>
													</div>
													<div class="col-lg-4">
														<input type="text" name="consult_invite_accept" value="${inviteData.consult_invite_accept!'现在咨询'}" autocomplete="off" class="layui-input" onkeyup="$('#consult_invite_accept').text($(this).val())" onchange="$('#consult_invite_accept').text($(this).val())">
													</div>
												</div>
											</div>
										</div>
										
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">5、显示“稍后咨询”按钮</div>
											<div class="box-item">
												<div class="row">
													<div class="col-lg-8">
														<p>启用访客邀请功能，在弹出的邀请框里显示“稍后咨询”的按钮”</p>
														<p style="color:#888888;font-size:13px;margin-top:10px;">默认提示文本是：稍后咨询</p>
													</div>
													<div class="col-lg-4">
														<input type="text" name="consult_invite_later" id="consult_invite_later" value="${inviteData.consult_invite_later!'稍后再说'}"  autocomplete="off" class="layui-input" onkeyup="$('#consult_invite_later').text($(this).val())" onchange="$('#consult_invite_later').text($(this).val())">
													</div>
												</div>
											</div>
										</div>
										
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl" style="clear:both;">6、延时弹出邀请框</div>
											<div class="box-item">
												<div class="row">
													<div class="col-lg-8">
														<p>延时弹出访客邀请框</p>
														<p style="color:#888888;font-size:13px;margin-top:10px;">默认延时：5秒</p>
													</div>
													<div class="col-lg-4">
														<select name="consult_invite_delay">
														  <option value="0" <#if inviteData.consult_invite_delay == 0>selected="selected"</#if>>无延时</option>
														  <option value="1000" <#if inviteData.consult_invite_delay == 1000>selected="selected"</#if>>1秒</option>
														  <option value="2000" <#if inviteData.consult_invite_delay == 2000>selected="selected"</#if>>2秒</option>
														  <option value="3000" <#if inviteData.consult_invite_delay == 3000>selected="selected"</#if>>3秒</option>
														  <option value="4000" <#if inviteData.consult_invite_delay == 4000>selected="selected"</#if>>4秒</option>
														  <option value="5000" <#if inviteData.consult_invite_delay == 5000>selected="selected"</#if>>5秒</option>
														  <option value="10000" <#if inviteData.consult_invite_delay == 10000>selected="selected"</#if>>10秒</option>
														</select> 
													</div>
												</div>
											</div>
										</div>
										
										<div class="ukefu-webim-prop">
											<div class="ukefu-webim-tl">7、自定义访客邀请框背景图片 （建议尺寸：273*230）</div>
											<div class="box-item" style="position: relative;">
												<span class="ukefu-logo">
													<img id="invite_show" src="<#if inviteData?? && inviteData.consult_invite_bg??>/res/image?id=${inviteData.consult_invite_bg?url}<#else>/im/img/webwxgetmsgimg.jpg</#if>" style="height:175px;">
												</span>
												<div class="layui-box layui-upload-button" style="position:absolute;left: 290px;">
													<input type="file" id="invoteupdata" name=invotebg accept="image/gif, image/jpeg, image/png" lay-ext="jpg|png|gif"  class="layui-upload-file" onchange="$('#invotebg').html($(this).val());$('.invite_close').show()">
													<span class="layui-upload-icon"><i class="layui-icon"></i>上传图片</span>
												</div>

												<div style="display: inline-block;margin-left: 70px">
													<span id="invotebg" style="float: left"></span>
													<i class="layui-icon invite_close" style="display: none;float: left;margin-top: -5px;cursor:pointer;" onclick="  $('#invotebg').html('');$('#invite_show').attr('src','<#if inviteData?? && inviteData.consult_invite_bg??>/res/image?id=${inviteData.consult_invite_bg?url}<#else>/im/img/webwxgetmsgimg.jpg</#if>'); $('.invite_close').hide(); clearInvoteupdata()">&#x1006;</i>
												</div>

											</div>
										</div>
										
										
									</div>
									<div class="col-md-5">
										<div class="ukefu-im-preview" id="ukefu-point" style="height:183px;position: absolute;">
											<div id="ukefu-cousult-invite-dialog" class="ukefu-im-preview-bar ukefu-cousult-invite-dialog ukefu-theme-color theme<#if inviteData??>${inviteData.consult_invite_color!'1'}<#else>1</#if>" style="padding:5px;height:160px;width:400px;background:url('<#if inviteData?? && inviteData.consult_invite_bg??>/res/image?id=${inviteData.consult_invite_bg}<#else>'/im/img/webwxgetmsgimg.jpg'</#if>') no-repeat">
												<span style="float:right;">	
													<i class="layui-icon" style="font:size:12px;color:#ffffff;">&#x1006;</i>
												</span>
												<div class="ukefu-cousult-invite-content" id="ukefu-cousult-invite-content">
													${inviteData.consult_invite_content!''}
												</div>
												<div class="ukefu-cousult-invite-btn">
													<button class="layui-btn layui-btn-primary theme${inviteData.consult_invite_color!'1'}" id="invite-btn" style="border-color:#FFFFFF !important;color:#FFFFFF;">${inviteData.consult_invite_later!'稍后再说'}</button>
													<button class="layui-btn layui-btn-primary">${inviteData.consult_invite_accept!'现在咨询'}</button>
												</div>
												
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-3"></div>
				<div class="col-lg-9">
					<div class="layui-form-item">
			          <div class="layui-input-block">
			            <button class="layui-btn" lay-submit="" lay-filter="formDemo">保存</button>
			            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
			          </div>
			        </div>
				</div>
			</div>
		</form>
	</div>
</div>
</div>

<script type="text/javascript">

	function clearInvoteupdata(){
		invoteupdata.outerHTML = invoteupdata.outerHTML;
	}

	layui.use('form', function(){
		var form = layui.form;
		form.render('select'); //刷新select选择框渲染
	});
	$(document)
	.ready(
			function() {
				$('.ukefu-im-ping-color .ukefu-im-item')
						.click(
								function() {
									var theme = $(this).attr(
											'data-class');
									$('#ukefu-point .ukefu-theme-color , #invite-btn')
											.each(
													function() {
														$(this)
																.removeClass(
																		"theme1 theme2 theme3 theme4 theme5 theme6")
																.addClass(
																		theme);
													});
									$('.ukefu-im-ping-color .ukefu-im-item')
											.removeClass(
													'ukefu-im-checked');
									$(this).addClass('ukefu-im-checked');
									$('#consult_invite_color').val($(this).attr('data-value'));
								});
			});			
</script>

