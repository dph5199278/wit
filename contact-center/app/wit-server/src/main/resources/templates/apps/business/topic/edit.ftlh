<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/topic/update" method="post">
		<input type="hidden" name="id" value="${topic.id!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">知识信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">标题：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<input type="text" name="title" autocomplete="off" value="${topic.title!''}" class="layui-input" required="required" lay-verify="required" maxlength="255">
								
							</div>
						</div>
						<div class="layui-inline">
							<label style="color:red;">* 必填项</label>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">有效期开始：</label>
							<div class="layui-input-inline">
								<input type="text" name="begintime" value="<#if topic.begintime>${topic.begintime?string('yyyy-MM-dd')}</#if>" id="begintime" readOnly="readOnly" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">有效期结束：</label>
							<div class="layui-input-inline">
								<input type="text" id="endtime" name="endtime" value="<#if topic.endtime>${topic.endtime?string('yyyy-MM-dd')}</#if>" autocomplete="off" class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">类似问题：</label>
							<div class="layui-input-inline" style="width: 664px;" id="mlt">
								<#if topic.silimar?? && topic.silimar?size gt 0>
								<#list topic.silimar as mlt>
								<div style="margin-bottom:5px;position: relative;" class="mlt_item">
									<input type="text" name="silimar" autocomplete="off" value="${mlt!''}" class="layui-input" maxlength="255" style="width:600px;">
									<span style="position: absolute;right:0px;top:0px;">
										<a href="javascript:void(0)" onclick="$('#mlt').append($(this).closest('.mlt_item').prop('outerHTML'));">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#xe608;</i></label>
										</a>
										<a href="javascript:void(0)" onclick="if($('.mlt_item').length > 1){$(this).closest('.mlt_item').remove();}else{top.layer.alert('类似问题最少需要保留一个，如果不需要，请删除内容');}" class="ukefu-group-remove">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#x1006;</i></label>
										</a>
									</span>
								</div>
								</#list>
								<#else>
								<div style="margin-bottom:5px;position: relative;" class="mlt_item">
									<input type="text" name="silimar" autocomplete="off" value="" class="layui-input" maxlength="255" style="width:600px;">
									<span style="position: absolute;right:0px;top:0px;">
										<a href="javascript:void(0)" onclick="$('#mlt').append($(this).closest('.mlt_item').prop('outerHTML'));">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#xe608;</i></label>
										</a>
										<a href="javascript:void(0)" onclick="if($('.mlt_item').length > 1){$(this).closest('.mlt_item').remove();}else{top.layer.alert('类似问题最少需要保留一个，如果不需要，请删除内容');}" class="ukefu-group-remove">
											<label style="color:#5FB878;font-size:15px;"><i class="layui-icon">&#x1006;</i></label>
										</a>
									</span>
								</div>
								</#if>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">热点问题：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="top" value="1" title="是" <#if topic.top == true>checked</#if>> 
									<input type="radio" name="top" value="0" title="否" <#if topic.top == false>checked</#if>>
								</div>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">知识分类：</label>
							<div class="layui-input-inline" style="width:230px;">
								<input type="hidden" id="type" name="type" value="<#if topic?? && topic.cate??>${topic.cate}<#else>0</#if>">
								<input type="text" name="typename" id="typename" required value="<#if topic.cate?? && topic.cate == '0'>知识分类<#else><#if knowledgeTypeList??><#list knowledgeTypeList as kt><#if topic.cate == kt.id>${kt.name!''}</#if></#list></#if></#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
								<div id="menuContent" style="display:none; position: absolute;z-index:10000;width: 228px;" class="ukefu-ztree">
									<ul id="topicTypeTree" class="ztree" style="width:218px;"></ul>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">知识内容</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">回复内容：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="content" id="content" placeholder="请输入内容" class="layui-textarea" style="height:160px;resize:none;">${topic.content!''}</textarea>
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
	layui.use('element', function(){
		var element = layui.element;
	});
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
	});
	layui.use('laydate', function() {
		var laydate = layui.laydate;

		var date = {
			min : '1950-01-01 00:00:00',
			istoday : false
		};

		date.elem = document.getElementById('begintime');
		laydate.render(date);

		date.elem = document.getElementById('endtime');
		laydate.render(date);

	});
	KindEditor.create('#content', {
		themeType : 'simple',
		width:"100%",
		resizeType:0,
		uploadJson : "/res/image/upload",
		items : ['fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
				 'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
				 'insertunorderedlist', '|', 'emoticons', 'image', 'link'],
        allowFileManager : false,
		fontsize:16
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
		var zTree = $.fn.zTree.getZTreeObj("topicTypeTree"),
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
		$("#type").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	    { id:'0', pId:'00', name:"公共分类", open:true , topic : false}
	    <#if knowledgeTypeList??>
	    <#list knowledgeTypeList as topicType>
	    ,{ id:'${topicType.id!''}', pId:'${topicType.parentid!''}', name:"${topicType.name!''}", open:true , topic : true }
	    </#list>
	    </#if>
	];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#topicTypeTree"), setting, zNodes);
		<#if topic.cate??>
		var node = ztree.getNodeByParam('id', '${topic.cate!''}');//获取id为1的点  
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