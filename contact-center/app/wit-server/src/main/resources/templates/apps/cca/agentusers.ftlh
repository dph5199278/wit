<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1">
			对话列表
			<form class="layui-form" name="search" method="post" id="searchConversation">
				<div class="layui-form-item">
					<div class="layui-inline" style="margin-right:0px;width: 100%">
						<select name="skill" class="ukefu-input" lay-ignore style="width: 90px;display: inline-block" onchange="renderAgentsByOrgan(this.value)" >
							<option value="">所有技能组</option>
							<#if skillGroups??>
							<#list skillGroups as tpskill>
							<option value="${tpskill.id}" <#if skill?? && skill == tpskill.id>selected="selected"</#if>>${tpskill.name!''}</option>
							</#list>
							</#if>
						</select>
				        <label>~</label>
						<select id="agent" name="agentno" class="ukefu-input" lay-ignore style="width: 100px;display: inline-block">
							<option value="">请选择技能组</option>
						</select>
						<div class="layui-input-inline" style="float: right;width: auto;margin-right: 0px">
							<button class="layui-btn layui-btn-sm layui-btn-primary" style="color:#ffffff;">
								<i class="layui-icon"></i>
							</button>
						</div>
					</div>
				</div>
			</form>
		</h1>
		<ul class="dialog-list" id="chat_users">
			<#if agentUserList??>
			<#list agentUserList as agentuser>
			<li class="clearfix chat-list-item <#if curagentuser?? && curagentuser.id?? && agentuser.id?? && curagentuser.id == agentuser.id>active</#if>"
			id="agentuser_${agentuser.userid!''}" remove-id="${agentuser.id!''}" data-id="${agentuser.userid!''}">
			<a href="/apps/cca/agentuser?id=${agentuser.id!''}&channel=${agentuser.channel!''}" data-toggle="load" data-target="#ukefu-chat-agent" onclick="$('.chat-list-item.active').removeClass('active');$(this).closest('li.chat-list-item').addClass('active');cleanTopMsgTip(this) ;$('#last_msg_${agentuser.userid!''}').text(0).hide();">
					<img src="<#if agentuser.headimgurl?? && agentuser.headimgurl != ''>${agentuser.headimgurl}<#else>/images/im/user.png</#if>" style="width:45px;height:45px;">
				  <#if agentuser.status?? && agentuser.status == 'end'>
						<#if agentuser.channel?? && agentuser.channel == "weixin">
						<i class="kfont ukefu-channel-icon-end" id="tip_icon_wenxin_${agentuser.userid!''}">&#xe64d;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "webim">
						<i class="kfont ukefu-channel-icon-end" id="tip_icon_webim_${agentuser.userid!''}">&#xe60d;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "skype">
						<i class="csfont ukefu-channel-icon-end" id="tip_icon_skype_${agentuser.userid!''}">&#59404;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "phone">
						<img class="ukefu-channel-image" src="/images/cde-ico-gray.png" id="tip_icon_phone_${agentuser.userid!''}">
					    </#if>
					<#else>
						<#if agentuser.channel?? && agentuser.channel == "weixin">
						<i class="kfont ukefu-channel-icon" id="tip_icon_wenxin_${agentuser.userid!''}">&#xe64d;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "webim">
						<i class="kfont ukefu-channel-icon" id="tip_icon_webim_${agentuser.userid!''}">&#xe60d;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "skype">
						<i class="csfont ukefu-channel-icon" id="tip_icon_skype_${agentuser.userid!''}">&#59404;</i>
						<#elseif agentuser.channel?? && agentuser.channel == "phone">
						<img class="ukefu-channel-image" src="/images/phone-ico.png" id="tip_icon_phone_${agentuser.userid!''}">
					    </#if>
			        </#if>
					<div class="dialog-info">
						<div class="address">
							<span style="width:90px;display:block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;word-wrap: break-word;
word-break: break-all;">${agentuser.username!''}</span>
						</div>
						<div class="news">
							<#if agentUser.servicetime??>${agentuser.servicetime?string('yyyy-MM-dd HH:mm:ss')}<#else>${agentuser.servicetime?string('yyyy-MM-dd HH:mm:ss')}</#if>
							<#if agentuser.status?? && agentuser.status == 'end'>
								<small class="label bg-gray pull-right" id="tip_message_${agentuser.userid!''}">离开</small>
							<#else>
								<small class="label bg-green pull-right" id="tip_message_${agentuser.userid!''}">在线</small>
							</#if>
							
						</div>
					</div>
					<div class="last-msg">
						<small class="ukefu-badge bg-red" id="last_msg_${agentuser.userid!''}" style="<#if agentuser.tokenum == 0 || (curagentuser?? && curagentuser.id == agentuser.id)>display:none;</#if>">${agentuser.tokenum!0}</small>
					</div>
				</a>
			</li>
			</#list>
			</#if>
		</ul>
	</div>
</div>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<script>
	function renderAgentsByOrgan(organid){
		if(organid.length>0){
			restApiRequest({
				silent: true,
				path: 'user',
				data: {
					ops: 'findByOrgan',
					organ: organid
				}
			}).then(function(result){
				if(result.rc == 0){
					$("#agent").html('<option value="">所有坐席</option>');
					for (var i=0 ; i<result.data.length ; i++) {
						$("#agent").prepend('<option value="' + result.data[i].id + '">' + result.data[i].uname + '</option>')
					};
				}
			}, function(error){
				// 服务器异常
				top.layer.msg('服务器抽风，请稍后再试！', {icon: 2, offset: 'b', time: 3000})
			})
		}else{
			$("#agent").html("<option value=\"\">请选择技能组</option>");
		}

	}

	$(function(){
		$('#searchConversation').on('submit', function(e){
			e.preventDefault();  //prevent form from submitting
			var data = $("#searchConversation :input").serializeArray();
			console.log(data)
			var params = {};
			for(var index = 0; index < data.length; index++){
				params[data[index]['name']] = data[index]["value"];
			}
			$.ajax({
				url:'/apps/cca/query',
				data:{skill:params["skill"],agentno:params["agentno"]},
				type:"post",
				success:function(data){
					$("#chat_users").html(data);
					if($("#chat_users li").length > 0){
						if($("#chat_users li:first-child").attr('id').substring(10) !== cursession){
							$("#chat_users li:first-child a").click();
						}else{
							$("#chat_users li:first-child").addClass("active")
						}
					}else {
						cursession = "";
                        $("#ukefu-chat-agent").html(" <div style=\"height: 100%;background: #fff;padding-top: 100px\" class=\"box-body ukefu-im-theme\"><div class=\"ukefu-empty\"><i class=\"layui-icon\">&#xe63a;</i><div style=\"\">没有搜索结果</div>\</div>\</div>");
					}
				}
			});
		});
	});
</script>


