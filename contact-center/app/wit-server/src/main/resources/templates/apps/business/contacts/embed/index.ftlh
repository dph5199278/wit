<div class="ukefu-contacts-embed">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1 ukefu-tab" style="border-top:1px solid #e6e6e6;height:26px;">
				<span class="ukefu-bt">
					<i class="layui-icon ukewo-btn" style="font-size:20px;"></i>
					<div class="ukefu-bt-text">
						<div class="ukefu-bt-text-title">
							全部联系人（${contactsList.totalElements}）
						</div>
					</div>
				</span>
				
				<div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
					<form class="layui-form" name="contactsSearch" action="/apps/contacts/embed/index" data-toggle="ajax-form" data-close="false" data-target="#mainajaxwin" style="float: left;">
						<div class="layui-form-item">
							<div class="layui-inline">
								<div class="layui-input-inline" style="width: 220px">
									<input type="text" name="q" value="${q!''}" style="height: 30px;line-height: 30px;margin-top: 4px;border: 1px solid #32c24d;"
										placeholder="请输入联系人姓名或电话" autocomplete="off" class="layui-input">
									<i class="layui-icon" onClick="$('#contactsFormSubmit').click();" style="position: absolute;cursor: pointer;right: 3px;top: 4px;font-size: 20px;color:#32c24d;">&#xe615;</i>
<!--									<button type="submit" id="contactsFormSubmit"></button>-->
								</div>
							</div>
						</div>
					</form>
					<div class="layui-btn-group">
					  <a href="/apps/contacts/embed/add" title="新建联系人"  data-toggle="load" data-target="#mainajaxwin" class="layui-btn layui-btn-sm" ><i class="layui-icon">&#xe608;</i>新建联系人</a>
					</div>
				</div>
			</h1>
			<table class="layui-table" lay-skin="line" style="table-layout: fixed;word-break: break-all;">
				<thead>
					<tr class="design-sortable-tr">
						<th width="20px"></th>
						<th width="30%">联系人姓名</th>
						<th width="140px">手机</th>
						<th>邮件</th>
						<th width="50px">性别</th>
						<th>类型</th>
						<th width="70px">操作</th>
					</tr>
				</thead>
				<tbody>
					<#if contactsList?? && contactsList.content?? && contactsList.content?size gt 0>
					<#list contactsList.content as contacts>
				<tr class="tdHasContorllor design-dropable-td ukefu-contacts">
						<td style="width:1px;padding:10px 0px 10px 10px;">
							<input type="checkbox" <#if currentContacsId == contacts.id>checked</#if> name="id" class="ctxid" value="${contacts.id!''}" id="contacts_${contacts.id}">
							<#if currentContacsId == contacts.id && currentAgentUserContactsId!''>
								<input type="text" id="currentContacsId" value="${currentAgentUserContactsId!''}" style="display: none">
							</#if>

						</td>
						<td title="${contacts.name!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;">
							${contacts.name!''}
						</td>
						<td>
							<span class="ukefu-phone-number">
							${contacts.mobileno!''} 
							</span>
						</td>
						<td>
							${contacts.email!''} 
						</td>
						<td>
							<#if contacts.gender?? && contacts.gender == '1'>男</#if>
							<#if contacts.gender?? && contacts.gender == '0'>女</#if>
							<#if contacts.gender?? && contacts.gender == '-1'>未知</#if>
						</td>
						<td>
							${uKeFuDic[contacts.ckind!''].name!''} 
						</td>
						<td style="white-space:nowrap;width:1%;" nowrap="nowrap">
							<a href="/apps/contacts/embed/edit?id=${contacts.id!''}" data-toggle="load" data-target="#mainajaxwin">
								<i class="layui-icon">&#xe642;</i>
								编辑
							</a>
					  	</td>
					</tr>
					</#list>
					<#else>
						<tr>
							<td colspan="6" style="height:400px;">
								<div class="ukefu-empty"  style="background: none">
									<i class="layui-icon">&#xe63a;</i>
									<div style="">还没有联系人信息</div>
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
<div class="layui-form-button">
	<div class="layui-button-block">
		<button class="layui-btn" lay-submit lay-filter="contactsForm">立即提交</button>
		<button type="reset" class="layui-btn layui-btn-primary" onclick="layer.close(top.layerwin);">关闭</button>
	</div>
</div>
<script>

	$("input:checkbox").click(function () {
		if ( !$(this).is(':checked') ){
			setTimeout(function () {
				$("input[type='checkbox']").prop("checked",false);
			},0)

		}
	});

	layui.use('form', function(){
		var form = layui.form;
	  	form.on('submit(contactsForm)', function(data){
	  		var targetIFrame = eval(iframe);
	  		var selectedContactId = $("input:checked.ctxid").val();
	  		if(selectedContactId){
	  			targetIFrame.Proxy.execLinkContactsFunction(selectedContactId) ;
	  		}else {
				var currentAgentUserContactsId = $("#currentContacsId").val();
				targetIFrame.Proxy.execCancelContactsFunction(currentAgentUserContactsId) ;
			}

	  		layer.closeAll("page");
	    	return false;
	  	});
	});

	layui.use('layer', function(){
		var layer = layui.layer;
		<#if msg?? && msg == 'new_contacts_success'>
			layer.msg('联系人添加成功',{icon: 1, time: 1500})
		<#elseif msg?? && msg == 'new_contacts_fail'>
			layer.msg('联系人添加失败，因为存在相同Skype ID',{icon: 2, time: 1500})
		<#elseif msg?? && msg == 'edit_contacts_success'>
			layer.msg('联系人编辑成功',{icon: 1, time: 1500})
		<#elseif msg?? && msg == 'edit_contacts_fail'>
			layer.msg('联系人编辑失败，因为存在相同Skype ID',{icon: 2, time: 1500})
		</#if>
	});

	layui.use(['laypage'], function(){
		  var laypage = layui.laypage ;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if contactsList??>${contactsList.totalPages}<#else>0</#if> //总页数
				,curr:<#if contactsList??>${contactsList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						loadURL("/apps/contacts/embed/index?<#if q??>q=${q!''}&</#if>p="+data.curr  , "#mainajaxwin");
					}
				}
		   });
	});
	$('.ukefu-contacts td:not(:last-child)').on("click" , function(){
		$('.ctxid').prop("checked" , false);
		if($(this).parent().find("input").prop("checked")){
			$(this).parent().find("input").prop("checked" , false);
		}else{
			$(this).parent().find("input").prop("checked" , true);
		}
	});
</script>
