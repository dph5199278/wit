<script src="/js/utils.js"></script>
<script src="/js/CSKeFu_Rest_Request.v1.js"></script>
<h1 class="site-h1" style="border-top:1px solid #e6e6e6;">
    列表
</h1>

<#if chatbots??>
<ul class="layui-nav layui-nav-tree" lay-filter="chatbots-nav-tree">
    <li class="layui-nav-item layui-nav-itemed">
        <dl class="layui-nav-child">
            <#list chatbots as chatbot>
            <dd style="text-align: center;border-bottom:thin solid #F5F5F5; list-style: none;" <#if currentbotid?? && chatbot.id == currentbotid>class="layui-this"</#if>>
                <a href="/apps/chatbot/index?chatbotid=${chatbot.id}">${chatbot.name}</a>
            </dd>
            </#list>
        </dl>
    </li>
</ul>
<#else>
    <div class="ukefu-empty" style="background: none">
        <i class="layui-icon">&#xe63a;</i>
        <div style="">还没有集成智能机器人</div>
    </div>
</#if>

