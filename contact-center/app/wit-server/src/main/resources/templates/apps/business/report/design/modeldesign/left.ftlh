<div class="layui-side" style="width:260px;overflow:hidden;">
	<div class="layui-side-scroll tpcom" style="width:260px;overflow:hidden;">
		<div class="layui-row">
		    <div class="layui-col-md12" style="background-color:#f0f2f5;padding-bottom:10px;">
		    	<div class="" style="position: absolute;left:2px;top:10px;">
					<i class="layui-icon" style="font-size:45px;color:#1E90FF;">&#xe620;</i>
				</div>
		    	<div class="" style="line-height:40px;font-size:16px;font-weight:600;margin-left:60px;margin-top:18px;">
					<a  href="/apps/report/cube/selpbcubeindex?mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-width="950" data-height="500" data-title="选择模型">
						<#if reportModel?? && reportModel.publishedcubeid??><#if cube??>${cube.name!''}<#else>点击选择模型</#if><#else>点击选择模型</#if>
					</a>
				</div>
		    </div>
		</div>
		<div class="layui-row">
			<div class="layui-col-md12">
				<div class="layui-tab">
				  <ul class="layui-tab-title">
				    <li class="<#if !tabid?? || (tabid?? && tabid =='data' ) >layui-this</#if>" >数据选项</li>
				    <li class="<#if tabid?? && tabid =='filter' >layui-this</#if>">过滤器</li>
				  </ul>
				  <div class="layui-tab-content" style="padding:10px 0;">
				    <div class="layui-tab-item  ukefu-menu <#if !tabid?? || (tabid?? && tabid =='data' ) >layui-show</#if>">
				    	<#if reportModel?? && reportModel.publishedcubeid??>
				    		<#if eltemplet?? && eltemplet.name == '数据表'>
				    			<div class="ukefu-design-group">
									<div class="ukefu-design-type">
								   		<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe632;</i>
								   		行维度
								   		<span style="float:right;">
											<button class="layui-btn layui-btn-xs layui-btn-normal" href="/apps/report/design/adddata?dtype=field&tabid=data&t=dim&cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-height="400" data-width="400" data-title="添加维度成员">
												<i class="layui-icon">&#xe608;</i>
												添加
											</button>
										</span>
								   	</div>
								   	<#if reportModel?? && reportModel.properties??>
										<ul id="rowdim_sortable" class="uk_sortable connectedSortable">
											<#list reportModel.properties as property>
									   		<li class="uk-state-default" id="rowsort_${property.id!''}">
									   			${property.dataname!''}
									   			<button class="layui-btn layui-btn-xs layui-btn-danger" style="float:right;" href="/apps/report/design/columndel?id=${property.id!''}" data-toggle="tip" data-title="请确认是否删除？"><i class="layui-icon">&#xe640;</i></button>
									   		</li>
											</#list>
										</ul>
									</#if>
								</div>
								
				    			<div class="ukefu-design-group">
									<div class="ukefu-design-type">
								   		<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe632;</i>
								   		列维度
								   		<span style="float:right;">
											<button class="layui-btn layui-btn-xs layui-btn-normal" href="/apps/report/design/adddata?dtype=cfield&tabid=data&t=dim&cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-height="400" data-width="400" data-title="添加维度成员">
												<i class="layui-icon">&#xe608;</i>
												添加
											</button>
										</span>
								   	</div>
								   	<#if reportModel?? && reportModel.colproperties??>
										<ul id="coldim_sortable" class="uk_sortable connectedSortable">
											<#list reportModel.colproperties as property>
									   		<li class="uk-state-default" id="colsort_${property.id!''}">
									   			${property.dataname!''}
									   			<button class="layui-btn layui-btn-xs layui-btn-danger" style="float:right;" href="/apps/report/design/columndel?id=${property.id!''}" data-toggle="tip" data-title="请确认是否删除？"><i class="layui-icon">&#xe640;</i></button>
									   		</li>
											</#list>
										</ul>
									</#if>
								</div>
				    		<#else>
							<div class="ukefu-design-group">
								<div class="ukefu-design-type">
							   		<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe632;</i>
							   		维度
							   		<span style="float:right;">
										<button class="layui-btn layui-btn-xs layui-btn-normal" href="/apps/report/design/adddata?tabid=data&t=dim&cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-height="400" data-width="400" data-title="添加维度成员">
											<i class="layui-icon">&#xe608;</i>
											添加
										</button>
									</span>
							   	</div>
							   	<#if reportModel?? && reportModel.properties??>
									<ul id="dim_sortable" class="uk_sortable">
										<#list reportModel.properties as property>
								   		<li class="uk-state-default" id="sort_${property.id!''}">
								   			${property.dataname!''}
								   			<button class="layui-btn layui-btn-xs layui-btn-danger" style="float:right;" href="/apps/report/design/columndel?id=${property.id!''}" data-toggle="tip" data-title="请确认是否删除？"><i class="layui-icon">&#xe640;</i></button>
								   		</li>
										</#list>
									</ul>
								</#if>
							</div>
							</#if>
							
							
							<div class="ukefu-design-group">
								<div class="ukefu-design-type">
							   		<i class="layui-icon" style="color:#1E90FF;font-size:20px;">&#xe632;</i>
							   		指标
							   		<span style="float:right;">
										<button class="layui-btn layui-btn-xs layui-btn-normal" href="/apps/report/design/adddata?tabid=data&t=measure&cubeid=<#if cube??>${cube.id!''}</#if>&mid=<#if reportModel??>${reportModel.id!''}</#if>" data-toggle="ajax" data-height="400" data-width="400" data-title="添加指标">
											<i class="layui-icon">&#xe608;</i>
											添加
										</button>
									</span>
							   	</div>
								<#if reportModel?? && reportModel.properties??>
									<ul id="mea_sortable" class="uk_sortable">
										<#list reportModel.measures as measure>
										   	<li class="uk-state-default"  id="sort_${measure.id!''}" title="${measure.dataname!''}">
										   		${measure.title!''}
										   		<a class="layui-btn layui-btn-xs layui-btn-danger" style="float:right;" href="/apps/report/design/columndel?id=${measure.id!''}" data-toggle="tip" data-title="请确认是否删除？"><i class="layui-icon">&#xe640;</i></a>
										   		<a class="layui-btn layui-btn-xs layui-btn-normal" style="float:right;margin-right:5px;" href="/apps/report/design/columnedit?id=${measure.id!''}" data-toggle="ajax" data-width="400" data-height="300"><i class="layui-icon">&#xe614;</i></a>
										   	</li>
										</#list>
									</ul>
								</#if>
							</div>
							</#if>
					</div>
				    <div class="layui-tab-item <#if tabid?? && tabid =='filter'>layui-show</#if>">
				    	<#if reportModel?? && reportModel.publishedcubeid??>
				    		<#include "/apps/business/report/design/modeldesign/filterlist.ftlh"/>
						</#if>
				    </div>
				  </div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
		<#if eltemplet?? && eltemplet.name == '数据表'>
			 $( "#coldim_sortable" ).sortable({
				 connectWith: ".connectedSortable",
				 placeholder: "uk-state-highlight",
				 update: function( event, ui ) {
					 sortableupdate();
				 }
			 })
			 $( "#coldim_sortable" ).disableSelection();
			 
			 $( "#rowdim_sortable" ).sortable({
				 connectWith: ".connectedSortable",
				 placeholder: "uk-state-highlight",
				 update: function( event, ui ) {
					 sortableupdate();
				 }
			 })
			 $( "#rowdim_sortable" ).disableSelection();
			 
			 var sortableupdate = function(){
				 let colsorted = $( "#coldim_sortable" ).sortable( "serialize", { key: "colsort" } );
				 let rowsorted = $( "#rowdim_sortable" ).sortable( "serialize", { key: "rowsort" } );
				 window.location.href= "/apps/report/design/sort?type=dim&modelId=${reportModel.id!''}&"+colsorted+"&"+rowsorted;
			 }
			  
		 <#else>
			$( "#dim_sortable" ).sortable({
				 placeholder: "uk-state-highlight",
				 update: function( event, ui ) {
					 let sorted = $( "#dim_sortable" ).sortable( "serialize", { key: "sort" } );
					 window.location.href= "/apps/report/design/sort?type=dim&modelId=${reportModel.id!''}&"+sorted;
				 }
			 })
			 $( "#dim_sortable" ).disableSelection();
		</#if>
		 $( "#mea_sortable" ).sortable({
			 placeholder: "uk-state-highlight",
			 update: function( event, ui ) {
				 let sorted = $( "#mea_sortable" ).sortable( "serialize", { key: "sort" } );
				 window.location.href= "/apps/report/design/sort?type=dim&modelId=${reportModel.id!''}&"+sorted;
			 }
		 })
		 $( "#mea_sortable" ).disableSelection();
		 $( "#filter_sortable" ).sortable({
			 placeholder: "uk-state-highlight",
			 update: function( event, ui ) {
				 let sorted = $( "#filter_sortable" ).sortable( "serialize", { key: "sort" } );
				 window.location.href= "/apps/report/design/sort?type=filter&modelId=${reportModel.id!''}&"+sorted;
			 }
		 })
		 $( "#filter_sortable" ).disableSelection();
	})
</script>