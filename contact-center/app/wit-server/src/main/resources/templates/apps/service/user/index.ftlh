<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<#include "/apps/service/include/left.ftlh">
	</div>
</div>
<div class="layui-body">
	<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1" style="background-color:#FFFFFF;">
					全部坐席<#if userList??>（${userList.totalElements!''}）</#if>
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
							  <th>在线</th>
							  <th>注册时间</th>
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
												${user.updatetime!''}
											</div>
										</div>
									</a>
									
							  </td>
							  <td>${user.uname!''}</td>
							  <td>${user.email!''}</td>
							  <td>${user.mobile!''}</td>
							  <td>
							  	<#if onlines[user.id]?? && onlines[user.id]>
							  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
							  	</#if>
							  </td>
							  <td>${user.createtime!''}</td>
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
						location.href = "/service/user/index?p="+data.curr ;
					}
				}
		   });
		});
</script>