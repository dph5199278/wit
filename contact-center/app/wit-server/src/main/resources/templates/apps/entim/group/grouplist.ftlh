<ul>
	<li class="ukefu-entim-organ-open">
		<h5 class="ukefu-entim-organ" lay-filter="spread" lay-type="true">
			<i class="layui-icon"></i>
			<span>我创建的群</span>
			<#assign curgroup = 0 >
			<#if groupList??>
			<#list groupList as imgroup>
				<#assign curgroup = curgroup+1 >
			</#list>
			</#if>
			<em>(<cite class="layim-count"> ${curgroup} </cite>)</em>
		</h5>
		<ul class="ukefu-user-list ukefu-group">
			<#if groupList??>
			<#list groupList as imgroup>
				<li id="group_${imgroup.id}">
					<a href="javascript:void(0)" onclick="openchat('/ent/im/group?id=${imgroup.id!''}', '/images/imgroup.png' , '${imgroup.name!''}' , '${imgroup.id!''}' , '创建时间:${imgroup.createtime?string('yyyy-MM-dd HH:mm:ss')}');">
						<img src="/images/imgroup.png">
						<span>${imgroup.name!''}</span>
					</a>
					<div class="ukefu-session-tip cont_${imgroup.id!''}"></div>
				</li>
			</#list>
			</#if>
		</ul>
	</li>
	<li class="ukefu-entim-organ-open">
		<h5 class="ukefu-entim-organ" lay-filter="spread" lay-type="true">
			<i class="layui-icon"></i>
			<span>我加入的群</span>
			<#assign curgroup = 0 >
			<#if joinGroupList??>
			<#list joinGroupList as imGroup>
				<#if imGroup.imgroup?? && user.id != imGroup.creater>
				<#assign curgroup = curgroup+1 >
				</#if>
			</#list>
			</#if>
			<em>(<cite class="layim-count"> ${curgroup} </cite>)</em>
		</h5>
		<ul class="ukefu-user-list ukefu-group">
			<#if joinGroupList??>
			<#list joinGroupList as imGroup>
				<#if imGroup.imgroup?? && user.id != imGroup.creater>
				<li id="group_${imGroup.imgroup.id}">
					<a href="javascript:void(0)" onclick="openchat('/ent/im/group?id=${imGroup.imgroup.id!''}', '/images/imgroup.png' , '${imGroup.imgroup.name!''}' , '${imGroup.imgroup.id!''}' , '创建时间:${imGroup.imgroup.createtime}');">
						<img src="/images/imgroup.png">
						<span>${imGroup.imgroup.name!''}</span>
					</a>
					<div class="ukefu-session-tip cont_${imGroup.imgroup.id!''}"></div>
				</li>
				</#if>
			</#list>
			</#if>
		</ul>
	</li>
</ul> 	