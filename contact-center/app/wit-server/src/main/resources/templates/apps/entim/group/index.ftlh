<div id="containter" class="clearfix" style="margin-right:200px;">
	<div class="content-left">
		<div class="chat-above" id="above">
			<div class="clearfix message connect-message">
				<span id="connect-message" style="background-color:#FFFFFF;color:#4665d4;">
					<a href="javascript:getMoreMessage()" style="color:#4665d4;">
						<i class="layui-icon">&#xe63a;</i>
						查看更多消息
					</a>
				</span>
			</div>
			<#include "/apps/entim/group/more.ftlh">
		</div>
		<div class="chat-bottom" id="bottom">
			<textarea id="message" name="content" style="visibility:hidden;"></textarea>
			<div class="btn-push clearfix">
				<div style="float:left;height:24px;line-height:34px;margin: 5px 20px 10px 5px;" id="surplus">0/200</div>
				<div style="display: inline-block;position: absolute;right: 110px;bottom: 14px;">Ctrl + Enter</div>
				<button type="button" class="send-btn active special  clearfix" id="sent" onclick="sendMessage()" style="background-color:#32c24d;font-weight: 200;padding:5px;">
					发送
				</button>
			</div>
		</div>
	</div>
	<div class="content-rig">
		<div class="content-head">
			<p>群公告</p>
		</div>
		<div class="content-list ukefu-group-tipmsg" id="group-tipmessage">
			<#include "/apps/entim/group/tipmsg.ftlh">
		</div>
		<div class="content-head">
			<p>群应用</p>
		</div>
		<div class="content-list" style="margin-top: 0px;">
			<ul class="ukefu-group-apps">
				<li class="ukefu-group-app">
					<a href="/ent/im/group/user?id=${imGroup.id}">
						<div class="ukefu-group-app-icon"><i class="layui-icon">&#xe613;</i></div>
						<div class="ukefu-group-app-name">成员</div>
					</a>
				</li>
				<#if user.id = imGroup.creater>
				<li class="ukefu-group-app">
					<a href="javascript:void(0)" onclick="group()">
						<div class="ukefu-group-app-icon"><i class="layui-icon">&#xe63a;</i></div>
						<div class="ukefu-group-app-name">公告</div>
					</a>
				</li>
				</#if>
			</ul>
		</div>
		<div class="content-head">
			<p>群成员（${imGroupUserList?size}）</p>
		</div>
		<div class="content-list" style="margin-top: 0px; height: calc(100% - 295px);overflow-y: auto;">
			<ul class="ukefu-user-list ukefu-group" style="display:block;">
				<#if imGroupUserList??>
				<#list imGroupUserList as imGroupUser>
					<li id="gruop_user_${imGroupUser.user.id}">
						<a href="javascript:void(0)" <#if imGroupUser.user.id != user.id>onclick="openchat('/ent/im/chat?userid=${imGroupUser.user.id!''}' , '/images/user-pc.png' , '${imGroupUser.user.uname!''}' , '${imGroupUser.user.id!''}' , '最近登陆:${imGroupUser.user.lastlogintime}');"</#if>>
							<img src="/images/user-pc.png">
							<span>${imGroupUser.user.uname!''}</span>
						</a>
						<#if imGroupUser.admin || imGroupUser.user.id == user.id>
							<div class="admin">
								<#if imGroup.creater == imGroupUser.user.id>
									<img src="/images/creater.png" title="创建人">
								<#elseif imGroupUser.admin == true>
									<img src="/images/admin.png" title="系统管理员">
								</#if>
							</div>
						</#if>
					</li>
				</#list>
				</#if>
			</ul>
		</div>
	</div>
</div>
<style>
	.ukefu-media-image{
		max-height:150px;
		max-width:200px;
		cursor: pointer;
	}
