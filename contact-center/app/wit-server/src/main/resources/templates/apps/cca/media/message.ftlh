<#if chatmessage.msgtype?? && chatmessage.msgtype == "image">
	<a href="/agent/message/image?id=${chatmessage.id!''}" data-toggle="ajax" data-width="950" data-height="600"  title="图片">
	<img src="${(chatmessage.message!'')?no_esc}" class="ukefu-media-image" id="${chatmessage.id!''}">
	</a>
<#elseif chatmessage.msgtype?? && chatmessage.msgtype == "cooperation">
	<a href="/agent/message/image?t=review&id=${chatmessage.message!''}" data-toggle="ajax" data-width="950" data-height="600"  title="图片">
	系统发送了一个协作邀请
	</a>
<#elseif chatmessage.msgtype?? && chatmessage.msgtype == "file">
<div class="ukefu-message-file">
	<div class="ukefu-file-icon">
		<i class="kfont">&#xe61e;</i>
	</div>
	<div class="ukefu-file-desc">
		<a href="${chatmessage.message!''}" target="_blank">
		<div>${chatmessage.filename!''}</div>
		<div>${(chatmessage.filesize/1024!0)?string("###,###.###")}Kb</div>
		</a>
	</div>
</div>
<#elseif chatmessage.msgtype?? && chatmessage.msgtype == "location">
	<div>${chatmessage.message!''}</div>
	<#if systemConfig?? && systemConfig.mapkey?? && systemConfig.mapkey != ''>
		<div class="ukefu-map" id="map_${chatmessage.id!''}"></div>
		<script type="text/javascript"> 
		var map = new BMap.Map("map_${chatmessage.id!''}");          // 创建地图实例  
		var ggPoint = new BMap.Point(${chatmessage.locy!'0'}, ${chatmessage.locx!'0'});  // 创建点坐标  
		MapUtil.convert(map,ggPoint , "${chatmessage.message!''}" , ${chatmessage.scale!'15'}) ;
		</script>
	</#if> 
<#elseif chatmessage.msgtype?? && chatmessage.msgtype == "voice">
	<p class="weixinAudio" id="voice_media_${chatmessage.id!''}" style="<#if chatmessage.duration?? && chatmessage.duration!= ''><#if chatmessage.duration gt 30>width:300px;<#else>width:${50+chatmessage.duration * 10}px;</#if><#else>width:50px</#if>">
		<audio src="${chatmessage.message!''}" id="media" width="1" height="1" preload></audio>
		<span id="audio_area" class="db audio_area" title="${chatmessage.expmsg!''}">
			<span class="audio_wrp db">
				<span class="audio_play_area">
					<i class="icon_audio_default"></i>
					<i class="icon_audio_playing"></i>
				</span>
				<span class="audio_length tips_global">${chatmessage.duration!''}秒</span>
				<span id="audio_progress" class="progress_bar" style="width: 0%;"></span>
			</span>
		</span>
	</p>
	<#if chatmessage.expmsg??>
	<div class="ukefu-asr">${chatmessage.expmsg!''}</div>
	</#if>
	<script type="text/javascript">
		$('#voice_media_${chatmessage.id!''}').weixinAudio({
			autoplay:false
		});
	</script>
<#else>
	${(chatmessage.message!'')?no_esc}
	<#if chatmessage.suggestmsg?? && chatmessage.suggestmsg!="">
	<ul class="agent-info-list">
		<#list chatmessage.suggest as item>
		<li class="ukefu-suggest-item">
			<a href="javascript:void(0)" class="suggestitem">
				${item.title!''}
			</a>
		</li>
		</#list>
	</ul>
	</#if>
</#if>