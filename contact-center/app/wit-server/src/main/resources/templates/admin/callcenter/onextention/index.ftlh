<div class="row" style="height:100%;">
	<div class="col-lg-12" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			已注册分机列表<#if onextentionList??>（${onextentionList?size}）</#if>
		</h1>
		<div class="row">
			<div class="col-lg-12">
				<div class="row" style="padding:5px;">
					<div class="col-lg-12">
						<table class="layui-table" lay-skin="line">
						  <colgroup>
								<col width="10%">
								<col width="20%">
								<col width="10%">
								<col width="10%">
								<col width="10%">
								<col width="10%">
								<col width="1%">
								<col>
						  </colgroup>
						  <thead>
							<tr>
							<#if onextentionList?? && onextentionList?size gt 0>
							  <th>user</th>
							  <th>agent</th>
							  <th>status</th>
							  <th>ping-time</th>
							  <th>host</th>
							  <th>network-ip</th>
							  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
							</#if>
							</tr> 
						  </thead>
						  <tbody>
							<#if onextentionList?? && onextentionList?size gt 0>
							<#list onextentionList as onextention>
							<tr>
							   <td>
									${onextention.sipauthuser!''}
							  </td>
							  <td>
									${onextention.agent!''}
							  </td>
							  <td>
									${onextention.pingstatus!''}
							  </td>
							  <td>
									${onextention.pingtime!''}
							  </td>
							  <td>
									${onextention.host!''}
							  </td>
							  <td>
									${onextention.networkip!''}
							  </td>
							  <td style="white-space:nowrap;" nowrap="nowrap">
							  		<a href="/admin/callcenter/online/extention/detail?extno=${onextention.sipauthuser!''}&hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="700" data-height="450" data-title="查看分机信息">
							  			<i class="layui-icon">&#xe642;</i>
							  			详情
							  		</a>
							  </td>
							</tr>
							</#list>
							<#else>
								<div class="ukefu-empty" style="background: none">
									<i class="layui-icon"></i>
									<div style="">
										还没有分机记录
									</div>
								</div>	
							</#if>
						  </tbody>
						</table>
					</div> 	   
				</div>
			</div>	
		</div>
	</div>	
</div>
