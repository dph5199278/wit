<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/user/save">
	  <div class="layui-form-item">
	    <label class="layui-form-label">用户名：</label>
	    <div class="layui-input-inline">
	      <input type="text" name="username" required lay-verify="required_username" placeholder="请输入用户名" autocomplete="off" class="layui-input">
	    </div>
	    <div class="layui-form-mid layui-word-aux">用户登录的账号</div>
	  </div>
	  <div class="layui-form-item">
	    <label class="layui-form-label">姓名：</label>
	    <div class="layui-input-inline">
	      <input type="text" name="uname" required lay-verify="required_name" placeholder="请输入用户姓名" autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-form-item">
	    <label class="layui-form-label">电子邮件：</label>
	    <div class="layui-input-inline">
	      <input type="email" name="email" placeholder="请输入电子邮件地址" autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-form-item">
	    <label class="layui-form-label">密码：</label>
	    <div class="layui-input-inline">
	      <input type="password" id="password" name="password" required lay-verify="required|pass" placeholder="请输入登录密码" autocomplete="off" class="layui-input">
	    </div>
	    <div class="layui-form-mid layui-word-aux" style="padding-top: 5px;">
	    	<input type="password" id="repassword" name="repassword" required lay-verify="repass" placeholder="请再次输入密码" autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-form-item">
	    <label class="layui-form-label">手机号：</label>
	    <div class="layui-input-inline">
	      <input type="mobile" name="mobile"  placeholder="请输入手机号码" autocomplete="off" class="layui-input">
	    </div>
	  </div>

	  <div class="layui-form-item">
	    <label class="layui-form-label">管理员：</label>
	    <div class="layui-input-inline">
	      <input type="checkbox" name="admin" lay-skin="switch" lay-filter="admincheckbox" value="1" lay-text="是|否">
	    </div>
        <div class="layui-form-mid layui-word-aux">管理员具备访问和修改所有系统资源的权限，同时也设定为多媒体坐席</div>
	  </div>
	  <div class="layui-form-item"  id="agentDiv">
	    <label class="layui-form-label">多媒体坐席：</label>
	    <div class="layui-input-inline">
	      <input type="checkbox" name="agent" lay-skin="switch" value="1" lay-filter="agent" lay-text="开启|关闭">
	    </div>
	  </div>
	  <#if models?seq_contains("callout")>
	  <div class="layui-form-item" id="callcenterDiv">
	    <label class="layui-form-label">呼叫中心坐席：</label>
	    <div class="layui-input-inline">
	      <input type="checkbox" name="callcenter" lay-skin="switch"  value="1" lay-filter="callcenter" lay-text="开启|关闭">
			</div>
			<div class="layui-form-mid layui-word-aux" id="sipAccountDiv">
				<input type="text" id="sipAccount" name="sipaccount" lay-verify="sip"  placeholder="请输入SIP地址" autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  </#if>
	  <div class="layui-form-button">
	    <div class="layui-button-block">
	      <button class="layui-btn" lay-submit lay-filter="sys-user-create">提交</button>
		  <button class="layui-btn" lay-submit lay-filter="sys-user-create-follow">提交并创建下一个</button>
	      <button type="reset" class="layui-btn layui-btn-warm">重置</button>
	    </div>
	  </div>
	</form>
</div>

<script>

layui.use('form', function(){
  var form = layui.form;
  form.render(); //更新全部
  form.verify({
	  repass: function(value){
	    if(value != $('#password').val()){
	      return '两次输入的密码不一致，请确认';
	    }
	  },
      sip: function(value) {
		if($("input[name=callcenter]:checked").length > 0 && value === ''){
            return 'SIP地址不能为空，请确认';
		}
      },
	  required_username: function (value) {
		  if (value.length > 50){
			  return "用户名不能超过50字";
		  }
	  },
	  required_name: function (value) {
		  if (value.length > 100){
			  return "用户姓名不能超过100字";
		  }
	  }
	});

    // 当账号为管理员类型时，强制设定为坐席！
    form.on('switch(admincheckbox)', function(data){
        if(data.elem.checked){
            // 坐席的按钮
            if($('#agentDiv > div > div > em').text() == "关闭"){
                // 之前未开启
                document.querySelector('#agentDiv > div > div > i').click();
            }
            // 设置为禁用
            $("#agentDiv > div > input[name=agent]").attr('disabled', 'disabled');
        } else {
            // 取消禁用
            $("#agentDiv > div > input[name=agent]").removeAttr('disabled');
        }
    });

	// 默认隐藏“呼叫中心坐席”
	$('#callcenterDiv').hide();
	$('#sipAccountDiv').hide();

	// 根据“多媒体坐席”显示“呼叫中心坐席”状态
  	form.on('switch(agent)', function(data){
        $("input[name=callcenter]").prop("checked", false);
        form.render('checkbox','callcenter');
		if(data.elem.checked){
			$('#callcenterDiv').show();
		}else{
			$('#callcenterDiv').hide();
		}
        $('#sipAccount').val("");
        $('#sipAccountDiv').hide();
	});

  	// 根据“呼叫中心坐席”状态 显示呼叫地址输入框
  	form.on('switch(callcenter)', function(data){
        $('#sipAccount').val("");
		if(data.elem.checked){
			$('#sipAccountDiv').show();
		}else{
			$('#sipAccountDiv').hide();
		}
	});

  	form.on('submit(sys-user-create)', function (data) {
		data.field['ops'] = 'create';
		restApiRequest({
			silent: true,
			path: 'user',
			data: data.field
		}).then(function(result){
			if(result.rc == 0){
                processUserAddOrUpdateResult(result.data, function () {
                    // close win
                    // 关闭form
                    $("span.layui-layer-setwin > a.layui-layer-close1").click();
                    // 刷新页面
                    document.getElementById("admin").contentWindow.location = '/admin/user/index';
                });
			}
		}, function(err){
            handleRestApiFail(err);
		});
		return false;
	});

	// 保存成功后，继续留在当前页面，重制并创建下一个
	form.on('submit(sys-user-create-follow)', function (data) {
        data.field['ops'] = 'create';
        restApiRequest({
            silent: true,
            path: 'user',
            data: data.field
        }).then(function(result){
            if(result.rc == 0){
                processUserAddOrUpdateResult(result.data, function () {
                    // 清空部分参数
                    $('input[name="username"]').val("");
                    $('input[name="uname"]').val("");
                    $('input[name="mobile"]').val("");
                    $('input[name="email"]').val("");
                });
            }
        }, function(err){
            handleRestApiFail(err);
        });
		return false;
	})
});
</script>
