<#include "/public/macro.ftlh">
<div class="uk-layui-form">
	<form class="layui-form" action="/apps/contacts/update" method="post">
		<input type="hidden" name="id" value="${contacts.id!''}">
		<input type="hidden" name="ckindId" value="${ckindId!''}">
		<input type="hidden" name="wluid" value="${contacts.wluid!''}">
		<input type="hidden" name="wlusername" value="${contacts.wlusername!''}">
		<input type="hidden" name="wlcid" value="${contacts.wlcid!''}">
		<input type="hidden" name="wlcompany_name" value="${contacts.wlcompany_name!''}">
		<input type="hidden" name="wlsid" value="${contacts.wlsid!''}">
		<input type="hidden" name="wlsystem_name" value="${contacts.wlsystem_name!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">获得时间：</label>
							<div class="layui-input-inline">
								<input type="text" name="touchtime" id="getdate" value="<#if contacts.touchtime??>${contacts.touchtime?string('yyyy-MM-dd')}</#if>" readOnly="readOnly" lay-verify="date" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font> (您得到此联系人的时间)
						</div>
						<div class="layui-inline">
							<label class="layui-form-label" style="width:60px;line-height: 35px;">类型：</label>
							<div class="layui-input-inline" style="width:218px;margin-right:0px;padding-top:9px;">
								<#if contacts.ckind?? && contacts.ckind != ''>
								<@select "com.dic.contacts.ckind" "ckind" contacts.ckind!'' "lay-ignore required lay-verify='required' "/>
								<#else>
								<@select "com.dic.contacts.ckind" "ckind" "" "lay-ignore"/>
								</#if>
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">共享联系人：</label>
							<div class="layui-input-inline" style="width:auto;">
								<div class="layui-input-block">
									<input type="radio" name="shares" value="none" title="不共享（仅创建人和直属上级可见）" <#if contacts?? && contacts.shares !='all'>checked</#if>> 
									<input type="radio" name="shares" value="all" title="所有人" <#if contacts?? && contacts.shares =='all'>checked</#if>>
									<!-- 
									<input type="radio" name="shares" value="1" title="指定人员或部门" checked>
									 -->
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">联系人信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">联系人名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="name" value="${contacts.name!''}" required lay-verify="required" autocomplete="off"
									class="layui-input">
							</div>
						</div>
						<div class="layui-inline">
							<font color="red">*（必填项）</font>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">性别：</label>
							<div class="layui-input-inline">
								<div class="layui-input-block" style="width:220px;">
									<input type="radio" name="gender" value="1" title="男" <#if contacts?? && contacts.gender =='1'>checked</#if>>
      								<input type="radio" name="gender" value="0" title="女" <#if contacts?? && contacts.gender =='0'>checked</#if>>
      								<input type="radio" name="gender" value="-1" title="未知" <#if contacts?? && (contacts.gender !='1' && contacts.gender !='0')>checked</#if>>
								</div>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">生日：</label>
							<div class="layui-input-inline">
								<input type="text" id="cusbirthday" name="cusbirthday" value="${contacts.cusbirthday!''}" autocomplete="off"
									class="layui-input">
								<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;">&#xe637;</i>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系电话：</label>
							<div class="layui-input-inline">
								<input type="text" name="phone" value="${contacts.phone!''}" autocomplete="off"
									class="layui-input">
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">手机号：</label>
							<div class="layui-input-inline">
								<input type="text" name="mobileno" value="${contacts.mobileno!''}" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">城市：</label>
							<div class="layui-input-inline" style="width:80px;">
								<@select "com.dic.address.area" "province" contacts.province "lay-ignore" "width:85px;"/>
							</div>
						</div>
						<div class="layui-inline">
							<div class="layui-input-inline" style="width:80px;" id="contacts_city">
								<@select contacts.city+".subdic" "city" contacts.city "lay-ignore" "width:86px;"/><!-- 二级字典，不存在的 CODE -->
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label">电子邮件：</label>
							<div class="layui-input-inline" style="margin-left:5px;">
								<input type="text" name="email" lay-verify="entemail" value="${contacts.email!''}" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" style="widht:80px;" id="contacts_skypeid">skypeID:</label>
							<div class="layui-input-inline">
								<input type="text" name="skypeid" lay-verify="skypeid" autocomplete="" value="${contacts.skypeid!''}" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系人地址：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<input type="text" name="address" autocomplete="off" value="${contacts.address!''}" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系人说明：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="memo" placeholder="请输入内容" class="layui-textarea" style="resize:none;">${contacts.memo!''}</textarea>
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
	$('#province').change(function(){
		loadURL('/res/dic?id='+$(this).val()+"&name=city&attr=lay-ignore&style=width:85px;display:inline-block;" , '#contacts_city');
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
			required: function (value) {
				if (value.length>100) {
					return '联系人名称不能超过100';
				}
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

		date.elem = document.getElementById('cusbirthday');
		laydate.render(date);

	});
</script>