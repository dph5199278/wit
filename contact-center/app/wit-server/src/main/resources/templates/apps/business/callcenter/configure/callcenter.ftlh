<document type="freeswitch/xml">
  <section name="configuration">
    <!--The content of the tag SECTION is identical to what you find in the public config files.-->
    <configuration name="callcenter.conf" description="CallCenter">
	  <settings>
	    <!--<param name="odbc-dsn" value="dsn:user:pass"/>-->
	    <!--<param name="dbname" value="/dev/shm/callcenter.db"/>-->
	  </settings>
	
	  <queues>
	    <#if skillGroups??>
	    <#list skillGroups as skill>
		<queue name="${skill.skill!''}">
	      <param name="strategy" value="longest-idle-agent"/>
	      <param name="moh-sound" value="ivr-welcome.wav"/>
	      <param name="record-template" value="${r'$${recordings_dir}'}/callcenter/${r'${strftime(%Y-%m-%d)}'}/${r'${strftime(%Y-%m-%d %H-%M-%S)}'}.${r'${destination_number}'}.${r'${caller_id_number}'}.${r'${uuid}'}.wav"/>
	      <param name="time-base-score" value="system"/>
	      <param name="max-wait-time" value="0"/>
	      <param name="max-wait-time-with-no-agent" value="0"/>
	      <param name="max-wait-time-with-no-agent-time-reached" value="5"/>
	      <param name="tier-rules-apply" value="false"/>
	      <param name="tier-rule-wait-second" value="300"/>
	      <param name="tier-rule-wait-multiply-level" value="true"/>
	      <param name="tier-rule-no-agent-no-wait" value="false"/>
	      <param name="discard-abandoned-after" value="60"/>
	      <param name="abandoned-resume-allowed" value="false"/>
	    </queue>
	  	</#list>
	  	</#if>
	
	  </queues>
	
	<!-- WARNING: Configuration of XML Agents will be updated into the DB upon restart. -->
	<!-- WARNING: Configuration of XML Tiers will reset the level and position if those were supplied. -->
	<!-- WARNING: Agents and Tiers XML config shouldn't be used in a multi FS shared DB setup (Not currently supported anyway) -->
	  <agents>
	  	<#if skillExtentionList??>
	  	<#list skillExtentionList as skillExtention>
	  	<#if skillExtention.extention?? && skillExtention.extention!="">
	  	<agent name="${skillExtention.extention}" type="callback" contact="[call_timeout=10]user/${skillExtention.extention}" status="Logged Out" max-no-answer="3" wrap-up-time="10" reject-delay-time="10" busy-delay-time="60" />
	  	</#if>
	  	</#list>
	  	</#if>
	  </agents>
	  <tiers>
	    <!-- If no level or position is provided, they will public to 1.  You should do this to keep db value on restart. -->
	    <#if skillExtentionList??>
	  	<#list skillExtentionList as skillExtention>
	  	<#if skillGroups??>
		<#list skillGroups as skill>
		<#if skill.id == skillExtention.skillid>	  	
	  	<tier agent="${skillExtention.extention}" queue="${skill.skill!''}" level="1" position="1"/>
	  	</#if>
	  	</#list>
	  	</#if>
	  	</#list>
	  	</#if>
	  </tiers>
	
	</configuration> 
  </section>
</document>
