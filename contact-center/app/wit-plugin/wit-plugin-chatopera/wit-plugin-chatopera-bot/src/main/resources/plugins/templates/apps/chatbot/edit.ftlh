<script src="/js/utils.js"></script>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<style>
    #create {
        margin: 10px;
        border-top: 1px solid #EEEEEE;
        padding-top: 5px;
    }

    #create input {
        width: 400px;
    }
</style>
<div class="row">
    <div class="col-lg-12">
        <div id="create">
            <form class="layui-form uk-form" style="width: 700px;margin: auto;">
                <input id="id" type="hidden" name="id" value="${id}">
                <div class="layui-form-item">
                    <label class="layui-form-label">网站渠道</label>
                    <div class="layui-input-inline">
                        <#if id!=null>
                            <input type="text" name="snsurl" required lay-verify="required" value="${snsurl}"
                                autocomplete="off" class="layui-input" disabled>
                            <#else>
                                <select id="snsid" name="snsid" lay-verify="required">
                                    <option></option>
                                </select>
                        </#if>
                    </div>
                    <div class="layui-form-mid layui-word-aux">智能机器人服务的渠道标识</div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">ClientId</label>
                    <div class="layui-input-inline">
                        <input type="text" name="clientId" required lay-verify="required" placeholder="请输入ClientId" autocomplete="off"
                            class="layui-input" value="${bot.clientId}">
                    </div>
                    <div class="layui-form-mid layui-word-aux">智能机器人ClientId，还没有？<a href="${botServiecProvider}/dashboard" target="_blank">现在去创建！</a></div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">Secret</label>
                    <div class="layui-input-inline">
                        <input type="password" name="secret" required lay-verify="required" placeholder="请输入Secret" autocomplete="off"
                               class="layui-input" value="${bot.secret}">
                    </div>
                    <div class="layui-form-mid layui-word-aux">智能机器人Secret</div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">工作模式</label>
                    <div class="layui-input-inline">
                        <select name="workmode" lay-verify="required">
                            <option <#if bot.workmode=="机器人客服优先">selected="selected"</#if>>机器人客服优先</option>
                            <option <#if bot.workmode=="人工客服优先">selected="selected"</#if>>人工客服优先</option>
                            <option <#if bot.workmode=="仅机器人客服">selected="selected"</#if>>仅机器人客服</option>
                        </select>
                    </div>
                    <div class="layui-form-mid layui-word-aux">来自访客的会话默认以什么方式接待</div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-inline">
                        <button class="layui-btn" lay-submit lay-filter="save">保存</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    // 保存成功，刷新页面
    function submitChatbotSucc(bot) {
        if(bot.rc != 0){
            submitChatbotFail(bot);
        } else {
            parent.location.href = "/apps/chatbot/index?chatbotid="+bot.data.id;
        }
    }

    // 保存失败
    function submitChatbotFail(err){
        layer.confirm(err.data||err.error, {
            btn: ['关闭'],
            icon: 2,
            title: '提示'
        }, function (popup, layero) {
            layer.close(popup)
        }, function (popup) {
            // 取消方法
        });
    }

    layui.use(['form'], function () {
        var form = layui.form();
        form.on('submit(save)', function (data) {
            var field = data.field;
            if (field.id) {
                field.ops = 'update';
                restApiRequest({
                    silent: true,
                    path: "chatbot",
                    data: field
                }).then(submitChatbotSucc, submitChatbotFail);
            } else {
                field.ops = 'create';
                restApiRequest({
                    path: "chatbot",
                    data: field
                }).then(submitChatbotSucc, submitChatbotFail);
            }
            return false;
        });

        if (!$('#id').val()) {
            restApiRequest({
                path: "chatbot",
                silent: true,
                data: {
                    ops: "vacant"
                }
            }).then(function (data) {
                if(data.rc == 0 &&  data.data.length > 0){
                    var options = $.map(data.data, function (r) {
                        return '<option value="' + r.snsid + '">' + r.snsurl + '</option>';
                    });

                    $('#snsid').html(options);
                    form.render('select');
                }
            }, function(error){
                console.log("error", error);
            })
        }
    })
</script>