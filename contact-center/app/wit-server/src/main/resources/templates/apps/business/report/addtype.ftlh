<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/report/type/save" method="post">
		<div class="layui-form-item" style="margin-top:20px;">
			<div class="layui-inline">
				<label class="layui-form-label" id="cusname">分类名称：</label>
				<div class="layui-input-inline">
					<input type="text" maxlength="20" name="name" required  lay-verify="required" autocomplete="off" maxlength="100"
						class="layui-input">
					
				</div>
				<font color="red">*</font>
			</div>
		</div>
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">上级分类：</label>
				<div class="layui-input-inline" style="position: relative;">
					<input type="hidden" id="parentid" name="parentid" value="<#if dataDic??>${dataDic.id!''}<#else>0</#if>">
					<input type="text" name="parentname" id="parentname" required value="<#if dataDic??><#if dataDicList??><#list dataDicList as qt><#if dataDic.id == qt.id>${qt.name!''}</#if></#list></#if><#else>报表分类</#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
						class="layui-input">
					<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
					<div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
						<ul id="dataDicTree" class="ztree"></ul>
					</div>
				</div>
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
	element.init();
});
</script>
<SCRIPT type="text/javascript">
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
		var zTree = $.fn.zTree.getZTreeObj("dataDicTree"),
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
		$("#parentname").attr("value", v);
		$("#parentid").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	             { id:'0', pId:'0', name:"报表分类", open:true }
	             <#if dataDicList??>
	             <#list dataDicList as dataDic>
	             ,{ id:'${dataDic.id!''}', pId:'${dataDic.parentid!''}', name:"${dataDic.name!''}", open:false }
	             </#list>
	             </#if>
	         ];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#dataDicTree"), setting, zNodes);
		<#if dataDic??>
		var node = ztree.getNodeByParam('id', '${dataDic.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		<#else>
		var node = ztree.getNodeByParam('id', '0');//获取id为1的点  
		ztree.selectNode(node);//选择点
		</#if>
	});
	function showMenu() {
		$("#menuContent").css({left:"0px", top:"38px"}).show();

		$("body").bind("mousedown", onBodyDown);
	}
	function hideMenu() {
		$("#menuContent").hide();
		$("body").unbind("mousedown", onBodyDown);
	}
	function onBodyDown(event) {
		if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
			hideMenu();
		}
	}

</SCRIPT>
