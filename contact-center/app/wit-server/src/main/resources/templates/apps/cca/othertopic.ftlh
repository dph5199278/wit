<ul class="info-list ukefu-quick-reply">
	<#if topicList?? && topicList?size gt 0>
	<#list topicList as topic>
	<li class="ukefu-agentservice-list">
		<a href="javascript:void(0)" class="othertopicdetail" data-href="/agent/other/topic/detail?id=${topic.id!''}">
			${topic.title!''}
		</a>
	</li>
	</#list>
	<#else>
	<li style="list-style: none;background-image: url();padding: 50px 0 50px;">
		<div class="ukefu-empty" style="background: none">
			<i class="layui-icon"></i>
			<div style="">还没有外部知识内容</div>
		</div>
	</li>
	</#if>
</ul>
<script type="text/javascript">
$('.othertopicdetail').on("dblclick",function(){
	loadURLWithTip($(this).data("href") , null , function(data){
		if(editor.count("text") == 0 ){
			editor.html(data);
		}else{
			top.layer.confirm("您已经输入了内容，覆盖当前输入框中内容？", {icon: 3, title:'覆盖提示'}, function(index){
				top.layer.close(index);
				editor.html(data);
			});
		}
	} , false , true);
});
</script>