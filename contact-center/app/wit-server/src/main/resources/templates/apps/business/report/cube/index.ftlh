<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<#include "/apps/business/report/cube/include/left.ftlh">
	</div>
</div>
<div class="layui-body" id="ukefu-cube-content">
	<#include "/apps/business/report/cube/list.ftlh">
</div>
<script>
	layui.use('layer', function(){
		<#if msg?? && msg =='CUBE_VALID_FAILED_1'>
			layer.alert('删除元数据失败，存在关联数据', {icon: 2});
		<#elseif msg?? && msg =='CUBE_VALID_FAILED_2'>
		layer.alert('验证失败，立方体模型至少要有一个指标。', {icon: 2});
		</#if>
	});
</script>
