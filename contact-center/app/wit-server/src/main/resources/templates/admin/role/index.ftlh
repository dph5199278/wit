<div class="row" style="height:100%;">
	<div class="col-lg-3" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			角色列表
			<span style="float:right;">
				<button class="layui-btn layui-btn-sm green" href="/admin/role/add" data-toggle="ajax" data-height="200" data-width="550" data-title="新建角色">
					新建角色
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<ul>
					<#if roleList??>
					<#list roleList as role>
					<li class="uk_role <#if roleData?? && roleData.id == role.id>this</#if>">
						<a href="/admin/role/index?role=${role.id!''}" style="word-wrap: break-word;word-break: break-all;">
							<i class="kfont">&#xe7d0;</i>
							${role.name!''}
						</a>
					</li>
					</#list>
					</#if>
				</ul>
			</div>
		</div>
	</div>
	<div class="col-lg-9">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			<span style="width:200px;display:inline-block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;word-wrap: break-word;
word-break: break-all;"><#if roleData??>${roleData.name!''}<#else>坐席组</#if><#if userList??>（${userList?size!''}）</#if></span>
			<span style="float:right;">
				<#if roleData??>
				<button class="layui-btn layui-btn-sm green" href="/admin/role/seluser?role=${roleData.id!''}" data-toggle="ajax" data-width="750" data-title="添加用户到角色">
					添加用户到角色
				</button>
				<button class="layui-btn layui-btn-primary layui-btn-sm" href="/admin/role/edit?id=${roleData.id}" data-toggle="ajax" data-width="550" data-height="200" data-title="修改角色">
					修改角色
				</button>
				<button class="layui-btn layui-btn-warm layui-btn-sm" href="/admin/role/auth?id=${roleData.id}" data-toggle="ajax" title="角色授权" data-width="400" data-height="500">
					角色授权
				</button>
				<button class="layui-btn layui-btn-danger layui-btn-sm" href="/admin/role/delete?id=${roleData.id!''}" data-toggle="tip" data-title="请确认是否删除角色？">
					删除角色
				</button>
				</#if>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="30%">
						<col width="20%">
						<col width="25%">
						<col width="24%">
						<col width="1%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th>用户</th>
					  <th>姓名</th>
					  <th>电子邮件</th>
					  <th>手机</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody style="table-layout: fixed; word-break: break-all;">
					<#if userRoleList?? && userRoleList.content??>
					<#list userRoleList.content as userRole>
					<tr>
					  <td style="150px">
							<a href="">
								<i class="layui-icon headimg">&#xe612;</i>
								<div style="margin-left:50px;margin-top:0px;">
									${userRole.user.username!''}
									<div style="color:#aaaaaa;font-size:12px;">
										${userRole.role.name!''}
									</div>
								</div>
							</a>
					  </td>
					  <td style="width:150px">${userRole.user.uname!''}</td>
					  <td style="wdth:150px">${userRole.user.email!''}</td>
					  <td>${userRole.user.mobile!''}</td>
					  <td style="white-space: nowrap;">
					  		<a href="/admin/role/user/delete?id=${userRole.id!''}&role=${userRole.role.id!''}"  data-toggle="tip" data-title="请确认是否移除？">
					  			<i class="layui-icon" style="color:red;">&#x1006;</i>
					  			移除
					  		</a>
					  </td>
					</tr>
					</#list>
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
	layui.use('layer', function(){
		var layer = layui.layer
	<#if msg?? && msg == 'admin_role_save_success'>
		layer.msg('添加成功',{icon: 1, time: 1000})
	</#if>
	<#if msg?? && msg == 'admin_role_save_exist'>
		layer.msg('角色添加失败，角色已存在',{icon: 2, time: 3000})
	</#if>
	<#if msg?? && msg == 'admin_role_update_success'>
		layer.msg('角色修改成功',{icon: 1, time: 1000})
	</#if>
	<#if msg?? && msg == 'admin_role_update_not_exist'>
		layer.msg('角色修改失败',{icon: 2, time: 3000})
	</#if>
	});

</script>
