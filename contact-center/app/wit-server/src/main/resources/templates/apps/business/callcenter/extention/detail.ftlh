{
	"id":"${extention.id!''}",
	"extention":"${extention.extention!''}",
	"hostid":"${extention.hostid!''}",
	"callout":"<#if models?seq_contains("callout")>${extention.callout!''}</#if>",
	"webrtc":"<#if extention.enablewebrtc?? && extention.enablewebrtc == true && pbxhost?? && pbxhost.enablewebrtc?? && pbxhost.enablewebrtc == true>true<#else>false</#if>",
	"webrtchost":"<#if pbxhost??>${pbxhost.webrtcaddress!''}</#if>",
	"webrtcport":"<#if pbxhost??>${pbxhost.webrtcport!''}</#if>",
	"webrtcssl":"<#if pbxhost?? && pbxhost.webrtcssl == true>true<#else>false</#if>",
	"orgi":"${extention.orgi!''}",
	"userid":"${user.id!''}"
}
