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
					服务小结列表<#if summaryList??>（${summaryList.totalElements}）</#if>
					<span style="float:right;">
						<div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
							<form class="layui-form" name="search" id="search"	action="/apps/agent/summary/index" method="get" style="float: left;margin-right:5px;">
								<div class="layui-form-item">
									<div class="layui-inline" style="margin-right:0px;">
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<label class="layui-form-label" style="width: auto;">服务时间：</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input name="begin" id="begin" class="layui-input ukefu-input" placeholder="开始时间" value="${begin!''}">
										    </div>
											<label class="layui-form-label" style="width: auto;">~</label>
											<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										      <input name="end" id="end" class="layui-input ukefu-input" placeholder="结束时间" value="${end!''}">
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
										<a href="/apps/agent/summary/expsearch?1=1<#if begin??>&begin=${begin}</#if><#if end??>&end=${end}</#if>&subtype=${subtype}" title="导出联系人" target="_blank">
											<i class="kfont">&#xe672;</i>
											导出当前搜索结果
										</a>
									</li>
									<li>
										<a href="/apps/agent/summary/expall" title="导出联系人" target="_blank">
											<i class="kfont">&#xe672;</i>
											导出全部数据
										</a>
									</li>
								</ul>
							</div>
						</div>
					</span>
				</h1>
				<table class="layui-table" lay-skin="line" style="table-layout: fixed;">
					<thead>
						<tr class="design-sortable-tr">
							<th width="10px">
								<input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
							</th>
							<th>访客</th>
							<th>服务坐席</th>
							<th>渠道</th>
							<th style="min-width: 80px">服务时间</th>
							<th>服务类型</th>
							<th>是否预约</th>
							<th>预约方式</th>
							<th style="min-width: 80px">预约时间</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody style="table-layout: fixed; word-break: break-all;">
						<#if summaryList?? && summaryList.content??>
						<#list summaryList.content as summary>
						<tr class="tdHasContorllor design-dropable-td">
							<td style="width:1%;">
								<input type="checkbox" class="ids" name="ids" value="${summary.id!''}"/>
							</td>
							<td class="ukefu-online-client" title="${summary.username!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								<a href="/service/online/index?userid=${summary.userid!''}&agentservice=${summary.agentserviceid!''}&id=${summary.id!''}">
								${summary.username!''}
								</a>
							</td>
							<td title="${summary.agentusername!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
								${summary.agentusername!''}
							</td>
							<td>
								${summary.channel!''}
							</td>
							<td class="minSize ">
								${summary.createtime?string('yyyy-MM-dd HH:mm:ss')}
							</td>
							<td>
								<#if summary.servicetype??>
								<#list summary.servicetype?split(",") as st>
									<#if tags??>
									<#list tags as tag>
										<#if st == tag.id>
											<#if st_index gt 0>，</#if>${tag.tag!''}
										</#if>
									</#list>
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
								<a href="/apps/agent/summary/process?id=${summary.id!''}" data-toggle="ajax" data-width="950" data-height="450" title="服务小结处理">
									<i class="layui-icon">&#xe642;</i>
									处理
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
				$('#batexp').attr('href' , '/apps/agent/summary/expids?'+ids)	;
			}else{
				top.layer.alert("请先选择需要导出的服务小结");
				$('#batexp').attr("href" , "javascript:void(0)");
			}
			
			
		});
		
	});
	var searchData = $("#search :input").serializeArray()
	var searchCondition = "";
	$.each(searchData, function() {
		searchCondition += '&'+this.name+'='+this.value
	});

	layui.use(['laypage', 'layer'], function(){
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
						location.href = "/apps/agent/summary/index?p="+data.curr+searchCondition;
					}
				}
		});
	});

	layui.use('laydate', function() {
		var laydate = layui.laydate;
	});
	function getNowFormatDate() {
		var date = new Date();
		var seperator = "-";
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var strDate = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (strDate >= 0 && strDate <= 9) {
			strDate = "0" + strDate;
		}
		var currentdate = year + seperator + month + seperator + strDate;
		return currentdate;
	}
	var start = {
		format: 'YYYY-MM-DD',
		max:getNowFormatDate(),//默认最大值为当前日期
		istoday: true,
		choose: function (datas) {
			end.min = datas; //开始日选好后，重置结束日的最小日期
		}
	};
	var end = {
		format: 'YYYY-MM-DD',
		max:getNowFormatDate(),//默认最大值为当前日期
		istoday: true,
		choose: function (datas) {
			start.max = datas; //结束日选好后，重置开始日的最大日期
		}
	};
	start.elem = document.getElementById('begin');
	laydate.render(start);
	end.elem = document.getElementById('end');
	laydate.render(end);
</script>

