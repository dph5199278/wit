<ul class="layui-nav layui-nav-tree" lay-filter="setting">
	<li class="layui-nav-item layui-nav-itemed">
		<a class="layui-nav-title" href="javascript:;">会话质检</a>
		<dl class="layui-nav-child">
			<dd <#if subtype?? && subtype == 'quality'>class="layui-this"</#if>>
				<a href="/apps/quality/index">文本对话</a>
			</dd>
		</dl>
		
	</li>
	<li class="layui-nav-item layui-nav-itemed">
		<a class="layui-nav-title" href="javascript:;">质检记录</a>
		<dl class="layui-nav-child">
			<dd <#if subtype?? && subtype == 'statcoment'>class="layui-this"</#if>>
				<a href="/service/stats/coment">质检任务</a>
			</dd>
			<dd <#if subtype?? && subtype == 'statagent'>class="layui-this"</#if>>
				<a href="/service/stats/agent">质检结果</a>
			</dd>
		</dl>
	</li>
</ul>