<#include "/public/macro.ftlh">
<div class="uk-layui-form">
            <div style="margin: 30px 30px 30px 30px;">
                <ul>
                    <li style="float: left;margin-right: 20px;">
                        <input name="AdPrintMode" id="textRadio" title="text" type="radio" checked onclick="divSelect(this)" />
                        <label for="textRadio">文字</label>
                    </li>
                    <li style="float: left;margin-right: 20px;">
                        <input name="AdPrintMode" id="picRadio" title="pic" type="radio" onclick="divSelect(this)" />
                        <label for="picRadio">图片</label>
                    </li>
                    <li style="margin-right: 20px;">
                        <input name="AdPrintMode" id="fileRadio" title="file" type="radio" onclick="divSelect(this)" />
                        <label for="fileRadio">文件</label>
                    </li>
                </ul>

            </div>
    <form class="layui-form" id="massForm" action="/apps/contacts/mass" method="post" enctype="multipart/form-data">
        <input type="text" name="ids" value="${ids}" style="display: none;">

        <div style="margin: 30px 30px 30px 30px;height: 209px">

            <div class="form_row" id="textSelect" style="height: 100%">
                <div class="layui-input-inline" style="width: 100%;height: 100%">
                    <textarea name="massMessageToOnlineUserText" placeholder="请输入内容" class="layui-textarea"
                                          style="height: 100%;resize: none" lay-verify="massMessageToOnlineUserText"></textarea>
                </div>
            </div>

            <div class="form_row" id="picSelect" style="display: none">
                <input type="file" name="massMessageToOnlineUserPic" accept="image/*" onchange="previewFile()" lay-verify="massMessageToOnlineUserPic"><br>
                <img id="selectImg" title="show" src="" height="200" style="max-width: 640px;margin-top: 10px;" alt="Image preview...">
            </div>

            <div class="form_row" id="fileSelect" style="display: none">
                <input type="file" name="massMessageToOnlineUserFile" lay-verify="massMessageToOnlineUserFile">
            </div>
            <div class="ukefu-imp-tips" style="position: absolute;bottom: 95px;width: 628px;">
                如果您已经设置Skype渠道，则在访客离线的情况下向其skype账号发送消息<a href="javascript:void(0)" onclick="switchToSkypeChannel()">设置skpye渠道</a>
            </div>
        </div>

        <div class="layui-form-button">
            <div class="layui-button-block">
                <button class="layui-btn" onsubmit lay-submit lay-filter="formDemo">立即提交</button>
                <div class="layui-btn layui-btn-primary" onclick="massMessageFormStatusReset()">重置</div>
            </div>
        </div>
</form>
</div>

<style>
    img[src=""] {
        display: none;
    }
</style>
<script>
    var theMassSelect= "text";

    layui.use('form', function () {
        var form = layui.form;
        form.render(); //更新全部
        form.verify({
            massMessageToOnlineUserText: function (value) {
                if (value == "" && theMassSelect == "text") {
                    return "请输入群发消息"
                }
            },
            massMessageToOnlineUserPic: function (value) {
                if (value == "" && theMassSelect == "pic") {
                    return "请上传群发图片"
                }
            },
            massMessageToOnlineUserFile: function (value) {
                if (value == "" && theMassSelect == "file") {
                    return "请上传群发文件"
                }
            }
        });

    });

    function massMessageFormStatusReset() {
        document.getElementById("massForm").reset();
        document.getElementById("selectImg").src="";
    }

    function previewFile() {
        var preview = document.getElementById("selectImg");
        var file    = document.querySelector('input[type=file]').files[0];
        var reader  = new FileReader();

        reader.onloadend = function () {
            preview.src = reader.result;
        }

        if (file) {
            reader.readAsDataURL(file);
        } else {
            preview.src = "";
        }
    }

    function divSelect(elem) {
        if (elem.title != theMassSelect) {
            massMessageFormStatusReset();
        }
        theMassSelect = elem.title;

        switch (theMassSelect) {
            case 'text':
                document.getElementById("textSelect").style.display = "block";
                document.getElementById("picSelect").style.display = "none";
                document.getElementById("fileSelect").style.display = "none";
                break;
            case 'pic':
                document.getElementById("textSelect").style.display = "none";
                document.getElementById("picSelect").style.display = "block";
                document.getElementById("fileSelect").style.display = "none";
                break;
            case 'file':
                document.getElementById("textSelect").style.display = "none";
                document.getElementById("picSelect").style.display = "none";
                document.getElementById("fileSelect").style.display = "block";
                break;
            default:
                document.getElementById("textSelect").style.display = "block";
                document.getElementById("picSelect").style.display = "none";
                document.getElementById("fileSelect").style.display = "none";
                break;
        }
    }

    function switchToSkypeChannel(){
        var multiMediaDialogWin = parent.$('#admin');
        var layer = layui.layer;
        layer.close(layer.index);
        if(multiMediaDialogWin.length>0){
            parent.$('#admin').attr('src' , '/admin/skype/index');
            parent.layui.element.tabChange('ukefutab','admin');
        } else {
            parent.$('#agentdesktop').attr('data-href' , '/admin/skype/index').click();
        }
    }

</script>