</style>
<script type="text/javascript">
	var editor , words;
	KindEditor.ready(function (K) {
		editor = K.create('textarea[name="content"]', {
			autoHeightMode: false,
			width: "100%",
			resizeType: 0,
			themeType: 'simple',
			fontsize: 14,
			newlineTag : "br" ,
			filterMode:true,
			items: ['emoticons', 'image', 'insertfile'],
			uploadJson:'/ent/im/image/upload?userid=${user.id!''}&group=${contextid!''}',
			filePostName: 'imgFile',
			allowImageRemote: false,
			allowFileUpload: true,
			allowInsertUpload: false,
			afterChange : function() {
				var count = this.count() ;

				words = this.count("text") ;
				var pattern = '字数：' + words + '字';
				document.getElementById('surplus').innerHTML = "字符： "+count+"  ，文字：" + pattern; //输入显示
				 ////////
			},
			afterCreate : function() { //设置编辑器创建后执行的回调函数
	            var self = this;
	            //Ctrl+Enter提交表单
	            K.ctrl(document, 13, function() {
	                self.sync();
	                sendMessage();
	            });
	            K.ctrl(self.edit.doc, 13, function() {
	                self.sync();
	                sendMessage();
	            });
	        }
		});
	});
	KindEditor.options.cssData = "body { font-size: 15px; font-family:'Microsoft Yahei', 'Helvetica', 'Simsun', 'Arial';}";
	var R3Ajax = {
		ajax:function(opt){
			var xhr = this.createXhrObject();
			xhr.onreadystatechange = function(){
				if(xhr.readyState!=4) return ;
				(xhr.status===200 ?
					opt.success(xhr.responseText,xhr.responseXML):
					opt.error(xhr.responseText,xhr.status));
			}
			xhr.open(opt.type,opt.url,true);
			if(opt.type!=='post')
				opt.data=null;
			else
				xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			opt.data = this.parseQuery(opt.data);
			xhr.send(opt.data);
		},
		post:function(url,success,data){
			var popt = {
				url:url,
				type:'post',
				data:data,
				success:success,
				error:function(data){}
			}
			this.ajax(popt);
		},
		get:function(url,success){
			var gopt = {
				url:url,
				type:'get',
				success:success,
				error:function(){}
			}
			this.ajax(gopt);
		},
		createXhrObject:function(){
			var methods = [
				function(){ return new XMLHttpRequest();},
				function(){ return new ActiveXObject('Msxml2.XMLHTTP');},
				function(){ return new ActiveXObject('Microsoft.XMLHTTP');}
			];
			for(var i=0;len=methods.length,i<len;i++){
				try{
					methods[i]();
				}catch(e){
					continue;
				}
				this.createXhrObject = methods[i];
				return methods[i]();
			}
			throw new Error('Could not create an XHR object.');
		},
		parseQuery:function(json){
			if(typeof json == 'object'){
				var str = '';
				for(var i in json){
					str += "&"+i+"="+encodeURIComponent(json[i]);
				}
				return str.length==0 ? str : str.substring(1);
			}else{
				return json;
			}
		}
	};
	Date.prototype.format = function(fmt) {
		 var o = {
			"M+" : this.getMonth()+1,                 //月份
			"d+" : this.getDate(),                    //日
			"h+" : this.getHours(),                   //小时
			"m+" : this.getMinutes(),                 //分
			"s+" : this.getSeconds(),                 //秒
			"q+" : Math.floor((this.getMonth()+3)/3), //季度
			"S"  : this.getMilliseconds()             //毫秒
		};
		if(/(y+)/.test(fmt)) {
				fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
		}
		 for(var k in o) {
			if(new RegExp("("+ k +")").test(fmt)){
				 fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
			 }
		 }
		return fmt;
	}
	var R3Helper = {
		resize : function(){
			var height = document.body.offsetHeight ;
			document.getElementById('above').style.height = (height - 194 - 50)+"px" ;
		}
	}
	function openchat(url , img , name, id , text){
		top.layer.open({
			  type: 2,
			  id : id,
			  title: ["<div style='position: relative;height: 42px;padding: 5px 15px 5px 0px;line-height: 20px;cursor: pointer;display: inline-block;vertical-align: top;' id='dialog_"+id+"'><img src='"+img+"' style='max-height:50px;'><div style='padding:0px 5px;line-height: 23px;display: inline-block;vertical-align: top;'><span style='vertical-align: top;font-size:18px;'>"+name+"<span class='ukefu-entim-status offline' id='chat_"+id+"' title='离线'><i class='layui-icon'></i></span></span><p style='vertical-align: top;font-size: 12px;color: #999;'>"+text+"</p></div></div>" , "height:55px"],
			  closeBtn: 1, //不显示关闭按钮
			  shade: false,
			  area: ['700px', '520px'],
			  maxmin: true,
			  anim: 2,
			  shade: 0,
			  content: url
		  });
		top.$('#'+id).next().find("a.layui-layer-maxmin").click();
	}
