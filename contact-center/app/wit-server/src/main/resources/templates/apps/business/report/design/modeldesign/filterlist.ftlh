<form class="layui-form"  action="/apps/report/design/modeldesign?id=${reportModel.id!''}&tabid=filter" method="post"  style="overflow-y:scroll;height:400px;">
<div class="ukefu-design-group">
	<div class="ukefu-design-type">
   		<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe632;</i>
   		过滤器
   		<span style="float:right;">
			<button class="layui-btn layui-btn-xs layui-btn-normal" href="/apps/report/design/filteradd?cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-height="550" data-width="850" data-title="添加过滤器">
				<i class="layui-icon">&#xe608;</i>
				添加
			</button>
		</span>
   	</div>
   	<#if reportModel?? && reportModel.filters??>
   	<ul id="filter_sortable" class="uk_sortable layui-form">
   	<#list reportModel.filters as filter>
	   		<li class="uk-state-default" id="sort_${filter.id!''}">
	   			<label>${filter.title!''}
	   				<button class="layui-btn layui-btn-xs layui-btn-danger" style="float:right;" href="/apps/report/design/filterdel?id=${filter.id!''}" data-toggle="tip" data-title="请确认是否删除过滤器？"><i class="layui-icon">&#xe640;</i></button>
	   				<button class="layui-btn layui-btn-xs layui-btn-normal" style="float:right;margin-right:5px;"  href="/apps/report/design/filteredit?tabid=filter&fid=${filter.id!''}" data-toggle="ajax" data-height="550" data-width="700" data-title="编辑过滤器"><i class="layui-icon">&#xe642;</i></button>
	   			</label>
	   			<div  class="layui-form-item" id="item_${filter.code!''}">
	   				<#include "/apps/business/report/design/modeldesign/filter.ftlh"/>
	   			</div>
	   		</li>
	</#list>
	</ul>
	</#if>
</div>
<div class="layui-form-item" style="margin-top:5px;text-align: right;padding-right: 10px;">
	<button class="layui-btn layui-btn-normal" lay-submit >查询</button>
</div>
</form>
<script>
	layui.use('form', function(){
		  var form = layui.form;
		  form.render();
		});
</script>
