<form class="layui-form" action="/service/transfer/save" method="post" >
	<input type="hidden" name="id" value="${agentserviceid!''}">
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
						<#if skill.id == user.organ>
						<#assign agentNum = 0 >
						<#if userList??><#list userList as agent><#if agent.affiliates?seq_contains(skill.id)><#assign agentNum = agentNum + 1 ></#if></#list></#if>
						<li title="${skill.name!''}(${agentNum!''})" class="uk_role <#if skill.id == currentorgan>this</#if>" style="margin:0px;padding:7px 0 7px 20px;" onclick="$('.uk_role.this').removeClass('this');$(this).addClass('this');"><a
							href="/agent/transfer/agent?organ=${skill.id!''}" data-toggle="load" data-target="#skillAgentList">
							<i style="margin-top: 3px;float: left" class="kfont"></i><span style="max-width: 120px;text-overflow: ellipsis;white-space: nowrap; overflow: hidden;float: left;margin: 0 3px">${skill.name!''}</span>(${agentNum!''})
						</a></li>
						</#if>
						</#list>
						</#if>
						
						<#if skillGroups??>
						<#list skillGroups as skill>
						<#if skill.id != user.organ>
						<#assign agentNum = 0 >
						<#if userList??><#list userList as agent><#if agent.affiliates?seq_contains(skill.id)><#assign agentNum = agentNum + 1 ></#if></#list></#if>
						<li title="${skill.name!''}${agentNum!''})" class="uk_role <#if skill.id == currentorgan>this</#if>" style="margin:0px;padding:7px 0 7px 20px;"  onclick="$('.uk_role.this').removeClass('this');$(this).addClass('this');"><a
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
			<button type="button" class="layui-btn layui-btn-primary" onclick="closedPage()">关闭</button>
		</div>
	</div>
</form>
<script>
	function closedPage(){
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

	layui.use('form', function(){
		var form = layui.form;
		//监听提交
		form.on('submit(contactsForm)', function(data){
			if(!data.field.agentno){
				layer.msg("请选择坐席");
				return false
			}
		});
	});


</script>

