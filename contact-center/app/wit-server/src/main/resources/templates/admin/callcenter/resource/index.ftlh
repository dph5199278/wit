<div class="row" style="border-bottom: 1px solid #EFEFEF;padding:5px;">
	<div class="col-lg-12" style="padding-left:0px;position: relative;">
		<div class="ukefu-bt"> 
			<div class="layui-icon ukewo-btn ukefu-content-ind" style="width: 40px;height: 40px;">
				<img src="/images/setting.png"></div>
			<div class="ukefu-bt-text">
				<div class="ukefu-bt-text-title" style="font-weight:400;font-size:20px;">
					语音资源设置<#if pbxHost??>，服务器：${pbxHost.hostname!''}（${pbxHost.ipaddr!''}）</#if>
				</div>
				<div class="ukefu-bt-text-content" style="">
					<span style="font-size:15px;color:#AAAAAA;">配置语音平台的资源信息，包括分机号、技能组、IVR、录音等信息。 </span>
				</div>
				
			</div>
		</div>
	</div>
</div>
<h1 class="site-h1 ukefu-tab" style="height:26px;">
	<div class="ukefu-tab-title" style="margin-left:0px;">
		<ul class="tab-title" id="pbxHostTab">
			<li class="layui-this"><a href="/admin/callcenter/resource/config?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">服务配置</a></li>
			<li><a href="/admin/callcenter/extention?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">分机配置</a></li>
			<li><a href="/admin/callcenter/skill?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">技能分组</a></li>
			<li><a href="/admin/callcenter/router?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">路由规则</a></li>
			<li><a href="/admin/callcenter/ivr?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">IVR配置</a></li>
			<li><a href="/admin/callcenter/acl?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">访问列表</a></li>
			<li><a href="/admin/callcenter/media?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">媒体资源</a></li>
			<li><a href="/admin/callcenter/siptrunk?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">SIP网关</a></li>
			<li><a href="/admin/callcenter/online/extention?hostid=${pbxHost.id!''}" data-toggle="load" data-target="#callCenterContent">已注册分机</a></li>
		</ul>					
	</div>
</h1>
<div id="callCenterContent" style="height:calc(100% - 105px);overflow-y:auto;">
	<#include "/admin/callcenter/resource/config.ftlh">
</div>
<script>layui.use('element',
    function() {
        var element = layui.element;
        element.init();
    });
	$('#pbxHostTab li').on("click" , function(){
		$('#pbxHostTab li').removeClass("layui-this");
		$(this).addClass("layui-this");
	});	
	$('.uk_callcenter_pbxhost').on("click" , function(){
		$('.uk_callcenter_pbxhost').removeClass("this");
		$(this).addClass("this");
	})
</script>
