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
					满意度评价<#if serviceList??>（${serviceList.totalElements}）</#if>
				</h1>
				<table class="layui-table" lay-skin="line" style="table-layout: fixed; word-break: break-all;">
					<thead>
						<tr class="design-sortable-tr">
							<th>用户名</th>
							<th width="60px">接入渠道</th>
							<th>服务坐席</th>
							<th>咨询时间</th>
							<th>评价时间</th>
							<th width="70px">评价</th>
							<th>回复意见</th>
						</tr>
					</thead>
					<tbody>
						<#if serviceList?? && serviceList.content?? && serviceList.content?size gt 0>
						<#list serviceList.content as agentService>
						<tr class="tdHasContorllor design-dropable-td">
							<td class=" first_td_head" title="${agentService.username!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								<a href="/service/online/index?userid=${agentService.userid!''}&agentservice=${agentService.id!''}">
								${agentService.username!''}
								</a>
							</td>
							<td>
								${agentService.channel!''}
							</td>
							<td title="${agentService.agentusername!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								${agentService.agentusername!''}
							</td>
							<td>
								<#if agentService.servicetime??>
								${agentService.servicetime?string('yyyy-MM-dd HH:mm:ss')}
								</#if>
							</td>
							<td class=" ">${agentService.satistime?string("yyyy-MM-dd HH:mm:ss")}</td>
							<td class=" ">
								<#list uKeFuDic['com.dic.webim.comment'] as comment>
									<#if agentService.satislevel?? && comment.code == agentService.satislevel>${comment.name!''}</#if>
								</#list>
							</td>
							<td class=" ">
								<#if uKeFuDic[agentService.satiscomment!'']??>${uKeFuDic[agentService.satiscomment!''].name!''}<#else>${agentService.satiscomment!''}</#if> 
								
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
				,pages: <#if serviceList??>${serviceList.totalPages}<#else>0</#if> //总页数
				,curr:<#if serviceList??>${serviceList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/service/comment/index?p="+data.curr ;
					}
				}
		   });
		});
</script>
