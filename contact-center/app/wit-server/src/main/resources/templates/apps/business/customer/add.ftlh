<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/customer/save" method="post">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">获得时间：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.touchtime" id="getdate" readOnly="readOnly" lay-verify="date" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font> (您得到此客户的时间)
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" style="width:60px">类型：</label>
							<div class="layui-input-inline" style="width:218px;margin-right:0px;padding-top:9px;">
								<input type="radio" name="entcustomer.etype" value="enterprise" title="企业客户" checked lay-filter="etype">
								<input type="radio" name="entcustomer.etype" value="personal" title="个人客户" lay-filter="etype">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">共享客户：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="entcustomer.shares" value="none" title="不共享（仅创建人和直属上级可见）"> 
									<input type="radio" name="entcustomer.shares" value="all" title="所有人" checked>
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
								<input type="text" name="entcustomer.name" required  lay-verify="customer_username" autocomplete="off"
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
								<input type="text" name="entcustomer.phone" autocomplete="off"
									class="layui-input">
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">电子邮件：</label>
							<div class="layui-input-inline">
								<input type="text" name="entcustomer.email" lay-verify="entemail" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">城市：</label>
							<div class="layui-input-inline" style="width:80px;">
								<@select "com.dic.address.area" "entcustomer.province" "" "lay-ignore" "width:85px;"/>
							</div>
						</div>
						<div class="layui-inline">
							<div class="layui-input-inline" style="width:80px;" id="city">
								<@select "com.dic.address.area.city" "entcustomer.city" "" "lay-ignore" "width:86px;"/><!-- 二级字典，不存在的 CODE -->
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户地址：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<input type="text" name="entcustomer.address" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户类型：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.entype" "entcustomer.ekind" ekind!'' "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">客户级别：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.elevel" "entcustomer.elevel" "" "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户来源：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.esource" "entcustomer.esource" "" "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">成熟度：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.maturity" "entcustomer.maturity" "" "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">行业：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.industry" "entcustomer.industry" "" "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">客户状态：</label>
							<div class="layui-input-inline">
								<@select "com.dic.contacts.validstatus" "entcustomer.validstatus" "" "lay-ignore"/>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">客户说明：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="entcustomer.description" placeholder="请输入内容" class="layui-textarea" style="resize:none;"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item" id="contacts_info">
				<h2 class="layui-colla-title">联系人信息<font color='red'>（提示：创建企业信息后可在个人客户里添加更多联系人）</font></h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">姓名：</label>
							<div class="layui-input-inline">
								<input type="text" name="contacts.name" autocomplete="off"
									class="layui-input" lay-verify="customer_username">
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">性别：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="contacts.gender" value="男" title="男">
      								<input type="radio" name="contacts.gender" value="女" title="女" checked>
								</div>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">生日：</label>
							<div class="layui-input-inline">
								<input type="text" id="birthday" name="contacts.cusbirthday" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">座机电话：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="text" name="contacts.phone" autocomplete="off" class="layui-input">
								</div>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">电子邮件：</label>
							<div class="layui-input-inline">
								<input type="text" name="contacts.email" autocomplete="off" lay-verify="entemail" class="layui-input">
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">手机号：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="text" name="contacts.mobile" autocomplete="off" class="layui-input">
								</div>
							</div>
						</div>
					</div>

					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系人地址：</label>
							<div class="layui-input-inline" style="width: 660px;">
								<input type="text" name="contacts.address" autocomplete="off" class="layui-input">
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
	layui.use('form', function() {
		var form = layui.form;
		form.render(); //更新全部
		form.verify({
			entemail: function(value){
			    if(value!="" && !new RegExp(/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/).test(value)){
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
				$('#contacts_info').show();
				$('#cusname').text("客户名称：");
			}else{
				$('#contacts_info').hide();
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
			min : '1950-01-01 00:00:00',
			max : 0,
			istoday : false
		};

		date.elem = document.getElementById('getdate');
		laydate.render(date);

		date.elem = document.getElementById('birthday');
		laydate.render(date);

	});
	$('#entcustomer_province').change(function(){
		loadURL('/res/dic?id='+$(this).val()+"&name=entcustomer.city&attr=lay-ignore&style=width:85px;" , '#entcustomer_city');
	})
</script>
