<div class="uk-layui-form">
	<div class="box">
		<div class="box-body">
			<table class="layui-table" lay-skin="line">
				<thead>
					<tr class="design-sortable-tr">
						<th>用户名</th>
						<th>终端/系统/浏览器</th>
						<th>访问时间</th>
						<th>离开时间</th>
						<th>地域</th>
						<th>标题</th>
					</tr>
				</thead>
				<tbody>
					<#if traceHisList?? && traceHisList.content?? && traceHisList.content?size gt 0>
					<#list traceHisList.content as traceHis>
					<tr class="tdHasContorllor design-dropable-td">
						<td class=" first_td_head">
							${traceHis.username!''}
						</td>
						
						<td class="ukefu-online-client">
							<#if traceHis.mobile?? && traceHis.mobile == "1">
							<i class="kfont">&#xe603;</i>
							<#else>
							<i class="kfont">&#xe601;</i>
							</#if>
							<#if traceHis.ostype?? && traceHis.ostype == "windows">
							<i class="kfont">&#xe801;</i>
							<#elseif traceHis.ostype?? && traceHis.ostype == "linux">
							<i class="kfont">&#xe726;</i>
							<#elseif traceHis.ostype?? && traceHis.ostype == "mac">
							<i class="kfont">&#xe6be;</i>
							</#if>
							${traceHis.browser!''}
						</td>
						<td class=" "><#if traceHis.createtime??>${traceHis.createtime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
						<td class=" "><#if traceHis.updatetime??>${traceHis.updatetime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
						<td class=" ">${traceHis.country!''}</td>
						<td class=" "><a href="${traceHis.url!''}" target="_blank" title="${traceHis.title!''}">
							<#if traceHis.title?? && traceHis.title?length gt 15>${traceHis.title[0..15]}...<#else>${traceHis.title!''}</#if></a></td>
					</tr>
					</#list>
					<#else>
						<tr>
							<td colspan="20" style="height:400px;">
								<div class="ukefu-empty" style="background: none">
									<i class="layui-icon">&#xe63a;</i>
									<div style="">没有访问轨迹信息</div>
								</div>
							</td>
						</tr>
					</#if>
				</tbody>
			</table>	
		</div>
	</div>
        <div class="layui-form-button">
            <div class="layui-button-block">
                <button class="layui-btn" lay-submit lay-filter="formDemo" onclick="layer.close(layerwin);">关闭</button>
            </div>
        </div>
</div>
