<table class="layui-table ukefu-report">
  <thead>
	<#if reportData?? && reportData.col?? && reportData.col.title??>
	  <#list reportData.col.title as tlist>
		<tr class="sortableTr">
		  <#if reportData.col.title?size gt 1 && tlist_index==0 && reportData.row.title?size gt 0>
			<td align="center" colspan="${reportData.row.title?size}" rowspan="${reportData.col.title?size-1}" class="s_blue"></td>
		  </#if>
		  <#if (tlist_index+1)==reportData.col.title?size && reportData.row?? && reportData.row.firstTitle??>
			<#list reportData.row.firstTitle as first>
			  <td align="center" data-title="${first.name?url}" data-flag="dim">${first.rename!first.name!''}
				<#if first.description??>
				  <img style="cursor: pointer;" onclick="showHelpMsg(this)" title="维度描述" width="10" height="10" src="/assets/images/help.png"></#if>
				<textarea style="display: none">${first.description!''}</textarea></td>
			</#list>
		  </#if>
		  <#if tlist??>
			<#list tlist as tl>
			  <td align="center" <#if tl.leveltype?? && tl.leveltype=="newcol">rowspan="${reportData.col.title?size}"</#if>colspan="${tl.colspan}" > ${tl.rename!tl.name!""}
				</td>
			</#list>
			</#if>
	</tr>
	</#list>
	</#if>
  </thead>
  <tbody>
	<#if reportData?? && reportData.data??>
	  <#list reportData.data as values>
		<tr class="rowcell">
		  <#if reportData.row?? && reportData.row.title?? && reportData.row.title?size gt 0>
			<#list reportData.row.title as tl>
			  <#assign rows=0>
				<#list tl as title>
				  <#if title??>
					<#if rows==values_index && title.name !="TOTAL_TEMP">
					  	<#if title.leveltype !="newrow_extend_sum">
                         <td title="${title.formatName!''}" style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" nowrap="nowrap" align="center" class="blue_k <#if title.total == true>total</#if>" rowspan="${title.rowspan!'0'}" <#if title.colspan gt 1>colspan="${title.colspan}"</#if>>
						  ${title.formatName!''}</td>
						</#if>
						<#if title.valueData??>
						  <#list title.valueData as value>
							<#if value.merge==false>
							  <td rowspan="${value.rowspan}" colspan="${value.colspan}" align="center" class="measure ${value.vtclass!''}" nowrap="nowrap" <#if value.cellmergeid??>data-cellmerge="${value.cellmergeid}"</#if>>
							${value.valueStyle!value.foramatValue!''}</td>
							</#if>
							</#list>
						  </#if>
					  </#if>
					  <#assign rows=rows + title.rowspan>
				  </#if>
			</#list>
	</#list>
	<#else>
	  <#list values as value>
		<td class="row " style="text-align: right;<#if value.valueType?? && value.valueType == 'total'>background-color:#c5daed;</#if>">
		  ${value.valueStyle!value.foramatValue!''}</td>
	  </#list>
	  </#if>
	  </tr>
	  </#list>
	  </#if>
  </tbody>
</table>