<form method="post" key="set-mine" action="/admin/callcenter/resource/save" class="layui-form">
	<input type="hidden" name="id" value="${pbxHost.id!''}">
	<div class="row">
		<div class="col-lg-12">
			<div class="ukefu-customer-div setting-wrapper">
				<div class="box default-box">
					<div class="box-header">
						<h3 class="box-title">网络配置</h3>
					</div>
					<div class="box-body ukefu-im-theme">
						<div class="row">
							<div class="col-lg-8">
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">1、语音平台服务器名称<font color="red">（必填项*）</font></div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音接入服务器名称</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch服务器名称</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="name" id="name" value="${pbxHost.name!''}" required="required" lay-verify="required" alue="" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">2、语音平台服务器IP地址 <font color="red">（必填项*）</font></div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音接入服务的网络地址</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch服务器的网络IPv4地址，如果是其他厂商的设备，请填写软电话的接入地址</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="ipaddr" id="ipaddr" value="${pbxHost.ipaddr!''}" required="required" lay-verify="required" alue="" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">3、语音平台服务器主机名称 <font color="red">（必填项*）</font></div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音接入服务的网络地址</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch服务器的网络地址，如果是其他厂商的设备，请填写软电话的接入地址</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="hostname" id="hostname" value="${pbxHost.hostname!''}" required="required" lay-verify="required" alue="" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">4、语音平台服务端口</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音接入服务的网络端口</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch服务器的网络端口，如果是其他厂商的设备，根据需要填写</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="port" id="port" lay-verify="number" value="${pbxHost.port}" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">5、语音平台服务访问密码</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>连接语音服务器的密码</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch服务器的网络访问密码，如未修改，无需填写</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="password" name="password" id="password" value="" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">6、语音设备类型</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音设备的类型，按照厂商选择</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">不同厂商设备的配置参数和链接方式可能会不同</p>
											</div>
											<div class="col-lg-4">
												<select name="device">
													<option value="default" <#if pbxHost?? && pbxHost.device?? && pbxHost.device == "default">selected="selected"</#if>>默认设备</option>
												</select>
											</div>
										</div>
									</div>
								</div>
								
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">7、FreeSwitch安装路径</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>FreeSwitch的安装路径</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch安装的根目录，Linux下的默认安装路径是/usr/local/freeswitch</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="fspath" id="fspath" value="${pbxHost.fspath!''}" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">8、FreeSwitch服务器录音文件URL</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>FreeSwitch的录音文件访问URL（远程录音文件需要添加http://或https://）</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">默认为空，表示从Freeswitch安装路径下读取，填写后从URL读取</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="recordpath" id="recordpath" value="${pbxHost.recordpath!''}" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">9、语音服务分机注册类型</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>语音服务分机注册类型</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">SIP话机注册在FreeSwitch上的类型</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<select name="ivrpath">
													<option value="internal" <#if !(pbxHost?? && pbxHost.ivrpath?? && pbxHost.ivrpath == "external")>selected="selected"</#if>>internal</option>
													<option value="external" <#if pbxHost?? && pbxHost.ivrpath?? && pbxHost.ivrpath == "external">selected="selected"</#if>>external</option>
												</select>
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">10、启用话机端自动应答（Call Info）</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，回拨的时候，坐席话机会启用自动应答功能</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">需要SIP话机支持此功能</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="autoanswer" id="autoanswer" <#if pbxHost?? && pbxHost.autoanswer>checked="checked"</#if>>
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">11、启用话机端自动应答（SIP Auto Answer ）</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，回拨的时候，坐席话机会启用自动应答功能</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">需要SIP话机支持此功能</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="sipautoanswer" id="sipautoanswer" <#if pbxHost?? && pbxHost.sipautoanswer>checked="checked"</#if>>
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">12、启用呼叫中心功能模块</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，呼叫中心相关功能启用</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">需要预先配置Freeswitch呼叫中心功能模块</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="callcenter" id="callcenter" <#if pbxHost?? && pbxHost.callcenter>checked="checked"</#if>>
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">13、坐席通话结束后变更状态到后处理</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，坐席在呼叫中心队列中通话结束后自动进入后处理状态</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">需要预先配置Freeswitch呼叫中心功能模块</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="afterprocess" id="afterprocess" <#if pbxHost?? && pbxHost.afterprocess>checked="checked"</#if>>
											</div>
										</div>
									</div>
								</div>
								
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">14、发起呼叫的默认编码</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>发起呼叫的请求参数</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">FreeSwitch配置选项，通过软电话条发起呼叫是使用</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="abscodec" id="abscodec" value="${pbxHost.abscodec!''}" autocomplete="off" class="layui-input">
											</div>
										</div>
									</div>
								</div>
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">15、启用WebRTC</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，可以使用网页上的WebRTC功能拨打和接听电话</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">WebRTC功能支持网页端接听和拨打电话，无需SIP话机</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="enablewebrtc" id="enablewebrtc" lay-filter="webrtc" <#if pbxHost?? && pbxHost.enablewebrtc == true>checked="checked"</#if>>
											</div>
										</div>
									</div>
									<div class="box-item" id="webrtc" style="<#if pbxHost?? && pbxHost.enablewebrtc == false>display:none;</#if>">	
										<div class="row">
											<div class="col-lg-8">
												<p>WebRTC访问地址</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="text" name="webrtcaddress" id="webrtcaddress" value="${pbxHost.webrtcaddress!''}" autocomplete="off" class="layui-input">
											</div>
										</div>
										<div class="row" style="margin-top:20px;">
											<div class="col-lg-8">
												<p>访问端口</p>
											</div>
											<div class="col-lg-4">
												<input type="text" name="webrtcport" id="webrtcport" lay-verify="port" value="${pbxHost.webrtcport!''}" autocomplete="off" class="layui-input">
											</div>
										</div>
										
										<div class="row" style="margin-top:20px;">
											<div class="col-lg-8">
												<p>WebRTC启用SSL</p>
											</div>
											<div class="col-lg-4">
												
												<input type="checkbox" title="启用" name="webrtcssl" id="webrtcssl" <#if pbxHost?? && pbxHost.webrtcssl == true>checked="checked"</#if>>
											</div>
										</div>
									</div>
								</div>
								<div class="ukefu-webim-prop">
									<div class="ukefu-webim-tl" style="clear:both;">16、外呼时在SIP客户端隐藏外呼号码</div>
									<div class="box-item">
										<div class="row">
											<div class="col-lg-8">
												<p>启用此功能后，外呼的SIP客户端（包括SIP话机）上隐藏外呼号码</p>
												<p style="color:#888888;font-size:13px;margin-top:10px;">需要启用电销组件，并且需要在电销组件的元数据里开启字段隐藏号码功能</p>
											</div>
											<div class="col-lg-4" style="text-align:right;">
												<input type="checkbox" title="启用" name="dissipphone" id="dissipphone" <#if pbxHost?? && pbxHost.dissipphone>checked="checked"</#if>>
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
<script type="text/javascript">
	layui.use('form', function(){
		var form = layui.form;
		form.render("select"); //刷新select选择框渲染
		form.on("checkbox(webrtc)" , function(data){
			if(data.elem.checked == true){
				$('#webrtc').show();		
			}else{
				$('#webrtc').hide();
			}
		});
		form.verify({
			port: [
			    /^[\d]{0,5}$/
			    ,'WebRTC端口只能输入数字'
			] 
		});      
	});
	
</script>