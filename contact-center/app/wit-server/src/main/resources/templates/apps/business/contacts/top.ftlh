<ul class="tab-title">
	<#if user?? && (user.roleAuthMap["A02_A01_A02_B01"]?? || user.admin) >
	<li <#if subtype == 'index'>class="layui-this"</#if>><a href="/apps/contacts/index<#if ckind??>?ckind=${ckind}</#if>">全部联系人<#if subtype == 'index'>（<#if	contactsList??>${contactsList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A02_A01_A02_B02"]?? || user.admin) >
	<li <#if subtype == 'today'>class="layui-this"</#if>><a href="/apps/contacts/today<#if ckind??>?ckind=${ckind}</#if>">今日新增<#if subtype == 'today'>（<#if	contactsList??>${contactsList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A02_A01_A02_B03"]?? || user.admin) >
	<li <#if subtype == 'week'>class="layui-this"</#if>><a href="/apps/contacts/week<#if ckind??>?ckind=${ckind}</#if>">本周新增<#if subtype == 'week'>（<#if contactsList??>${contactsList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A02_A01_A02_B04"]?? || user.admin) >
	<li <#if subtype == 'creater'>class="layui-this"</#if>><a href="/apps/contacts/creater<#if ckind??>?ckind=${ckind}</#if>">我的联系人<#if subtype == 'creater'>（<#if contactsList??>${contactsList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
</ul>