</script>
<script>
	layui.use('element', function(){
	  var $ = layui.jquery
	  ,element = layui.element; //Tab的切换功能，切换事件监听等，需要依赖element模块
	});
	var hostname = location.hostname ;
	var socket = io.connect(location.protocol+"//"+top.hostname+":"+top.port+"/im/ent?userid=${user.id!''!''}&orgi=${user.orgi!''}&contextid=${contextid!''}&group=${contextid!''}",{transports: ['websocket'], upgrade: false});

	socket.on('message', function(data) {
		data.createtime = formatDate(data.createtime);
		var chat=document.getElementsByClassName('chatting-left').innerText;
		chat = data.message;

		if(data.msgtype == 'image'){
			chat = "<a href='"+data.message+"&original=true' target='_blank'><img src='"+data.message+"' class='ukefu-media-image'/></a>" ;
		}else if(data.msgtype == 'file'){
			chat = "<div class='ukefu-message-file'><div class='ukefu-file-icon'><img src='/im/img/file.png'></div><div class='ukefu-file-desc'><a href='"+data.message+"' target='_blank'><div>"+data.filename+"</div><div>"+(data.filesize/1024).toFixed(3)+"Kb</div></a></div></div>" ;
		}

		var user = data.username ;
		if(data.userid == "${user.id!''}"){
			output('<div class="chat-right"> <img class="user-img" src="/im/img/user.png" alt=""><div class="chat-message"><label class="time">'+data.createtime+'</label><label  class="user">' + user + '</label> </div><div class="chatting-right"><i class="arrow"></i><div class="chat-content">'+chat+'</div></div>' , "chat-block");
		}else{
			output('<div class="chat-left"> <img class="user-img" src="/im/img/user.png" alt=""><div class="chat-message"><label  class="user">' + user + '</label><label class="time">'+data.createtime+'</label> </div><div class="chatting-left"><i class="arrow"></i><div class="chat-content">'+chat+'</div></div>' , "chat-block");
		}
	});
	function sendMessage() {
		editor.sync();
		var count = editor.count("text");
		if(count>0){
			var message = document.getElementById('message').value;
			if(message!= ""){
				socket.emit('message', {
					userid:"${user.id!''}",
					type:"message" ,
					username:"${user.uname!''}",
					session:"${user.id!''}",
					touser:"${contextid!''}",
					contextid:"${contextid!''}",
					orgi:"${user.orgi!''}",
					model:"entim",
					chatype:"group",
					message : message
				});
			}
		}
		editor.html('');
	}
	function output(message , clazz) {
		if(clazz == "message connect-message"){
			var messages = document.getElementsByClassName("connect-message") ;
			for(inx =0 ; inx < messages.length ; ){
				document.getElementById('above').removeChild(messages[inx]) ;
				inx++ ;
			}
		}
		var element = ("<div class='clearfix "+clazz+"'>" +" " + message + "</div>");
		document.getElementById('above').innerHTML= (document.getElementById('above').innerHTML + element);
		document.getElementById('above').scrollTop = document.getElementById('above').scrollHeight  ;
	}
	document.getElementById('above').scrollTop = document.getElementById('above').scrollHeight  ;
	top.$('#chat_${imGroup.id}').removeClass('offline').addClass('online').attr('title' , '在线');
	function group(){
		layer.open({
			  title: '公告' ,
			  type: 1
			  ,id:'tipmsg'
			  ,content: '<div><div id="" class="layui-layer-content ukefu-entim-search"><#if user.id = imGroup.creater><textarea type="text" id="tipmsgcontent" class="layui-layer-input" style="height:100px;widht:300px;"></textarea><#else>${imGroup.tipmessage!''}</#if></div></div>'
			  ,btn: ['保存','关闭']
			  ,shade: 0 //不显示遮罩
			  ,yes: function(){
				  loadURL('/ent/im/group/tipmsg?id=${imGroup.id!''}&tipmsg='+encodeURIComponent($('#tipmsgcontent').val()) , '#group-tipmessage');
				  layer.closeAll();
			  }
		});
	}

	function getMoreMessage(){
		let firstCreateTime=$('.time').first().text();
		R3Ajax.get("/ent/im/group/more?id=${contextid!''}&createtime="+firstCreateTime,function (html) {
			$('.connect-message').after(html);
		});
	}
</script>
<style>
	#group-tipmessage {
		overflow-y: auto;
		word-break: break-all;
		text-indent: 0px;
	}
</style>