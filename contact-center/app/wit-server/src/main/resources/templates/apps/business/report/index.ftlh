<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>

<div class="layui-side layui-bg-black layui-layout-content ukefu-tab">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1">
				报表分类
				<span style="float:right;">
					<button class="layui-btn layui-btn-sm green" href="/apps/report/addtype<#if dataDic??>?dicid=${dataDic.parentid}</#if>" data-toggle="ajax" data-height="450" data-width="400" data-title="创建分类">
						<i class="layui-icon">&#xe608;</i>
						创建分类
					</button>
				</span>
			</h1>
			<div class="box">
				<ul class="ztree" id="dataDicTree"></ul>
			</div>
		</div>
	</div>
</div>
<div class="layui-body" id="ukefu-datadic-content">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1" style="background-color:#FFFFFF;">
				<#if dataDic??>
				${dataDic.name!''}
				<#else>
				报表列表
				</#if>
				<#if reportList??>（${reportList.totalElements}）</#if>
				<div style="float:right;" class="ukefu-bt-text-content">
					<#if dataDic??>
					<button class="layui-btn layui-btn-sm" href="/apps/report/edittype?id=${dataDic.id!''}" data-toggle="ajax" data-width="400" data-height="450" data-title="编辑分类">
						编辑分类
					</button>
					<button class="layui-btn layui-btn-sm layui-btn-danger" href="/apps/report/deletetype?id=${dataDic.id!''}" data-toggle="tip" data-title="删除分类将会同时删除分类下的所有报表，请确认是否删除？">
						删除分类
					</button>
					</#if>
					<button class="layui-btn layui-btn-sm green" href="/apps/report/add<#if dataDic??>?dicid=${dataDic.id!''}</#if>" data-toggle="ajax" data-width="400" data-height="450"  data-title="创建新报表">
						添加报表
					</button>
					<#if dataDic??>
						<div class="layui-btn-group ukefu-btn-group">
							<button class="layui-btn layui-btn-sm" href="/apps/report/imp?<#if dataDic??>type=${dataDic.id}</#if>" title="导入报表" data-toggle="ajax" data-width="950" data-height="600">
								<i class="kfont">&#xe641;</i> 导入 
							</button>
						</div>
					</#if>
					<div class="layui-btn-group ukefu-btn-group">
						<button class="layui-btn layui-btn-sm dropdown-menu">
							<i class="kfont">&#xe672;</i> 导出 
							<i class="layui-icon">&#xe625;</i>
						</button>
						<ul class="ukefu-dropdown-menu layui-anim layui-anim-upbit">
							<li>
								<a href="javascript:void(0)" id="batexp" title="导出报表" target="_blank">
									<i class="kfont">&#xe672;</i>
									导出选中结果
								</a>
							</li>
							<li>
								<a href="/apps/report/expall?<#if dataDic??>type=${dataDic.id}</#if>" title="导出全部报表数据" target="_blank">
									<i class="kfont">&#xe672;</i>
									导出全部数据
								</a>
							</li>
							<li>
								<a href="javascript:void(0)" id="batdel" title="删除知识条目" target="_blank">
									<i class="kfont">&#xe672;</i>
									删除选中结果
								</a>
							</li>
						</ul>
					</div>
				</div>
			</h1>
			<div class="row" style="padding:5px;">
				<div class="col-lg-12">
					<table class="layui-table" lay-skin="line">
					  <colgroup>
					  		<col width="1%">
							<col width="60%">
							<col width="24%">
							<col width="20%">
							<col width="1%">
						<col>
					  </colgroup>
					  <thead>
						<tr>
						  <th style="width:1%;padding-right:0px;">
						  	<input type="checkbox" id="checkall" />
						  </th>
						  <th>标题</th>
						  <th>创建时间</th>
						  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
						</tr> 
					  </thead>
					  <tbody>
						<#if reportList?? && reportList.content??>
						<#list reportList.content as report>
						<tr>
						  <td style="width:1%;padding-right:0px;">
							<input type="checkbox" class="ids" name="ids" value="${report.id!''}"/>
						  </td>
						  <td>
								<#if report.name?? && report.name?length gt 100>${report.name[0..100]}...<#else>${report.name!''}</#if>(${report.code!''})
						  </td>
						  <td>${report.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
						  <td style="white-space:nowrap;" nowrap="nowrap">
								<a href="/apps/report/edit?id=${report.id!''}" data-toggle="ajax" data-width="400" data-height="450" data-title="编辑报表信息">
									<i class="layui-icon">&#xe642;</i>
									编辑
								</a>
								<a href="/apps/report/design/index?id=${report.id!''}" target="_blank" style="margin-left:10px;">
									<i class="layui-icon">&#xe63c;</i>
									设计
								</a>
								<a href="/apps/report/design/reportpublish?reportid=${report.id!''}" style="margin-left:10px;"  data-toggle="ajax" data-width="600" data-height="300" data-title="发布模型">
						  			<i class="layui-icon">&#xe609;</i>
						  			发布
						  		</a>
								<a href="/apps/report/delete?id=${report.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除报表？">
									<i class="layui-icon" style="color:red;">&#x1006;</i>
									删除
								</a>
						  </td>
						</tr>
						</#list>
						<#else>
						<tr>
							<td colspan="20" style="height:400px;">
								<div class="ukefu-empty" style="background: none">
									<i class="layui-icon">&#xe63a;</i>
									<div style="">还没有记录</div>
								</div>
							</td>
						</tr>
						</#if>
					  </tbody>
					</table>
				</div> 	   
			</div>
			<div class="row" style="padding:5px;">
				<div class="col-lg-12" id="page" style="text-align:center;"></div>
			</div>
		</div>	
	</div>
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
		location.href = "/apps/report/index?dicid="+treeNode.id ;
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
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage.render({
				cont: 'page'
				,pages: <#if reportList??>${reportList.totalPages}<#else>0</#if> //总页数
				,curr:<#if reportList??>${reportList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						loadURL("/apps/report/replylist?p="+data.curr+"&typeid=<#if dataDic??>${dataDic.id!''}<#else>0</#if>","#ukefu-quickreply-content");
					}
				}
		   });
	});
	$('#batexp').click(function(){
		var ids = "";
		var num = 0 ;
		$('.ids').each(function(){
			if($(this).prop("checked")){
				if(ids!=""){
					ids += "&" ;
				}
				ids += "ids=" + $(this).val();
				num++;
			}
		});
		if(num>0){
			$('#batexp').attr('href' , '/apps/report/expids?'+ids)	;
		}else{
			top.layer.alert("请先选择需要导出的报表");
			$('#batexp').attr("href" , "javascript:void(0)");
		}
		
		
	});
	$('#batdel').click(function(){
		var ids = "";
		var num = 0 ;
		$('.ids').each(function(){
			if($(this).prop("checked")){
				if(ids!=""){
					ids += "&" ;
				}
				ids += "ids=" + $(this).val();
				num++;
			}
		});
		if(num>0){
			top.layer.confirm("请确认是否删除报表？", {icon: 3, title:'提示'}, function(index){
				top.layer.close(index);
				if(confirm){
					location.href = '/apps/report/batdelete?<#if dataDic??>type=${dataDic.id!''}&</#if>'+ids ;
				}
			});
		}else{
			top.layer.alert("请先选择需要删除的报表");
			$('#batexp').attr("href" , "javascript:void(0)");
		}
	});
</SCRIPT>