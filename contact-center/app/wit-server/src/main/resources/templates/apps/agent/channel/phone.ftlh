<ul class="info-list">
	<li class="ukefu-channel-tip">
		访问渠道：<i class="kfont"	style="position: relative;">&#xe629;</i> 
		语音
	</li>
	<#if pbxHost??>
	<li class="ukefu-channel-tip">
		接入线路：${pbxHost.name!''}（${pbxHost.hostname!''}）
	</li>
	</#if>
	
	<li>
		主叫号码：
	    <span class="tgreen">
		<#if statusEvent??>${statusEvent.discaller!''}</#if>
	    </span>
	</li>
	<li>
		被叫号码：<#if statusEvent??>${statusEvent.discalled!''}</#if>
	</li>
	<li>
		类型：<#if statusEvent??>${statusEvent.calltype!''}</#if>
	</li>
	<li>
		主叫方：<#if statusEvent??>${statusEvent.host!''}</#if>
	</li>
	<li>
		IP地址：<#if statusEvent??>${statusEvent.ipaddr!''}</#if>
	</li>
	<li>
		拨打时间：<#if statusEvent??>${statusEvent.localdatetime!''}</#if>
	</li>
	<#if mobileAddress??>
	<li>
		来电省份：${mobileAddress.province!''}
	</li>
	<li>
		来电城市：${mobileAddress.city!''}
	</li>
	<li>
		运营商：${mobileAddress.isp!''}
	</li>
	</#if>
	
	<li>
		来电类型：
		<#if extentionList?? && extentionList?size gt 0>
		内线
		<#else>
		外线
		</#if>
	</li>
	<#list uKeFuDic["com.dic.callcenter.comment"] as comment>
	<#if statusEvent.satisfaction?? && comment.code== statusEvent.satisfaction >
	<li>
		满意度评价： ${comment.name}
	</li>
	<li>
		<#if statusEvent.satisfdate??>评价时间：${statusEvent.satisfdate?string('yyyy-MM-dd HH:mm:ss')}</#if>
	</li>
	</#if>
	</#list>
	<li>
		满意度：<#if statusEvent.satisfaction==null>未评价</#if>
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
                <dd><a href="javascript:void(0)" data-href="/agent/tagrelation?tagid=${tag.id!''}&userid=${curAgentService.userid!''}&dataid=${onlineUser.id!''}" data-theme="theme${tag.color!'1'}" data-id="${tag.id!''}" data-name="${tag.tag!''}" class="tag-switch">${tag.tag!''}</a></dd>
                </#list>
                </#if>
            </dl>
        </div>
    </li>
</ul>
<script>
    layui.use('layer', function() {
        layer = layui.layer;
        $('#tag').click(function() {
            $('#taglist').show();
        })
        $('.tag-switch').click(
            function() {
                if ($('#tag_' + $(this).data("id")).length > 0) {
                    $('#tag_' + $(this).data("id")).remove();
                } else {
                    $("#tags").append(
                        "<small id='tag_" + $(this).data("id")
                        + "' class='ukefu-label "
                        + $(this).data("theme") + "'>"
                        + $(this).data("name") + "</small>");
                }
                loadURL($(this).data("href"));
            });
        $("#taglist").hover(null, function() {
            $(this).hide();
        });
    });
</script>