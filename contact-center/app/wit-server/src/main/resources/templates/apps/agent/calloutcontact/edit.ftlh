<!-- <#include "/public/macro.ftlh"> -->
<div class="uk-layui-form">
    <div class="box-header" style="background-color: #f5f5f5;">
        <h1 class="site-h1" style="background-color:#FFFFFF;">编辑联系人</h1>
    </div>
    <form class="layui-form" action="/agent/calloutcontact/update" method="post">
		<input type="hidden" name="id" value="${contacts.id!''}">
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
                                <span><font color="red">*</font><b style="font-size: 10px;">(您得到此联系人的时间)</b></span>
							</div>
						</div>
					</div>
                    <div class="layui-form-item">
                        <div class="layui-inline">
                            <label class="layui-form-label">类型：</label>
                            <div class="layui-input-inline">
                                <#if contacts.ckind?? && contacts.ckind != ''>
                                <@select "com.dic.contacts.ckind" "ckind" contacts.ckind!'' "lay-ignore required lay-verify='required' "/>
                                <#else>
                                <@select "com.dic.contacts.ckind" "ckind" "" "lay-ignore"/>
                            </#if>
                        </div>
                        <span><font color="red">*</font></span>
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
                            <span><font color="red">*</font></span>
						</div>
					</div>
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">性别：</label>
							<div class="layui-input-inline">
								<div class="layui-input-block" style="width: 113%;">
									<input type="radio" name="gender" value="1" title="男" <#if contacts?? && contacts.gender =='1'>checked</#if>>
      								<input type="radio" name="gender" value="0" title="女" <#if contacts?? && contacts.gender =='0'>checked</#if>>
      								<input type="radio" name="gender" value="-1" title="未知" <#if contacts?? && (contacts.gender !='1' && contacts.gender !='0')>checked</#if>>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="layui-form-item">
						<div class="layui-inline">
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
								<input type="text" name="phone"  lay-verify="entphone" value="${contacts.phone!''}" autocomplete="off"
									class="layui-input">
							</div>
						</div>
                    </div>
                    <div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">手机号：</label>
							<div class="layui-input-inline">
								<input type="text" name="mobileno" id="getdate" lay-verify="entphone" value="${contacts.mobileno!''}" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">城市：</label>
							<div class="layui-input-inline">
								<@select "com.dic.address.area" "province" contacts.province "lay-ignore"/>
							</div>
						</div>
						<div class="layui-inline">
							<div class="layui-input-inline" id="contacts_city">
								<@select contacts.city+".subdic" "city" contacts.city "lay-ignore"/><!-- 二级字典，不存在的 CODE -->
							</div>
						</div>
                    </div>
                    <div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">电子邮件：</label>
							<div class="layui-input-inline">
								<input type="text" name="email" lay-verify="entemail" value="${contacts.email!''}" autocomplete="off" class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系人地址：</label>
							<div class="layui-input-inline">
								<input type="text" name="address" autocomplete="off" value="${contacts.address!''}" class="layui-input" style="width: 236%;">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">联系人说明：</label>
							<div class="layui-input-inline" style="width: 174%;">
								<textarea name="memo" placeholder="请输入内容" class="layui-textarea">${contacts.memo!''}</textarea>
							</div>
						</div>
					</div>

<div class="layui-form-button" style="position: inherit;">
    <div class="layui-button-block">
        <button class="layui-btn" lay-submit lay-filter="formDemo">立即更新</button>
        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>
</div>
				</div>

			</div>
		</div>
	</form>
    <!--未选择标签style-->
    <style>
        .unselectedClass {
            display: inline-block;
            font-weight: 400;
            color: #000000;
            text-align: center;
            white-space: nowrap;
            vertical-align: baseline;
            border-radius: .25em;
            background-color: #FFFFFF !important;
            height: 22px;
            line-height: 22px;
            padding: 0 5px;
            font-size: 14px;
            border: solid 1px #CCCCCC;
        }
    </style>
    <div class="box-body ukefu-im-theme">
        <div class="uk-layui-form">
            <div class="layui-collapse">
                <div class="layui-colla-item">
                    <h2 class="layui-colla-title">标签</h2>
                    <div class="layui-colla-content layui-show">
                        <div class="layui-form-item">
                            <div class="layui-inline">
                                <label class="layui-form-label" style="text-align: left;">已有标签：</label>
                                <div id="contactTags" class="layui-input-inline" style="width: 100%;">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="box-body ukefu-im-theme">
        <div class="uk-layui-form">
            <div class="layui-collapse">
                <div class="layui-colla-item">
                    <h2 class="layui-colla-title">笔记</h2>
                    <div class="layui-colla-content layui-show">
                        <div class="layui-form-item">
                            <label class="layui-form-label" style="text-align: left;">笔记分类：</label>
                            <div class="layui-input-inline">
                                <select id="notesCategory" name="notesCategory" lay-filter="category" required lay-verify="required" style="display: inline">
                                    <option value="callout">外呼</option>
                                    <option value="callin">呼入</option>
                                    <option value="webim">网页</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-inline">
                                <label class="layui-form-label" style="text-align: left;">内容：</label>
                                <div class="layui-input-inline" style="width: 174%;">
                                    <textarea id="notesContent" name="notes" class="layui-textarea" ></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-button-inline" style="float: left;">
                                <button class="layui-btn" lay-submit lay-filter="notesbtn" id="notesAddBtn">立即添加</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
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
			entphone: function(value){
			    if(value!="" && !new RegExp(/^1[3456789]\d{9}$/).test(value)){
			      return '请输入正确的电话号码';
			    }
			},
			entemail: function(value){
			    if(value!="" && !new RegExp(/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/).test(value)){
			      return '请输入正确的电子邮箱地址';
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