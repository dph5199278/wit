<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<#include "/apps/service/include/left.ftlh">
	</div>
</div>
<div class="layui-body">
	<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1" style="border-top:1px solid #e6e6e6;">
					当前对话<#if agentServiceList??>（${agentServiceList.totalElements}）</#if>
				</h1>
				<table class="layui-table" lay-skin="line" style="table-layout: fixed">
					<thead>
						<tr class="design-sortable-tr">
							<th>用户名</th>
							<th width="60px">接入渠道</th>
							<th width="50px">状态</th>
							<th>服务坐席</th>
							<th>服务时间</th>
							<th>等待时长</th>
							<th>地域</th>
							<th class="operating">操作</th>
						</tr>
					</thead>
					<tbody style="table-layout: fixed; word-break: break-all;">
						<#if agentServiceList?? && agentServiceList.content?? && agentServiceList.content?size gt 0>
						<#list agentServiceList.content as agentService>
						<tr class="tdHasContorllor design-dropable-td" title="${agentService.username!''}">
							<td class=" first_td_head" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								<a href="/service/online/index?userid=${agentService.userid!''}&agentservice=${agentService.id!''}">
								${agentService.username!''}
								</a>
							</td>
							<td>
								${agentService.channel!''}
							</td>
							<td>
								<#if agentService.status?? && agentService.status == 'end'>已结束<#elseif agentService.status?? && agentService.status == 'inservice'>服务中</#if>
							</td>
							<td style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" title="${agentService.agentusername!''}">
								${agentService.agentusername!''}
							</td>
							<td>
								<#if agentService.servicetime??>
								${agentService.servicetime?string('yyyy-MM-dd HH:mm:ss')}
								</#if>
							</td>
							<td>
								${(agentService.waittingtime/(1000*60*60))?string('00')}:${((agentService.waittingtime%(1000*60*60))/(1000*60))?string('00')}:${((agentService.waittingtime%(1000*60))/(1000))?string('00')}
							</td>
							<td class=" ">${agentService.region!''}</td>
							<td style="white-space:nowrap">
								<a href="/service/current/trans?id=${agentService.id!''}" data-toggle="ajax" data-title="转接坐席" data-width="750" data-height="550">
									<i class="kfont">&#xe62c;</i>
									转接
								</a>

								<#if agentService.channel?? && agentService.channel != 'skype'  && !(agentService.agent??) >
								<a href="/service/current/invite?id=${agentService.id!''}" style="margin-left:10px;" class="isInvite">
									<i class="kfont">&#xe62c;</i>
									邀请
								</a>
								</#if>
								<a href="/service/current/end?id=${agentService.id!''}" data-toggle="tip" data-title="关闭对话将会强制中断坐席和访客的对话，请确认是否关闭？" style="margin-left:10px;">
									<i class="kfont">&#xe609;</i>
									关闭
								</a>
								<a href="/service/trace?sessionid=${agentService.sessionid!''}" title="查看访客轨迹" data-toggle="ajax" data-width="1050" style="margin-left:10px;">
									<i class="kfont">&#xe638;</i>轨迹
								</a>
							</td>
						</tr>
						</#list>
						<#else>
							<tr>
								<td colspan="8" style="height:400px;">
									<div class="ukefu-empty" style="background: none">
										<i class="layui-icon">&#xe63a;</i>
										<div style="">当前没有进行中的对话</div>
									</div>
								</td>
							</tr>
						</#if>
					</tbody>
				</table>

			</div>
		</div>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12" id="page" style="text-align:center;"></div>
		</div>
	</div>
</div>
<script>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if agentServiceList??>${agentServiceList.totalPages}<#else>0</#if> //总页数
				,curr:<#if agentServiceList??>${agentServiceList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/service/current/index?p="+data.curr ;
					}
				}
		   });
		});

	$(function(){
		if($(".isInvite").length>0){
			$(".operating").css('width','230px')
		}else{
			$(".operating").css('width','175px')
		}
	});
</script>

