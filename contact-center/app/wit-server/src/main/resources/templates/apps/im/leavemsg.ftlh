<!DOCTYPE html>
<html lang="zh">
	<head>
	    <meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta http-equiv="Cache-Control" content="no-siteapp" />
		<meta name="viewport"
			content="width=device-width, maximum-scale=1.0, initial-scale=1.0,initial-scale=1.0,user-scalable=no" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
	    <title>在线咨询</title>
	    
		<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.ico?t=1489039620156"/>
	    <link rel="stylesheet" type="text/css" href="/im/css/ukefu.css">
	    <link rel="stylesheet" id="skin" type="text/css" href="/im/css/default/ukefu.css">
	</head>
	<body style="overflow:hidden;" class="<#if type?? && type='text'>ukefu-point-text</#if>">
		<form action="/im/leavemsg/save?appid=${appid!''}" method="post">
		<div class="large ukefu-im-theme <#if type?? && type='text'>ukefu-theme-border-${inviteData.consult_dialog_color!''}</#if>">
		    <div id="containter" class="clearfix">
		    	<div id="header" class="theme${inviteData.consult_dialog_color!''}">
			   		<img src="<#if inviteData?? && inviteData.consult_dialog_logo??>/res/image?id=${inviteData.consult_dialog_logo?url}<#else>/images/logo.png</#if>" style="height:30px;padding:10px;">
			    </div>
			    <div class="ukef-leavemsg-tip" style="padding-left: 15px">
					<#if inviteData?? && inviteData.leavemessage>
						<#if sessionConfig?? && sessionConfig.hourcheck>
							  <#if isInWorkingHours>
								   <#if sessionConfig?? && sessionConfig.noagentmsg??>${(sessionConfig.noagentmsg!'')?no_esc}</#if>
							  <#else>
									<#if sessionConfig?? && sessionConfig.notinwhmsg??>${(sessionConfig.notinwhmsg!'')?no_esc}</#if>
							  </#if>
						<#else>
			                <#if sessionConfig?? && sessionConfig.noagentmsg??>${(sessionConfig.noagentmsg!'')?no_esc}</#if>
						</#if>
					</#if>
			    </div>
		    	<div class="leaveCon">
				    <form action="" method="" name="">
				    	<#if inviteData?? && inviteData.lvmname>
				        <p>
				            <span>您的姓名：<b>*</b></span>
				            <input type="text" name="name" class="txt01 txt02" required="required" maxlength="50">
				            
				        </p>
				        </#if>
				        <#if inviteData?? && inviteData.lvmphone>
				        <p>
				            <span>您的电话：<b>*</b></span>
				            <input type="text" class="txt01 txt02" name="mobile" required="required"  maxlength="30">
				            
				        </p>
				        </#if>
				        <#if inviteData?? && inviteData.lvmemail>
				        <p>
				            <span>您的邮箱：</span>
				            <input type="text" class="txt01 txt02" name="email" maxlength="100">
				            
				        </p>
				        </#if>
				        <#if inviteData?? && inviteData.lvmaddress>
				        <p>
				            <span>您的地址：</span>
				            <input type="text" class="txt01 txt02" name="address" maxlength="50">
				            
				        </p>
				        </#if>
				        <#if inviteData?? && inviteData.lvmqq>
				        <p>
				            <span>您的QQ：</span>
				            <input type="text" class="txt01 txt02" name="qq" maxlength="30">
				            
				        </p>
				        </#if>
				        <p>
				            <span>留言内容：</span>
				            <textarea class="txt01 txt03" style="position: relative; overflow: hidden;" name="content" maxlength="240"></textarea>
				        </p>
				        <p class="submitBtnWrap">
				            <input type="submit" class="btn submitBtn theme${inviteData.consult_dialog_color!''}" value="提 交"></p>
					</form>
				</div>
		    </div>
		</div>
		</form>
	</body>
</html>
<style>
   .leaveCon{
	   width: 100%;
	   max-width:550px ;
	   padding: 0 15px;
   }
   .leaveCon .txt01{
	   width: calc(100% - 130px);
   }
</style>