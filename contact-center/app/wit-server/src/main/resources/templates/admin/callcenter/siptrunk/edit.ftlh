<#include "/public/macro.ftlh">
<div class="uk-layui-form">
    <form class="layui-form uk-form" action="/admin/callcenter/siptrunk/update" data-toggle="ajax-form" data-inner="#callCenterContent">
    	<input type="hidden" name="hostid" value="${pbxHost.id!''}">
    	<input type="hidden" name="id" value="${siptrunk.id!''}">
        <div class="layui-form-item">
            <label class="layui-form-label">名称：</label>
            <div class="layui-input-inline">
                <input type="text" name="name" value="${siptrunk.name!''}" required lay-verify="required" placeholder="请输入SIP网关名称" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            	SIP中继名称，请使用英文或拼音代码
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">SIP网关地址：</label>
            <div class="layui-input-inline">
                <input type="text" name="sipserver" value="${siptrunk.sipserver!''}" required lay-verify="required" placeholder="请输入SIP网关地址" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">端口：</label>
            <div class="layui-input-inline">
                <input type="text" name="port" value="${siptrunk.port!''}" required lay-verify="required" value="5060" placeholder="SIP网关端口（默认5060）" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">心跳检测：</label>
            <div class="layui-input-inline">
                <input type="text" name="heartbeat" value="${siptrunk.heartbeat!''}" lay-verify="number" value="5" placeholder="" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font>
            	心跳检测频率，默认5秒 
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">协议：</label>
            <div class="layui-input-inline">
                <select name="protocol" lay-verify="required">
			        <option value="UDP" <#if siptrunk.protocol?? && siptrunk.protocol == "UDP">selected="selected"</#if>>UDP</option>
			        <option value="TCP" <#if siptrunk.protocol?? && siptrunk.protocol == "TCP">selected="selected"</#if>>TCP</option>
			      </select>
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">注册：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="register" lay-skin="switch" <#if siptrunk.register>checked="checked"</#if> lay-text="是|否">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">默认网关：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="defaultsip" lay-skin="switch" <#if siptrunk.defaultsip?? && siptrunk.defaultsip == true>checked="checked"</#if> lay-text="是|否">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">网关号码：</label>
            <div class="layui-input-inline">
                <input type="text" name="outnumber" value="${siptrunk.outnumber!''}" required lay-verify="required" placeholder="接入的网关号码" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">坐席忙转号码：</label>
            <div class="layui-input-inline">
                <input type="text" name="busyext" placeholder="坐席忙的时候转入的号码" value="${siptrunk.busyext!''}" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">坐席离线转手机：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="enablecallagent" lay-skin="switch" <#if siptrunk.enablecallagent>checked="checked"</#if> lay-text="启用|禁用">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">无坐席在线转号码：</label>
            <div class="layui-input-inline">
                <input type="text" name="notready" placeholder="无坐席在线的时候转入号码" value="${siptrunk.notready!''}" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">无名单或未分配：</label>
            <div class="layui-input-inline">
                <input type="text" name="noname" value="${siptrunk.noname!''}" placeholder="无名单或未分配的时候转入号码" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">归属地：</label>
				<div class="layui-input-inline">
					<@select "com.dic.address.area" "province" siptrunk.province!"" "lay-ignore" ""/>
				</div>
			</div>
			<div class="layui-inline">
				<div class="layui-input-inline" id="contacts_city">
					<@select siptrunk.city!''+".subdic" "city" siptrunk.city!'' "lay-ignore" ""/><!-- 二级字典，不存在的 CODE -->
				</div>
			</div>
		</div>
		
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">加拨前缀：</label>
				<div class="layui-input-inline">
					<input type="radio" name="prefix" value="1" title="启用" <#if siptrunk.prefix?? && siptrunk.prefix == "1">checked="checked"</#if>>
					<input type="radio" name="prefix" value="0" title="禁用" <#if !(siptrunk.prefix?? && siptrunk.prefix == "1")>checked="checked"</#if>>
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
		loadURL('/res/dic?id='+$(this).val()+"&name=city&attr=lay-ignore&style=display:inline-block;" , '#contacts_city');
	})
layui.use('form', function(){
  var form = layui.form;
  form.render();
});
</script>