<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/kbs/type/save" method="post">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">知识基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">标题：</label>
							<div class="layui-input-inline" style="width:665px;">
								<input type="text" name="title" required  lay-verify="required" autocomplete="off" maxlength="100"
									class="layui-input">
							</div>
							<font color="red">*</font>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">知识分类：</label>
							<div class="layui-input-inline">
								<input type="hidden" id="tptype" name="tptype" value="0">
								<input type="text" name="tpname" id="tpname" required value="知识库目录" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
								<div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
									<ul id="kbsTypeTree" class="ztree"></ul>
								</div>
							</div>
							<font color="red">*</font>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">发布人：</label>
							<div class="layui-input-inline">
								<div class="layui-input-block">
									${user.username!''}（${user.uname!''}）
								</div>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">有效期开始：</label>
							<div class="layui-input-inline">
								<input type="text" name="begintime" id="begintime" readOnly="readOnly" autocomplete="off"
									class="layui-input" onclick="layui.laydate.render({elem: this, show: true, istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">有效期结束：</label>
							<div class="layui-input-inline">
								<input type="text" id="endtime" name="endtime" autocomplete="off" class="layui-input" onclick="layui.laydate.render({elem: this, show: true, istime: true, format: 'YYYY-MM-DD hh:mm:ss'})">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
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
							<label class="layui-form-label">知识内容：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="content" id="content" placeholder="请输入内容" class="layui-textarea" style="resize:none;width:100%;height:300px;" maxlength="255"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">扩展信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<label class="layui-form-label">知识标签：</label>
						<div class="layui-input-inline" style="width:665px;">
							<select lay-ignore name="tags" class="ukefu-select" placeh style="width: 100%" multiple="multiple">
							 	<#if tags??>
							 	<#list tags as tag>
							 	<option value="${tag.id!''}">${tag.tag!''}</option>
							 	</#list>
							 	</#if> 
							</select>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">关键词：</label>
							<div class="layui-input-inline" style="width:665px;">
								<select lay-ignore name="keyword" class="ukefu-select-input" placeh style="width: 100%" multiple="multiple">
								</select>
							</div>
							<button class="layui-btn layui-btn-sm green" href="/apps/kbs/addtype" data-toggle="ajax" data-height="450" data-width="950" data-title="提取关键词">
								提取关键词
							</button>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">知识摘要：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="summary" id="summary" placeholder="请输入摘要内容" class="layui-textarea" style="height:100px;resize:none;"></textarea>
							</div>
							<button class="layui-btn layui-btn-sm green" href="/apps/kbs/addtype" data-toggle="ajax" data-height="450" data-width="950" data-title="提取摘要">
								提取摘要
							</button>
						</div>
					</div>
					<div class="layui-form-item">
						<label class="layui-form-label">
							附件：
						</label>
						<div class="layui-input-block ukefu-form-block" style="margin-left:120px;">
							<div class="layui-box layui-upload-button">
								<input type="file" name="files"
									accept="*"
									class="layui-upload-file"
									onchange="$(this).closest('.layui-form-item').append($(this).closest('.ukefu-form-block').prop('outerHTML'));$(this).closest('.ukefu-form-block').find('.file_ad').html($(this).val());$(this).closest('.ukefu-form-block').find('.file_del').show();">
									<span class="layui-upload-icon"><i class="layui-icon">&#xe608;</i>上传附件</span>
							</div>
							<span>
								<span class="file_ad"></span>
								<span class="file_del" style="display:none;">
									<a href="javascript:void(0)" style="margin-left:10px;" onclick="$(this).closest('.ukefu-form-block').remove();">
							  			<i class="layui-icon" style="color:red;">&#x1006;</i>
							  			删除
							  		</a>
								</span>
							</span>
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
		var zTree = $.fn.zTree.getZTreeObj("kbsTypeTree"),
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
		$("#tpname").attr("value", v);
		$("#tptype").attr("value", value);
	}


	var zNodes =[
	             { id:'0', pId:'0', name:"知识库目录", open:true }
	             <#if kbsTypeResList??>
	             <#list kbsTypeResList as kbsType>
	             ,{ id:'${kbsType.id!''}', pId:'${kbsType.parentid!''}', name:"${kbsType.name!''}", open:true }
	             </#list>
	             </#if>
	         ];
	$(document).ready(function(){
		$.fn.zTree.init($("#kbsTypeTree"), setting, zNodes);
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
	layui.use('laydate', function() {
		var laydate = layui.laydate;
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
	KindEditor.options.cssData = "body { font-size: 15px; font-family:'Microsoft Yahei', 'Helvetica', 'Simsun', 'Arial';}";
	$(".ukefu-select").select2({minimumResultsForSearch: Infinity});
	$(".ukefu-select-input").select2({tags:true});
</SCRIPT>
