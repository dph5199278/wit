<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/role/auth/save">
		<input type="hidden" name="id" value="${role.id!''}">
		<input type="hidden" name="menus" id="menus" value="<#if roleAuthList??><#list roleAuthList as roleAuth><#if roleAuth_index gt 0>,</#if>${roleAuth.dicid!''}</#list></#if>">
		<div class="layui-form-item">
			<div class="layui-input-inline">
				<ul id="roleTree" class="ztree"></ul>
			</div>
		</div>
		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button id="reset" type="reset" class="layui-btn layui-btn-primary" href="/admin/role/auth?id=${role.id}">重置</button>
			</div>
		</div>
	</form>
</div>
<SCRIPT type="text/javascript">
		var setting = {
			check: {
				enable: true
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				onCheck: zTreeOnCheck
			}
		};
		
		//获取所有选中节点的值
	    function zTreeOnCheck() {
	        var treeObj = $.fn.zTree.getZTreeObj("roleTree");
	        var nodes = treeObj.getCheckedNodes(true);
	        var msg = "";
	        for (var i = 0; i < nodes.length; i++) {
	        	if(msg!='' && nodes[i].id != ''){
					msg = msg+"," ;	        		
	        	}
	            msg += nodes[i].id;
	        }
	        $("#menus").val(msg);
	    }    

		var zNodes =[
		    <#if sysDic??>{ id:'${sysDic.id!''}', pId:'0', name:"菜单资源", open:true , value : ""}</#if>
		    <#if resourceList??>
		    <#list resourceList as dic>
			,{ id:'${dic.id}', pId:'${dic.parentid!''}' <#if roleAuthList??><#list roleAuthList as roleAuth><#if roleAuth.dicid == dic.id>,checked:true</#if></#list></#if> , name:"${dic.name!''}" , value : "${dic.code!''}", open:true , icon : "<#if dic.level?? && (dic.level == '1' || dic.level == '2')>/images/dir.png<#else>/images/menu.png</#if>"}
			</#list>
			</#if>
		];
		
		$(document).ready(function(){
			$.fn.zTree.init($("#roleTree"), setting, zNodes);
		});
	    $("#reset").on('click',function () {
			$.fn.zTree.init($("#roleTree"), setting, zNodes);
		})
	</SCRIPT>
