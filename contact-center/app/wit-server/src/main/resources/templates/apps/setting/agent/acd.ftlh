<div class="layui-layout layui-layout-content">
	<div class="layui-side layui-bg-black">
		<div class="layui-side-scroll">
		<#include "/apps/setting/include/left.ftlh"/></div>
	</div>
	<div class="layui-body">
		<div class="layui-side-scroll">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="site-h1" style="background-color:#FFFFFF;">
						ACD策略（坐席分配策略）<#if userList??>（${userList.totalElements!''}）</#if>
						<span style="float:right;">
							<button class="layui-btn layui-btn-sm green" href="/admin/user/add" data-toggle="ajax" data-width="750" data-title="创建新用户">
								添加策略
							</button>
						</span>
					</h1>
					<div class="row" style="padding:5px;">
						<div class="col-lg-12">
							<table class="layui-table" lay-skin="line">
							  <colgroup>
									<col width="20%">
									<col width="15%">
									<col width="25%">
									<col width="15%">
									<col width="10%">
									<col width="15%">
									<col width="1%">
								<col>
							  </colgroup>
							  <thead>
								<tr>
								  <th>用户</th>
								  <th>姓名</th>
								  <th>电子邮件</th>
								  <th>手机</th>
								  <th>坐席</th>
								  <th>注册时间</th>
								  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
								</tr> 
							  </thead>
							  <tbody>
								<#if userList?? && userList.content??>
								<#list userList.content as user>
								<tr>
								  <td>
										<a href="">
											<i class="layui-icon headimg">&#xe612;</i>
											<div style="margin-left:50px;margin-top:0px;">
												${user.username!''}
												<div style="color:#aaaaaa;font-size:12px;">
													系统管理员
												</div>
											</div>
										</a>
										
								  </td>
								  <td>${user.uname!''}</td>
								  <td>${user.email!''}</td>
								  <td>${user.mobile!''}</td>
								  <td>
								  	<#if user.agent>
								  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
								  	</#if>
								  </td>
								  <td>${user.createtime!''}</td>
								  <td style="white-space:nowrap;" nowrap="nowrap">
								  		<a href="/admin/user/edit?id=${user.id!''}" data-toggle="ajax" data-width="750" data-title="编辑用户信息">
								  			<i class="layui-icon">&#xe642;</i>
								  			编辑
								  		</a>
								  		<a href="/admin/user/delete?id=${user.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除记录？">
								  			<i class="layui-icon" style="color:red;">&#x1006;</i>
								  			删除
								  		</a>
								  </td>
								</tr>
								</#list>
								<#else>
								<tr>
									<td colspan="20" style="height:400px;">
										<div class="ukefu-empty" style="background: none">
											<i class="layui-icon">&#xe63a;</i>
											<div style="">还没有ACD策略</div>
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
		</div>
	</div>
</div>

<script>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if userList??>${userList.totalPages}<#else>0</#if> //总页数
				,curr:<#if userList??>${userList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/user/index?p="+data.curr ;
					}
				}
		   });
		});
</script>