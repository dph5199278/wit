<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/report/design/dataset">
		<input type="hidden" name="mid" value="${mid!''}">
		<div  style="height:calc(100% - 60px);">
			<div class="layui-side-scroll">
				<#include "/apps/business/report/design/cube/leftforpb.ftlh">
			</div>
		</div>
		<div class="layui-body" id="ukefu-cube-content" style="height:calc(100% - 60px);left:240px;">
			<#include "/apps/business/report/design/cube/pbcubelist.ftlh">
		</div>
		<div class="layui-form-button" style="z-index:1000;">
			<div class="layui-button-block">
				<button class="layui-btn layui-btn-normal" lay-submit lay-filter="formDemo">立即提交</button>
			</div>
		</div>
	</form>
</div>
<script>
layui.use('form', function(){
	  var form = layui.form;
	  //监听提交
	 /* form.on('submit(formDemo)', function(data){
		  if(!$("input[name='publishedcubeid']:checked").val()){
			  layer.alert('请选择一个模型', {icon: 2});
			  return false;
		  }
		 loadURL("/apps/report/design/dataset?mid="+$("input[name='mid']").val()+"&cubeid="+$("input[name='publishedcubeid']:checked").val(),"#dataset");
		 layer.closeAll();
		 return false;
	  }); */
	});
</script>