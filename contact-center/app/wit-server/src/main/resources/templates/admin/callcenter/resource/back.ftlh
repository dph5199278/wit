<div class="row" style="height:100%;">
	<div class="col-lg-3" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			服务器列表<#if pbxHostList??>（${pbxHostList?size}）</#if>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<ul>
					<#if pbxHostList??>
					<#list pbxHostList as host>
					<li class="uk_callcenter_pbxhost <#if pbxHost?? && pbxHost.id == host.id>this</#if>" href="/admin/callcenter/resource/pbxhost?hostid=${host.id!''}" data-toggle="load" data-target="#callCenterResource">
						<i class="kfont" style="position: relative;">&#xe7fa;</i>
						${host.name!''}
						<div style="padding-left:22px;margin-top:5px;color:#cccccc;">
							服务器：${host.hostname!''}:${host.port!''}
						</div>
					</li>
					</#list>
					</#if>
				</ul>
			</div>
		</div>
	</div>
	<div class="col-lg-9" id="callCenterResource">
		<#include "/admin/callcenter/resource/pbxhost.ftlh">
	</div>	
</div>
