<style>
    .ukefu-report{
		position: absolute;
		margin-top: 10px;
    	width: auto;
    }	
	.ukefu-report td{
		max-width:150px;
		min-width:50px;
		text-overflow: ellipsis;
		white-space: nowrap;
		overflow: hidden;
	}	
</style>
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
					满意度统计
				</h1>
				<span>
					<div class="ukefu-bt-text-content">
						<form class="layui-form" name="search"	action="/service/stats/coment" method="get" style="float: left;margin-right:5px;">
							<div class="layui-form-item">
								<div class="layui-inline" style="margin-right:0px;">
									<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										<label class="layui-form-label" style="width: auto;">技能组：</label>
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<select name="skill" class="ukefu-input" lay-ignore>
												<option value="">请选择技能组</option>
												<#if skillGroups??>
												<#list skillGroups as tpskill>
													<option value="${tpskill.id}" <#if skill?? && skill == tpskill.id>selected="selected"</#if>>${tpskill.name!''}</option>
												</#list>
												</#if>
											</select>
										</div>
									</div>
								</div>
								<div class="layui-inline" style="margin-right:0px;">
									<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										<label class="layui-form-label" style="width: auto;">坐席：${agentskill!''}</label>
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<select name="agent" class="ukefu-input" lay-ignore>
												<option value="">请选择坐席</option>
												<#if agentList??>
												<#list agentList as useragent>
													<option value="${useragent.id}" <#if agent?? && agent == useragent.id>selected="selected"</#if>>${useragent.uname!''}</option>
												</#list>
												</#if>
											</select>
										</div>
									</div>
								</div>
								<div class="layui-inline" style="margin-right:0px;">
									<div class="layui-input-inline" style="width: auto;margin-right:0px;">
										<label class="layui-form-label" style="width: auto;">日期范围：</label>
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<input type="text" name="begin" class="layui-input ukefu-input" id="start_time" value="${begin!''}" placeholder="开始时间">
										</div>
										<label class="layui-form-label" style="width: auto;">~</label>
										<div class="layui-input-inline" style="width: auto;margin-right:0px;">
											<input type="text" name="end" class="layui-input ukefu-input" value="${end!''}" id="end_time" placeholder="结束时间">
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
							<a href="/service/stats/coment/exp?t=${.now?long}<#if skill??>&skill=${skill!''}</#if><#if agent??>&agent=${agent!''}</#if><#if begin??>&begin=${begin!''}</#if><#if end??>&end=${end!''}</#if>">
								<button class="layui-btn layui-btn-sm dropdown-menu">
									<i class="kfont">&#xe672;</i> 导出
								</button>
							</a>
						</div>
					</div>
				</span>
				<#include "/apps/service/stats/report.ftlh">
			</div>
		</div>
	</div>
</div>
<script>
//Demo
layui.use('form', function(){
  var form = layui.form;
  form.render(); //更新全部
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
	istime: true,
	format: 'YYYY-MM-DD',
	max:getNowFormatDate(),//默认最大值为当前日期
	istoday: true,
	choose: function (datas) {
		end.min = datas; //开始日选好后，重置结束日的最小日期
	}
};

var end = {
	istime: true,
	format: 'YYYY-MM-DD',
	max:getNowFormatDate(),//默认最大值为当前日期
	istoday: true,
	choose: function (datas) {
		start.max = datas; //结束日选好后，重置开始日的最大日期
	}
};

start.elem = document.getElementById('start_time');
laydate.render(start);
end.elem = document.getElementById('end_time');
laydate.render(end);


</script>