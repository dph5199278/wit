<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/agent/summary/save" data-toggle="ajax-form" data-close="true" data-inner="#ukefu-chat-agent" method="post" >
		<#if summary??><input type="hidden" name="id" value="${summary.id!''}"></#if>
		<input type="hidden" name="userid" value="${userid!''}">
		<input type="hidden" name="agentserviceid" value="${agentserviceid!''}">
		<input type="hidden" name="agentuserid" value="${agentuserid!''}">
        <input type="hidden" name="channel" value="${channel!''}">
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" id="cusname">服务类型：</label>
				<div class="layui-input-inline" style="width: 765px;">
					<select name = "servicetype" class="tags" multiple required  lay-verify="required" lay-ignore style="width:100%;">
						<#if tags??>
						<#list tags as tag>
						<option value="${tag.id!''}" <#if summary?? && summary.servicetype?? && summary.servicetype?index_of(tag.id) gte 0>selected="selected"</#if>>${tag.tag!''}</option>
						</#list>
						</#if>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<font color="red">*</font>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">是否预约：</label>
				<div class="layui-input-inline" style="width:150px;">
					<div class="layui-input-block">
						<input type="radio" name="reservation" lay-filter="reservation" value="1" title="是" <#if summary?? && summary.reservation>checked</#if>>
						<input type="radio" name="reservation" lay-filter="reservation" value="0" title="否" <#if !(summary?? && summary.reservation)>checked</#if>>
					</div>
				</div>
			</div>
			<div class="layui-inline ukefu_reservation" style="<#if summary?? && summary.reservation == true>display:;<#else>display:none;</#if>">
				<label class="layui-form-label" style="width: 80px;">预约方式：</label>
				<div class="layui-input-inline" style="width: 120px;">
					<#if summary?? && summary.reservtype??>
					<@select "com.dic.summary.reservtype" "reservtype" summary.reservtype!'' "lay-ignore" "width:120px;"/>
					<#else>
					<@select "com.dic.summary.reservtype" "reservtype" '' "lay-ignore" "width:120px;"/>
					</#if>
				</div>
			</div>
			<div class="layui-inline ukefu_reservation" style="<#if summary?? && summary.reservation == true>display:;<#else>display:none;</#if>">
				<label class="layui-form-label" style="width: 80px;">预约时间：</label>
				<div class="layui-input-inline" style="width: 245px;">
					<input type="text" id="reservtime" name="reservtime" autocomplete="off" value="<#if summary?? && summary.reservtime??>${summary.reservtime?string('yyyy-MM-dd HH:mm:ss')}</#if>"
						class="layui-input">
					<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
				</div>
			</div>
		</div>
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">服务小结：</label>
				<div class="layui-input-inline" style="width: 765px;">
					<textarea name="summary" placeholder="服务小结" class="layui-textarea" style="resize:none;height:180px"><#if summary??>${summary.summary!''}</#if></textarea>
				</div>
			</div>
		</div>

		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button type="reset" class="layui-btn layui-btn-primary" onclick="layer.close(layer.index);">关闭</button>
			</div>
		</div>
	</form>
</div>

<script>
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
		form.on("radio(reservation)" , function(data){
			if(data.value == '1'){
				$('.ukefu_reservation').show();
			}else{
				$('.ukefu_reservation').hide();
			}	
		});
	});
	layui.use('element', function(){
		var element = layui.element;
	});
	layui.use('laydate', function() {
		var laydate = layui.laydate;

		var date = {
			min: 0,
			istime:true,
			format:"YYYY-MM-DD hh:mm:ss",
			istoday : false
		};

		date.elem = document.getElementById('reservtime');
		laydate.render(date);

	});
	$(".tags").select2();
</script>