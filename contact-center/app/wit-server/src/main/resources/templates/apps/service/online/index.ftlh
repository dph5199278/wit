<style>
	.beyond-hiding td{
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	 }

	.beyond-hiding th , .beyond-hiding td{
		width: 70px;
		padding: 9px 5px;
		cursor:default;
	}
</style>


<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1">
					访客视图
				</h1>
				<div class="box ukefu-console" style="padding-top:0px !important;">
					<span class="user-webim-icon"><i class="layui-icon ukewo-btn" style="font-size:40px;">&#xe612;</i></span>
					<div class="ukefu-user-index">
						<h1 title="${agentUser.username!''} <#if contacts??>（${contacts.name!''}）</#if>" style="display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp: 1;overflow: hidden;">
							${agentUser.username!''}
							<#if contacts??>（${contacts.name!''}）</#if>
						</h1>
						<div class="ukefu-join-time ukefu-text-muted" title="访问时间">
							<#if agentUser?? && agentUser.createtime??>${agentUser.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if>
						</div>
					</div>
				</div>
				<div class="box ukefu-console">
					<div class="ukefu-relation">
						<span lay-event="" class="ukefu-relation-item" style="width:20%;">
							咨询
							<a href="/service/history/index" class="measure"> <#if agentServiceList??>${agentServiceList?size}</#if></a>
						</span>
						<span lay-event="" class="ukefu-relation-item">
							邀请
							<a href="/apps/onlineuser" class="measure">
								${inviteResult.users + inviteResult.inviteusers + inviteResult.refuseusers}
							</a>
						</span>
						<span lay-event="" class="ukefu-relation-item">
							拒绝
							<a href="/apps/onlineuser" class="measure" style="color:red !important;"> ${inviteResult.refuseusers}</a>
						</span>
					</div>
				</div>
				<div class="box ukefu-console">
					<div class="ukefu-relation">
						
						<span class="ukefu-relation-item" style="width:55%;">平均会话时长
							<a href="/service/history/index" class="measure">
								<#if agentServicesAvg??>${(agentServicesAvg /(1000*60*60))?string('00')}:${((agentServicesAvg %(1000*60*60))/(1000*60))?string('00')}:${((agentServicesAvg %(1000*60))/(1000))?string('00')}<#else>00:00:00</#if>
							</a>
						</span> 
					</div>
				</div>
				<div class="box">
					<div class="box-title">
						<h1 class="site-h1" style="background-color:#EEEEEE;">
							访问信息
							<!-- 
							<div style="float:right;">
								<button class="layui-btn layui-btn-sm layui-btn-radius layui-btn-danger" data-href="/agent/blacklist?userid=${curagentuser.userid!''}&agentserviceid=${curagentuser.agentserviceid!''}&agentuserid=${curagentuser.id}&times=${serviceCount!0}&chattime=<#if onlineUser?? && onlineUser.betweentime??>${onlineUser.betweentime}<#else>0</#if>" data-toggle="tip" data-title="拉黑访客后，此访客不会再接入人工坐席，当前对话会立即结束，可以在“客服设置->黑名单”中取消拉黑，请确认是否拉黑该访客？" data-callback="Proxy.tipMsgForm">拉黑访客</button>
							</div>
							 -->
						</h1>
					</div>
					<div class="box-body" style="padding:0px 10px;position: relative;">
						<#if agentUser.channel?? && agentUser.channel == "webim">
								<#include "/apps/agent/channel/webim.ftlh">
						<#elseif agentUser.channel?? && agentUser.channel == "weixin">
								<#include "/apps/agent/channel/weixin.ftlh">
						</#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="layui-body ukefu-chat-agent" id="ukefu-chat-agent">
	<div class="main-agentuser" style="overflow-y:auto;">
		<div class="box">
			<div class="box-title">
				<h1 class="site-h1" style="border-top:1px solid #e6e6e6;height:50px;">
					<span class="ukefu-bt">
						<div class="ukefu-bt-text" style="width: 99%;padding-left: 0px;">
						<i class="layui-icon ukewo-btn" style="font-size:28px;margin-right: 10px"></i>
							<div title="${agentUser.username!''}<#if onlineUser??>（${onlineUser.region!''}）<#elseif weiXinUser??>（${weiXinUser.country} ${weiXinUser.province!''} ${weiXinUser.city!''}）</#if>" class="ukefu-bt-text-title" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								${agentUser.username!''}<#if onlineUser??>（${onlineUser.region!''}）<#elseif weiXinUser??>（${weiXinUser.country} ${weiXinUser.province!''} ${weiXinUser.city!''}）</#if>
							</div>
							<div class="ukefu-bt-text-content" style="margin-top: 0px;line-height: 20px;">
								访客视图-访客访问记录，访客咨询记录
							</div>
						</div>
					</span>
					
				</h1>
			</div>
			<div class="box-body">
				<div class="layui-tab">
				  <ul class="layui-tab-title">
				    <li class="layui-this">咨询记录</li>
				    <li>访问记录</li>
				    <#if contacts??>
				    <li>联系人</li>
				    </#if>
				  </ul>
				  <div class="layui-tab-content">
				    <div class="layui-tab-item layui-show">
				    	<table class="layui-table beyond-hiding" lay-skin="line" style="word-wrap:break-word; word-break:break-all;table-layout: fixed">
							<thead>
								<tr class="design-sortable-tr">
									<th>用户名</th>
									<th>接入渠道</th>
									<th>服务坐席</th>
									<th>访问时间</th>
									<th>咨询时间</th>
									<th>咨询时长</th>
									<th>状态</th>
								</tr>
							</thead>
							<tbody>
								<#if agentServiceList?? && agentServiceList?size gt 0>
								<#list agentServiceList as agentService>
								<tr class="tdHasContorllor design-dropable-td <#if curAgentService?? && curAgentService.id == agentService.id>ukefu-current-agentservice</#if> ukefu-agentservice-list">
									<td title="${agentService.username!''}">
										<a href="/service/online/chatmsg?id=${agentService.id!''}" data-toggle="load" data-target="#onlinecontent">
										${agentService.username!''}
										</a>
									</td>
									<td title="${agentService.channel!''}">
										${agentService.channel!''}
									</td>
									<td title="${agentService.agentusername!''}">
										${agentService.agentusername!''}
									</td>
									<td <#if agentService.logindate??>title="${agentService.logindate?string('yyyy-MM-dd HH:mm:ss')}"</#if>>
								        <#if agentService.logindate??>${agentService.logindate?string('yyyy-MM-dd HH:mm:ss')}</#if>
							        </td>
									<td <#if agentService.servicetime??>title="${agentService.servicetime?string('yyyy-MM-dd HH:mm:ss')}"</#if>>
										<#if agentService.servicetime??>
										${agentService.servicetime?string('yyyy-MM-dd HH:mm:ss')}
										</#if>
									</td>
									
									<td class="minSize">
										${(agentService.sessiontimes/(1000*60*60))?string('00')}:${((agentService.sessiontimes%(1000*60*60))/(1000*60))?string('00')}:${((agentService.sessiontimes%(1000*60))/(1000))?string('00')}
									</td>
									<td class="minSize">
										<#if agentService.status?? && agentService.status == 'inservice'>
											<small class="ukefu-label theme1" title="对话中">对话中</small>
										<#else> 	
											<small class="ukefu-label theme4" title="访客已邀请">已结束</small>
										</#if>
									</td>
									
								</tr>
								</#list>
								<#else>
									<tr>
										<td colspan="7" style="height:400px;">
											<div class="ukefu-empty" style="background: none">
												<i class="layui-icon">&#xe63a;</i>
												<div style="">还没有历史会话</div>
											</div>
										</td>
									</tr>
								</#if>
							</tbody>
						</table>	
				    </div>
				    <div class="layui-tab-item">
				    	<table class="layui-table" lay-skin="line">
							<thead>
								<tr class="design-sortable-tr">
									<th width="22%">终端/操作系统/浏览器</th>
									<th width="20%">访问时间</th>
									<th>停留时间</th>
									<th>地域</th>
									<th>邀请</th>
									<th>拒绝</th>
								</tr>
							</thead>
							<tbody>
								<#if onlineUserHistList?? && onlineUserHistList?size gt 0>
								<#list onlineUserHistList as onlineuser>
								<tr class="tdHasContorllor design-dropable-td">
									<td class="ukefu-online-client">
										<#if onlineuser.mobile?? && onlineuser.mobile == "1">
										<i class="kfont">&#xe603;</i>
										<#else>
										<i class="kfont">&#xe601;</i>
										</#if>
										
										<#if onlineuser.opersystem?? && onlineuser.opersystem == "windows">
										<i class="kfont">&#xe801;</i>
										<#elseif onlineuser.opersystem?? && onlineuser.opersystem == "linux">
										<i class="kfont">&#xe726;</i>
										<#elseif onlineuser.opersystem?? && onlineuser.opersystem == "mac">
										<i class="kfont">&#xe6be;</i>
										</#if>
										
										${onlineuser.browser!''}
									</td>
									<td class=" "><#if onlineuser.createtime??>${onlineuser.createtime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
									<td class="minSize ">
										${(onlineuser.betweentime/(1000*60*60))?string('00')}:${((onlineuser.betweentime%(1000*60*60))/(1000*60))?string('00')}:${((onlineuser.betweentime%(1000*60))/(1000))?string('00')}
									</td>
									<td class=" ">${onlineuser.region!''}</td>
									<td class=" ">${onlineuser.invitetimes!''}</td>
									<td class=" ">${onlineuser.refusetimes!''}</td>
								</tr>
								</#list>
								<#else>
									<tr>
										<td colspan="7" style="height:400px;">
											<div class="ukefu-empty"  style="background: none">
												<i class="layui-icon">&#xe63a;</i>
												<div style="">还没有在线访客记录</div>
											</div>
										</td>
									</tr>
								</#if>
							</tbody>
						</table>
				    </div>
				    <div class="layui-tab-item">
				    	<h1 class="site-h1">
							联系人详细信息
						</h1>
						<div class="box ukefu-console" style="padding-top:0px !important;">
							<#include "/apps/service/online/contacts.ftlh">
						</div>
				    	
				    </div>
				  </div>
				</div>
			</div>
		</div>
	</div>
	<div class="ukefu-chat-prop">
		<div class="ukefu-prop-list">
			<div class="layui-side-scroll" id="onlinecontent">
				<#include "/apps/service/online/chatmsg.ftlh">
			</div>
		</div>
	</div>	
</div>

<script>
	$('#tag').click(function() {
		$('#taglist').show();
	})
	$('.tag-switch').click(
			function() {
				if ($('#tag_' + $(this).data("id")).length > 0) {
					$('#tag_' + $(this).data("id")).remove();
				} else {
					$("#tags").append(
							"<small id='tag_" + $(this).data("id")
									+ "' class='ukefu-label "
									+ $(this).data("theme") + "'>"
									+ $(this).data("name") + "</small>");
				}
				loadURL($(this).data("href"));
			});
	$("#taglist").hover(null, function() {
		$(this).hide();
	});
	$('.ukefu-agentservice-list').on("click" , function(){
		$('.ukefu-agentservice-list').removeClass("ukefu-current-agentservice");
		$(this).addClass("ukefu-current-agentservice");
	});
	$('.quick-reply').click(function(){
		var target = $(this).data("id") ;
		if($('#'+target).is(":hidden")){
			$('#'+target).show() ;
		}else{
			$('#'+target).hide() ;
		}
	});
</script>