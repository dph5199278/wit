<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <#include "/apps/chatbot/include/left">
    </div>
</div>

<div class="layui-body">
    <#if currentbotid??>
    <div class="row">
        <div class="col-lg-12">
            <h1 class="site-h1 ukefu-tab" style="border-top:1px solid #e6e6e6;height:26px;">
                <span class="ukefu-bt">
                    <div class="ukefu-bt-text">
                        <div class="ukefu-bt-text-title">
                            智能机器人
                        </div>
                    </div>
                </span>
                <!-- 按钮组件 -->
                <div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
                    <div class="layui-btn-group ukefu-btn-group">
                        <button class="layui-btn layui-btn-small" onclick="showChatbotEditDialogue('${currentbotid}')">
                            <i class="layui-icon">&#xe643;</i> 绑定
                        </button>
                        <button class="layui-btn layui-btn-small" onclick="openChatbotBotPlatform('${currentbot.clientId}')">
                            <i class="layui-icon">&#xe631;</i> 配置
                        </button>
                        <button class="layui-btn layui-btn-small" onclick="showChatbotCreateDialogue()">
                            <i class="layui-icon">&#xe654;</i> 新建
                        </button>
                        <button class="layui-btn layui-btn-danger layui-btn-small" onclick="showChatbotDeleteDialogue()">
                            <i class="layui-icon">&#xe640;</i> 删除
                        </button>
                        <button class="layui-btn layui-btn-warm layui-btn-small" onclick="openChatbotIntegrationInfoCenter()">
                            <i class="layui-icon">&#xe60a;</i> 文档中心
                        </button>
                    </div>
                </div>
            </h1>
        </div>
    </div>

    <!-- 提示 -->
    <div class="row" style="padding-left:5px;">
        <div class="row">
            <blockquote class="layui-elem-quote">
                <p>智能机器人用于在访客端实现机器人客服，具体使用参考文档中心<a href="https://docs.chatopera.com/products/cskefu/work-chatbot" target="_blank">《智文客服机器人客服》</a>。
                </p>
            </blockquote>
        </div>
    </div>

    <!-- 工作状态 -->
    <div class="row" style="padding:5px;">
        <div class="col-lg-12">
            <fieldset class="layui-elem-field layui-field-title">
                <legend>智能客服</legend>
                <div class="layui-field-box">
                    <blockquote class="layui-elem-quote layui-quote-nm">
                        <p>在开启状态下，在访客端可以看到<b>智能客服</b>对话窗口并与机器人客服对话。</p>
                    </blockquote>
                    <form class="layui-form" action="">
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <input type="checkbox" lay-filter="bot-workstatus" name="switch" lay-skin="switch" lay-text="启用|禁用" <#if currentbot.enabled>checked</#if>>
                            </div>
                        </div>
                    </form>
                </div>
                <legend>知识库建议</legend>
                <div class="layui-field-box">
                    <blockquote class="layui-elem-quote layui-quote-nm">
                        <p>知识库建议包括<b>知识库联想</b>和<b>知识库快捷</b>。<b>知识库联想</b>：当坐席与访客进行对话过程中，坐席输入消息时是否得到机器人的建议回复；<b>知识库快捷</b>：当坐席与访客进行对话过程中，坐席收到访客的消息时是否得到机器人的建议回复。</p>
                    </blockquote>
                    <form class="layui-form" action="">
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <input type="checkbox" lay-filter="bot-aisuggest" name="bot-aisuggest" lay-skin="switch" lay-text="启用|禁用" <#if currentbot.aisuggest>checked</#if>>
                            </div>
                        </div>
                    </form>
                </div>
            </fieldset>
        </div>
    </div>

    <!-- 绑定 -->
    <div class="row" style="padding-left:5px;">
        <div class="col-lg-12">
            <fieldset class="layui-elem-field layui-field-title">
                <legend>绑定</legend>
                <div class="layui-field-box">
                    <blockquote class="layui-elem-quote layui-quote-nm">
                        <p>以下信息为智文客服使用该智能机器人集成的信息，去<a href="javascript:void(0)" onclick="showChatbotEditDialogue('${currentbotid}')">设置</a>。</p>
                    </blockquote>
                    <table class="layui-table" lay-even lay-skin="nob">
                        <colgroup>
                            <col width="150">
                            <col width="200">
                            <col width="400">
                        </colgroup>
                        <tbody>
                        <tr>
                            <td>渠道</td>
                            <td>
                                <#if currentbot.channel == "webim">
                                网站
                                <#elseif currentbot.channel == "skype">
                                Skype
                                <#elseif currentbot.channel == "callout">
                                外呼
                                <#else>
                                未知渠道类型
                            </#if>
                            </td>
                            <td><p style="color: #9C9C9C">访客接入的方式</p></td>
                        </tr>
                        <tr>
                            <td>渠道名称</td>
                            <td>${snsAccountName!""}</td>
                            <td><p style="color: #9C9C9C">限定了智能机器人出现的渠道标识。</p></td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>${creatorname!"未知"}</td>
                            <td><p style="color: #9C9C9C">创建人初次设定了智能机器人。</p></td>
                        </tr>
                        <tr>
                            <td>工作模式</td>
                            <td>
                                ${currentbot.workmode}
                            </td>
                            <td><p style="color: #9C9C9C">工作模式有"机器人优先"，"人工坐席优先"和"仅机器人客服"三种，这决定了访客连线后默认对接的方式。</p></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </fieldset>
        </div>
    </div>

    <!-- 配置 -->
    <div class="row" style="padding:5px;">
        <div class="col-lg-12">
            <fieldset class="layui-elem-field layui-field-title">
                <legend>配置</legend>
                <div class="layui-field-box">
                    <blockquote class="layui-elem-quote layui-quote-nm">
                        <p><a href="javascript:void(0)" onclick="openChatbotBotPlatform('${currentbot.clientId}')">进入Chatopera云服务</a>设置以下信息及知识库、多轮对话、意图识别、使用情况分析和对话历史等信息。</p>
                    </blockquote>
                    <table class="layui-table" lay-even lay-skin="nob">
                    <colgroup>
                        <col width="150">
                        <col width="200">
                        <col width="300">
                    </colgroup>
                    <tbody>
                        <tr>
                            <td>描述</td>
                            <td>${currentbot.description}</td>
                            <td><p style="color: #9C9C9C">智能机器人的描述，侧重于业务，访客不会看到该信息。</p></td>
                        </tr>
                        <tr>
                            <td>欢迎语</td>
                            <td>${currentbot.welcome}</td>
                            <td><p style="color: #9C9C9C">与访客建立连接后，智能机器人发送的打招呼信息。</p></td>
                        </tr>
                        <tr>
                            <td>兜底回复</td>
                            <td>${currentbot.fallback}</td>
                            <td><p style="color: #9C9C9C">当智能机器人不清楚如何回复时的回复。</p></td>
                        </tr>
                    </tbody>
                </table>
                </div>
            </fieldset>
        </div>
    </div>
    <#else>
    <div class="row">
        <div class="col-lg-12">
            <h1 class="site-h1 ukefu-tab" style="border-top:1px solid #e6e6e6;height:26px;">
                <span class="ukefu-bt">
                    <div class="ukefu-bt-text">
                        <div class="ukefu-bt-text-title">
                            智能机器人
                        </div>
                    </div>
                </span>
                <!-- 按钮组件 -->
                <div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
                    <div class="layui-btn-group ukefu-btn-group">
                        <button class="layui-btn layui-btn-small" onclick="showChatbotCreateDialogue()">
                            <i class="layui-icon">&#xe654;</i> 新建
                        </button>
                        <button class="layui-btn layui-btn-small" onclick="openChatbotBotPlatform()">
                            <i class="csfont">&#xe602;</i> 登录Chatopera云服务
                        </button>
                        <button class="layui-btn layui-btn-warm layui-btn-small" onclick="openChatbotIntegrationInfoCenter()">
                            <i class="layui-icon">&#xe60a;</i> 文档中心
                        </button>
                    </div>
                </div>
            </h1>
        </div>
    </div>
    <div class="row" style="padding:5px;">
        <div class="col-lg-12">
            还没有智能机器人，<a href="javascript:void(0)" onclick="showChatbotCreateDialogue()">现在去创建！</a>
        </div>
    </div>
    </#if>
