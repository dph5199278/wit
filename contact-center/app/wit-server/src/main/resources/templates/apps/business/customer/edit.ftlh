<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/customer/update" method="post">
		<input type="hidden" name="entcustomer.id" value="${entCustomer.id!''}">
		<input type="hidden" name="ekindId" value="${ekindId!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">获得时间：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.touchtime" readOnly="readOnly" lay-verify="date" id="getdate" autocomplete="off"
									class="layui-input" value="<#if entCustomer.touchtime??>${entCustomer.touchtime?string('yyyy-MM-dd')}</#if>">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font> (您得到此客户的时间)
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" style="width:60px">类型：</label>
							<div class="layui-input-inline" style="width:218px;margin-right:0px;padding-top:9px;">
								<input type="radio" name="entcustomer.etype" value="enterprise" title="企业客户" lay-filter="etype" <#if entCustomer.etype?? && entCustomer.etype == 'enterprise'>checked</#if>>
								<input type="radio" name="entcustomer.etype" value="personal" title="个人客户" lay-filter="etype" <#if entCustomer.etype?? && entCustomer.etype == 'personal'>checked</#if>>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">共享客户：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="entcustomer.shares" value="none" title="不共享（仅创建人和直属上级可见）"  <#if entCustomer?? && entCustomer.shares !='all'>checked</#if>> 
									<input type="radio" name="entcustomer.shares" value="all" title="所有人" <#if entCustomer?? && entCustomer.shares =='all'>checked</#if>>
									<!-- 
									<input type="radio" name="entcustomer.shares" value="1" title="指定人员或部门" checked>
									 -->
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">企业信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">客户名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.name" required  lay-verify="customer_username" autocomplete="off" value="${entCustomer.name!''}"
									class="layui-input">
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系电话：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.phone" autocomplete="off" value="${entCustomer.phone!''}"
									class="layui-input">
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">电子邮件：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.email" lay-verify="entemail" autocomplete="off" class="layui-input" value="${entCustomer.email!''}">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">省份：</label>
							<div class="layui-input-inline" style="width:80px;">
								<@select "com.dic.address.area" "entcustomer.province" entCustomer.province!'' "lay-ignore" "width:85px;"/>
							</div>
						</div>
						<div class="layui-inline">
							<div class="layui-input-inline" style="width:80px;" id="city">
								<@select entCustomer.city+".subdic" "entcustomer.city" entCustomer.city!'' "lay-ignore" "width:86px;"/><!-- 二级字典，不存在的 CODE -->
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户地址：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<input type="text" name="entcustomer.address" autocomplete="off" class="layui-input" value="${entCustomer.address!''}">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户类型：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.entype" "entcustomer.ekind" entCustomer.ekind!'' "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">客户级别：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.elevel" "entcustomer.elevel" entCustomer.elevel!'' "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户来源：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.esource" "entcustomer.esource" entCustomer.esource!'' "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">成熟度：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.maturity" "entcustomer.maturity" entCustomer.maturity!'' "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">行业：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.industry" "entcustomer.industry" entCustomer.industry!'' "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">客户状态：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.validstatus" "entcustomer.validstatus" entCustomer.validstatus!'' "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户说明：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="entcustomer.description" placeholder="请输入内容" class="layui-textarea" style="resize:none;">${entCustomer.description!''}</textarea>
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
	//Demo
	$('#entcustomer_province').change(function(){
		loadURL('/res/dic?id='+$(this).val()+"&name=entcustomer.city&attr=lay-ignore&style=width:85px;" , '#entcustomer_city');
	})
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
		form.verify({
			entemail: function(value){
			    if(value!="" && !new RegExp(/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/).test(value)){
			      return '请输入正确的电子邮箱地址';
			    }
			},
			customer_username: function (value) {
				if (value.length > 100){
					return "用户姓名不能超过100字";
				}
			}
		});  
		form.on("radio(etype)" , function(data){
			if(data.value == "enterprise"){
				$('#cusname').text("客户名称：");
			}else{
				$('#cusname').text("客户姓名：");
			}
		});
	});
	layui.use('element', function(){
		var element = layui.element;
	});
	layui.use('laydate', function() {
		var laydate = layui.laydate;

		var date = {
			min : '2010-01-01 00:00:00',
			max : 0,
			istoday : false
		};

		date.elem = document.getElementById('getdate');
		laydate.render(date);
	});
</script>