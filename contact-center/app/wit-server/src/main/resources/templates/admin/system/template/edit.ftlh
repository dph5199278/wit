<#include "/public/macro.ftlh">
<div class="uk-layui-form">
    <form class="layui-form uk-form" action="/admin/template/update">
    	<input type="hidden" name="templettype" value="${sysDic.id!''}">
    	<input type="hidden" name="id" value="${template.id!''}">
        <div class="layui-form-item">
            <label class="layui-form-label">模板名称：</label>
            <div class="layui-input-inline">
                <input type="text" name="name" value="${template.name!''}" required lay-verify="required" placeholder="请输入模板名称" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            	模板名称，请使用字母填写
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">模板代码：</label>
            <div class="layui-input-inline">
                <input type="text" name="code" placeholder="请输入模板代码" value="${template.code!''}"  autocomplete="off" class="layui-input">
            </div>
        </div>
        
        <#if sysDic.discode == true>
        <div class="layui-form-item">
            <label class="layui-form-label">图标：</label>
            <div class="layui-input-inline">
                <input type="text" name="iconstr" required lay-verify="required" value="${template.iconstr!''}"  placeholder="请输入模板图标" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            	模板图标，显示的图标路径
            </div>
        </div>
        <#if sysDic.ctype?? && sysDic.ctype == "layout">
        <div class="layui-form-item">
            <label class="layui-form-label">列数：</label>
            <div class="layui-input-inline">
                <select name="layoutcols" lay-verify="">
                	<#list 1..12 as inx>
				  		<option value="${inx}" <#if template.layoutcols == inx>selected="selected"</#if>>${inx}列</option>
				  	</#list>
				</select>   
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            	布局模板的列数量
            </div>
        </div>
        </#if>
        <#if sysDic.ctype?? && sysDic.ctype == "report">
        <!-- <div class="layui-form-item">
            <label class="layui-form-label">样例数据：</label>
            <div class="layui-input-inline">
                <@select "com.dic.template.datatype" "datatype" template.datatype!'' "lay-ignore required" "width: 220px;"/>
            </div>
            <div class="layui-form-mid layui-word-aux">
            	<font color="red">*</font> 
            	报表默认数据
            </div>
        </div> -->
        <div class="layui-form-item">
            <label class="layui-form-label">报表类型：</label>
            <div class="layui-input-inline">
                <input type="text" name="charttype" required lay-verify="required" value="${template.charttype!''}" placeholder="请输入报表类型" autocomplete="off" class="layui-input">
            </div>
        </div>
        </#if>
         </#if>
        
        <div class="layui-form-button">
            <div class="layui-button-block">
                <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>
<script>
layui.use('form', function(){
  var form = layui.form;
  form.render();
});
</script>