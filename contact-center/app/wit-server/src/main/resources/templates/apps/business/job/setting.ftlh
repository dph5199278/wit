<SCRIPT type="text/javascript">
	function pannelMgr(idflag){
		$(".nav_tapWrap li").attr("class","nav_tapIthems");
		$("#"+idflag+"_bnt").attr("class","nav_tapIthems cur");
		$("#mainDiv>div").css("display","none");
		$("#"+idflag+"_div").css("display","block");
	}
	function repeatMgr(){
		if($("#isRepeat").is(":checked")){
			$("#repeat_div").css("display","");
		}else{
			$("#repeat_div").css("display","none");
		}
	}
	
	function makeupMgr(){
		if($("#ismakeup").is(":checked")){
			$("#makeup_div").css("display","");
		}else{
			$("#makeup_div").css("display","none");
		}
	}
	
	function exchangeTab(){
		var runType = $("input[name='runCycle']:checked").val();
		if(runType=="day"){
			$("#run_space_tr").css("display","block");
			$("#run_datetype_tr").css("display","block");
			$("#run_time_tr").css("display","none");
			$("#space_unit").html("天");
		}else if(runType=="week"){
			$("#run_space_tr").css("display","block");
			$("#run_datetype_tr").css("display","block");
			$("#run_time_tr").css("display","block");
			$("#runWeekTime_div").css("display","block");
			$("#runMonthTime_div").css("display","none");
			$("#space_unit").html("周");
		}else{
			$("#run_space_tr").css("display","none");
			$("#run_datetype_tr").css("display","none");
			$("#run_time_tr").css("display","block");
			$("#runMonthTime_div").css("display","block");
			$("#runWeekTime_div").css("display","none");
			
		}
	}

	function runSpaceMgr(){
		$("input[name='runDates']").attr("checked",false);
		exchangeTab();
	}
