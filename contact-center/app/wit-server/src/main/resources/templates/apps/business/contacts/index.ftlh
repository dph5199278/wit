<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <#include "/apps/business/contacts/include/left.ftlh">
    </div>
</div>

<div class="layui-body">
    <div class="layui-side-scroll">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="site-h1 ukefu-tab" style="border-top:1px solid #e6e6e6;height:26px;">
                    <span class="ukefu-bt">
                      <i class="layui-icon ukewo-btn" style="font-size:20px;"></i>
                      <div class="ukefu-bt-text">
                        <div class="ukefu-bt-text-title">
                          全部联系人
                        </div>
                    </span>
                    <div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
                        <form class="layui-form" name="search" action="/apps/contacts/${subtype!''}" method="get" style="float: left;">
<!--                           <input type="text" id="selflocation">-->
                            <#if ckind??><input type="hidden" name="ckind" value="${ckind!''}"></#if>
                            <#if ckind??><input type="hidden" name="saveStatus" value="${saveStatus!''}"></#if>

                            <div class="layui-form-item">
                                <div class="layui-inline">
                                    <div class="layui-input-inline" style="width: 218px;">
                                        <input type="text" id="q" name="q" value="${q!''}" style="height: 30px;line-height: 30px;margin-top: 4px;border: 1px solid #32c24d;" placeholder="请输入联系人姓名或电话" autocomplete="off" class="layui-input">
                                        <i class="layui-icon" onclick="search.submit()" style="position: absolute;cursor: pointer;right: 3px;top: 4px;font-size: 20px;color:#32c24d;">&#xe615;</i>
                                    </div>
                                </div>
                            </div>
                        </form>
                        <#if user?? && (user.roleAuthMap[ "A02_A01_A02_B05"]?? || user.admin)>
                            <div class="layui-btn-group">
                                <a href="/apps/contacts/add<#if ckind??>?ckind=${ckind}</#if>" title="新建联系人" data-toggle="ajax" data-width="950" data-height="600" class="layui-btn layui-btn-sm"><i class="layui-icon">&#xe608;</i>新建联系人</a>
                            </div>
                        </#if>
                        <#if user?? && (user.roleAuthMap[ "A02_A01_A02_B08"]?? || user.admin)>
                            <div class="layui-btn-group ukefu-btn-group">
                                <button class="layui-btn layui-btn-sm" href="/apps/contacts/imp<#if ckind??>?ckind=${ckind}</#if>" title="导入联系人" data-toggle="ajax" data-width="950" data-height="600">
								    <i class="kfont">&#xe641;</i> 导入
							    </button>
                            </div>
                        </#if>
                        <#if user?? && (user.roleAuthMap[ "A02_A01_A02_B09"]?? || user.admin)>
                            <div class="layui-btn-group ukefu-btn-group">
                                <button class="layui-btn layui-btn-sm dropdown-menu">
                                    <i class="kfont">&#xe672;</i> 导出
                                    <i class="layui-icon">&#xe625;</i>
							    </button>
                                <ul class="ukefu-dropdown-menu layui-anim layui-anim-upbit">
                                    <li>
                                        <a href="javascript:void(0)" id="batexp" title="导出联系人" target="_blank">
                                            <i class="kfont">&#xe672;</i> 导出选中结果
                                        </a>
                                    </li>
                                    <li>
                                        <a href="/apps/contacts/expsearch?1=1<#if q??>&q=${q!''}</#if><#if ckind??>&ckind=${ckind}</#if>&subtype=${subtype}" title="导出联系人" target="_blank">
                                            <i class="kfont">&#xe672;</i> 导出当前搜索结果
                                        </a>
                                    </li>
                                    <li>
                                        <a href="/apps/contacts/expall<#if ckind??>?ckind=${ckind}</#if>" title="导出联系人" target="_blank">
                                            <i class="kfont">&#xe672;</i> 导出全部数据
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </#if>
                    </div>
                </h1>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-12">

                <div class="ukefu-tab-title" style="margin-top: 2px;margin-left: 2px">
                    <#include "/apps/business/contacts/top.ftlh">
                </div>
                <table class="layui-table" lay-skin="line" style="table-layout: fixed">
                    <thead>
                        <tr class="design-sortable-tr">
                            <th width="10px">
                                <input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));" />
                            </th>
                            <th>联系人姓名</th>
                            <th>手机</th>
                            <th>邮件</th>
                            <th width="30px">性别</th>
                            <th>生日</th>
                            <th>类型</th>
                            <th>所有者</th>
                            <th width="120px">操作</th>
                        </tr>
                    </thead>
                    <tbody style="table-layout: fixed; word-break: break-all;">
                        <#if contactsList?? && contactsList.content?? && contactsList.content?size gt 0>
                            <#list contactsList.content as contacts>
                                <tr class="tdHasContorllor design-dropable-td">
                                    <td style="width:1%;">
                                        <input type="checkbox" class="ids" name="ids" value="${contacts.id!''}" />
                                    </td>
                                    <td title="${contacts.name!''}">
                                        <p>
                                            <a href="/apps/contacts/detail?id=${contacts.id!''}">${contacts.name!''}</a>
                                        </p>
                                    </td>
                                    <td>
                                        <span class="ukefu-phone-number">
								${contacts.mobileno!''}
								</span>
                                    </td>
                                    <td title="${contacts.email!''}">
                                        <p>
                                            ${contacts.email!''}
                                        </p>
                                    </td>
                                    <td>
                                        <#if contacts.gender?? && contacts.gender=='1'>男</#if>
                                        <#if contacts.gender?? && contacts.gender=='0'>女</#if>
                                        <#if contacts.gender?? && contacts.gender=='-1'>未知</#if>
                                    </td>
                                    <td>
                                        ${contacts.cusbirthday!''}
                                    </td>
                                    <td title="${uKeFuDic[contacts.ckind!''].name!''}">
                                        <p>
                                            ${uKeFuDic[contacts.ckind!''].name!''}
                                        </p>
                                    </td>
                                    <td title="<#if contacts.user??>${contacts.user.username!''}</#if>">
                                        <p>
                                            <#if contacts.user??>${contacts.user.username!''}</#if>
                                        </p>
                                    </td>
                                    <td>
                                        <#if approachable?? && approachable?seq_contains(contacts.id)>
                                        <a href="#" onclick="openDialogWinByContactid('${contacts.id}')">
                                            <i class="layui-icon">&#xe606;</i> 聊天
                                        </a>
                                        <#else>
                                        <a href="#" onclick="unreachableDialogWinByContactid('${contacts.id}')" class="disabled">
                                            <i class="layui-icon">&#xe60f;</i> 聊天
                                        </a>
                                        </#if>

                                        <a href="/apps/contacts/detail?id=${contacts.id!''}" style="margin-left:10px;">
                                            <i class="layui-icon">&#xe60a;</i> 详情
                                        </a>
                                        <#if user?? && (user.roleAuthMap[ "A02_A01_A02_B06"]?? || user.admin)>
                                            <a href="/apps/contacts/edit?id=${contacts.id!''}&ckind=${ckind!''}" data-toggle="ajax" data-width="950" data-height="600" data-title="编辑联系人信息">
                                                <i class="layui-icon">&#xe642;</i> 编辑
                                            </a>
                                        </#if>
                                        <#if user?? && (user.roleAuthMap[ "A02_A01_A02_B07"]?? || user.admin)>
                                            <a href="/apps/contacts/delete?id=${contacts.id!''}&ckind=${ckind!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除联系人？">
                                                <i class="layui-icon" style="color:red;">&#x1006;</i> 删除
                                            </a>
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                            <#else>
                                <tr>
                                    <td colspan="9" style="height:400px;">
                                        <div class="ukefu-empty"  style="background: none">
                                            <i class="layui-icon">&#xe63a;</i>
                                            <div style="">还没有联系人信息</div>
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

