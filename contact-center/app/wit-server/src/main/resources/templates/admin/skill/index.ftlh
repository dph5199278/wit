<div class="row" style="height:100%;">
	<div class="col-lg-3" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			坐席组
			<span style="float:right;">
				<button class="layui-btn layui-btn-sm green" href="/admin/skill/add" data-toggle="ajax" data-height="200" data-width="550" data-title="创建新用户">
					创建组
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<ul>
					<#if skillGroups??>
					<#list skillGroups as skill>
					<li class="uk_skill <#if skillData?? && skillData.id == skill.id>this</#if>">
						<a href="/admin/skill/index?skill=${skill.id!''}">
							<i class="kfont icon-iconfontagent" style="position: relative;"></i>
							${skill.name!''}
						</a>
					</li>
					</#list>
					</#if>
				</ul>
			</div>
		</div>
	</div>
	<div class="col-lg-9">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			<#if skillData??>${skillData.name!''}<#else>坐席组</#if><#if userList??>（${userList?size!''}）</#if>
			<span style="float:right;">
				<#if skillData??>
				<button class="layui-btn layui-btn-sm green" href="/admin/user/add" data-toggle="ajax" data-width="750" data-title="创建新用户">
					添加坐席到当前组
				</button>
				<button class="layui-btn layui-btn-primary layui-btn-sm" href="/admin/skill/edit?id=${skillData.id}" data-toggle="ajax" data-width="550" data-height="200" data-title="修改坐席组">
					修改坐席组
				</button>
				<button class="layui-btn layui-btn-primary layui-btn-sm" href="/admin/skill/delete?id=${skillData.id!''}" data-toggle="tip" data-title="请确认是否删除坐席组？">
					删除坐席组
				</button>
				</#if>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="25%">
						<col width="25%">
						<col width="25%">
						<col width="24%">
						<col width="1%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th>用户</th>
					  <th>姓名</th>
					  <th>电子邮件</th>
					  <th>手机</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if userList??>
					<#list userList as user>
					<tr>
					  <td>
							<a href="">
								<i class="layui-icon headimg">&#xe612;</i>
								<div style="margin-left:50px;margin-top:0px;">
									${user.username!''}
									<div style="color:#aaaaaa;font-size:12px;">
										系统管理员
									</div>
								</div>
							</a>
							
					  </td>
					  <td>${user.uname!''}</td>
					  <td>${user.email!''}</td>
					  <td>${user.mobile!''}</td>
					  <td style="white-space: nowrap;">
					  		<a href="/admin/user/delete?id=${user.id!''}"  data-toggle="tip" data-title="请确认是否从坐席组中移除坐席？">
					  			<i class="layui-icon" style="color:red;">&#x1006;</i>
					  			移除
					  		</a>
					  </td>
					</tr>
					</#list>
					</#if>
				  </tbody>
				</table>
			</div> 	   
		</div>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12" id="page" style="text-align:center;"></div>
		</div>
	</div>	
</div>
