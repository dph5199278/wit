<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <#include "/apps/service/include/left.ftlh">
    </div>
</div>
<div class="layui-body">
    <div class="layui-side-scroll">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="site-h1" style="border-top:1px solid #e6e6e6;">
                    访客留言<#if leaveMsgList??>（${leaveMsgList.totalElements}）</#if>
                </h1>
                <table class="layui-table overflow" lay-skin="line" style="table-layout: fixed; word-break: break-all;">
                    <thead>
                        <tr class="design-sortable-tr">
                            <th>渠道</th>
                            <th>名称</th>
                            <th>姓名</th>
                            <th>电话</th>
                            <th>邮件</th>
                            <th>地址</th>
                            <th>QQ</th>
                            <th width="20%">内容</th>
                            <th width="80px">留言时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if leaveMsgList?? && leaveMsgList.content??>
                            <#list leaveMsgList.content as leaveMsg>
                                <tr class="tdHasContorllor design-dropable-td">
                                    <td valign="top" class=" first_td_head" title="${leaveMsg.channel.snstype!''}">
                                        <p>
                                            <a href="javascript:void(0)">
                                                <#if leaveMsg.channel.snstype=="webim">
                                                    网站
                                                <#elseif leaveMsg.channel.snstype=="phone">
                                                    电话
                                                <#elseif leaveMsg.channel.snstype=="skype">
                                                    Skype
                                                <#else>
                                                    未知
                                                </#if>
                                            </a>
                                        </p>
                                    </td>
                                    <td title="${leaveMsg.channel!''}" valign="top">
                                        <p>${leaveMsg.channel.name!''}</p>
                                    </td>
                                    <td title="${leaveMsg.name!''}" valign="top">
                                        <p>${leaveMsg.name!''}</p>
                                    </td>
                                    <td title="${leaveMsg.mobile!''}" valign="top">
                                        <p>${leaveMsg.mobile!''}</p>
                                    </td>
                                    <td title="${leaveMsg.email!''}" valign="top">
                                        <p>${leaveMsg.email!''}</p>
                                    </td>
                                    <td title="${leaveMsg.address!''}" valign="top">
                                        <p>${leaveMsg.address!''}</p>
                                    </td>
                                    <td title="${leaveMsg.qq!''}" valign="top">
                                        <p>${leaveMsg.qq!''}</p>
                                    </td>
                                    <td title="${leaveMsg.content!''}" valign="top" title="${leaveMsg.content!''}">
                                        <p>${leaveMsg.content!''}</p>
                                    </td>
                                    <td valign="top">
                                        <#if leaveMsg.createtime??>${leaveMsg.createtime?string('yyyy-MM-dd HH:mm:ss')}
                                        </#if>
                                    </td>
                                    <td valign="top">
                                        <a href="/service/leavemsg/delete?id=${leaveMsg.id!''}" data-toggle="tip"
                                            title="删除留言不可恢复,请确认是否删除留言？">
                                            <i class="layui-icon">&#x1006;</i>删除
                                        </a>
                                    </td>
                                </tr>
                            </#list>
                            <#else>
                                <tr>
                                    <td colspan="8" style="height:400px;">
                                        <div class="ukefu-empty" style="background: none">
                                            <i class="layui-icon">&#xe63a;</i>
                                            <div style="">当前没有在线坐席</div>
                                        </div>
                                    </td>
                                </tr>
                        </#if>
                    </tbody>
                </table>

            </div>
        </div>
        <div class="row" style="padding:5px;">
            <div class="col-lg-12" id="page" style="text-align:center;"></div>
        </div>
    </div>
</div>

<style>
    .overflow td p {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
    }
</style>

<script>
    layui.use(['laypage', 'layer'], function () {
        var laypage = layui.laypage
            , layer = layui.layer;

        laypage.render({
            cont: 'page'
            , pages: <#if leaveMsgList ??> ${ leaveMsgList.totalPages } <#else> 0</#if> //总页数
				, curr:<#if leaveMsgList ??> ${ leaveMsgList.number + 1 }<#else> 0</#if>
				, groups: 5 //连续显示分页数
                , jump: function(data, first) {
                    if (!first) {
                        location.href = "/service/leavemsg/index?p=" + data.curr;
                    }
                }
    });
		});
</script>