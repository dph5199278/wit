
<div class="ukefu-chat-history">
	<#if title??>
	<#else>
	<h1 class="site-h1" style="padding-bottom:0px;margin-bottom:0px;border-bottom:0px;">
		<ul class="layui-tab-title">
			<li class="layui-this" id="chat_his">对话内容</li>
			<#if summary??>
			<li id="summary">服务小结</li>
			</#if>
		</ul>
	</h1>
	</#if>
	<div id="chat_msg_list" class="chat-message chat_msg_list">
		<#if agentUserMessageList?? && agentUserMessageList.content??>
			<#list agentUserMessageList.content?reverse as chatmessage>
			<div class="clearfix chat-block">
				<div class="<#if (chatmessage.userid?? && chatmessage.userid == user.id) || (chatmessage.calltype?? && chatmessage.calltype == '呼出')>chat-right<#else>chat-left</#if>">
					<img alt="" src="<#if chatmessage.userid?? && chatmessage.userid == user.id>/images/im/user.png<#else><#if curagentuser.headimgurl?? && curagentuser.headimgurl !=''>${curagentuser.headimgurl}<#else>/images/im/user.png</#if></#if>" style="width:45px;height:45px;" class="user-img">
					<div class="chat-message">
						<span class="user">${chatmessage.username!''}</span>
						<span class="time">${chatmessage.createtime!''}</span>
					</div>
					<div class="<#if (chatmessage.userid?? && chatmessage.userid == user.id) || (chatmessage.calltype?? && chatmessage.calltype == '呼出')>chatting-right<#else>chatting-left</#if>">
						<i class="userarrow"></i>
						<div class="chat-content"><#include "/apps/agent/media/message.ftlh"></div>
					</div>
				</div>
			</div>
			</#list>
		<#elseif curagentuser??>
			<#if curagentuser.status?? && curagentuser.status == "end">
				<div class="clearfix chat-block connect-end" id="agentuser-curstatus">
					<span class="connect-message">用户“${curagentuser.username!''}”已经离开</span>
				</div>
			<#else>
				<div class="clearfix chat-block connect-begin" id="agentuser-curstatus">
					<span class="connect-message">用户“${curagentuser.username!''}”开始对话</span>
				</div>
			</#if>
		</#if>
	</div>
	<#if summary??>
	<div class="box" id="summary_content" style="display:none;">
	    <div class="box-body" style="padding:0px 10px;position: relative;padding-top:20px;">
	        <ul class="info-list">
	            <li>记录时间：${summary.createtime?string('yyyy-MM-dd HH:mm:ss')}</li>
	            <#if summary.username??><li>姓名：${summary.username!''}</li></#if>
	            <#if summary.phone??><li>电话：${summary.phone!''}</li></#if>
	            <#if summary.email??><li>邮件：${summary.email!''}</li></#if>
	            <#if summary.resion??><li>来访原因：${summary.resion!''}</li></#if>
	            <li>服务类型：
	            	<#if summary.servicetype??>
					<#list summary.servicetype?split(",") as st>
						<#if summaryTags??>
						<#list summaryTags as tag>
							<#if st == tag.id>
								<#if st_index gt 0>，</#if>${tag.tag!''}
							</#if>
						</#list>
						</#if>
					</#list>
					</#if>
	            </li>
	            <li>是否预约：<#if summary.reservation>
								<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
								</#if></li>
	            <li>预约方式： ${uKeFuDic[summary.reservtype!''].name!''}</li>
	            <li>预约时间： ${summary.reservtime!''}</li>
	            <li>当前状态：<#if summary.status?? && summary.status == "1"><i class="layui-icon" style="color:#19a55d;">&#xe618;</i> 已处理<#else>未处理</#if></li>
	            <li>服务小结：
	            	${summary.summary!''}
	            </li>
	            
	        </ul>
	    </div>
	</div>
	</#if>	
</div>
<script type="text/javascript">
	$('#chat_his').click(function(){
		$('#summary_content').hide();
		$('#chat_msg_list').show();
	})
	$('#summary').click(function(){
		$('#chat_msg_list').hide();
		$('#summary_content').show();
	})
</script>
