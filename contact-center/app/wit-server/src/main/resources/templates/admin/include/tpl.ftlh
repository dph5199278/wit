<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta http-equiv="Cache-Control" content="no-siteapp" />
	<meta name="viewport"
		content="width=device-width, maximum-scale=1.0, initial-scale=1.0,initial-scale=1.0,user-scalable=no" />
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<title>智文客服-全渠道智能客服</title>
	<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.ico?t=1487250759056"/>
	<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
	<link rel="stylesheet" href="/css/flexboxgrid.min.css">
	<link rel="stylesheet" href="/layui/css/layui.css">
	<link rel="stylesheet" href="/res/css">
	
	<link rel="stylesheet" href="/css/flexboxgrid.min.css">
	<script src="/js/jquery-1.10.2.min.js"></script>
	<script src="/js/jquery.form.js"></script>
	<script src="/js/ztree/jquery.ztree.all.min.js"></script>
	<script src="/js/echarts.common.min.js"></script>
	<script src="/js/theme/wonderland.js"></script>
	<script src="/layui/layui.js"></script>
	<script src="/js/cskefu.js"></script>
</head>

<body>
	<div class="layui-layout layui-layout-content">
		<div class="layui-side layui-bg-black">
			<div class="layui-side-scroll">
			<#include "/admin/include/left.ftlh"/></div>
		</div>
		<div class="layui-body">
			<div class="layui-side-scroll"><#include "${data.page}.ftlh"/>
			</div>
		</div>
	</div>
	<script>
		layui.use('element', function() {
			var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
			//监听导航点击
			element.on('nav(layui)', function(elem) {
				
			});
		});
	</script>
</body>

</html>
