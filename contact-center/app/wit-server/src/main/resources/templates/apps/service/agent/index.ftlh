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
					在线坐席<#if agentStatusList??>（${agentStatusList?size}）</#if>
				</h1>
				<table class="layui-table" lay-skin="line">
					<thead>
						<tr class="design-sortable-tr">
							<th>用户名</th>
							<th>技能组</th>
							<th>接入用户数</th>
							<th>最大用户数</th>
							<th>在线时间</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody style="table-layout: fixed; word-break: break-all;">
						<#if agentStatusList??>
						<#list agentStatusList as agentStatus>
						<tr class="tdHasContorllor design-dropable-td">
							<td class=" first_td_head" style="width: 150px">
								<a href="javascript:void(0)">
								${agentStatus.username!''}
								</a>
							</td>
							<td style="width: 150px">
								${agentStatus.skillname!''}
							</td>
							<td>
								${agentStatus.users!''}
							</td>
							<td>${agentStatus.maxusers!''}</td>
							
							<td><#if agentStatus.logindate??>${agentStatus.logindate?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
							<td>
								<#if agentStatus.agentno != user.id && user.admin>
								<a href="/service/agent/offline?id=${agentStatus.id!''}" data-toggle="tip" title="强制使登陆坐席离线，离线后，坐席不再分配访客，请确认是否操作？">
									<i class="layui-icon">&#x1006;</i>强制离线
								</a>	
								</#if>
							</td>
							
						</tr>
						</#list>
						<#else>
							<tr>
								<td colspan="20" style="height:400px;">
									<div class="ukefu-empty" style="background: none">
										<i class="layui-icon">&#xe63a;</i>
										<div style="">当前没有在线坐席</div>
									</div>
								</td>
							</tr>
						</#if>
					</tbody>
				</table>

			</div>
		</div>
	</div>
</div>
