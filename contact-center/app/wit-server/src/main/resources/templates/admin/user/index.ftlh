<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			用户列表<#if userList??>（${userList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-sm green" href="/admin/user/add" data-toggle="ajax" data-width="750" data-height="485" data-title="创建新用户">
					创建新用户
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<p style="color:red">提示：除管理员外，账号必须在【组织结构】中关联一个部门，必须在【系统角色】中关联一个角色，才可以使用资源。设置后，该账号需重新登系统才能生效。</p>
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line" style="table-layout: fixed;word-break: break-all">
				  <colgroup>
						<col>
						<col>
						<col>
						<col>
						<col>
						<#if models?seq_contains("callout")>
						<col>
						</#if>
						<col>
						<col>
						<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th width="18%">用户</th>
					  <th width="10%">注册时间</th>
					  <th width="15%">姓名</th>
					  <th width="12%">电子邮件</th>
					  <th width="12%">手机</th>
					  <th width="50px">多媒体</th>
					  <#if models?seq_contains("callout")>
					  <th width="60px">呼叫中心</th>
					  </#if>
					  <th width="50px">管理员</th>
					  <th style="white-space:nowrap;width: 110px" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody >
					<#if userList?? && userList.content??>
					<#list userList.content as user>
					<tr>
					  <td>
							<a href="javascript:void(0)">
								<i class="layui-icon headimg" style="margin-right: 10px">&#xe612;</i>
								<div title="${user.username!''}" style="margin-top:10px;width: 90px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
									${user.username!''}
									<#if user.datastatus == true><i style="color:red;">（已删除）</i></#if>
		                      	</div>
							</a>
					  </td>
			          <td>${user.createtime!''}</td>
					  <td title="${user.uname!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">${user.uname!''}</td>
					  <td>${user.email!''}</td>
					  <td>${user.mobile!''}</td>
					  <td>
					  	<#if user.agent>
					  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
					  	</#if>
					  </td>
					  <#if models?seq_contains("callout")>
					  <td>
					  	<#if user.callcenter>
					  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
					  	</#if>
					  </td>
					  </#if>
					  <td>
					  	<#if user.admin>
					  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
					  	</#if>
					  </td>
					  <td style="white-space:nowrap;" nowrap="nowrap">
					  		<a href="/admin/user/edit?id=${user.id!''}" data-toggle="ajax" data-width="750" data-height="485" data-title="编辑用户信息">
					  			<i class="layui-icon">&#xe642;</i>
					  			编辑
					  		</a>
					  		<#if !(user.admin) && user.datastatus != true >
					  		<a href="/admin/user/delete?id=${user.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除记录？">
					  			<i class="layui-icon" style="color:red;">&#x1006;</i>
					  			删除
					  		</a>
					  		</#if>
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
	  var layer = layui.layer;
	  <#if msg?? && msg == 'username_exist'>
		layer.msg('用户名存在，请重新填写',{icon: 2, time: 3000})
	  <#elseif msg?? && msg == 'email_exist'>
		layer.msg('邮件存在，请重新填写',{icon: 2, time: 3000})
      <#elseif msg?? && msg == 'mobile_exist'>
		layer.msg('手机存在，请重新填写',{icon: 2, time: 3000})
      <#elseif msg?? && msg == 'sip_account_exist'>
		layer.msg('SIP地址已经存在，请重新填写',{icon: 2, time: 3000})
      <#elseif msg?? && msg == 't1'>
		layer.msg('当前用户坐席就绪或对话未结束，不能切换为非坐席',{icon: 2, time: 3000})
	  <#elseif msg?? && msg == 'new_user_success'>
		layer.msg('新用户创建成功',{icon: 1, time: 1000})
		<#elseif msg?? && msg == 'edit_user_success'>
			layer.msg('用户编辑成功',{icon: 1, time: 1000})
		</#if>
	});
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
