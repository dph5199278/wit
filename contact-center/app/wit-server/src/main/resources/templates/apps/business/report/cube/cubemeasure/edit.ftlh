<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/report/cubemeasure/update" method="post">
		<input type="hidden" name="id" value="${cubemeasure.id!''}">
		<input type="hidden" name="cubeid" value="${cubemeasure.cubeid!''}">
		<input type="hidden" name="tablename" value="${table.tablename!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<!-- <div class="layui-inline">
							<label class="layui-form-label">数据表：</label>
							<div class="layui-input-inline">
								<select lay-ignore class="ukefu-select" name="tablename" id="fktable" style="width: 100%">
									<option value="">选择数据表</option>
								 	<#if fktableList??>
								 	<#list fktableList as fktable>
								 	<option value="${fktable.tb.name!''}" idval="${fktable.tb.id!''}" <#if fktable.tb.name == cubemeasure.tablename>selected="selected"</#if>>${fktable.tb.name!''}</option>
								 	</#list>
								 	</#if> 
								</select>
							</div>
						</div> -->
						<div class="layui-inline" id="fktableiddiv">
							<#include "/apps/business/report/cube/cubemeasure/fktableiddiv.ftlh">
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="name"  lay-verify="required" autocomplete="off"
									class="layui-input" value="${cubemeasure.name!''}">
							</div>
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" >参数：</label>
							<div class="layui-input-inline">
								<input type="text" name="parameters" 
									class="layui-input">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">自定义计算：</label>
							<div class="layui-input-inline">
								<input type="radio" name="calculatedmember" value="1" title="是" id="calculatedmember" lay-filter="calculatedmember" <#if cubemeasure.calculatedmember?? && cubemeasure.calculatedmember>checked</#if>> 
								<input type="radio" name="calculatedmember" value="0" title="否" lay-filter="calculatedmember" <#if cubemeasure.calculatedmember?? && !cubemeasure.calculatedmember>checked</#if>>
							</div>
						</div>
						<div class="layui-inline" id="aggregatordiv">
							<label class="layui-form-label" >聚合方式：</label>
							<div class="layui-input-inline" >
								<@select "com.dic.cubemeasure.aggregator" "aggregator" cubemeasure.aggregator!'' "required lay-ignore"/>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" >数值格式：</label>
							<div class="layui-input-inline">
								<input type="text" name="formatstring"  
									class="layui-input" value="${cubemeasure.formatstring!''}">
							</div>
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" >排序位置：</label>
							<div class="layui-input-inline">
								<input type="text" name="sortindex" value="1"
									class="layui-input" value="${cubemeasure.sortindex!''}">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">属性编码：
								<br/>
								<div><a href="javascript:void(0)" style="margin-right:20px;margin-top:20px;" onclick="return false;" id="attributedemo">样例代码</a></div>
							</label>
							<div class="layui-input-inline" style="width: 520px;">
								<textarea name="attribue" id="attribute" class="layui-textarea" style="resize:none;">${cubemeasure.attribue!''}</textarea>
							</div>
						</div>
					</div>
					<!-- 
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">描述：</label>
							<div class="layui-input-inline" style="width: 520px;">
								<textarea name="description" class="layui-textarea" style="resize:none;">${cubemeasure.description!''}</textarea>
							</div>
						</div>
					</div>
					 -->
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
	$(document).ready(function(){
		if("0" == $('input[name=calculatedmember]:checked').val()){
			$("#aggregatordiv").show();
		}else{
			$("#aggregatordiv").hide();
		}
	})
	/* $('#fktable').change(function(){
		loadURL('/apps/report/cubemeasure/fktableid?tableid='+$('#fktable option:selected').attr("idval"), '#fktableiddiv');
	}) */
	$('input[name=calculatedmember]').click(function(){
		if("0" == $('input[name=calculatedmember]:checked').val()){
				$("#aggregatordiv").show();
			}else{
				$("#aggregatordiv").hide();
			}
	})
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
		form.on('radio(calculatedmember)', function(data){
		  if("0" == data.value){
				$("#aggregatordiv").show();
			}else{
				$("#aggregatordiv").hide();
			}
		}); 
	});
	layui.use('element', function(){
		var element = layui.element;
	});
	$('#attributedemo').on("click",function(){
		if($('#attribute').val().trim() == ""){
			if($('#calculatedmember ').prop("checked") == true){
				$('#attribute').val($('#calcattributetext').text());
			}else{
				$('#attribute').val($('#attributetext').text());
			}
		}else{
			top.layer.alert('属性已有内容', {icon: 1});
		}
		return false ;
	});
</script>
<script type="text/html" id="attributetext"><MeasureExpression>
	<SQL>
		(case when status = 'ready' then 1 else 0 end)
	</SQL>
</MeasureExpression>
</script>
<script type="text/html" id="calcattributetext"><Formula>
[Measures].[示例]-[Measures].[示例二]
</Formula>
</script>