<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<script>
    $(document).ready(function() {
        $('#batexp').click(function() {
            var ids = "";
            var num = 0;
            $('.ids').each(function() {
                if ($(this).prop("checked")) {
                    if (ids != "") {
                        ids += "&";
                    }
                    ids += "ids=" + $(this).val();
                    num++;
                }
            });
            if (num > 0) {
                $('#batexp').attr('href', '/apps/contacts/expids?' + ids);
            } else {
                top.layer.alert("请先选择需要导出的联系人信息");
                $('#batexp').attr("href", "javascript:void(0)");
            }
        });
    });
    layui.use(['laypage', 'layer'], function() {
        var laypage = layui.laypage,
            layer = layui.layer;
        laypage.render({
            cont: 'page'
            ,pages: <#if contactsList??>${contactsList.totalPages}<#else>0</#if>    //总页数
            ,curr:<#if contactsList??>${contactsList.number+1}<#else>0</#if>
            ,groups : 5 //连续显示分页数
            ,jump : function(data, first) {
                if (!first) {
                    location.href = "/apps/contacts/${subtype!'index'}?ckind=${ckind!''}&p=" + data.curr + "&q=" + $("#q").val();
                }
            }
        });

        layui.use('layer', function(){
            var layer = layui.layer;
        <#if msg?? && msg == 'new_contacts_success'>
            layer.msg('联系人添加成功',{icon: 1, time: 1500})
        <#elseif msg?? && msg == 'new_contacts_fail'>
            layer.msg('联系人添加失败，因为存在相同skypeid',{icon: 2, time: 1500})
        <#elseif msg?? && msg == 'edit_contacts_success'>
            layer.msg('联系人编辑成功',{icon: 1, time: 1500})
        <#elseif msg?? && msg == 'edit_contacts_fail'>
            layer.msg('联系人编辑失败，因为存在相同Skype ID',{icon: 2, time: 1500})
        </#if>
        });
    });

    function unreachableDialogWinByContactid(id){
        layer.msg('该联系人正在被其它客服服务或没有可触达的联系方式！',{icon: 2, time: 3000})
    }

    function openDialogWinByContactid(id){
       var multiMediaDialogWin = parent.$('#multiMediaDialogWin');
        restApiRequest({
            path: 'contacts',
            data: {
                ops: "approach",
                contactsid: id
            },
            silent: true
        }).then(function (data) {
            if (data.rc == 0){
                // 返回可用的触达渠道列表 data.data
                if(multiMediaDialogWin.length > 0){
                    multiMediaDialogWin.load(function(){
                        parent.layui.element.tabChange('ukefutab','multiMediaDialogWin');
                    }).attr('src' , '/agent/proactive?contactid='+id+'&channels='+data.data);
                } else {
                    // 使用API调用，由SocketIO传达弹屏通知，跳转到该访客页面
                    restApiRequest({
                        path: 'contacts',
                        silent: true,
                        data: {
                            ops: 'proactive',
                            channels: data.data,
                            contactid: id
                        }
                    }).then(function(result){
                        if(result.rc == 0){
                            // 操作成功，等待弹屏
                        } else {
                            // 操作未成功，进行提示
                            top.layer.msg('未能成功发起会话，请稍后再试！',{icon: 2, time: 3000})
                        }
                    }, handleRestApiFail);
                }
            } else if(data.status) {
                handleRestApiFail(data.status, data.data);
            }else {
                layer.msg('该联系人没有触达的方式',{icon: 2,  time: 1500})
            }
        });
    }
</script>
<style>
    .design-sortable-tr{
        text-align: center;
    }
    .design-sortable-tr th {
        /*设置表格文字左右和上下居中对齐*/
        vertical-align: middle;
        text-align: left;
        /*设置表格文字在一行显示*/
        text-overflow:ellipsis;
        word-break:keep-all;
        white-space:nowrap;
    }
    .design-dropable-td td p{
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
    }
</style>
