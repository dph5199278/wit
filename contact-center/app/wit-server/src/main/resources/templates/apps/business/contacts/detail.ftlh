<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <#include "/apps/business/contacts/include/left.ftlh">
    </div>
</div>

<div class="layui-body">
    <div class="layui-side-scroll">
        <div class="box-header">
            <h1 class="site-h1" style="background-color:#FFFFFF;">
                <span class="ukefu-bt">
                <i class="layui-icon ukewo-btn" style="font-size:20px;text-align: center;">&#xe612;</i>
                联系人详情
                </span>
            </h1>
            <div class="row">
                <div class="col-lg-6" style="padding-right: 10px;">
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
                                            <label class="layui-form-label">笔记分类：</label>
                                            <div class="layui-input-inline">
                                                <select id="notesCategory" name="notesCategory" lay-filter="category" required lay-verify="required" style="display: inline">
                                                    <option value="webim">网页</option>
                                                    <option value="callout">外呼</option>
                                                    <option value="callin">呼入</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">内容：</label>
                                                <div class="layui-input-inline">
                                                    <textarea id="notesContent" name="notes" class="layui-textarea" ></textarea>
                                                </div>
                                            </div>
                                            <div class="layui-button-block">
                                                <button class="layui-btn" lay-submit lay-filter="notesbtn" id="notesAddBtn">立即添加</button>
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
                                    <h2 class="layui-colla-title">往来历史</h2>
                                    <div class="layui-colla-content layui-show">
                                        <div id="timeline" class="timeline-container" type="text" style="height: 617px; overflow-y: auto;">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="box-body ukefu-im-theme">
                        <div class="uk-layui-form">
                            <input hidden value="${contacts.id!''}" id="contactsId"/>
                            <input hidden value="${contacts.creater!''}" id="creater"/>
                            <div class="layui-collapse" id="basic">
                                <div class="layui-colla-item">
                                    <h2 class="layui-colla-title">基本信息</h2>
                                    <div class="layui-colla-content layui-show">
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">获得时间：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    <#if contacts.touchtime??>${contacts.touchtime?string('yyyy-MM-dd')}</#if>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">类型：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    ${uKeFuDic[contacts.ckind!''].name!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">共享联系人：</label>
                                                <div class="layui-input-inline" style="width:auto; line-height: 2.5em;">
                                                    <#if contacts?? && contacts.shares?? && contacts.shares == 'none'>
                                                        不共享（仅创建人和直属上级可见）
                                                    <#elseif contacts?? && contacts.shares?? && contacts.shares == 'all'>
                                                        所有人
                                                    </#if>
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
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    ${contacts.name!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">性别：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    <#if contacts.gender?? && contacts.gender == '1'>男</#if>
                                                    <#if contacts.gender?? && contacts.gender == '0'>女</#if>
                                                    <#if contacts.gender?? && contacts.gender == '-1'>未知</#if>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">生日：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    ${contacts.cusbirthday!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">联系电话：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    ${contacts.phone!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">手机号：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em;">
                                                    ${contacts.mobileno!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">skype账号：</label>
                                                <div class="layui-input-inline" style="line-height: 2.5em">
                                                    ${contacts.skypeid!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">城市：</label>
                                                <div class="layui-input-inline" style="width:80px;line-height: 2.5em;">
                                                    ${uKeFuDic[contacts.province!''].name!''}
                                                </div>
                                            </div>
                                            <div class="layui-inline">
                                                <div class="layui-input-inline" style="width:80px; line-height: 2.5em;" id="contacts_city">
                                                    ${uKeFuDic[contacts.city!''].name!''}
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">电子邮件：</label>
                                                <div class="layui-input-inline" style="margin-left:5px;line-height: 2.5em;">
                                                    ${contacts.email!''}
                                                </div>
                                            </div>
                                        </div>
                                        <#if contacts.wlusername?? || contacts.wluid??>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">用户名(id)：</label>
                                                <div class="layui-input-inline" style="margin-left:5px;line-height: 2.5em;">
                                                    ${contacts.wlusername!''}
                                                    <span>(${contacts.wluid!''})</span>
                                                </div>
                                            </div>
                                        </div>
                                        </#if>
                                        <#if contacts.wlcompany_name?? || contacts.wlcid??>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">公司名称(id)：</label>
                                                <div class="layui-input-inline" style="margin-left:5px;line-height: 2.5em;">
                                                    ${contacts.wlcompany_name!''}
                                                    <span>(${contacts.wlcid!''})</span>
                                                </div>
                                            </div>
                                        </div>
                                        </#if>
                                        <#if contacts.wlsystem_name?? || contacts.wlsid??>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">子系统名称(id)：</label>
                                                <div class="layui-input-inline" style="margin-left:5px;line-height: 2.5em;">
                                                    ${contacts.wlsystem_name!''}
                                                    <span>(${contacts.wlsid!''})</span>
                                                </div>
                                            </div>
                                        </div>
                                        </#if>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">联系人地址：</label>
                                                <div class="layui-input-inline" style="width: 159%;">
                                                    <input type="text" name="address" readonly value="${contacts.address!''}" class="layui-input" style="width: 425px;">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="layui-form-item">
                                            <div class="layui-inline">
                                                <label class="layui-form-label">联系人说明：</label>
                                                <div class="layui-input-inline" style="width: 149%;">
                                                    <textarea name="memo" class="layui-textarea" readonly style="width: 425px;">${contacts.memo!''}</textarea>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
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
<style>
    .card {
        /* Add shadows to create the "card" effect */
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
        transition: 0.3s;
        margin-top: 5px;
    }

    /* On mouse-over, add a deeper shadow */
    .card:hover {
        box-shadow: 0 8px 16px 0 rgba(0,0,0,0.2);
    }

    /* Add some padding inside the card container */
    .container {
        width: 100%;
        word-break: break-all;
    }

    #basic .layui-form-label{
        width: 115px;
    }
    #basic .layui-input-inline{
        width: 270px;
    }
</style>
<script>
    function getNotesByContactId () {
        // 获取数据
        var id = $('#contactsId').val();
        var payload = {
            path: 'contacts/notes',
            data: { ops: "fetch", contactid: id },
            silent: true
        };
        restApiRequest(payload).then(function (data) {
            // remove a click exchange and set color to gray
            // AUTH_ERROR
            if(data.status && data.status === "AUTH_ERROR"){
                handleRestApiFail(data.status);
                return;
            }

            if(data.rc === 0){
                if(data.totalElements > 0) {
                    for(var item of data.data){
                        $("#timeline").append('<div class="card">' +
                            '<div class="container">' +
                            '<h4>时间：' + item.updatetime + '</h4>' +
                            '<h5>笔记者：' + item.creatername + '</h5>' +
                            '<h5>事件类型：' + item.category + '</h5>' +
                            '<div>笔记：' + item.content + '</div>' +
                            '</div>' +
                            '</div>');
                    }
                }
            } else {
            }
        }, function (err) {
            console.log(err)
        });
    }

    // unselect tag
    function unOrselectTag(id) {
        var el = $("#tag_" + id);
        var xid = el.attr("name");

        if(xid){
            // unselected tag
            var payload = {
                path: 'contacts/tags',
                data: {
                    ops: "remove",
                    xid: xid,

                },
                silent: true
            };
            restApiRequest(payload).then(function (data) {
                if(data.status && data.status === "AUTH_ERROR"){
                    handleRestApiFail(data.status);
                    return;
                }

                if(data.rc === 0){
                    console.log(data.data);
                    el.removeClass("ukefu-label").addClass("unselectedClass").attr("name", "");
                } else {
                }
            }, function (err) {
                console.log(err)
            });
        } else  {
            // select tag
            var contactid = $('#contactsId').val();
            var payload = {
                path: 'contacts/tags',
                data: {
                    ops: "create",
                    contactid: contactid,
                    tagId: id,
                },
                silent: true
            };

            restApiRequest(payload).then(function (data) {
                if(data.status && data.status === "AUTH_ERROR"){
                    handleRestApiFail(data.status);
                    return;
                }

                if(data.rc === 0){
                    console.log(data.data);
                    el.removeClass("unselectedClass").addClass("ukefu-label").attr("name", data.data.id);
                } else {
                }
            }, function (err) {
                console.log(err)
            });
        }
    }

    // get tags
    function getTags(){
        var id = $('#contactsId').val();
        var payload = {
            path: 'contacts/tags',
            data: {
                ops: "fetch",
                contactid: id,
            },
            silent: true
        };
        restApiRequest(payload).then(function (data) {
            if(data.status && data.status === "AUTH_ERROR"){
                handleRestApiFail(data.status);
                return;
            }

            if(data.rc === 0){
                var tagsData = data.data;

                for (var item of tagsData){
                    if(item.tagged){
                        $("#contactTags").append(
                            '<small name="' + item.xid + '" id="tag_' + item.id + '" class="ukefu-label" style="margin: 0 6px 5px 0;cursor: pointer;" onclick="unOrselectTag(\'' + item.id + '\')">' + item.name + '</small>'
                        );
                    } else  {
                        $("#contactTags").append(
                            '<small name="" id="tag_' + item.id + '" class="unselectedClass" style="margin: 0 6px 5px 0; cursor: pointer;" onclick="unOrselectTag(\'' + item.id + '\')">' + item.name + '</small>'
                        );
                    }
                }
            } else {
            }
        }, function (err) {
            console.log(err)
        });
    }

    layui.use('layer', function() {
        layer = layui.layer;

        // get all notes by contact id.
        getNotesByContactId();

        // get tags
        getTags()

        // add notes
        $('#notesAddBtn').on('click', function(){
            var content = $('#notesContent').val() || '';
            if(!content) return;

            var id = $('#contactsId').val();
            var category = $("#notesCategory option:selected").text();

            var payload = {
                path: 'contacts/notes',
                data: {
                    ops: "create",
                    contactid: id,
                    category: category,
                    content: content,
                    agentuser: '',
                    onlineuser: ''
                },
                silent: true
            };
            restApiRequest(payload).then(function (data) {
                if(data.status && data.status === "AUTH_ERROR"){
                    handleRestApiFail(data.status);
                    return;
                }

                if(data.rc === 0){
                    $('#notesContent').val('').change();
                    var item = data.data;
                    // get all notes by contact id.
                    $("#timeline").prepend('<div class="card">' +
                        '<div class="container">' +
                        '<h4><b>时间：' + item.updatetime + '</b></h4>' +
                        '<h5>笔记者：' + item.creatername + '</h5>' +
                        '<h5>事件类型：' + category + '</h5>' +
                        '<div>笔记：' + content + '</div>' +
                        '</div>' +
                        '</div>');
                } else {
                }
            }, function (err) {
                console.log(err)
            });
        });
    });
</script>
