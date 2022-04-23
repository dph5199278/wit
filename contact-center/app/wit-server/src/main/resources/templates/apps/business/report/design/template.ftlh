<#if templet?? && templet.code?? && templet.code == "layout">
	<#include "layout.ftlh"/>
<#elseif templet?? && templet.code?? && templet.code == "filter">
	<#include "filter.ftlh"/>
<#elseif templet.templettype?? && templet.templettype == "report">
	<#include "layout.ftlh"/>
</#if>