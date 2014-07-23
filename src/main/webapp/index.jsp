<html ng-app="myApp">

<head>
<!-- Required Stylesheets -->
<link href="./css/bootstrap.css" rel="stylesheet">
<link href="./css/angular-toggle-switch.css" rel="stylesheet">
<!-- Required Javascript -->
<script src="./js/angular.min.js"></script>
<script src="./js/ui-bootstrap-tpls-0.11.0.min.js"></script>
<script src="./js/app.js"></script>
<script src="./js/angular-toggle-switch.min.js"></script>
<script src="./js/run_prettify.js"></script>
<script src="./js/ZeroClipboard.js"></script>
<title>PPaas-ASF Mapping Viewer v0.1</title>
</head>
<body>
	<div ng-controller="mapCtrl">
		<script type="text/ng-template" id="myModalContent.html">
        <div class="modal-header">
            <h3 class="modal-title">Generated Code:</h3>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" id="copy-button" ng-click="ok()">Copy</button>
            <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
        </div>
        <div class="modal-body">
            <pre class="prettyprint">{{gencode}}</pre>
        </div>
    </script>
	</div>

	<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#" style="font-size: 22px">PPaas-ASF
					Mapping Viewer</a>

			</div>
			<ul class="nav navbar-nav" style="float: right">
				<li class="active"><a href="#">Doc</a></li>
				<li><a href="#">Contact Us</a></li>
			</ul>
		</div>
	</nav>

	<!-- <nav class="navbar navbar-default" role="navigation">
	<a class="navbar-brand" href="#"><h2>PPaas-ASF Mapping Viewer</h2></a>
	</nav> -->
	<div class="container-fluid" ng-controller="mapCtrl">
		<div class="row">
			<div class="col-lg-6">
				<table width="100%">
					<tr>
						<td><h4>
								<span class="label label-primary">From Class:</span>
							</h4></td>
						<td><select class="form-control" ng-model="from_class"
							ng-change="getData()" ng-options="o as o for o in from_classes"></select>
						</td>
						<td><h4>
								<span class="label label-primary">Mapping Id:</span>
							</h4></td>
						<td><select class="form-control" ng-model="currentMap"
							ng-change="loadMap()" ng-options="map.id for map in mappings"></select></td>
					</tr>
				</table>
			</div>
			<div class="col-lg-4">
				<table width="100%">
					<tr>
						<td><h4>
								<span class="label label-primary">To Class:</span>
							</h4></td>
						<td><input class="form-control" disabled ng-model="to_class" />
						</td>
					</tr>
				</table>
			</div>
			<div class="col-lg-2">
				<table align="center" width="80%">
					<tr>
						<td><button class="btn btn-info" ng-click="genCode()">
								<span class="glyphicon glyphicon-export"></span>Gen Code
							</button></td>
						<td><button class="btn btn-info" ng-click="genTest()">
								<span class="glyphicon glyphicon-export"></span>Gen Test
							</button></td>
					</tr>
				</table>
			</div>


		</div>

		<div class="row">
			<div class="col-lg-6">
				<div id="ltree">
					<h4>
						<span class="label label-primary">Filter:</span>
					</h4>
					<input class="form-control" ng-model="searchText">
					<table class="table table-condensed table-striped table-hover"
						style="table-layout: fixed;" id="ltable">
						<tr>
							<th style="width: 90%">Name</th>
							<th style="width: 10%">Status</th>
						</tr>
						<tr ng-click="setSelected()" class="{{item.highlight_class}}"
							ng-repeat="item in mapping | filter:searchText">
							<td title="Type: {{item.type}}">{{item.name}}</td>
							<td title="Linked To: {{item.mapto}}"><span
								class="label {{item.link_class}}">{{item.link}}</span></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="col-lg-6">
				<h4>
					<span class="label label-primary">Filter:</span>
				</h4>
				<table width="100%">
					<tr>
						<td width="90%"><input class="form-control"
							ng-model="searchTextRight"></td>
						<!-- <td width="10%" align="right"><button class="{{guessClass}}"
								ng-click="guessOnOff()">{{guessStatus}}</button></td> -->
						<td width="10%"><toggle-switch model="guessStatus"
								knob-label="Guess"> </toggle-switch></td>
					</tr>
				</table>
				<table class="table table-condensed table-striped table-hover"
					style="table-layout: fixed;" id="rtable">
					<tr>
						<th style="width: 90%">Name</th>
						<th style="width: 10%">Action</th>
					</tr>
					<tr ng-click="setSelectedRight()" class="{{item.highlight_class}}"
						ng-repeat="item in mappingRight | filter:searchTextRight">
						<td title="Type: {{item.type}}, Mapto: {{item.mapto}}">{{item.name}}</td>
						<td title="Type: {{item.type}}, Mapto: {{item.mapto}}"><button
								ng-click="link()" class="btn {{item.action_class}}">{{item.action}}</button></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>
<footer id="footer"></footer>
</html>
