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
						黑名单列表<#if blackList??>（${blackList.totalElements!''}）</#if>
					</h1>
					<div class="row" style="padding:5px;">
						<div class="col-lg-12">
							<table class="layui-table" lay-skin="line">
							  <thead>
								<tr>
								  <th>来源</th>
								  <th>用户标识</th>
								  <th>拉黑时间</th>
								  <th>结束时间</th>
								  <th>最后一次对话时长</th>
								  <th>备注</th>
								  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
								</tr> 
							  </thead>
							  <tbody>
								<#if blackList?? && blackList.content??>
								<#list blackList.content as black>
								<tr>
								  <td>
									${black.channel!''}
								  </td>
								  
								  <td>${black.agentuser!''}（${black.userid!''}）</td>
								  <td>${black.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
								  <td><#if black.endtime??>${black.endtime?string("yyyy-MM-dd HH:mm:ss")}<#else>永久</#if></td>
								  <td>
								  	${(black.chattime/(1000*60*60))?string('00')}:${((black.chattime%(1000*60*60))/(1000*60))?string('00')}:${((black.chattime%(1000*60))/(1000))?string('00')}
								  </td>
								  <td>${black.description!''}</td>
								  <td style="white-space:nowrap;" nowrap="nowrap">
								  		<a href="/setting/blacklist/delete?id=${black.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除记录？">
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
											<div style="">还没有黑名单记录</div>
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
				,pages: <#if blackList??>${blackList.totalPages}<#else>0</#if> //总页数
				,curr:<#if blackList??>${blackList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/blacklist?p="+data.curr ;
					}
				}
		   });
		});
</script>