</div>
<script>

    // 打开文档中心
    function openChatbotIntegrationInfoCenter(){
        window.open("https://docs.chatopera.com/products/cskefu/work-chatbot", "_blank");
    }

    // 展示编辑机器人的窗口
    function showChatbotEditDialogue(chatbotid) {
        console.log("showChatbotEditDialogue", chatbotid);
        layer.open({
            title: '编辑智能机器人',
            type: 2,
            area: ['800px', '450px'],
            content: 'edit?id=' + chatbotid
        })
    }
    
    // 展示创建机器人的对话框
    function showChatbotCreateDialogue() {
        layer.open({
            title: '集成智能机器人',
            type: 2,
            area: ['800px', '450px'],
            content: 'edit'
        })
    }

    // 删除机器人
    function showChatbotDeleteDialogue() {
        var lindex = layer.confirm('请确认是否删除？', {
            btn: ['确认', '删除']
        }, function () {
            return restApiRequest({
                path: "chatbot",
                data: {
                    ops: 'delete',
                    id: '${currentbotid}'
                }
            }).then(function (result) {
                // 刷新页面
                layer.close(lindex);
                setTimeout(function () {
                    location.href = "/apps/chatbot/index";
                }, 300);
            }, function (reason) {
                // TODO 提示错误
                console.log("[ChatbotDelete] error: ", reason);
                layer.close(lindex);
            });
        }, function () {
            layer.close(lindex);
        });

    }
    
    // 打开机器人管理地址
    function openChatbotBotPlatform(chatbotid){
        var botMgrUrl = "${botServiecProvider}";
        if(chatbotid){
            botMgrUrl = "${botServiecProvider}/dashboard/clients/"+chatbotid+"/control-center";
        }

        window.open(botMgrUrl, "_blank");
    }

    // 设置机器人工作状态
    function setChatbotWorkstatus(id, workstatus){
        restApiRequest({
            path: "chatbot",
            silent: true,
            data: {
                ops: workstatus?"enable":"disable",
                id: id
            }
        });
    }

    function setChatbotAiSuggest(id, aisuggest){
        restApiRequest({
            path: "chatbot",
            silent: true,
            data: {
                ops: aisuggest?"enableAiSuggest":"disableAiSuggest",
                id: id
            }
        });
    }

    layui.use(['form'], function(){
        var form = layui.form();
        form.render();

        form.on('switch(bot-workstatus)', function (data) {
            setChatbotWorkstatus('${currentbotid}', data.elem.checked);
        });

        form.on('switch(bot-aisuggest)', function (data) {
            setChatbotAiSuggest('${currentbotid}', data.elem.checked);
        });
    });
</script>