<#if contacts??>
<ul class="info-list">
	<li class="ukefu-channel-tip">
		姓名：<span class="tgreen">${contacts.name!''}</span>
	</li>
	<li>
		性别： <#if contacts.gender?? && contacts.gender == '1'>男</#if>
			<#if contacts.gender?? && contacts.gender == '0'>女</#if>
			<#if contacts.gender?? && contacts.gender == '-1'>未知</#if>
	</li>
	<li>
		生日：${contacts.cusbirthday!''}
	</li>
	<li>
		电话：${contacts.phone!''}
	</li>
	<li>
		手机：${contacts.mobile!''}
	</li>
	<li>
		邮件：${contacts.email!''}
	</li>
	
	<li>
		地址：${contacts.address!''}
	</li>
	<li>
		类型：${uKeFuDic[contacts.ckind!''].name!''} 
	</li>
	<#if contacts?? && contacts.touchtime??>
	<li>
		获得时间：${contacts.touchtime?string('yyyy-MM-dd')}
	</li>
	</#if>
	<li>
		备注：${contacts.memo!''} 
	</li>
</ul>
<#if models?seq_contains("workorders")>
<script type="text/javascript">
	$(document).ready(function(){
		loadURL("/agent/workorders/list?contactsid=${contacts.id!''}", "#workorders");
	});		
</script>
</#if>
</#if>
