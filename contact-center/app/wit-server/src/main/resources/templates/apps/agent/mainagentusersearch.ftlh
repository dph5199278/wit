<#list agentUserMessageList.content?reverse as chatmessage>
<div class="clearfix chatblock" name="${chatmessage.createtime?string('yyyy-MM-dd HH:mm:ss')}" id="${chatmessage.id}">
    <div class="chat-left">
        <!--        <img alt=""-->
        <!--             src="<#if chatmessage.userid?? && chatmessage.userid == user.id>/images/im/user.png<#else><#if curagentuser.headimgurl?? && curagentuser.headimgurl !=''>${curagentuser.headimgurl}<#else>/images/im/user.png</#if></#if>"-->
        <!--             style="width:25px;height:25px;" class="user-img">-->
        <div class="chat-message">
            <span class="user <#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>ratemy<#else>rateyou</#if>">
                 <#if chatmessage?? && chatmessage.calltype?? && chatmessage.calltype == '呼出'>
                    ${chatmessage.intervented?string(chatmessage.supervisorname,chatmessage.username)}
                <#else>
                    ${chatmessage.username!''}
                </#if>
            </span>
            <span class="time <#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>ratemy<#else>rateyou</#if>">${chatmessage.createtime?string('yyyy-MM-dd HH:mm:ss')}</span>
            <span class="rateright">
                            <i class="layui-icon iconclick" id=${chatmessage.id}
                               name="<#if (chatmessage.islabel == true??)>yeslabe<#else>nolabe</#if>"
                               style="font-size: 15px; color:<#if (chatmessage.islabel == true??)>#46cad4;<#else>#aaaaaa;</#if>;"><#if (chatmessage.islabel == true??)>&#xe616;<#else>&#x1005;</#if></i>
                        </span>
        </div>
        <div class="chatting">
            <!--            <i class="userarrow"></i>-->
            <div class="chat-content"><#include "/apps/agent/media/message.ftlh"></div>
        </div>
    </div>
</div>
</#list>
<#if agentUserMessageList.totalElements??>
<input id="totalSearches" type="hidden" value="${agentUserMessageList.totalElements}">
</#if>
<style>
    .chatblock{
        padding: 10px 0;
    }
    .chatblock:hover{
        background: #f5f7fa;
    }
    .chat-left .chat-message {
        margin-left: 10px !important;
    }
    .chatting{
        padding-right: 5px;
        padding-left: 10px;
        float: left;
        min-width: 25px;
        word-break: break-all;
        border: none;
        margin-top: 5px;
    }
    .chatting .chat-content{
        color: #000;
        font-size: 20px;
        width: 280px;
        overflow: hidden;
    }
    .chat-content img{
        border: 1px solid #ccc;
    }
    .ratemy {
        color: #7e8790 !important;
        font-size: 18px;
    }
    .rateyou {
        color: #258dee !important;
        font-size: 18px;
    }
</style>