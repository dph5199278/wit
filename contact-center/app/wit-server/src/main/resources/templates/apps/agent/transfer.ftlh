<script src="/js/utils.js"></script>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>

<form id="selfTransAgentUserForm" class="layui-form" method="post" >
	<input type="hidden" name="userid" value="${userid!''}">
	<input type="hidden" name="agentserviceid" value="${agentserviceid!''}">
	<input type="hidden" name="agentuserid" value="${agentuserid!''}">
	<div class="row" style="height:295px;">
		<div class="col-lg-3">
			<div class="box">
				<div class="box-title">
					<h1 class="site-h1" style="background-color:#FFFFFF;margin:0px;padding:0 0 4px 20px;">
						技能组
					</h1>
				</div>
				<div class="box-body" style="padding:0px 0px;position: relative;height:250px;overflow-y:auto;">
					<ul>
						<#if skillGroups??>
						<#list skillGroups as skill>
						<#if user.affiliates?seq_contains(skill.id)>
						<#assign agentNum = 0 >
						<#if userList??><#list userList as agent><#if agent.skills[skill.id]??><#assign agentNum = agentNum + 1 ></#if></#list></#if>
						<li title="${skill.name!''}(${agentNum!''})" class="uk_role <#if skill.id == currentorgan>this</#if>" style="margin:0px;padding:7px 0 7px 20px;max-width: 100%;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;" onclick="$('.uk_role.this').removeClass('this');$(this).addClass('this');"><a
							href="/agent/transfer/agent?organ=${skill.id!''}" data-toggle="load" data-target="#skillAgentList">
								<i class="kfont" style="margin-top: 3px;float: left"></i>
							    <span style="max-width: 120px;text-overflow: ellipsis;white-space: nowrap; overflow: hidden;float: left;margin: 0 3px">${skill.name!''}</span>(${agentNum!''})
						</a></li>
						</#if>
						</#list>
						</#if>
						
						<#if skillGroups??>
						<#list skillGroups as skill>
						<#if !user.affiliates?seq_contains(skill.id)>
						<#assign agentNum = 0 >
						<#if userList??><#list userList as agent><#if agent.skills[skill.id]??><#assign agentNum = agentNum + 1 ></#if></#list></#if>
						<li title="${skill.name!''}(${agentNum!''})" class="uk_role <#if skill.id == currentorgan>this</#if>" style="margin:0px;padding:7px 0 7px 20px;"  onclick="$('.uk_role.this').removeClass('this');$(this).addClass('this');"><a
							href="/agent/transfer/agent?organ=${skill.id!''}" data-toggle="load" data-target="#skillAgentList">
							<i style="margin-top: 3px;float: left" class="kfont"></i><span style="max-width: 120px;text-overflow: ellipsis;white-space: nowrap; overflow: hidden;float: left;margin: 0 3px">${skill.name!''}</span>(${agentNum!''})
						</a></li>
						</#if>
						</#list>
						</#if>
					</ul>
				</div>
			</div>
		</div>
		<div class="col-lg-9" style="border-left:1px solid #dedede;">
			<div class="box">
				<div class="box-body" style="padding:0px 2px;position: relative;height:295px;overflow-y:auto;" id="skillAgentList">
					<#include "/apps/agent/transferagentlist.ftlh">
				</div>
			</div>
		</div>
	</div>
	<div class="row" style="margin-top:5px;">
		<div class="col-lg-12">
			<h1 class="site-h1" style="background-color:#EEEEEE;padding-left: 20px">
				转接附言（最多不超过255个字符）</h1>
			<div class="layui-form-item" style="padding-left:10px;padding-top:5px;">
				<div class="layui-inline">
					<div class="layui-input-inline" style="width: 720px;">
						<textarea name="memo" placeholder="请输入转接附言" class="layui-textarea"
							style="resize:none;height:90px;min-height:90px;margin-left: 5px" maxlength="255"><#if agentservice??>${agentservice.transmemo!''}</#if></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="layui-form-button">
		<div class="layui-button-block">
			<button class="layui-btn" lay-submit lay-filter="contactsForm">转接</button>
			<button type="button" class="layui-btn layui-btn-primary" onclick="closeTransferOutDialogue()">关闭</button>
		</div>
	</div>
</form>

<script>
	/**
	 * 转接访客坐席
	 * @param agentUserId
	 * @param targetAgentno
	 * @param agentUserServiceId
	 * @param memo
	 */
	function submitTransAgentUserOutByAgent(agentUserId, targetAgentno, agentUserServiceId, memo) {
		restApiRequest({
			silent: true,
			path: 'agentuser',
			data: {
				ops: 'transout',
				agentno: targetAgentno,
				agentUserId: agentUserId,
				agentServiceId: agentUserServiceId,
				memo: memo
			}
		}).then(function(result){
			if(result.rc === 0){
				// 转接成功
				top.layer.msg('转接已完成',{icon: 1, time: 1000})
				closeTransferOutDialogue();
				//  进行显示调整！
				if(multiMediaDialogWin.$("#chat_users li").length>1){
					multiMediaDialogWin.$("li[remove-id="+agentUserId+"]").remove();
					multiMediaDialogWin.$("#chat_users li:first-child a").click();
				}else{
					$('#agentdesktop').click();
				}
			} else if(result.rc === 1){
				top.layer.msg('不合法的请求参数',{icon: 2, time: 3000})
			} else if(result.rc === 2){
				top.layer.msg('不合法的操作',{icon: 2, time: 3000})
			} else if(result.rc === 3){
				top.layer.msg('您没有权限执行该操作',{icon: 2, time: 3000})
			} else if(result.rc === 4){
				top.layer.msg('该访客会话不存在',{icon: 2, time: 3000})
			} else if(result.rc === 5){
				top.layer.msg('参数不合法',{icon: 2, time: 3000})
			}
		}, function(error){
			console.log("error", error);
			// 服务器异常
			top.layer.msg('服务器抽风，请稍后再试！', {icon: 2, time: 3000})
		})
	}

	function closeTransferOutDialogue(){
		layer.closeAll("page")
	}

	$('.ukefu-agentstatus td:not(:last-child)').on("click" , function(){
		$('.ctxid').prop("checked" , false);
		if($(this).parent().find("input").prop("checked")){
			$(this).parent().find("input").prop("checked" , false);
		}else{
			$(this).parent().find("input").prop("checked" , true);
		}
	});

	layui.use('form', function() {
		var form = layui.form;
		// form.render(); //更新全部
		form.on('submit(contactsForm)', function(data){
			console.log("contactForm", data.field)
			// 验证数据的完整，以下为必填
			if(data.field["userid"] && data.field["agentuserid"] && data.field["agentserviceid"] && data.field["agentno"]){
				submitTransAgentUserOutByAgent(data.field["agentuserid"], data.field["agentno"], data.field["agentserviceid"], data.field["memo"]);
			} else {
				top.layer.msg('未选择合理的转接信息！',{icon: 2, time: 3000});
			}
			return false;
		});
	})
</script>
