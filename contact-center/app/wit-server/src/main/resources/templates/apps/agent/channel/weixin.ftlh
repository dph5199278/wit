<div class="ukefu-user-headimg">
	<img src="${weiXinUser.headimgurl!''}" style="width:64px;height:64px;">
</div>
<ul class="info-list">
	<li class="ukefu-channel-tip">
		访问渠道：<i class="kfont">&#xe64d;</i>
			        微信
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
		公众号：${snsAccount.name}
	</li>
	</#if>
	
	<li>
		     昵称：
	    <span class="tgreen">
		${weiXinUser.nickname!''}
	    </span>
	</li>
	<li>
		国家：${weiXinUser.country!''}
	</li>
	<li>
		位置：${weiXinUser.province!''} ${weiXinUser.city!''}
	</li>
	<li>
	    访问次数：${serviceCount!0}次
	</li>
	<li>
	    停留时间：
		<#if curAgentService.servicetime??>${curAgentService.servicetime?string("yyyy-MM-dd HH:mm:ss")}</#if>
	</li>
	
	<li style="position: relative;">
		标签：
		<span id="tags">
			<#if tagRelationList??>
				<#list tagRelationList as tagRelation>
					<#if tags??>
					<#list tags as tag>
					<#if tag.id == tagRelation.tagid>
					<small id="tag_${tag.id}" class="ukefu-label theme${tag.color!'1'}">${tag.tag!''}</small>
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
				<dd><a href="javascript:void(0)" data-href="/agent/tagrelation?tagid=${tag.id!''}&userid=${curAgentService.userid!''}&dataid=${weiXinUser.id!''}" data-theme="theme${tag.color!'1'}" data-id="${tag.id!''}" data-name="${tag.tag!''}" class="tag-switch">${tag.tag!''}</a></dd>
				</#list>
				</#if>
			</dl>
		</div>
	</li>
</ul>