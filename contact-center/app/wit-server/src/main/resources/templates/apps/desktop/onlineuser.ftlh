<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<script src="/js/CSKeFu_Agent_Invite.v1.js"></script>

<div class="layui-side layui-bg-black layui-left-black">
	<div class="layui-side-scroll">
		<#include "/apps/desktop/include/left.ftlh">
	</div>
</div>
<div class="layui-body">
	<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1" style="border-top:1px solid #e6e6e6;height:50px;">
					<span class="ukefu-bt">
						<i class="layui-icon ukewo-btn" style="font-size:28px;">&#xe612;</i>
						<div class="ukefu-bt-text">
							<div class="ukefu-bt-text-title" style="">
								当前在线用户列表<#if onlineUserList??>（${onlineUserList.totalElements}）</#if>
							</div>
							<div class="ukefu-bt-text-content" style="margin-top: 0px;line-height: 20px;">
								在线的访客详细信息
							</div>
						</div>
					</span>
					
				</h1>
				<table class="layui-table" lay-skin="line" style="table-layout: fixed; word-break: break-all">
					<thead>
						<tr class="design-sortable-tr">
							<th width="15%">用户名</th>
							<th>终端</th>
							<th>操作系统</th>
							<th>浏览器</th>
							<th>访问时间</th>
							<th>停留时间</th>
							<th>地域</th>
							<th>邀请次数</th>
							<th width="110px">操作</th>
						</tr>
					</thead>
					<tbody>
						<#if onlineUserList?? && onlineUserList.content?? && onlineUserList.content?size gt 0>
						<#list onlineUserList.content as onlineuser>
						<tr class="tdHasContorllor design-dropable-td">
							<td title="${onlineuser.username!''}<#if onlineuser.contacts??>（${onlineuser.contacts.name!''}）</#if>" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden">
								<a href="/service/online/index?userid=${onlineuser.userid!''}">${onlineuser.username!''}
								<#if onlineuser.contacts??>（${onlineuser.contacts.name!''}）</#if></a>
							</td>
							
							<td class="ukefu-online-client">
								<#if onlineuser.mobile?? && onlineuser.mobile == "1">
								<i class="kfont">&#xe603;</i>
								<#else>
								<i class="kfont">&#xe601;</i>
								</#if>
							</td>
							<td class="ukefu-online-client">
								<#if onlineuser.opersystem?? && onlineuser.opersystem == "windows">
								<i class="kfont">&#xe801;</i>
								<#elseif onlineuser.opersystem?? && onlineuser.opersystem == "linux">
								<i class="kfont">&#xe726;</i>
								<#elseif onlineuser.opersystem?? && onlineuser.opersystem == "mac">
								<i class="kfont">&#xe6be;</i>
								</#if>
							</td>
							<td class=" ">${onlineuser.browser!''}</td>
							<td class=" "><#if onlineuser.createtime??>${onlineuser.createtime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
							<td class="minSize ">
								${(onlineuser.betweentime/(1000*60*60))?string('00')}:${((onlineuser.betweentime%(1000*60*60))/(1000*60))?string('00')}:${((onlineuser.betweentime%(1000*60))/(1000))?string('00')}
							</td>
							<td class=" ">${onlineuser.region!''}</td>
							<td class=" ">${onlineuser.invitetimes!''}</td>
							<td>
								<#if onlineuser.invitestatus?? && onlineuser.invitestatus == 'invite'>
									<button class="layui-btn layui-btn-xs" title="访客已邀请">已邀请</button>
								<#elseif onlineuser.invitestatus?? && onlineuser.invitestatus == 'inserv'>
									<button class="layui-btn layui-btn-xs" title="访客已邀请">对话中</button>
								<#elseif onlineuser.invitestatus?? && onlineuser.invitestatus == 'refuse'>
									<a href="javascript:;" name="${onlineuser.id!''}" title="访客已拒绝邀请" class="layui-btn layui-btn-danger layui-btn-xs" onclick="sendInvitationToOnlineUser('${onlineuser.id}', false)">再次邀请</a>
								<#else> 	
									<a href="javascript:;" name="${onlineuser.id!''}" title="邀请访客" class="layui-btn  layui-btn-normal layui-btn-xs" onclick="sendInvitationToOnlineUser('${onlineuser.id}', true)">邀请</a>
								</#if>
								<a href="/service/trace?sessionid=${onlineuser.sessionid!''}" title="查看访客轨迹" data-toggle="ajax" data-width="1050" class="layui-btn  layui-btn-normal layui-btn-xs">轨迹</a>
							</td>
						</tr>
						</#list>
						<#else>
							<tr>
								<td colspan="9" style="height:400px;">
									<div class="ukefu-empty" style="background: none">
										<i class="layui-icon">&#xe63a;</i>
										<div style="">还没有在线访客记录</div>
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
				,pages: <#if onlineUserList??>${onlineUserList.totalPages}<#else>0</#if> //总页数
				,curr:<#if onlineUserList??>${onlineUserList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/apps/onlineuser?p="+data.curr ;
					}
				}
		   });
		});
</script>
