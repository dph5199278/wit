<ul class="info-list">
	<li class="ukefu-channel-tip">
		访问渠道：<i class="layui-icon">&#xe63a;</i>
		<span>在线客服</span>
	</li>

	<li class="ukefu-channel-tip">
		服务小结： <#if summary??>
				<i class="layui-icon">&#xe618;</i>已记录
				<#else>
				<i class="layui-icon">&#xe64d;</i>未记录
				</#if>
	</li>

	<#if snsAccount??>
	<li class="ukefu-channel-tip">
		渠道名称：${snsAccount.name!''}
		<#if snsAccount?? && snsAccount.snstype == "webim" >
			接入网站：${snsAccount.name!''}
			（${snsAccount.baseURL!''}）
		</#if>
	</li>
	</#if>
	<#if onlineUser?? && onlineUser.source?? >
		<li>
				  首次访问来源：
			<span class="tgreen">
			${onlineUser.source!''}
			</span>
		</li>
	</#if>

	<#if onlineUser?? && onlineUser.region?? >
		<li>
			位置：${curagentuser.region!''}
		</li>
	</#if>

	<#if serviceCount?? >
		<li>
			访问次数：${serviceCount!0}次
		</li>
	</#if>

	<#if curagentuser.title?? && curagentuser.url??>
	<li>
	    进入页面：<a href="${curagentuser.url!''}" target="_blank"><#if curagentuser.title?length gt 15>${curagentuser.title[0..15]}<#else>${curagentuser.title}</#if></a>
	</li>
	</#if>
	<li>
	    访问轨迹：<a href="/service/trace?sessionid=${curagentuser.sessionid!''}" title="查看访客轨迹" data-toggle="ajax" data-width="1050" style="margin-left:10px;">
					<i class="kfont">&#xe638;</i>轨迹
				</a>
	</li>

	<#if onlineUser?? && onlineUser.betweentime?? >
		<li>
			停留时间：
			<#if onlineUser?? && onlineUser.betweentime??>
			${(onlineUser.betweentime/(1000*60*60))?string('00')}:${((onlineUser.betweentime%(1000*60*60))/(1000*60))?string('00')}:${((onlineUser.betweentime%(1000*60))/(1000))?string('00')}
				</#if>
		</li>
	</#if>

	<#if onlineUser?? && onlineUser.browser?? >
		<li>
			访问浏览器：
			${onlineUser.browser!''}
		</li>
	</#if>

	<#if onlineUser?? && onlineUser.opersystem?? >
		<li>
			 操作系统：
				<span class="ukefu-online-user">
				<#if onlineUser.opersystem?? && onlineUser.opersystem == "windows">
					<i class="kfont">&#xe801;</i>
					<#elseif onlineUser.opersystem?? && onlineUser.opersystem == "linux">
					<i class="kfont">&#xe726;</i>
					<#elseif onlineUser.opersystem?? && onlineUser.opersystem == "mac">
					<i class="kfont">&#xe6be;</i>
					</#if>
					${onlineUser.opersystem!''}
				</span>
		</li>
	</#if>

<#if onlineUser?? && onlineUser.mobile?? >
		<li>
			访问终端：
				<span class="ukefu-online-user">
			<#if onlineUser.mobile?? && onlineUser.mobile == "1">
				<i class="kfont">&#xe603;</i> 移动
				<#else>
				<i class="kfont">&#xe601;</i> PC
				</#if>
				</span>
		</li>
</#if>

	<li style="position: relative;">
		标签：
		<span id="tags">
			<#if tagRelationList??>
				<#list tagRelationList as tagRelation>
					<#if tags??>
						<#list tags as tag>
						<#if tag.id == tagRelation.tagid>
							<small id="tag_${tag.id}" class="ukefu-access-label theme${tag.color!'1'}">${tag.tag!''}</small>
						</#if>
						</#list>
					</#if>
				</#list>
			</#if>
		</span>
		<div class="ukefu-tag-add">
			<a href="javascript:void(0)" id="tag"><i class="layui-icon">&#xe608;</i></a>
			<dl class="ukefu-tag-list layui-anim layui-anim-upbit" id="taglist">
					<#if tags??>
					<#list tags as tag>

				<#assign  tagValue = 0>

				<#if tagRelationList??>
									<#list tagRelationList as tagRelation>
				<#if tag.id == tagRelation.tagid>
				<#assign  tagValue = 1>
			</#if>
							</#list>
	</#if>


<#if tagValue == 1>
<dd class="labeldd" style="background: red" onclick="switchbackground(this)">
	<a href="javascript:void(0)" data-href="/agent/tagrelation?tagid=${tag.id!''}&userid=${curAgentService.userid!''}&dataid=${onlineUser.id!''}" data-theme="theme${tag.color!'1'}" data-id="${tag.id!''}" data-name="${tag.tag!''}" class="tag-switch">${tag.tag!''}
		<i class="layui-icon layui-icon-close">&#x1006;</i>
	</a>
</dd>
<#assign  tagValue = 1>
<#else>
<dd class="labeldd" onclick="switchbackground(this)">
	<a href="javascript:void(0)" data-href="/agent/tagrelation?tagid=${tag.id!''}&userid=${curAgentService.userid!''}&dataid=${onlineUser.id!''}" data-theme="theme${tag.color!'1'}" data-id="${tag.id!''}" data-name="${tag.tag!''}" class="tag-switch">${tag.tag!''}
		<i class="layui-icon layui-icon-close">&#x1006;</i>
	</a>
</dd>
<#assign  tagValue = 1>
</#if>

					</#list>
					</#if>
			</dl>
		</div>
	</li>
</ul>
<script>

	function switchbackground(ele){
		console.log(ele)
		if( ele.style.background != "#32c24d" ){
			ele.style.background = "#32c24d";
		}else {
			ele.style.background = "none";
		}
	}

</script>