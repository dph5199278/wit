<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1">
			工作绩效（${.now?string('yyyy-MM-dd')}）
		</h1>
		<div class="box ukefu-console" style="padding-top:0px !important;">
			<img src="/images/agent.png"
				alt="admin">
			<div class="ukefu-user-index">
				<h1 style="width:120px;display:block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;word-wrap: break-word;
word-break: break-all;">
					${user.username!''}（${user.uname!''}）
				  	<span style="color:#c00;">
				  		<#if user?? && user.roleList??>
				  			<#list user.roleList as role>
				  				${role.name!''}
				  			</#list>
				  		</#if>
				  	</span>
				</h1>
				<#if user.lastlogintime??>
				<div class="ukefu-join-time ukefu-text-muted">
                                                                      登录时间：${user.lastlogintime?string("HH:mm:ss")}
				</div>
				</#if>
			</div>
		</div>
		<div class="box ukefu-console">
			<div class="ukefu-relation">
				<span lay-event="" class="ukefu-relation-item">会话<a
					href="/service/current/index"
					class="measure"> ${agentUserCount!0} </a>
				</span> <span lay-event="" class="ukefu-relation-item">邀请<a
					id="agentInviteTotalTimes"
					href="/apps/onlineuser"
					class="measure"> ${inviteResult.users + inviteResult.inviteusers + inviteResult.refuseusers} </a>
				</span> <span lay-event="" class="ukefu-relation-item">拒绝<a
					href="/apps/onlineuser"
					class="measure" <#if inviteResult.refuseusers gt 0>style="color:red !important;"</#if>> ${inviteResult.refuseusers} </a>
				</span>
			</div>
		</div>
		<div class="box ukefu-console">
			<div class="ukefu-relation">
				<span lay-event="" class="ukefu-relation-item" style="width:20%;">已接待<a
					href="/service/history/index"
					class="measure"> ${agentServicesCount!0} </a>
				</span>
				<span class="ukefu-relation-item" style="width:55%;">平均会话时长
					<a
						href="/service/history/index"
						class="measure"> <#if agentServicesAvg??>${(agentServicesAvg /(1000*60*60))?string('00')}:${((agentServicesAvg %(1000*60*60))/(1000*60))?string('00')}:${((agentServicesAvg %(1000*60))/(1000))?string('00')}<#else>00:00:00</#if></a>
				</span> 
			</div>
		</div>
	</div>
</div>	
<div class="row">
	<div class="col-lg-12"  style="padding:5px;border-top: 5px solid #e6e6e6;">
		<div class="layui-tab layui-tab-brief ukefu-webim-tab" lay-filter="docDemoTabBrief">
		  <ul class="layui-tab-title">
			<li class="layui-this"><i class="layui-icon">&#xe631;</i></li>
			<!-- 
			<li><i class="kfont">&#xe604;</i></li>
			<li><i class="layui-icon">&#xe615;</i></li>
			 -->
		  </ul>
		  <div class="layui-tab-content">
			<div class="layui-tab-item layui-show">
				<ul class="ukewo-btn-group">
					<!-- <li <#if subtype == 'content'>class="layui-this"</#if>>
						<a href="/apps/content" class="ukefu-bt">
							<i class="kfont ukewo-btn">&#xe717;</i>
							<div class="ukefu-bt-text">
								<div class="ukefu-bt-text-title" style="">
									首页
								</div>
								<div class="ukefu-bt-text-content" style="">
									在线的访客详细信息
								</div>
							</div>
						</a>
					</li>
					<li <#if subtype == 'onlineuser'>class="layui-this"</#if>>
						<a href="/apps/onlineuser" class="ukefu-bt">
							<i class="layui-icon ukewo-btn">&#xe612;</i>
							<div class="ukefu-bt-text">
								<div class="ukefu-bt-text-title" style="">
									在线访客
								</div>
								<div class="ukefu-bt-text-content" style="">
									在线的访客详细信息
								</div>
							</div>
						</a>
					</li> -->
					<!-- 
					<li>
						<a href="" class="ukefu-bt">
							<i class="kfont ukewo-btn ukefu-bg-color-deep-blue">&#xe7eb;</i>
							<div class="ukefu-bt-text">
								<div class="ukefu-bt-text-title" style="">
									对话历史
								</div>
								<div class="ukefu-bt-text-content" style="">
									当前客服与访客的对话历史记录
								</div>
							</div>
						</a>
					</li>
					<li>
						<a href="" class="ukefu-bt">
							<i class="kfont ukewo-btn ukefu-bg-color-pink">&#xe634;</i>
							<div class="ukefu-bt-text">
								<div class="ukefu-bt-text-title" style="">
									我的收件箱
								</div>
								<div class="ukefu-bt-text-content" style="">
									消息、任务、提醒收件箱
								</div>
							</div>
						</a>
					</li>
					 -->
				</ul>
			</div>
		  </div>
		</div>
	</div>
</div>	
