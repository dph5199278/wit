<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/setting/quickreply/type/update" method="post">
		<input type="hidden" name="id" value="${quickType.id!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">分类基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">分类名称：</label>
							<div class="layui-input-inline">
								<input type="text" value="${quickType.name!''}" name="name" required  lay-verify="required" autocomplete="off" maxlength="20"
									class="layui-input">
							</div>
							<font color="red">*</font>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label" id="cusname">排序序号：</label>
							<div class="layui-input-inline ukefu-limit-height">
								<select name="inx">
									<#list 1..100 as inx>
									<option value="${inx}" <#if quickType.inx == inx>selected="selected"</#if>>${inx}</option>
									</#list>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">扩展信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">上级分类：</label>
							<div class="layui-input-inline" style="position: relative;">
								<input type="hidden" id="parentid" name="parentid" value="${quickType.parentid!''}">
								<input type="text" name="parentname" id="parentname" required value="<#if quickType.parentid == '0'>问答分类<#else><#if quickTypeList??><#list quickTypeList as qt><#if quickType.parentid == qt.id>${qt.name!''}</#if></#list></#if></#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
								<div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
									<ul id="quickTypeTree" class="ztree"></ul>
								</div>
							</div>
						</div>
					</div>

					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">分类备注：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="description" placeholder="请输入内容" class="layui-textarea" style="resize:none;" maxlength="255">${quickType.description!''}</textarea>
							</div>
						</div>
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
		var zTree = $.fn.zTree.getZTreeObj("quickTypeTree"),
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
	             { id:'0', pId:'0', name:"问答分类", open:true }
	             <#if quickTypeList??>
	             <#list quickTypeList as quickType>
	             ,{ id:'${quickType.id!''}', pId:'${quickType.parentid!''}', name:"${quickType.name!''}", open:false }
	             </#list>
	             </#if>
	         ];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#quickTypeTree"), setting, zNodes);
		<#if quickType??>
		var node = ztree.getNodeByParam('id', '${quickType.id!''}');//获取id为1的点  
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
