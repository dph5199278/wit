<ul class="layui-nav layui-nav-tree" lay-filter="setting">
	<li class="layui-nav-item layui-nav-itemed">
		<#if user?? && (user.roleAuthMap["A03_A01"]?? || user.admin) >
			<a class="layui-nav-title" href="javascript:;">全部客户</a>
		</#if>
		<dl class="layui-nav-child">
			<#if user?? && (user.roleAuthMap["A03_A01_A01"]?? || user.admin) >
				<dd <#if !(ekind??)>class="layui-this"</#if>>
					<a href="/apps/customer/index">全部客户</a>
				</dd>
			</#if>
			<#if user?? && (user.roleAuthMap["A03_A01_A02"]?? || user.admin) >
				<#if uKeFuDic['com.dic.contacts.entype']??>
				<#list uKeFuDic['com.dic.contacts.entype'] as dic>
				<dd <#if ekind?? && ekind == dic.id>class="layui-this"</#if>>
					<a href="/apps/customer/${subtype!'index'}?ekind=${dic.id!''}<#if q??>&q=${q?url}</#if>">${dic.name!''}</a>
				</dd>
				</#list>
				</#if>
			</#if>
		</dl>
	</li>
</ul>
