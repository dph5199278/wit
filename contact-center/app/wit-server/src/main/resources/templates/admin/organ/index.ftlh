<style>
	.ztree li span {
		display: inline-block !important;
		white-space: pre-wrap;
		width: 90px;
		overflow:hidden;
		white-space:nowrap;
		text-overflow:ellipsis;
	}
</style>
<div class="row" style="height:100%;">
	<div class="col-lg-3" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			组织机构
			<span style="float:right;">
				<button class="layui-btn layui-btn-sm green" href="/admin/organ/add<#if organData>?parent=${organData.id!''}</#if>" id="areaadd" data-toggle="ajax" data-height="400" data-width="650" data-title="创建部门">
					创建部门
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<ul id="organTree" class="ztree"></ul>
			</div>
		</div>
	</div>
	<span class="col-lg-9">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			<span style="width:150px;display:inline-block;;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"><#if organData??>${organData.name!''}<#else>默认机构</#if><#if userList??>（${userList?size!''}）</#if></span>
			<span style="float:right;">
				<#if organData??>
				<button class="layui-btn layui-btn-sm green" href="/admin/organ/seluser?organ=${organData.id!''}" data-toggle="ajax" data-width="750" data-title="添加用户到当前部门">
					添加用户到当前部门
				</button>
				<button class="layui-btn layui-btn-primary layui-btn-sm" href="/admin/organ/area?id=${organData.id}" data-toggle="ajax" data-width="450" data-height="400" data-title="地区设置">
					地区设置
				</button>
				<button class="layui-btn layui-btn-primary layui-btn-sm" href="/admin/organ/edit?id=${organData.id}" data-toggle="ajax" data-width="650" data-height="400" data-title="修改部门">
					修改部门
				</button>
				<button class="layui-btn layui-btn-danger layui-btn-sm" href="/admin/organ/delete?id=${organData.id!''}" data-toggle="tip" data-title="请确认是否删除该部门？">
					删除部门
				</button>
				</#if>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="30%">
						<col width="20%">
						<col width="25%">
						<col width="24%">
						<col width="1%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th>用户</th>
					  <th>姓名</th>
					  <th>电子邮件</th>
					  <th>手机</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody style="table-layout: fixed; word-break: break-all;">
					<#if userList??>
					<#list userList as organUser>
					<tr>
					  <td style="width:150px;">
							<a href="">
								${organUser.username!''}
							</a>
					  </td>
					  <td style="width: 100px">${organUser.uname!''}</td>
					  <td>${organUser.email!''}</td>
					  <td>${organUser.mobile!''}</td>
					  <td style="white-space: nowrap;">
					  		<a href="/admin/organ/user/delete?id=${organUser.id!''}&organ=${organData.id!''}"  data-toggle="tip" data-title="请确认是否从坐席组中移除坐席？">
					  			<i class="layui-icon" style="color:red;">&#x1006;</i>
					  			移除
					  		</a>
					  </td>
					</tr>
					</#list>
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
		if(treeNode.topic){
			location.href = "/admin/organ/index?organ="+treeNode.id;
		}else if(treeNode.id == "0"){
			location.href = "/admin/organ/index";
		}else if(treeNode.area){
			$('#areaadd').attr("href" , "/admin/organ/add?area="+treeNode.id);
		}
	}

	var zNodes =[
	    { id:'0', pId:'00', name:"组织机构", open:true , topic : false}
	    <#if organList??>
	    <#list organList as organ>
	    ,{ id:'${organ.id!''}', pId:'${organ.parent!'0'}', name:"${organ.name!''}", open:true , topic : true ,  <#if organ.area?? && organ.area != "">icon:"/images/area.png" <#else>icon:"/images/dept.png"</#if> }
	    </#list>
	    </#if>
	];
	
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#organTree"), setting, zNodes);
		<#if organData??>
		var node = ztree.getNodeByParam('id', '${organData.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点
		</#if>

		//
		// var x = document.getElementById("node_Name");
		// var y = x.getElementsByClassName("child");
		// var i;
		// for (i = 0; i < y.length; i++) {
		// 	y[i].style.backgroundColor = "red";
		// }
	});
	
	layui.use('layer', function(){
		  var layer = layui.layer;
		<#if msg?? && msg == 'admin_organ_update_name_not'>
			layer.msg('组织机构分类名已存在！',{icon: 2, time: 1500})
		<#elseif msg?? && msg == 'admin_oran_not_delete'>
			layer.msg('无法删除，因为存在子组织',{icon: 2, time: 1500})
		<#elseif msg?? && msg == 'admin_organ_update_success'>
			layer.msg('修改组织成功',{icon: 1, time: 1500})
		<#elseif msg?? && msg == 'admin_organ_new_success'>
			layer.msg('新建组织成功',{icon: 1, time: 1500})
		<#elseif msg?? && msg == 'admin_organ_update_not_standard'>
			layer.msg('修改无法完成，上级机构选择错误',{icon: 2, time: 1500})
		</#if>


		});

</SCRIPT>

