<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1 ukefu-tab" style="border-top:1px solid #e6e6e6;height:26px;">
					<span class="ukefu-bt">
						<div class="ukefu-bt-text">
							<div class="ukefu-bt-text-title">
								全部模型
							</div>
						</div>
					</span>
					<div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
						<#if cubeType??>
						<button class="layui-btn layui-btn-sm" href="/apps/report/cube/type/edit?id=${cubeType.id!''}" data-toggle="ajax" data-width="950" data-height="450" data-title="编辑分类">
							编辑分类
						</button>
						<button class="layui-btn layui-btn-sm layui-btn-danger" href="/apps/report/cube/type/delete?id=${cubeType.id!''}" data-toggle="tip" data-title="删除分类将会同时删除分类下的所有模板，请确认是否删除？">
							删除分类
						</button>
						</#if>
						<div class="layui-btn-group">
						  <a href="/apps/report/cube/add<#if typeid??>?typeid=${typeid}</#if>" title="新建模型" data-toggle="ajax" data-width="600" data-height="400" class="layui-btn layui-btn-sm" ><i class="layui-icon">&#xe608;</i>新建模型</a>
						</div>
						<!-- <div class="layui-btn-group ukefu-btn-group">
							<button class="layui-btn layui-btn-sm" href="/apps/contacts/imp<#if ckind??>?ckind=${ckind}</#if>" title="导入联系人" data-toggle="ajax" data-width="950" data-height="600">
								<i class="kfont">&#xe641;</i> 导入 
							</button>
						</div>
						<div class="layui-btn-group ukefu-btn-group">
							<button class="layui-btn layui-btn-sm dropdown-menu">
								<i class="kfont">&#xe672;</i> 导出 
								<i class="layui-icon">&#xe625;</i>
							</button>
							<ul class="ukefu-dropdown-menu layui-anim layui-anim-upbit">
								<li>
									<a href="javascript:void(0)" id="batexp" title="导出联系人" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出选中结果
									</a>
								</li>
								<li>
									<a href="/apps/contacts/expsearch?1=1<#if q??>&q=${q!''}</#if><#if ckind??>&ckind=${ckind}</#if>&subtype=${subtype}" title="导出联系人" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出当前搜索结果
									</a>
								</li>
								<li>
									<a href="/apps/contacts/expall<#if ckind??>?ckind=${ckind}</#if>" title="导出联系人" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出全部数据
									</a>
								</li>
							</ul>
						</div> -->
					</div>
				</h1>
				<table class="layui-table" lay-skin="line">
					<thead>
						<tr class="design-sortable-tr">
							<th>
								<input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
							</th>
							<th>名称</th>
							<th>创建时间</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<#if cubeList?? && cubeList.content?? && cubeList.content?size gt 0>
						<#list cubeList.content as cube>
						<tr class="tdHasContorllor design-dropable-td">
							<td style="width:1%;">
								<input type="checkbox" class="ids" name="ids" value="${cube.id!''}"/>
							</td>
							<td class=" first_td_head">
								<a href="/apps/report/cube/detail?id=${cube.id!''}">${cube.name!''}</a>
							</td>
							<td>
								${cube.createtime!''}
							</td>
							<td style="white-space:nowrap;width:1%;" nowrap="nowrap">
						  		<a href="/apps/report/cube/edit?id=${cube.id!''}" data-toggle="ajax" data-width="600" data-height="400" data-title="编辑模型">
						  			<i class="layui-icon">&#xe642;</i>
						  			编辑
						  		</a>
						  		<a href="/apps/report/cube/cubepublish?cubeid=${cube.id!''}" style="margin-left:10px;"  data-toggle="ajax" data-width="600" data-height="300" data-title="发布模型">
						  			<i class="layui-icon">&#xe609;</i>
						  			发布
						  		</a>
						  		<a href="/apps/report/cube/delete?id=${cube.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除模型？">
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
										<i class="layui-icon">&#xe60a;</i>
										<div style="">还没有模型</div>
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
	
<script>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if cubeList??>${cubeList.totalPages}<#else>0</#if> //总页数
				,curr:<#if cubeList??>${cubeList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						loadURL("/apps/report/cube/list?p="+data.curr+"&typeid=<#if typeid??>${typeid!''}<#else>0</#if>","#ukefu-cube-content");
					}
				}
		   });
		});
</script>