<#include "/public/macro.ftlh">
<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<#include "/apps/service/include/left.ftlh">
	</div>
</div>
<div class="layui-body">
	<div class="layui-side-scroll">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="site-h1" style="border-top:1px solid #e6e6e6;">
					已处理服务小结列表<#if summaryList??>（${summaryList.totalElements}）</#if>
					<span style="float:right;">
						<div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
							<form class="layui-form" name="search"	action="/apps/callcenter/summary/index" method="get" style="float: left;margin-right:5px;">
								<div class="layui-form-item">
									<div class="layui-inline" style="margin-right:0px;">
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<label class="layui-form-label" style="width: auto;">主叫：</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input type="text" name="ani" placeholder="请输入主叫号码" autocomplete="off" value="${ani!''}" class="layui-input ukefu-input">
										    </div>
										</div>
									</div>
									<div class="layui-inline" style="margin-right:0px;">
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<label class="layui-form-label" style="width: auto;">被叫：</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input type="text" name="called" placeholder="请输入被叫号码" autocomplete="off" value="${called!''}" class="layui-input ukefu-input">
										    </div>
										</div>
									</div>
									<div class="layui-inline" style="margin-right:0px;">
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<label class="layui-form-label" style="width: auto;">拨打时间：</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input name="begin" class="layui-input ukefu-input" placeholder="开始时间" value="${begin!''}" onclick="layui.laydate.render({elem: this, show: true, istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
										    </div>
											<label class="layui-form-label" style="width: auto;">~</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input name="end" class="layui-input ukefu-input" placeholder="结束时间" value="${end!''}" onclick="layui.laydate.render({elem: this, show: true, istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
										    </div>
										</div>
									</div>
									<div class="layui-inline" style="margin-right:0px;">
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <button class="layui-btn layui-btn-sm layui-btn-primary" style="color:#ffffff;">
												  <i class="layui-icon"></i>
												</button>
										    </div>
										</div>
									</div>
									
								</div>
							</form>
							<div class="layui-btn-group ukefu-btn-group" style="margin-top:2px;">
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
										<a href="/apps/callcenter/summary/expsearch?1=1<#if ani??>&ani=${ani!''}</#if><#if called??>&called=${called}</#if><#if begin??>&begin=${begin}</#if><#if end??>&end=${end}</#if>&subtype=${subtype}" title="导出联系人" target="_blank">
											<i class="kfont">&#xe672;</i>
											导出当前搜索结果
										</a>
									</li>
									<li>
										<a href="/apps/callcenter/summary/expall" title="导出联系人" target="_blank">
											<i class="kfont">&#xe672;</i>
											导出全部数据
										</a>
									</li>
								</ul>
							</div>
						</div>
					</span>
				</h1>
				<table class="layui-table" lay-skin="line">
					<thead>
						<tr class="design-sortable-tr">
							<th>
								<input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
							</th>
							<th>主叫号码</th>
							<th>被叫号码</th>
							<th>拨打时间</th>
							<th>服务类型</th>
							<th>是否预约</th>
							<th>预约方式</th>
							<th>预约时间</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<#if summaryList?? && summaryList.content??>
						<#list summaryList.content as summary>
						<tr class="tdHasContorllor design-dropable-td">
							<td style="width:1%;">
								<input type="checkbox" class="ids" name="ids" value="${summary.id!''}"/>
							</td>
							<td class="ukefu-online-client">
								<a href="/apps/callcenter/detail/index?id=${summary.statuseventid!''}">
								${summary.ani!''}
								</a>
							</td>
							<td>
								${summary.called!''}
							</td>
							<td class="minSize ">
								${summary.createtime?string('yyyy-MM-dd HH:mm:ss')}
							</td>
							<td>
								<#if tags??>
								<#list tags as tag>
									<#if summary.servicetype?? && summary.servicetype?index_of(tag.id) gte 0>
									${tag.tag!''}
									</#if>
								</#list>
								</#if>
							</td>
							<td>
								<#if summary.reservation>
								<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
								</#if>
							</td>
							<td class="minSize ">
								${uKeFuDic[summary.reservtype!''].name!''}
							</td>
							<td class="minSize ">
								${summary.reservtime!''}
							</td>
							<td class="minSize ">
								<a href="/apps/callcenter/processed/process?id=${summary.id!''}" data-toggle="ajax" data-width="950" data-height="450" title="服务小结处理">
									<i class="layui-icon">&#xe642;</i>
									再次处理
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
	$(document).ready(function(){
		$('#batexp').click(function(){
			var ids = "";
			var num = 0 ;
			$('.ids').each(function(){
				if($(this).prop("checked")){
					if(ids!=""){
						ids += "&" ;
					}
					ids += "ids=" + $(this).val();
					num++;
				}
			});
			if(num>0){
				$('#batexp').attr('href' , '/apps/callcenter/summary/expids?'+ids)	;
			}else{
				top.layer.alert("请先选择需要导出的服务小结");
				$('#batexp').attr("href" , "javascript:void(0)");
			}
			
			
		});
		
	});
	layui.use(['laypage', 'layer' , 'laydate'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  var laydate = layui.laydate;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if summaryList??>${summaryList.totalPages}<#else>0</#if> //总页数
				,curr:<#if summaryList??>${summaryList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/apps/callcenter/summary/index?p="+data.curr ;
					}
				}
		   });
		});
</script>

