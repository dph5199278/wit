<#if contactsList?? && contactsList.content?? && contactsList.content?size gt 0>
<#list contactsList.content as contacts>
<tr class="tdHasContorllor design-dropable-td ukefu-contacts">
	<td style="width:1px;padding:10px 0px 10px 10px;">
		<input type="checkbox" name="id" class="ctxid" value="${contacts.id!''}" id="contacts_${contacts.id}">
	</td>
	<td class=" first_td_head">
		${contacts.name!''}
	</td>
	<td>
		${contacts.mobileno!''} 
	</td>
	<td>
		${contacts.email!''} 
	</td>
	<td>
		<#if contacts.gender?? && contacts.gender == '1'>男</#if>
		<#if contacts.gender?? && contacts.gender == '0'>女</#if>
		<#if contacts.gender?? && contacts.gender == '-1'>未知</#if>
	</td>
	<td>
		${uKeFuDic[contacts.ckind!''].name!''} 
	</td>
	<td style="white-space:nowrap;width:1%;" nowrap="nowrap">
		<a href="/apps/contacts/embed/edit?id=${contacts.id!''}" data-toggle="load" data-target="#mainajaxwin">
			<i class="layui-icon">&#xe642;</i>
			编辑
		</a>
	</td>
</tr>
</#list>
</#if>