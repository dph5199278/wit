<#list agentUserMessageList as chatmessage>
<div class="clearfix chat-block" name="${chatmessage.createtime}"
     id="<#if (chatmessage.id == agentusersearchdetails)>agentusersearchdetails<#else></#if>">
    <div class="<#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>chat-right<#else>chat-left</#if>">
        <img alt=""
             src="<#if chatmessage.userid?? && chatmessage.userid == user.id>/images/im/user.png<#else><#if curagentuser.headimgurl?? && curagentuser.headimgurl !=''>${curagentuser.headimgurl}<#else>/images/im/user.png</#if></#if>"
             style="width:45px;height:45px;" class="user-img">
        <div class="chat-message">
                        <span class="<#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>rateright<#else>rateleft</#if>">
                            <i class="layui-icon iconclick" id=${chatmessage.id}
                               name="<#if (chatmessage.islabel == true??)>yeslabe<#else>nolabe</#if>"
                               style="cursor:pointer;font-size: 30px; color:<#if (chatmessage.islabel == true??)>#46cad4;<#else>#aaaaaa;</#if>;"><#if (chatmessage.islabel == true??)>&#xe616;<#else>&#x1005;</#if></i>
                        </span>
            <span class="user">
                    <#if chatmessage?? && chatmessage.calltype?? && chatmessage.calltype == '呼出'>
                        ${chatmessage.intervented?string(chatmessage.supervisorname,curagentuser.agentname)}
                    <#else>
                        ${curagentuser.username!''}
                    </#if>
            </span>
            <span class="time">${chatmessage.createtime?string('yyyy-MM-dd HH:mm:ss')}</span>
            <span class="<#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>rateleft<#else>rateright</#if>">
                            <i class="layui-icon iconclick" id=${chatmessage.id}
                               name="<#if (chatmessage.islabel == true??)>yeslabe<#else>nolabe</#if>"
                               style="cursor:pointer;font-size: 30px; color:<#if (chatmessage.islabel == true??)>#46cad4;<#else>#aaaaaa;</#if>;"><#if (chatmessage.islabel == true??)>&#xe616;<#else>&#x1005;</#if></i>
                        </span>
        </div>
        <div class="<#if (chatmessage.calltype?? && chatmessage.calltype == '呼出')>chatting-right<#else>chatting-left</#if>">
            <i class="userarrow"></i>
            <div class="chat-content"><#include "/apps/agent/media/message.ftlh"></div>
        </div>
    </div>
    <#if agentUserMessageListnum??>
    <input id="totalElements" type="hidden" value="${agentUserMessageListnum}">
</#if>

</div>
</#list>
<style>
    .chat-left .chat-message .rateright {
        display: inline-block;
    }

    .chat-left .chat-message .rateleft {
        display: none;
    }

    .chat-right .chat-message .rateright {
        display: inline-block;
    }

    .chat-right .chat-message .rateleft {
        display: none;
    }
</style>
<script>
    $('.iconclick').click(function () {
        if ($(this).attr('name') == 'nolabe') {
            $(this).html('&#xe616;')
            $(this).css('color', '#46cad4')
            $(this).attr('name', 'yeslabe')
        } else {
            $(this).html('&#x1005;')
            $(this).css('color', '#aaaaaa')
            $(this).attr('name', 'nolabe')
        }
        $.ajax({
            url: '/agent/agentuserLabel',
            data: {'iconid': $(this).attr('id')},
            type: "get",
            success: function () {
            }
        });

    });
</script>