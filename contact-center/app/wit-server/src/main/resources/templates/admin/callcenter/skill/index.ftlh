<div class="row" style="height:100%;">
    <div class="col-lg-12" style="border-right:1px solid #e6e6e6;padding-right:0px;">
        <h1 class="site-h1" style="background-color:#FFFFFF;">技能组列表
            <#if skillGroups??>（${skillGroups?size}）</#if>
            <span style="float:right;">
                <button class="layui-btn layui-btn-sm green" href="/admin/callcenter/skill/add?hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="550" data-height="260" data-title="添加技能组">添加技能组</button>
                <button class="layui-btn layui-btn-sm green" href="/admin/callcenter/skill/imp?hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="550" data-height="400" data-title="导入坐席">导入坐席</button></span>
        </h1>
        <div class="row">
            <div class="col-lg-12" style="padding-left:5px;">
                <div class="layui-collapse">
                	<#if skillGroups??>
                	<#list skillGroups as skill>
                    <div class="layui-colla-item">
                        <h2 class="layui-colla-title">${skill.name!''}（${skill.skill!''}）</h2>
                        <div class="layui-colla-content layui-show" style="padding:0px;">
                            <table class="layui-table" lay-skin="line" style="border-left:0px;">
                                <colgroup>
                                    <col width="30%">
                                    <col width="20%">
                                    <col width="1%">
                                    <col>
                                </colgroup>
                                <tbody>
                                    <#if skillExtentionList??>
                                        <#list skillExtentionList as skillExtention>
                                        <#if skillExtention.skillid == skill.id>
                                            <tr>
                                                <td>${skillExtention.extention!''}</td>
                                                <td>${skillExtention.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
                                                <td style="white-space:nowrap;" nowrap="nowrap">
                                                    <a href="/admin/callcenter/skill/extention/delete?id=${skillExtention.id!''}&hostid=${pbxHost.id!''}" data-target="#callCenterContent" data-callback="loadURL" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否将分机移除？">
                                                        <i class="layui-icon" style="color:red;">&#x1006;</i>移除</a>
                                                </td>
                                            </tr>
                                        </#if>
                                        </#list>
                                    </#if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    </#list>
                    </#if>
                </div>
            </div>
        </div>
        <div class="row" style="padding:5px;">
            <div class="col-lg-12" id="page" style="text-align:center;"></div>
        </div>
    </div>
</div>
<script>layui.use('element',
    function() {
        var element = layui.element;
        element.init();
    });
</script>