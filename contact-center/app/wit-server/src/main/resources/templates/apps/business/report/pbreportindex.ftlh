<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>

<div class="layui-side layui-bg-black layui-layout-content ukefu-tab">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1">
				报表分类
			</h1>
			<div class="box">
				<ul class="ztree" id="dataDicTree"></ul>
			</div>
		</div>
	</div>
</div>
<div class="layui-body" id="ukefu-datadic-content">
	<#include "/apps/business/report/pbreportlist.ftlh">
</div>

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
		location.href = "/apps/report/pbreportindex?dicid="+treeNode.id ;
	}

	var zNodes =[
	    { id:'0', pId:'0', name:"报表分类", open:true }
	    <#if dataDicList??>
	    <#list dataDicList as dataDic>
	    ,{ id:'${dataDic.id!''}', pId:'${dataDic.parentid!''}', name:"${dataDic.name!''}", open:true }
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
		layui.use('layer', function(){
			var layer = layui.layer;
			<#if msg?? && msg == 'qr_type_exist'>
			layer.alert('分类名称重复，请重新填写', {icon: 2});
		  	</#if>
		  	<#if msg?? && msg == 'rt_name_exist'>
			layer.alert('报表名称重复，请重新填写', {icon: 2});
		  	</#if>
		  	
		}); 
	});
	
	$(document).ready(function(){
		var hide ;
		$('.dropdown-menu').on("click" , function(){
			var distance = getDistance(this);
			if(hide = true){
				$(this).closest(".ukefu-btn-group").addClass("open");
			}else{
				$(this).closest(".ukefu-btn-group").removeClass("open");
			}
			if(distance.right < 200){
				$(this).next().css("right" , "0px").css("left" , "auto");
			}
		}).hover(function(){
			hide = true ;
		} , function(){
			hide = false ;
			var btn = $(this); 
			setTimeout(function(){
				if(hide){
					$(btn).removeClass("open");
				}
			} , 500);
		});
		$('.ukefu-btn-group').hover(function(){
			$(this).addClass("open");
			$(this).find('.ukefu-dropdown-menu').css("right" , "0px").css("left" , "auto");
			hide = false ;
		} , function(){
			hide = true ;
			setTimeout(function(){
				if(hide){
					$(".ukefu-btn-group.open").removeClass("open");
				}
			} , 500);
		});
		
		$('#checkall').click(function(){
			var isCheckAll = $(this).prop('checked');
			if (isCheckAll) {  
	               $("input[type='checkbox']").each(function() {  
	                   this.checked = true;  
	               });  
	           } else {  
	               $("input[type='checkbox']").each(function() {  
	                   this.checked = false;  
	               });  
	           } 
		})
	});
	
</SCRIPT>