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
						${tagType.name!''}标签<#if userList??>（${userList.totalElements!''}）</#if>
						<span style="float:right;">
							<button class="layui-btn layui-btn-sm green" href="/setting/tag/add?tagtype=${tagType.code!''}" data-toggle="ajax" data-width="650" data-height="270" data-title="添加标签">
								添加标签
							</button>
						</span>
					</h1>
					<div class="row" style="padding:5px;">
						<div class="col-lg-12">
							<table class="layui-table" lay-skin="line">
							  <colgroup>
									<col width="20%">
									<col width="20%">
									<col width="20%">
									<col width="20%">
									<col width="1%">
								<col>
							  </colgroup>
							  <thead>
								<tr>
								  <th>标签</th>
								  <th>创建时间</th>
								  <th>使用次数</th>
								  <th>颜色</th>
								  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
								</tr> 
							  </thead>
							  <tbody>
								<#if tagList?? && tagList.content?? && tagList.content?size gt 0>
								<#list tagList.content as tag>
								<tr>
								  <td>
										<a href="javascript:void(0)">${tag.tag!''}</a>
								  </td>
								  <td>${tag.createtime?string('yyyy-MM-dd HH:mm:ss')}</td>
								  <td>${tag.times!'0'}</td>
								  <td><#if tag.color??><div class="ukefu-im-theme"><div class="ukefu-tag theme${tag.color}" style="color:#ffffff;">${tag.tag!''}</div></div></#if></td>
								  <td style="white-space:nowrap;" nowrap="nowrap">
								  		<a href="/setting/tag/edit?id=${tag.id!''}&tagtype=${tagType.code!''}" data-toggle="ajax" data-width="650" data-height="270" data-title="编辑标签">
								  			<i class="layui-icon">&#xe642;</i>
								  			编辑
								  		</a>
								  		<a href="/setting/tag/delete?id=${tag.id!''}&tagtype=${tagType.code!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除标签？">
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
											<div style="">还没有标签记录</div>
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
				,pages: <#if tagList??>${tagList.totalPages}<#else>0</#if> //总页数
				,curr:<#if tagList??>${tagList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/setting/tag?p="+data.curr ;
					}
				}
		   });
		});
</script>