</SCRIPT>
<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/job/save" method="post" data-toggle="ajax-form" data-close="true" data-callback="location.href=location.href">
		<input type="hidden" name="id" value="${job.id!''}">
	  	<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">执行方式：</label>
				<div class="layui-input-inline" style="width:200px;padding-top:2px;">
					<input name="plantask" <#if !job??||(job ?? && job.plantask ?? && job.plantask)> checked="checked"</#if> value="1" type="radio" title="自动"/>
					<input name="plantask" <#if job ?? && job.plantask ?? && !job.plantask> checked="checked"</#if> value="0" type="radio" title="手动"/>
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">类型：</label>
				<div class="layui-input-inline" style="width:280px;padding-top:2px;">
					<input name="runCycle" value="day" lay-filter="exchange" checked="checked" type="radio" title="每天"/>
					<input name="runCycle" value="week" lay-filter="exchange" <#if taskinfo ?? && taskinfo.runCycle ?? && taskinfo.runCycle=='week'> checked="checked"</#if>  type="radio" title="每周"/>
					<input name="runCycle" value="month" lay-filter="exchange" <#if taskinfo ?? && taskinfo.runCycle ?? && taskinfo.runCycle=='month'> checked="checked"</#if>  type="radio" title="每月"/>
				</div>
			</div>
		</div>
		<div id="run_space_tr">
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">日期类型：</label>
					<div class="layui-input-inline" style="width:360px;padding-top:2px;">
						<input lay-skin="primary" <#if taskinfo?? && taskinfo.datetypes??><#list taskinfo.datetypes as type><#if type=='0'>checked="checked"</#if></#list></#if> value="0" name="datetypes" type="checkbox" title="工作日">
						<input lay-skin="primary" <#if taskinfo?? && taskinfo.datetypes??><#list taskinfo.datetypes as type><#if type=='1'>checked="checked"</#if></#list></#if> value="1" name="datetypes" type="checkbox" title="节日">
						<input lay-skin="primary" <#if taskinfo?? && taskinfo.datetypes??><#list taskinfo.datetypes as type><#if type=='2'>checked="checked"</#if></#list></#if> value="2" name="datetypes" type="checkbox" title="假日">
					</div>
				</div>
			</div>
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">运行间隔：</label>
					<div class="layui-input-inline" style="width:100px;padding-top:2px;">
						<input name="runSpace" <#if  taskinfo ??>value="${taskinfo.runSpace!''}"<#else>value=""</#if> type="text" class="layui-input"><span id="space_unit" style="position: absolute;right: -20px;top: 15px;">天</span>
					</div>
				</div>
			</div>
		</div>
		<div id="run_time_tr">
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">于：</label>
					<div class="layui-input-inline" style="width:560px;">
						<div id="runWeekTime_div" style="width: 100%">
							<input name="runDates" value="2" lay-skin="primary" id="runDates2" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '2' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周一"/>
							<input name="runDates" value="3" lay-skin="primary" id="runDates3" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '3' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周二"/>
							<input name="runDates" value="4" lay-skin="primary" id="runDates4" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '4' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周三"/>
							<input name="runDates" value="5" lay-skin="primary" id="runDates5" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '5' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周四"/>
							<input name="runDates" value="6" lay-skin="primary" id="runDates6" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '6' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周五"/>
							<input name="runDates" value="7" lay-skin="primary" id="runDates7" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '7' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周六"/>
							<input name="runDates" value="1" lay-skin="primary" id="runDates1" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == '1' && taskinfo.runCycle == 'week'>checked="checked"</#if></#list></#if> title="周日"/>
						
						</div>
						<div id="runMonthTime_div"  style="width: 100%">
							<#list 1..12 as a>
							<input name="runDates" id="runDatesM${a}" value="${a}" lay-skin="primary" type="checkbox" <#if taskinfo?? && taskinfo.runDates??><#list taskinfo.runDates as days><#if days == a?string  && taskinfo.runCycle == 'month'>checked="checked"</#if></#list></#if> title="${a}月"/>
							</#list>
							<br/>
							<div style="width:100px;margin-top:10px;position: relative;">
								<select name="runBeginDate" style="width:100px;">
								<#list 1..31 as a>
								<option value="${a}" <#if taskinfo?? && taskinfo.runBeginDate?? && taskinfo.runBeginDate == a>selected='selected'</#if>>${a}</option>
								</#list>
								</select>
								<div  style="position: absolute;left: 110px;top: 7px;">日</div> 
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">开始时间：</label>
				<div class="layui-input-inline" style="width:60px;padding-top:2px;">
					<select name="runBeginHour">
						<#list 0..23 as a>
						<option value="${a}" <#if  taskinfo ??&&taskinfo.runBeginHour??><#if a==taskinfo.runBeginHour> selected </#if></#if>><#if a < 10 >0</#if>${a}</option>
						</#list>
					</select>
				</div>
				<div class="layui-input-inline" style="width:20px;padding-top:15px;margin-right:2px">：</div>
				<div class="layui-input-inline" style="width:60px;padding-top:2px;">
					<select name="runBeginMinute">
						<#list 0..59 as a>
						<option value="${a}" <#if  taskinfo ??&&taskinfo.runBeginMinute??><#if a==taskinfo.runBeginMinute> selected </#if></#if>><#if a < 10 >0</#if>${a}</option>
						</#list>
					</select>
				</div>
				<div class="layui-input-inline" style="width:20px;padding-top:15px;margin-right:2px">：</div>
				<div class="layui-input-inline" style="width:60px;padding-top:2px;">
					<select name="runBeginSecond">
						<#list 0..59 as a>
						<option value="${a}" <#if  taskinfo ??&&taskinfo.runBeginSecond??><#if a==taskinfo.runBeginSecond> selected </#if></#if>><#if a < 10 >0</#if>${a}</option>
						</#list>
					</select>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label"></label>
				<div class="layui-input-inline" style="width:60px;padding-top:2px;">
					<input lay-filter="repeatMgr" id="isRepeat" name="isRepeat"  <#if taskinfo ?? && taskinfo.isRepeat?? && taskinfo.isRepeat?string("true","false")=="true"> checked </#if>value="1" type="checkbox" title="在该日内重复"/>
				</div>
			</div>
		</div>
		<div  id="repeat_div" <#if !(taskinfo ?? && taskinfo.isRepeat?? && taskinfo.isRepeat?string("true","false")=="true")> style="display:none" </#if>>
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">重复间隔：</label>
					<div class="layui-input-inline" style="width:100px;">
						<input id="repeatSpace"  class="layui-input" name="repeatSpace" <#if  taskinfo ??>value="${taskinfo.repeatSpace!''}"</#if> type="text">
					</div>
					<div class="layui-input-inline" style="width:100px;">
						分钟
					</div>
				</div>
				<div class="layui-inline">
					<label class="layui-form-label">持续时间：</label>
					<div class="layui-input-inline" style="width:100px;">
						<input id="repeatJustTime"  class="layui-input" name="repeatJustTime" <#if  taskinfo ??>value="${taskinfo.repeatJustTime!''}"</#if> type="text">
					</div>
					<div class="layui-input-inline" style="width:100px;">
						小时
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
	exchangeTab();
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
		form.on("radio(exchange)",function(data){
			runSpaceMgr();
		});
		form.on("checkbox(repeatMgr)",function(data){
			repeatMgr();
		});
		
	});
	layui.use('element', function(){
		var element = layui.element;
	});
</script>
