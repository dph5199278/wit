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
						${advType.name!''} 广告位<#if adTypeList??>（${adTypeList?size}）</#if> ${advType.description!''}
						<span style="float:right;">
							<button class="layui-btn layui-btn-sm green" href="/setting/adv/add?adpos=${advType.id!''}" data-toggle="ajax" data-width="950" data-height="500" data-title="添加广告 ${advType.description!''}">
								添加广告
							</button>
						</span>
					</h1>
					<div class="row" style="padding:5px;">
						<div class="col-lg-12">
							<table class="layui-table" lay-skin="line">
							  <colgroup>
									<col width="20%">
									<col width="10%">
									<col width="10%">
									<col width="54%">
									<col width="5%">
									<col width="1%">
								<col>
							  </colgroup>
							  <thead>
								<tr>
								  <th>广告名称</th>
								  <th>类型</th>
								  <th>权重</th>
								  <th>内容</th>
								  <th style="white-space:nowrap;" nowrap="nowrap">创建时间</th>
								  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
								</tr> 
							  </thead>
							  <tbody>
								<#if adTypeList??>
								<#list adTypeList as ad>
								<tr>
								  <td>
								  	<a title="${ad.tiptext!''}" href="${ad.url!''}" target="_blank">${ad.name!''}</a>
								  </td>
								  
								  <td>
								  	  <#if ad.adtype?? && ad.adtype == "image">图片</#if>
								  	  <#if ad.adtype?? && ad.adtype == "text">文本</#if>
								  </td>
								  <td>${ad.weight}</td>
								  <td>
								  	  <#if ad.adtype?? && ad.adtype == "image"><img src="${ad.imgurl!''}" class="ukefu-media-image" data-type="preview" style="max-width:100px;max-height:50px;"></#if>
								  	  <#if ad.adtype?? && ad.adtype == "text">${(ad.content!'')?no_esc}</#if>
								  </td>
								  <td style="white-space:nowrap;" nowrap="nowrap">${ad.createtime?string('yyyy-MM-dd HH:mm:ss')}</td>
								  <td style="white-space:nowrap;" nowrap="nowrap">
								  		<a href="/setting/adv/edit?id=${ad.id!''}&adpos=${ad.adpos!''}" data-toggle="ajax" data-width="950" data-height="500" data-title="编辑广告">
								  			<i class="layui-icon">&#xe642;</i>
								  			编辑
								  		</a>
								  		<a href="/setting/adv/delete?id=${ad.id!''}&adpos=${ad.adpos!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除广告？">
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
				</div>	
			</div>
		</div>
	</div>
</div>
