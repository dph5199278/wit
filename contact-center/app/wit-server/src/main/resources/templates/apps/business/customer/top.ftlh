<ul class="tab-title">
	<#if user?? && (user.roleAuthMap["A03_A01_A02_B01"]?? || user.admin) >
	<li <#if subtype == 'index'>class="layui-this"</#if>><a href="/apps/customer/index<#if ekind??>?ekind=${ekind}</#if>">全部客户<#if subtype == 'index'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A03_A01_A02_B02"]?? || user.admin) >
	<li <#if subtype == 'today'>class="layui-this"</#if>><a href="/apps/customer/today<#if ekind??>?ekind=${ekind}</#if>">今日新增<#if subtype == 'today'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A03_A01_A02_B03"]?? || user.admin) >
	<li <#if subtype == 'week'>class="layui-this"</#if>><a href="/apps/customer/week<#if ekind??>?ekind=${ekind}</#if>">本周新增<#if subtype == 'week'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A03_A01_A02_B04"]?? || user.admin) >
	<li <#if subtype == 'enterprise'>class="layui-this"</#if>><a href="/apps/customer/enterprise<#if ekind??>?ekind=${ekind}</#if>">企业客户<#if subtype == 'enterprise'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<#if user?? && (user.roleAuthMap["A03_A01_A02_B05"]?? || user.admin) >
	<li <#if subtype == 'personal'>class="layui-this"</#if>><a href="/apps/customer/personal<#if ekind??>?ekind=${ekind}</#if>">个人客户<#if subtype == 'personal'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
	</#if>
	<li <#if subtype == 'creater'>class="layui-this"</#if>><a href="/apps/customer/creater<#if ekind??>?ekind=${ekind}</#if>">我的客户<#if subtype == 'creater'>（<#if	entCustomerList??>${entCustomerList.totalElements}<#else>0</#if>）</#if></a></li>
</ul>
