<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/organ/update">
	  <input type="hidden" name="id" value="${organData.id!''}">
	  <input type="hidden" name="area" id="area" value="${organData.area!''}">
	  <div class="layui-form-item">
	    <label class="layui-form-label">部门：</label>
	    <div class="layui-input-inline">
	      <input type="text" name="name" lay-verify="requiredOrgiName" value="${organData.name!''}" placeholder="请输入部门名称" autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-form-item">
	  	<div class="layui-inline">
				<label class="layui-form-label">上级机构：</label>
				<div class="layui-input-inline" style="position: relative;">
					<input type="hidden" id="parent" name="parent" value="<#if organData??>${organData.parent}<#else>0</#if>">
					<input type="text" name="parentname" id="parentname" required value="<#if organData??>${organData.name}<#else>组织机构</#if>" lay-verify="requiredTwo" autocomplete="off" readOnly="readonly" onClick="showMenu();"
						class="layui-input">
					<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
					<div id="menuContent" style="display:none; position: absolute;z-index:10000;width: 218px;" class="ukefu-ztree">
						<ul id="organTree" class="ztree" style="width:208px;"></ul>
					</div>
				</div>
			</div>
	  </div>
	  <div class="layui-form-item">
	    <label class="layui-form-label">启用技能组：</label>
	    <div class="layui-input-block">
	      <input type="checkbox" name="skill" lay-skin="switch" lay-filter="area"  lay-text="开启|关闭" value="1" <#if organData.skill>checked</#if>>
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
//Demo
layui.use('form', function(){
  var form = layui.form;
  form.render(); //更新全部
	form.verify({
		requiredOrgiName: function (value) {
			if (value == $('#parentname').val()){
				return '部门和上级部门不能一致';
			}
			if (value.length > 50){
				return "部门名称不能超过50字";
			}

			if(!value){
				return "部门名称必填";
			}
		},

	})
  form.on("switch(area)",function(data){
  	if(data.elem.checked){
  		$('#area').show();
  	}else{
  		$('#area').hide();
  	}
  }) ;
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
			onClick: onClick,
			beforeClick: beforeClick
		}
	};
	
	function beforeClick(treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("organTree")
		nodes = zTree.getSelectedNodes();
		var allow = true , lastnode ;
		for (var i=0, l=nodes.length; i<l; i++) {
			lastnode = nodes[i].type ;
			if(nodes[i].organ && (nodes.length >= 1 && (event.ctrlKey || event.shiftKey))){
				allow = false ; break ;
			}
		}
		if(lastnode != treeNode.type && (event.ctrlKey || event.shiftKey)){
			allow = false ;
		}
		
		if (allow == false) {
			top.layer.alert("组织机构不能点选多个上级机构，也不能同时选择地区和机构", {icon: 3});
			return false;
		} else {
			return true;
			
		}
	}
	
	function onClick(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("organTree"),
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
		if(treeNode.type == "area"){
			$("#parentname").attr("value", v);
			$("#parent").attr("value", "0");
			$("#area").attr("value", value);
		}else{
			$("#parentname").attr("value", v);
			$("#parent").attr("value", value);
			$("#area").attr("value", "");
		}
		if (!event.ctrlKey && !event.shiftKey) {
			hideMenu() ;
		}
	}


	var zNodes =[
	    { id:'0', pId:'00', name:"组织机构", open:true , organ : true, type : "organ"}
	    <#if organList??>
	    <#list organList as organ>
	    ,{ id:'${organ.id!''}', pId:'${organ.parent!'0'}', name:"${organ.name!''}", open:true , organ : true , type : "organ",  icon:"/images/dept.png" }
	    </#list>
	    </#if>
	];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#organTree"), setting, zNodes);
		<#if organData??>
		var node = ztree.getNodeByParam('id', '${organData.parent!''}');//获取id为1的点
		ztree.selectNode(node);//选择点  
		$("#parentname").attr("value", node.name);
		<#if organData.area??>
		<#list organData.area?split(",") as area>
		var areanode_${area_index} = ztree.getNodeByParam('id', '${area}');//获取id为1的点
		ztree.selectNode(areanode_${area_index} , true );//选择点  
		</#list>
		</#if>
		
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