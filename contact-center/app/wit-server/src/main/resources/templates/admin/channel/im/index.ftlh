<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			网站列表<#if snsAccountList??>（${snsAccountList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-sm green" href="/admin/im/add" data-toggle="ajax" data-width="550" data-height="300" data-title="创建新网站">
					创建新网站
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line" style="table-layout: fixed; word-break: break-all">
				  <thead>
					<tr>
					  <th>名称</th>
					  <th>网站地址</th>
					  <th width="80px">网站标识</th>
					  <th>创建时间</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if snsAccountList?? && snsAccountList.content??>
					<#list snsAccountList.content as snsAccount>
					<tr>
					  <td title="${snsAccount.name!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
                          ${snsAccount.name!''}
					  </td>
					  <td title="${snsAccount.baseURL!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">${snsAccount.baseURL!''}</td>
					  <td>${snsAccount.snsid!''}</td>
					  <td>${snsAccount.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
					  <td style="white-space:nowrap;" nowrap="nowrap">
						   <a href="/admin/webim/index?snsid=${snsAccount.snsid!''}">
							   <i class="layui-icon">&#xe632;</i>
                               接入
						   </a>
					  		<a href="/admin/im/edit?id=${snsAccount.id!''}" data-toggle="ajax"  data-width="550" data-height="300" data-title="编辑网站">
					  			<i class="layui-icon">&#xe642;</i>
					  			编辑
					  		</a>
					  		<#if !(snsAccount.admin) && snsAccount.datastatus != true >
					  		<a href="/admin/im/delete?id=${snsAccount.id!''}" style="margin-left:10px;" data-toggle="tip"  <#if secret?? && secret.enable == true>data-confirm="请输入二次安全验证密码"</#if> title="删除网站需要进行二次密码验证，请确认是否删除？" >
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
	<#if execute?? && execute == "false">
	top.layer.alert('二次 验证密码设置失败，输入的密码错误，请重新操作！', {icon: 2});
	</#if>
	<#if msg??>
		<#if msg == '1'>
			top.layer.alert('二次验证密码设置成功', {icon: 1});
		<#elseif msg == '2'>
			top.layer.alert('二次验证密码设置失败，请确认两次输入的密码一致', {icon: 2});
		<#elseif msg == '3'>
			top.layer.alert('二次验证密码设置失败，旧的密码验证失败，请输入正确的密码', {icon: 2});
		</#if>
	</#if>
	layui.use('layer', function(){
		var layer = layui.layer;
		console.log(window.location.href)
	<#if status?? && status == 'new_webim_success'>
		layer.msg('网站添加成功',{icon: 1, time: 1000})
	<#elseif status?? && status == 'new_webim_fail'>
		layer.msg('网站添加失败',{icon: 2, time: 3000})
	</#if>
	});

	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  laypage.render({
				cont: 'page'
				,pages: <#if snsAccountList??>${snsAccountList.totalPages}<#else>0</#if> //总页数
				,curr:<#if snsAccountList??>${snsAccountList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/im/index?p="+data.curr ;
					}
				}
		   });
		});
</script>
