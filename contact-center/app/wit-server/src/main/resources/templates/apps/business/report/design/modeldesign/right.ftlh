<div>
	<div class="ukefu-design-title">图表设置</div>
	<form id="chartppy" class="layui-form layui-form-pane" style="padding:5px;" action="/apps/report/design/changechartppy" method="post" >
		<input type="hidden" name="id" value="${reportModel.id!''}" >
		<div class="layui-form-item" >
			<label class="layui-form-label">选择组件</label>
			<div class="layui-input-block">
				<select name="template"  lay-filter="templatesel">
					<#if reportList??>
						<#list reportList as rtpl>
						<option value="${rtpl.id!''}" <#if  eltemplet?? &&  eltemplet.id == rtpl.id>selected="selected"</#if>>${rtpl.name!''}</option>
						</#list>
					</#if>
			      </select>
			</div>
		</div>
		<div class="ukefu-right-item">
			<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe620;</i>
			行列交换
			<span class="ukefu-right-item-span">
			<input type="checkbox" name="exchangerw" value="1" lay-skin="switch" lay-filter="exchangerw"<#if reportModel?? && reportModel.exchangerw>checked</#if>>
			</span>		
		</div>
		<div>
			<div class="ukefu-right-item">
				<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe620;</i>
				数据量
				<span class="ukefu-right-item-span">
				<input type="checkbox" name="isloadfulldata" value="true" lay-skin="switch" lay-filter="isloadfulldata" <#if reportModel?? && reportModel.isloadfulldata == 'true'>checked</#if>>
				</span>
			</div>
			<div class="ukefu-right-content" id="isloadfulldata" <#if reportModel?? && reportModel.isloadfulldata != 'true'>style="display:none"</#if> >
				<span>
					<input type="radio" lay-filter="pagesize"  name="pagesize" value="5" title="5" <#if reportModel?? && reportModel.pagesize== 5>checked</#if> >
				</span>
				<span>
					<input type="radio" lay-filter="pagesize"  name="pagesize" value="10" title="10" <#if reportModel?? && reportModel.pagesize== 10>checked</#if> >
				</span>
				<span>
					<input type="radio" lay-filter="pagesize"  name="pagesize" value="20" title="20" <#if reportModel?? && reportModel.pagesize== 20>checked</#if> >
				</span>
				<span>
					<input type="radio" lay-filter="pagesize"  name="pagesize" value="50" title="50" <#if reportModel?? && reportModel.pagesize== 50>checked</#if>>
				</span>
				<span>
					<input type="radio" lay-filter="pagesize"  name="pagesize" value="100" title="100" <#if reportModel?? && reportModel.pagesize== 100>checked</#if>>
				</span>
				<span>
					<input type="radio"  lay-filter="pagesize"  name="pagesize" value="500" title="500" <#if reportModel?? && reportModel.pagesize== 500>checked</#if>>
				</span>
			</div>
		</div>
		<div>
			<div class="ukefu-right-item">
				<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe620;</i>
				显示图例
				<span class="ukefu-right-item-span">
				<input type="checkbox" name="legen" value="true" lay-skin="switch" lay-filter="legen" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.legen >checked</#if>>
				</span>
			</div>
			<div class="ukefu-right-content" id="legen" <#if reportModel?? && reportModel.chartProperties?? && !reportModel.chartProperties.legen>style="display:none"</#if> >
				<span>
					<input type="radio" lay-filter="legenalign"  name="legenalign" value="top" title="顶部" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.legenalign== 'top'>checked</#if> >
				</span>
				<span>
					<input type="radio" lay-filter="legenalign"  name="legenalign" value="right" title="右端" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.legenalign== 'right'>checked</#if> >
				</span>
				<span>
					<input type="radio" lay-filter="legenalign"  name="legenalign" value="bottom" title="底部" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.legenalign== 'bottom'>checked</#if>>
				</span>
				<span>
					<input type="radio" lay-filter="legenalign"  name="legenalign" value="left" title="左端" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.legenalign== 'left'>checked</#if>>
				</span>
			</div>
		</div>
		<div>
			<div class="ukefu-right-item">
				<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe620;</i>
				显示数值
				<span class="ukefu-right-item-span">
				<input type="checkbox" name="dataview" value="true" lay-skin="switch" lay-filter="dataview" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.dataview >checked</#if>>
				</span>
			</div>
		</div>
		<div>
			<div class="ukefu-right-item">
				<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe620;</i>
				数值格式化
				<span class="ukefu-right-item-span" style="width:100px;">
					<select name="format"   lay-filter="format">
							<option value="" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='' >selected="selected"</#if>>默认</option>
							<option value="####" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='####' >selected="selected"</#if>>####</option>
							<option value="0.0" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='0.0' >selected="selected"</#if>>0.0</option>
							<option value="0.00" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='0.00' >selected="selected"</#if>>0.00</option>
							<option value="0.000" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='0.000' >selected="selected"</#if>>0.000</option>
							<option value="100%" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='100%' >selected="selected"</#if>>100%</option>
							<option value="100.0%" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='100.0%' >selected="selected"</#if>>100.0%</option>
							<option value="100.00%" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='100.00%' >selected="selected"</#if>>100.00%</option>
							<option value="###,###" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='###,###' >selected="selected"</#if>>###,###</option>
							<option value="###,###.0" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='###,###.0' >selected="selected"</#if>>###,###.0</option>
							<option value="###,###.00" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='###,###.00' >selected="selected"</#if>>###,###.00</option>
							<option value="###,###.000" <#if reportModel?? && reportModel.chartProperties?? && reportModel.chartProperties.format =='###,###.000' >selected="selected"</#if>>###,###.000</option>
				      </select>
				</span>
			</div>
		</div>
		<!-- <div class="layui-form-item">
			<button class="layui-btn layui-btn-normal" lay-submit lay-filter="formDemo">立即提交</button>
		</div> -->
	</form>
</div>
<script>
	var submitchartproperty = function(){
		let params = $("#chartppy").serialize();
		loadURL("/apps/report/design/changechartppy?"+params,"#${reportModel.id!''}");
	}
	layui.use('form', function(){
		  var form = layui.form;
		  form.render();
		  form.on('select(templatesel)', function(data){
			  window.location.href= "/apps/report/design/changetpl?mid=${reportModel.id!''}&tplid="+data.value;
			}); 
		  form.on('select(format)', function(data){
			  submitchartproperty();
			}); 
		  form.on('switch(isloadfulldata)', function(data){
			  if(data.elem.checked){
				  $("#isloadfulldata").show();
			  }else{
				  $("#isloadfulldata").hide();
			  }
			  submitchartproperty();
			}); 
		  form.on('switch(exchangerw)', function(data){
			  submitchartproperty();
			}); 
		  form.on('radio(pagesize)', function(data){
			  submitchartproperty();
			}); 
		  form.on('switch(legen)', function(data){
			  if(data.elem.checked){
				  $("#legen").show();
			  }else{
				  $("#legen").hide();
			  }
			  submitchartproperty();
			}); 
		  form.on('radio(legenalign)', function(data){
			  submitchartproperty();
			}); 
		  form.on('switch(dataview)', function(data){
			  submitchartproperty();
			}); 
		});
</script>
