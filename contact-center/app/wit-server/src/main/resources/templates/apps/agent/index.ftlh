<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>
<script src="/js/utils.js"></script>
<script src="/js/lodash-4.17.4.min.js"></script>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<!--<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>-->
<script id="message_tpl" type="text/html">
	<div class="clearfix chat-block" <%if(data.type == 'writing'){%>id="writing"<%}%>>
		<div class="<% if(data.calltype == '呼出'){%>chat-right<%}else{%>chat-left<%}%>">
			<img alt="" src="<% if(data.headimgurl && data.headimgurl !=''){%><%=data.headimgurl%><%}else{%>/im/img/user.png<%} %>" class="user-img" style="width:45px;height:45px;">
			<div class="chat-message">
				<% if(data.userid == '${user.id!''}') {%>
				<span class="<% if(data.calltype == '呼出'){%>rateright<%}else{%>rateleft<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
					<span class="time"><%=data.createtime%></span>
					<span class="user"><% if(data.intervented && data.supervisorname){%><%=data.supervisorname%><%}else{%><%=data.username%><%}%></span>
				<span class="<% if(data.calltype == '呼出'){%>rateleft<%}else{%>rateright<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe"  style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
				<% }else{%>
				<span class="<% if(data.calltype == '呼出'){%>rateright<%}else{%>rateleft<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
					<span class="user"><% if(data.intervented && data.supervisorname){%><%=data.supervisorname%><%}else{%><%=data.username%><%}%></span>
					<span class="time"><%=data.createtime%></span>
				<span class="<% if(data.calltype == '呼出'){%>rateleft<%}else{%>rateright<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
				<%}%>
			</div>
			<div class="<% if(data.calltype == '呼出'){%>chatting-right<%}else{%>chatting-left<%}%>">
				<i class="userarrow"></i>
				<div class="chat-content">
					<% if(data.msgtype == 'image'){ %>
						<a href="/agent/message/image?id=<%:=data.id%>" data-toggle="ajax" data-width="950" data-height="600" title="图片"><img src="<%:=data.message%>" class="ukefu-media-image" id="<%:=data.id%>"></a>
					<% }else if(data.msgtype == 'cooperation'){ %>
						<a href="/agent/message/image?t=review&id=<%:=data.message%>" data-toggle="ajax" data-width="950" data-height="600"  title="图片">
							系统发送了一个协作邀请
						</a>
					<% }else if(data.msgtype == 'file'){ %>
						<div class="ukefu-message-file">
							<div class="ukefu-file-icon">
								<i class="kfont">&#xe61e;</i>
							</div>
							<div class="ukefu-file-desc">
								<a href="<%:=data.message%>" target="_blank">
								<div><%:=data.filename%></div>
								<div><%:=(data.filesize/1024).toFixed(3)%>Kb</div>
								</a>
							</div>
						</div>
					<% }else if(data.msgtype == 'location'){ %>
						<div><%:=data.message%></div>
						<div class="ukefu-map" id="map_<%:=data.id%>"></div>
						<script type='text/javascript'>
						var map = new BMap.Map("map_<%:=data.id%>");
						var ggPoint = new BMap.Point(<%:=data.locy%>, <%:=data.locx%>);
						MapUtil.convert(map,ggPoint , "<%:=data.message%>" , <%:=data.scale%>) ;
						<\/script>

					<% }else if(data.msgtype == 'voice'){ %>
						<p class="weixinAudio" id="voice_media_<%:=data.id%>" style="<% if(data.duration > 30) {%>width:300px;<% }else{ %>width:<%:=50+data.duration* 10%>px;<%}%>">
							<audio src="<%:=data.message%>" id="media" width="1" height="1" preload></audio>
							<span id="audio_area" class="db audio_area"  title="<%:=data.expmsg%>">
								<span class="audio_wrp db">
									<span class="audio_play_area">
										<i class="icon_audio_default"></i>
										<i class="icon_audio_playing"></i>
									</span>
									<span class="audio_length tips_global"><%:=data.duration %>秒</span>
									<span id="audio_progress" class="progress_bar" style="width: 0%;"></span>
								</span>
							</span>
						</p>
						<% if(data.expmsg != ''){ %>
						<div class="ukefu-asr"><%:=data.expmsg%></div>
						<%}%>
						<script type='text/javascript'>
							$('#voice_media_<%:=data.id%>').weixinAudio({
								autoplay:false
							});
						<\/script>
					<%}else{%>
						<%:=data.message%>
					<%}%>
				</div>
				<%if(data.type == 'writing'){%>
					<div class="chat-writing-message" title="正在输入...">
						<span class="loading">
							<span></span>
							<span></span>
							<span></span>
							<span></span>
						</span>
					</div>
				<%}%>
			</div>
		</div>
	</div>
