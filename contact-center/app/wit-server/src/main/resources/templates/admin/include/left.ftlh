<ul class="layui-nav layui-nav-tree" lay-filter="demo">
	<li class="layui-nav-item <#if subtype?? && subtype == 'content'>layui-this</#if>">
		<a class="layui-nav-default" href="/admin/content">系统概况</a>
	</li>
	<li class="layui-nav-item layui-nav-itemed">
	<a class="layui-nav-title" 
		href="javascript:;">用户和组</a>
		<dl class="layui-nav-child">
			<dd  <#if subtype?? && subtype == 'user'>class="layui-this"</#if>>
				<a href="/admin/user/index">用户账号</a>
			</dd>
			<dd <#if subtype?? && subtype == 'role'>class="layui-this"</#if>>
				<a href="/admin/role/index">系统角色</a>
			</dd>
			<dd  <#if subtype?? && subtype == 'organ'>class="layui-this"</#if>>
				<a href="/admin/organ/index">组织机构</a>
			</dd>
		</dl>
	</li>
	<li class="layui-nav-item layui-nav-itemed">
		<a class="layui-nav-title" href="javascript:;">客服接入</a>
		<dl class="layui-nav-child">
			<dd <#if subtype?? && (maintype == 'webim' || subtype == 'im')>class="layui-this"</#if>>
				<a href="/admin/im/index">网站列表</a>
			</dd>

			<#if models?seq_contains("skype")>
			<dd <#if subtype?? && (maintype == 'skype' || subtype == 'skype')>class="layui-this"</#if>>
				<a href="/admin/skype/index">Skype</a>
			</dd>
			</#if>

		    <#if models?seq_contains("callout")>
			<dd <#if subtype?? && (maintype == 'callout' || subtype == 'channel')>class="layui-this"</#if>>
				<a href="/admin/callout/index">语音渠道</a>
			</dd>
            </#if>
		</dl>
	</li>
	
	<#if models?seq_contains("wexin")>
	<li class="layui-nav-item layui-nav-itemed">
		<a class="layui-nav-title" href="javascript:;">微信接入</a>
		<dl class="layui-nav-child">
			<dd <#if subtype?? && subtype == 'weixin'>class="layui-this"</#if>>
				<a href="/admin/weixin/index">公众号配置</a>
			</dd>
			<!-- 
			<dd <#if subtype?? && subtype == 'material'>class="layui-this"</#if>>
				<a href="/admin/weixin/material">素材管理</a>
			</dd>
			<dd <#if subtype?? && subtype == 'interf'>class="layui-this"</#if>>
				<a href="/admin/weixin/interf">接口管理</a>
			</dd>
			 -->
		</dl>
	</li>
	</#if>
	<#if models?seq_contains("callcenter")>
		<li class="layui-nav-item layui-nav-itemed">
			<a class="layui-nav-title" href="javascript:;">呼叫中心</a>
			<dl class="layui-nav-child">
				<dd <#if subtype?? && subtype == 'callcenter'>class="layui-this"</#if>>
					<a href="/admin/callcenter/index">语音平台</a>
				</dd>
			</dl>
		</li>
	</#if>
	<#if user.admin>
	<li class="layui-nav-item layui-nav-itemed">
		<a class="layui-nav-title" href="javascript:;">系统设置</a>
		<dl class="layui-nav-child">
			<dd <#if subtype?? && subtype == 'config'>class="layui-this"</#if>>
				<a href="/admin/config/index">系统设置</a>
			</dd>
			<dd <#if subtype?? && subtype == 'sysdic'>class="layui-this"</#if>>
				<a href="/admin/sysdic/index">字典管理</a>
			</dd>
			<dd <#if subtype?? && subtype == 'metadata'>class="layui-this"</#if>>
				<a href="/admin/metadata/index">元数据</a>
			</dd>
			<dd <#if subtype?? && subtype == 'template'>class="layui-this"</#if>>
				<a href="/admin/template/index">系统模板</a>
			</dd>
			<dd <#if subtype?? && subtype == 'email'>class="layui-this"</#if>>
				
				<a href="/admin/email/index">邮件通知设置</a>
			</dd>
			<dd <#if subtype?? && subtype == 'sms'>class="layui-this"</#if>>
				<a href="/admin/sms/index">短信通知设置</a>
			</dd>
		</dl>
	</li>
	</#if>
</ul>
