<div class="ukefu ${element.id!''}" id="${element.id!''}" data-type="element">
	<div class="ukefu-title" id="t_${element.id!''}" data-type="element" data-mid="${element.id!''}" data-dsid="${element.tableid!''}" data-templet="${element.chartemplet!''}">
		<h5>${element.name!'组件'}</h5>
		<#if true>
		<div class="ukefu-design-btn">
			<span class="layui-btn layui-btn-danger layui-btn-sm ukefu-btn-small" style="margin-left:5px;">
				<a href="javascript:void(0)" data-toggle="tip" data-title="请确认是否从页面中删除组件？" data-callback="deletemodel('${element.id!''}')"  target="_blank" class="ukefu-report-design">删除</a>
			</span>
			<span class="layui-btn layui-btn-normal layui-btn-sm ukefu-btn-small" style="">
				<a  data-id="${element.id!''}" href="/apps/report/design/modeldesign?tabid=data&id=${element.id!''}" target="_blank"  class="ukefu-report-design">设计</a>
			</span>
		</div>
		</#if>
	</div>
	<div class="ukefu-content">
		<div class="ukefu-model no-padding ukefu-element-ajax" data-url="/apps/report/design/getelement?id=${element.id!''}&publishedid=<#if publishedReport??>${publishedReport.id!''}<#else></#if>" id="elementdiv_${element.id!''}" >
			<div class="ukefu-empty" style="text-align: center;line-height: 100px;height:250px;background: none">
				<i class="layui-icon layui-anim layui-anim-rotate layui-anim-loop" style="font-size: 50px;">&#xe63d;</i>
			</div>
		</div>                                 
	</div>
</div>
<script>
	$(document).ready(function(){
		loadURL("/apps/report/design/getelement?id=${element.id!''}&publishedid=<#if publishedReport??>${publishedReport.id!''}<#else></#if>","#elementdiv_${element.id!''}");
	});
</script>
