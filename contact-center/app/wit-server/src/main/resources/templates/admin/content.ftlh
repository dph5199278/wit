<div class="row" style="margin-bottom:20px;">
	<div class="col-lg-12">
		<h1 class="site-h1">
			在线客服业务概况
		</h1>
		<div class="row">
			<div class="col-lg-3">
				<div class="ukefu-measure">
					<a href="/apps/onlineuser" class="ukefu-bt">
						<i class="layui-icon ukewo-btn ukefu-measure-btn">&#xe612;</i>
						<div class="ukefu-bt-text">
							<div class="ukefu-bt-text-title" style="font-weight:400;font-size:19px;">${onlineUserCache!0}/${onlineUserClients!0}</div>
							<div class="ukefu-bt-text-content" style="">访客：在线/长连接</div>
						</div>
					</a>
				</div>
			</div>
			<div class="col-lg-3">
				<div class="ukefu-measure">
					<a href="/service/agent/index" class="ukefu-bt">
						<i class="kfont icon-iconfontagent ukewo-btn ukefu-measure-btn ukefu-bg-color-yellow"></i>
						<div class="ukefu-bt-text">
							<div class="ukefu-bt-text-title" style="font-weight:400;font-size:19px;">${agentReport.agents}/${agents!0}</div>
							<div class="ukefu-bt-text-content" style="">坐席:在线/全部</div>
						</div>
					</a>
				</div>
			</div>
			<div class="col-lg-3">
				<div class="ukefu-measure">
					<a href="/service/quene/index" class="ukefu-bt">
						<i class="layui-icon ukewo-btn ukefu-measure-btn ukefu-bg-color-pink">&#xe63a;</i>
						<div class="ukefu-bt-text">
							<div class="ukefu-bt-text-title" style="font-weight:400;font-size:19px;">${agentReport.users}/${agentReport.inquene}</div>
							<div class="ukefu-bt-text-content" style="">会话：服务/排队</div>
						</div>
					</a>
				</div>
			</div>
			<div class="col-lg-3">
				<div class="ukefu-measure">
					<a href="/apps/onlineuser" class="ukefu-bt">
						<i class="layui-icon ukewo-btn ukefu-measure-btn ukefu-bg-color-green">&#xe62c;</i>
						<div class="ukefu-bt-text" title="访客端对话建立的NettySocketIO链接数量/系统缓存数据（SystemCache）">
							<div class="ukefu-bt-text-title" style="font-weight:400;font-size:19px;">${chatClients!0}/${systemCaches!0}</div>
							<div class="ukefu-bt-text-content">缓存:Netty/Cache</div>
						</div>
					</a>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-lg-6" style="padding-right:5px;">
		<div class="box">
			<div class="box-title">
				<h1 class="site-h1" style="background-color:#EEEEEE;">接入站的访问统计</h1>
			</div>
			<div class="box-body" style="padding:5px;">
				<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
				<div id="ippv" style="width: 100%;height:200px;"></div>
				<script type="text/javascript">
					// 基于准备好的dom，初始化echarts实例
					var ippvchat = echarts.init(document.getElementById('ippv') , 'wonderland');
					
					// 指定图表的配置项和数据
					var ippvchat_option = {
						title: {
							text: ''
						},
						tooltip: {},
						legend: {
							data:['发帖量']
						},
						grid: {
							x: 30,
							y: 10,
							x2: 30,
							y2: 40,
							// width: {totalWidth} - x - x2,
							// height: {totalHeight} - y - y2,
							backgroundColor: 'rgba(0,0,0,0)',
							borderWidth: 1,
							borderColor: '#ccc'
						},					            
						xAxis: {
							data: [<#if webInviteReport??><#list webInviteReport as webinvite><#if webinvite_index gt 0>,</#if>"${webinvite.data!''}"</#list></#if>]
						},
						yAxis: {},
						series: [{
							name: '访问IP',
							type: 'line',
							data: [<#if webInviteReport??><#list webInviteReport as webinvite><#if webinvite_index gt 0>,</#if>"${webinvite.ipnums!''}"</#list></#if>]
						},{
							name: '访问PV',
							type: 'line',
							data: [<#if webInviteReport??><#list webInviteReport as webinvite><#if webinvite_index gt 0>,</#if>"${webinvite.pvnums!''}"</#list></#if>]
						}]
					};
			
					// 使用刚指定的配置项和数据显示图表。
					ippvchat.setOption(ippvchat_option);
					
				</script>
			</div>
		</div>
	</div>
	<div class="col-lg-6" style="padding-left:5px;">
		<div class="box">
			<div class="box-title">
				<h1 class="site-h1" style="background-color:#EEEEEE;">坐席咨询数据统计</h1>
			</div>
			<div class="box-body" style="padding:5px;">
				<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
				<div id="consult" style="width: 100%;height:200px;"></div>
				<script type="text/javascript">
					// 基于准备好的dom，初始化echarts实例
					var consultchart = echarts.init(document.getElementById('consult') , 'wonderland');
					
					// 指定图表的配置项和数据
					var consultchart_option = {
						title: {
							text: ''
						},
						tooltip: {},
						legend: {
							data:['日期']
						},
						grid: {
							x: 30,
							y: 10,
							x2: 30,
							y2: 40,
							// width: {totalWidth} - x - x2,
							// height: {totalHeight} - y - y2,
							backgroundColor: 'rgba(0,0,0,0)',
							borderWidth: 1,
							borderColor: '#ccc'
						},					            
						xAxis: {
							data: [<#if agentConsultReport??><#list agentConsultReport as consult><#if consult_index gt 0>,</#if>"${consult.data!''}"</#list></#if>]
						},
						yAxis: {},
						series: [{
							name: '坐席咨询量',
							type: 'line',
							data: [<#if agentConsultReport??><#list agentConsultReport as consult><#if consult_index gt 0>,</#if>"${consult.users!''}"</#list></#if>]
						}]
					};
			
					// 使用刚指定的配置项和数据显示图表。
					consultchart.setOption(consultchart_option);
					
				</script>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-lg-6" style="padding-right:5px;">
		<div class="box">
			<div class="box-title">
				<h1 class="site-h1" style="background-color:#EEEEEE;">咨询用户系统占比</h1>
			</div>
			<div class="box-body" style="padding:5px;">
				<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
				<div id="osname" style="width: 100%;height:200px;"></div>
				<script type="text/javascript">
					// 基于准备好的dom，初始化echarts实例
					var osnamechart = echarts.init(document.getElementById('osname') , 'wonderland');
					
					var osnamechart_option = {
						    title : {
						        text: '咨询用户操作系统占比',
						        x:'center'
						    },
						    tooltip : {
						        trigger: 'item',
						        formatter: "{a} <br/>{b} : {c} ({d}%)"
						    },
						    legend: {
						        orient: 'vertical',
						        left: 'left',
						        data: [<#if clentConsultReport??><#list clentConsultReport as osname><#if osname_index gt 0>,</#if>"${osname.data!''}"</#list></#if>]
						    },
						    series : [
						        {
						            name: '咨询用户操作系统占比',
						            type: 'pie',
						            radius : '55%',
						            center: ['50%', '60%'],
						            data:[
										<#if clentConsultReport??><#list clentConsultReport as osname><#if osname_index gt 0>,</#if>{value:${osname.users!''}, name:"${osname.data!''}"}</#list></#if>
						            ],
						            itemStyle: {
						                emphasis: {
						                    shadowBlur: 10,
						                    shadowOffsetX: 0,
						                    shadowColor: 'rgba(0, 0, 0, 0.5)'
						                }
						            }
						        }
						    ]
						};

					// 使用刚指定的配置项和数据显示图表。
					osnamechart.setOption(osnamechart_option);
					
				</script>
			</div>
		</div>
	</div>
	<div class="col-lg-6" style="padding-left:5px;">
		<div class="box">
			<div class="box-title">
				<h1 class="site-h1" style="background-color:#EEEEEE;">咨询用户浏览器占比</h1>
			</div>
			<div class="box-body" style="padding:5px;">
				<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
				<div id="browser" style="width: 100%;height:200px;"></div>
				<script type="text/javascript">
					// 基于准备好的dom，初始化echarts实例
					var browserchart = echarts.init(document.getElementById('browser') , 'wonderland');
			
					var browserchart_option = {
						    title : {
						        text: '咨询用户浏览器占比',
						        x:'center'
						    },
						    tooltip : {
						        trigger: 'item',
						        formatter: "{a} <br/>{b} : {c} ({d}%)"
						    },
						    legend: {
						        orient: 'vertical',
						        left: 'left',
						        data: [<#if browserConsultReport??><#list browserConsultReport as browser><#if browser_index gt 0>,</#if>"${browser.data!''}"</#list></#if>]
						    },
						    series : [
						        {
						            name: '咨询用户浏览器占比',
						            type: 'pie',
						            radius : '55%',
						            center: ['50%', '60%'],
						            data:[
										<#if browserConsultReport??><#list browserConsultReport as browser><#if browser_index gt 0>,</#if>{value:${browser.users!''}, name:"${browser.data!''}"}</#list></#if>
						            ],
						            itemStyle: {
						                emphasis: {
						                    shadowBlur: 10,
						                    shadowOffsetX: 0,
						                    shadowColor: 'rgba(0, 0, 0, 0.5)'
						                }
						            }
						        }
						    ]
						};
			
					// 使用刚指定的配置项和数据显示图表。
					browserchart.setOption(browserchart_option);
				</script>
			</div>
		</div>
	</div>
</div>