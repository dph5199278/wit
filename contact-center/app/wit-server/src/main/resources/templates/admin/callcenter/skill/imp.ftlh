<div class="uk-layui-form">
    <form class="layui-form uk-form" action="/admin/callcenter/skill/extention/save" data-toggle="ajax-form" data-inner="#callCenterContent">
    	<input type="hidden" name="hostid" value="${pbxHost.id!''}">
        <div class="layui-form-item">
            <label class="layui-form-label">分机列表：</label>
            <div class="layui-input-inline">
                <select name="exts" lay-verify="required" multiple="multiple"  lay-ignore style="width:220px;height:200px;">
                	<#if extentionList??>
                	<#list extentionList as extention>
			        <option value="${extention.extention}">${extention.extention}</option>
			        </#list>
			        </#if>
			    </select>
            </div>
        </div>
       
        <div class="layui-form-item">
            <label class="layui-form-label">技能组：</label>
            <div class="layui-input-inline">
                <select name="skillid" lay-verify="required">
                	<#if skillGroups??>
                	<#list skillGroups as skill>
			        <option value="${skill.id}">${skill.name}</option>
			        </#list>
			        </#if>
			    </select>
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
layui.use('form', function(){
  var form = layui.form;
  form.render('select')
});
</script>