<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <#include "/apps/business/customer/include/left.ftlh">
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
                        全部客户
                      </div>
                  </span>
                    <div class="ukefu-bt-text-content"
                         style="position: absolute;right: 5px;top: 0px;padding-right:10px;">
                        <form class="layui-form" name="search" action="/apps/customer/${subtype!''}" method="get"
                              style="float: left;">
                            <#if ekind??><input type="hidden" name="ekind" value="${ekind!''}"></#if>
                        <div class="layui-form-item">
                            <div class="layui-inline">
                                <div class="layui-input-inline" style="width: 218px">
                                    <input type="text" name="q" value="${q!''}"
                                           style="height: 30px;line-height: 30px;margin-top: 4px;border: 1px solid #32c24d;"
                                           placeholder="请输入联系人姓名或电话" autocomplete="off" class="layui-input">
                                    <i class="layui-icon" onclick="search.submit()"
                                       style="position: absolute;cursor: pointer;right: 3px;top: 4px;font-size: 20px;color:#32c24d;">&#xe615;</i>
                                </div>
                            </div>
                        </div>
                        </form>
                        <#if user?? && (user.roleAuthMap["A03_A01_A02_B06"]?? || user.admin) >
                        <div class="layui-btn-group ukefu-btn-group">
                            <a href="/apps/customer/add<#if ekind??>?ekind=${ekind}</#if>" title="新建客户"
                               data-toggle="ajax" data-width="950" data-height="600" class="layui-btn layui-btn-sm">
                                <i class="layui-icon">&#xe608;</i>
                                新建客户
                            </a>
                        </div>
                    </#if>
                    <#if user?? && (user.roleAuthMap["A03_A01_A02_B09"]?? || user.admin) >
                    <div class="layui-btn-group ukefu-btn-group">
                        <button class="layui-btn layui-btn-sm"
                                href="/apps/customer/imp<#if ekind??>?ekind=${ekind}</#if>" title="导入客户"
                                data-toggle="ajax" data-width="950" data-height="600">
                            <i class="kfont">&#xe641;</i> 导入
                        </button>
                    </div>
                </#if>


                <#if user?? && (user.roleAuthMap["A03_A01_A02_B010"]?? || user.admin) >
                <div class="layui-btn-group ukefu-btn-group">
                    <button class="layui-btn layui-btn-sm dropdown-menu">
                        <i class="kfont">&#xe672;</i> 导出
                        <i class="layui-icon">&#xe625;</i>
                    </button>
                    <ul class="ukefu-dropdown-menu layui-anim layui-anim-upbit">
                        <li>
                            <a href="javascript:void(0)" id="batexp" title="导出客户" target="_blank">
                                <i class="kfont">&#xe672;</i>
                                导出选中结果
                            </a>
                        </li>
                        <li>
                            <a href="/apps/customer/expsearch?1=1<#if q??>&q=${q!''}</#if><#if ekind??>&ekind=${ekind}</#if>&subtype=${subtype}"
                               title="导出客户" target="_blank">
                                <i class="kfont">&#xe672;</i>
                                导出当前搜索结果
                            </a>
                        </li>
                        <li>
                            <a href="/apps/customer/expall<#if ekind??>?ekind=${ekind}</#if>" title="导出客户"
                               target="_blank">
                                <i class="kfont">&#xe672;</i>
                                导出全部数据
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
            <#include "/apps/business/customer/top.ftlh">
        </div>
        <table class="layui-table" lay-skin="line" style="margin-top: 26px">
            <thead>
            <tr class="design-sortable-tr">
                <th>
                    <input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
                </th>
                <th>客户名称</th>
                <th>类型</th>
                <th>所在城市</th>
                <th>客户状态</th>
                <th>行业</th>
                <th>成熟度</th>
                <th>获得时间</th>
                <th>所有者</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody style="table-layout: fixed; word-break: break-all;">
            <#if entCustomerList?? && entCustomerList.content?? && entCustomerList.content?size gt 0>
            <#list entCustomerList.content as entCustomer>
            <tr class="tdHasContorllor design-dropable-td">
                <td style="width:1%;">
                    <input type="checkbox" class="ids" name="ids" value="${entCustomer.id!''}"/>
                </td>
                <td class="first_td_head" style="width: 90px">
                    ${entCustomer.name!''}
                </td>
                <td>
                    ${uKeFuDic[entCustomer.ekind!''].name!''}
                </td>
                <td>
                    <#if uKeFuDic[entCustomer.city!''].name != "市辖区" && uKeFuDic[entCustomer.city!''].name != "县">
                        ${uKeFuDic[entCustomer.city!''].name!'' }
                        <#else>
                        ${uKeFuDic[entCustomer.province!''].name!'' }
                    </#if>


                </td>
                <td>${uKeFuDic[entCustomer.validstatus!''].name!''}</td>
                <td>
                    ${uKeFuDic[entCustomer.industry!''].name!''}
                </td>
                <td>
                    ${uKeFuDic[entCustomer.maturity!''].name!''}
                </td>
                <td>
                    <#if entCustomer.touchtime??>
                    ${entCustomer.touchtime?string('yyyy-MM-dd')}
                </#if>
                </td>
                <td>
                    <#if entCustomer.user??>${entCustomer.user.username!''}
                </#if>
                </td>
                <td style="white-space:nowrap;width:1%;" nowrap="nowrap">
                    <#if user?? && (user.roleAuthMap["A03_A01_A02_B07"]?? || user.admin) >
                    <a href="/apps/customer/edit?id=${entCustomer.id!''}&ekind=${ekind!''}" data-toggle="ajax" data-width="950"
                       data-height="600" data-title="编辑用户信息">
                        <i class="layui-icon">&#xe642;</i>
                        编辑
                    </a>
                </#if>
                <#if user?? && (user.roleAuthMap["A03_A01_A02_B08"]?? || user.admin) >
                <a href="/apps/customer/delete?id=${entCustomer.id!''}&ekind=${ekind!''}" style="margin-left:10px;" data-toggle="tip"
                   data-title="请确认是否删除记录？">
                    <i class="layui-icon" style="color:red;">&#x1006;</i>
                    删除
                </a>
            </#if>
            </td>
            </tr>
            </#list>
            <#else>
            <tr>
                <td colspan="10" style="height:400px;">
                    <div class="ukefu-empty"  style="background: none">
                        <i class="layui-icon">&#xe63a;</i>
                        <div style="">还没有客户信息</div>
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
<script>
    $(document).ready(function(){
        layui.use('layer', function(){
            var layer = layui.layer;

        <#if msg?? && msg == 'new_entcustomer_success'>
            layer.msg('客户新建成功',{icon: 1, time: 1000})
        <#elseif msg?? && msg == 'edit_entcustomer_success'>
            layer.msg('客户编辑成功',{icon: 1, time: 1000})
         <#elseif msg?? && msg == 'mobile_exist'>
            layer.msg('手机存在，请重新填写',{icon: 2, time: 3000})
         <#elseif msg?? && msg == 'sip_account_exist'>
            layer.msg('SIP账号已经存在，请重新填写',{icon: 2, time: 3000})
        </#if>
        });


        $('#batexp').click(function(){
            var ids = "";
            var num = 0 ;
            $('.ids').each(function(){
                if($(this).prop("checked")){
                    if(ids!=""){
                        ids += "&" ;
                    }
                    ids += "ids=" + $(this).val();
                    num++;
                }
            });
            if(num>0){
                $('#batexp').attr('href' , '/apps/customer/expids?'+ids)	;
            }else{
                top.layer.alert("请先选择需要导出的客户信息");
                $('#batexp').attr("href" , "javascript:void(0)");
            }


        });

    });
    layui.use('form', function(){
        var form = layui.form;
    });
    layui.use(['laypage', 'layer'], function(){
        var laypage = layui.laypage
            ,layer = layui.layer;

        laypage.render({
            cont: 'page'
            ,pages: <#if entCustomerList??>${entCustomerList.totalPages}<#else>0</#if> //总页数
            ,curr:<#if entCustomerList??>${entCustomerList.number+1}<#else>0</#if>
            ,groups: 5 //连续显示分页数
            ,jump:function(data , first){
            if(!first){
                location.href = "/apps/customer/${subtype!'index'}?p="+data.curr + "&ekind=${ekind!''}";
            }
        }
    });
    });
</script>