</script>
<script id="end_tpl" type="text/html">
	<div class="clearfix chat-block connect-end" id="agentuser-curstatus">
		<span class="connect-message">用户“<%=data.username%>”已经离开</span>
	</div>
</script>
<script id="begin_tpl" type="text/html">
	<div class="clearfix chat-block connect-begin" id="agentuser-curstatus">
		<span class="connect-message">用户“<%=data.username%>”开始对话</span>
	</div>
</script>
<#if agentUserList?? && agentUserList?size gt 0>
	<div class="layui-side layui-bg-black layui-left-black">
		<div class="layui-side-scroll" id="agentusers">
			<#include "/apps/agent/agentusers.ftlh"/>
		</div>
	</div>
    <#if curagentuser?? && curagentuser.channel == "phone">
    <div class="layui-body ukefu-chat-agent" id="ukefu-chat-agent">
        <#include "/apps/agent/mainagentuser_callout.ftlh">
    </div>
	<#elseif curagentuser?? && curagentuser.channel == "skype">
	<div class="layui-body ukefu-chat-agent" id="ukefu-chat-agent">
		<#include "/apps/agent/mainagentuser_skype.ftlh">
	</div>
    <#else>
    <div class="layui-body ukefu-chat-agent" id="ukefu-chat-agent">
        <#include "/apps/agent/mainagentuser.ftlh">
    </div>
    </#if>
<#else>
	<div class="layui-layout layui-layout-content"  style="height: 100%;">
		<div class="box default-box" style="height: 100%;">
			<div class="box-header">
				<h3 class="box-title">坐席对话</h3>
			</div>
			<div class="box-body ukefu-im-theme">
				<div class="ukefu-empty" style="background: none">
					<i class="layui-icon">&#xe63a;</i>
					<div style="">还没有分配记录</div>
				</div>
			</div>
		</div>
	</div>
</#if>
<#if agentUserList?? && agentUserList?size gt 0>
<script type="text/javascript">
	$(document).ready(function(){
		$(document).bind("keydown", "Ctrl+return", function (ev) {
			sendMessage() ;
			return false;
		})
	});
	function otherTopicSearch(){
		console.log( "otherTopicSearch" )
		loadURLWithTip("/agent/other/topic?q="+ encodeURIComponent($("#otherSearchQuery").val()), "#topiclist" , null , false , true);
	}

	function otherTopicEnter(){
		if(window.event.keyCode == 13){
			otherTopicSearch();
		}
	}

	function cleanTopMsgTip(tip){
		var lastMsgNum = $(tip).find('.last-msg').text();
		if(lastMsgNum){
			var num = Number.parseInt(lastMsgNum);
			Proxy.cleanTopMsgTip(num)
		}
	}

	/**
	 * 结束会话，RestAPI请求
	 * @param agentUserId
	 * @param agentUserName
	 */
	function submitEndAgentUser(agentUserId, agentUserName){
		top.layer.confirm('请确认是否关闭和用户{0}的对话？'.format(agentUserName), {
			title: "提示",
			icon: 3
			},
			function(index, layero){
				top.layer.close(index);
				restApiRequest({
					silent: true,
					path: 'agentuser',
					data: {
						ops: 'end',
						id: agentUserId
					}
				}).then(function(result){
					if(result.rc === 0){
						// 会话关闭成功
						top.layer.msg('会话已关闭',{icon: 1, time: 1000})
						//  进行显示调整！
						if($("#chat_users li").length>1){
							$("li[remove-id="+agentUserId+"]").remove();
							$("#chat_users li:first-child a").click();
						}else{
							parent.$('#agentdesktop').click();
						}
					} else if(result.rc === 1){
						top.layer.msg('不合法的请求参数！',{icon: 2, time: 3000})
					} else if(result.rc === 2){
						top.layer.msg('不合法的操作！',{icon: 2, time: 3000})
					} else if(result.rc ===3){
						top.layer.msg('没有权限执行该操作',{icon: 2, time: 3000})
					} else if(result.rc ===4){
						top.layer.msg('未找到该访客会话',{icon: 2, time: 3000})
					}
				}, function(error){
					console.log("error", error);
					// 服务器异常
					top.layer.msg('服务器抽风，请稍后再试！',{icon: 2, time: 3000})
				})
			},
			function(index, layro){
				top.layer.close(index);
				// cancelled, do nothing
		})
	}

</script>
</#if>
