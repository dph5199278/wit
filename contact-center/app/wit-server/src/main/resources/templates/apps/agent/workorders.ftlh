<div class="box">
	<div class="box-title">
		<h1 class="site-h1" style="background-color:#EEEEEE;">
			工单
			<div style="float:right;">
				<button class="layui-btn layui-btn-sm layui-btn-normal" href="/apps/workorders/embed/add?contactsid=${contactsid!''}" data-toggle="ajax" title="新建工单" data-width="950" data-height="500">新建工单</button>
			</div>
		</h1>
	</div>
	<div class="box-body" style="padding:0px 10px;">
		<ul id="workOrdersPages">
			<#if workOrdersList??>
			<#list workOrdersList as workOrders>
			<li class="ukefu-workorders-list-item">
				<div class="ukefu-workorders-number" id="fav_${workOrders.id!''}">
					<#include "/apps/business/workorders/orderno.ftlh">
				</div>
				<div class="ukefu-workorders-priority">
					<#list uKeFuDic["com.dic.workorders.priority"] as workOrdersStatus>
					<#if workOrdersStatus.id == workOrders.priority>
					<i class="kfont" <#if workOrdersStatus.iconskin?? && workOrdersStatus.iconskin != "">style="color:${workOrdersStatus.iconskin}"</#if> title="${workOrdersStatus.name!''}">&#xe82d;</i>
					</#if>
					</#list>
				</div>
				<a href="/apps/workorders/embed/detail?id=${workOrders.id!''}" data-toggle="ajax" data-multi="true" data-title="${workOrders.title!''}" data-text="工单创建时间：${workOrders.createtime?string('yyyy-MM-dd HH:mm:ss')} 最后更新于：${workOrders.updatetime?string('yyyy-MM-dd HH:mm:ss')}" data-width="950" data-height="500">
				<div class="ukefu-workorders-block">
					<div class="ukefu-workorders-title">
						${(workOrders.title!'')?no_esc}
					</div>
				</div>
				<div class="ukefu-workorders-block">
					<div class="ukefu-workorders-contacts">
						联系人：<#if workOrders.contacts??>${workOrders.contacts.name!''}</#if>
					</div>
				</div>
				<div class="ukefu-workorders-block">
					<div class="ukefu-workorders-status">
						状态：
						<#list uKeFuDic['com.dic.workorders.status'] as workOrdersStatus>
						<#if workOrdersStatus.id == workOrders.status || workOrdersStatus.code == workOrders.status>
						<small class="ukefu-label" <#if workOrdersStatus.iconskin?? && workOrdersStatus.iconskin != "">style="background-color:${workOrdersStatus.iconskin}"</#if>>${workOrdersStatus.name!''}</small>
						</#if>
						</#list>
					</div>
				</div>
				<div class="ukefu-workorders-block">
					<div class="ukefu-workorders-time">
						更新时间：<#if workOrders?? && workOrders.updatetime??>${workOrders.updatetime?string("yyyy-MM-dd HH:mm:ss")}</#if>
					</div>
				</div>
				</a>
			</li>
			</#list>
			</#if>
		</ul>
	</div>
</div>

