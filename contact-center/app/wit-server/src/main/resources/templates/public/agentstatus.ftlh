<div class="agent-title" id="agents_status">服务中的人数：${agentStatusReport.users!''}人，当前排队人数：${agentStatusReport.inquene!''}人，在线坐席数：${agentStatusReport.agents!''}人，坐席忙：${agentStatusReport.busy!0}人</div>
	<div id="agentstatus"
		class="layui-unselect layui-form-switch checkStatus lay-filter <#if agentStatus??> <#if agentStatus.status == 'ready'> layui-form-onswitch <#elseif agentStatus.status == 'notready' > layui-form-offswitch </#if> </#if>" >
		<i class="checkStatusI"></i>
	</div>

<a href="javascript:void(0)" onclick="return false;" data-title="首页" data-href="/apps/content" class="iframe_btn" data-id="maincontent" data-type="tabChange" style="display: none;">
	<i class="kfont" style="position: relative;">&#xe717;</i>
	首页
</a>

	<div id="agentstatus_busy" style="<#if agentStatus??><#else>display:none;</#if>"
		class="layui-unselect layui-form-switch layui-form-onswitch-notbusy <#if agentStatus?? && agentStatus.busy?? && agentStatus.busy>layui-form-onswitch-busy</#if>">
		<i></i>
	</div>

	<script>

		$('#agentstatus').on("click" , function(){
			if($(this).hasClass('layui-form-onswitch')){ // 从就绪到非就绪
				checkAgentStatusData().then(function (data) {
					return handleAgentStatusData(data);
				}).then(function(data){
					if(data.length > 0){
						// 还有服务中的访客
						layer.confirm('您有正在服务中的访客' + data.length + '人，转为非就绪状态将不可邀请访客，不会分配新访客。', {
							btn: ['切换', '取消'], icon: 4, title:'提示',
							cancel: function (index, layero) {
								//点击关闭按钮
							}
						}, function (index, layero) {
							// 不分配
							setAgentAsNotReady();
							layer.close(index);
						}, function(index){
							layer.close(index);
						});
					} else {
						// 没有服务中的访客直接切换状态
						// 切换坐席状态为未就绪
						setAgentAsNotReady();
					}
				});
			} else { // 从非就绪到就绪
				setAgentAsReady();
			}
		});

		// 请求并将坐席置为就绪状态
		function setAgentAsReady(){
			$("#agentstatus").addClass("layui-form-onswitch");
			sessionStorage.setItem(agentstatus,"ready")
			loadURL("/agent/ready",null, function(){
				$('#agentstatus_busy').show();
			});
		}

		// 请求并将坐席置为非就绪状态
		function setAgentAsNotReady(){
			$('#agentstatus').removeClass("layui-form-onswitch");
			sessionStorage.setItem(agentstatus,"notready");
			loadURL("/agent/notready",null, function(){
				$('#agentstatus_busy').removeClass("layui-form-onswitch-busy").hide();
			});
		}

		$('#agentstatus_busy').on("click" , function(){
			if($(this).hasClass('layui-form-onswitch-busy')){
				$(this).removeClass("layui-form-onswitch-busy");
				$(this).addClass("layui-form-onswitch-notbusy");
				loadURL("/agent/notbusy")
			}else{
				$(this).removeClass("layui-form-onswitch-notbusy");
				$(this).addClass("layui-form-onswitch-busy");
				loadURL("/agent/busy") ;
			}
		});

	</script>