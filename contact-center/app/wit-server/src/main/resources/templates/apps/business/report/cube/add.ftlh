<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/report/cube/save" method="post">
		<div class="layui-form-item">
			<label class="layui-form-label">名称</label>
			<div class="layui-input-inline">
				<input type="text" name="name"  lay-verify="required" autocomplete="off"
					class="layui-input">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">分类：</label>
			<div class="layui-input-inline" style="position: relative;">
				<input type="hidden" id="typeid" name="typeid" value="<#if cubeType??>${cubeType.id!''}<#else>0</#if>">
				<input type="text" name="typename" id="typename" required value="<#if cubeType??><#if cubeTypeList??><#list cubeTypeList as qt><#if cubeType.id == qt.id>${qt.name!''}</#if></#list></#if><#else>模型分类</#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
					class="layui-input">
				<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
				<div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
					<ul id="cubeTypeTree" class="ztree"></ul>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label" >类型：</label>
			<div class="layui-input-inline" >
				<@select "com.dic.cube.modeltype" "modeltype" modeltype!'' "lay-ignore required lay-verify='required' " 'width:220px;'/>
			</div>
		</div>
		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button type="reset" class="layui-btn layui-btn-primary">重置</button>
			</div>
		</div>
	</form>
</div>

<script>
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('element', function(){
		var element = layui.element;
	});
	var setting = {
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				onClick: onClick
			}
		};
		
	function onClick(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("cubeTypeTree"),
		nodes = zTree.getSelectedNodes(),
		v = "" , value = "";
		nodes.sort(function compare(a,b){return a.id-b.id;});
		for (var i=0, l=nodes.length; i<l; i++) {
			v += nodes[i].name + ",";
			if(value != ""){
				value = value + "," ;
			}
			value = nodes[i].id ;
		}
		if (v.length > 0 ) v = v.substring(0, v.length-1);
		$("#typename").attr("value", v);
		$("#typeid").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	    { id:'0', pId:'0', name:"模型分类", open:true }
	    <#if cubeTypeList??>
	    <#list cubeTypeList as cubeType>
	    ,{ id:'${cubeType.id!''}', pId:'${cubeType.parentid!''}', name:"${cubeType.name!''}", open:true }
	    </#list>
	    </#if>
	];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#cubeTypeTree"), setting, zNodes);
		<#if cubeType??>
		var node = ztree.getNodeByParam('id', '${cubeType.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		</#if>
	});
</script>