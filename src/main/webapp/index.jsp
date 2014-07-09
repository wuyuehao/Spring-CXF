<html ng-app="myApp" >

  <head>
	<!-- Required Stylesheets -->
	<link href="./css/bootstrap.css" rel="stylesheet">
	<!-- Required Javascript -->
	<script src="./js/angular.min.js"></script>
	<script src="./js/ui-bootstrap-tpls-0.11.0.min.js"></script>
	<script src="./js/app.js"></script>
  </head>

<body>
<div ng-controller="mapCtrl">
    <script type="text/ng-template" id="myModalContent.html">
        <div class="modal-header">
            <h3 class="modal-title">Generated Code:</h3>
        </div>
        <div class="modal-body">
            <div class="well">{{gencode}}
</div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="ok()">OK</button>
            <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
        </div>
    </script>
</div>
	<h2>PPaas-ASF Mapping Viewer</h2>
	<div class="container-fluid" ng-controller="mapCtrl">
		<div class="row">
			<div class="col-lg-5">
				<table width="100%">
					<tr>
						<td>From:</td>
						<td><input class="form-control" type="text" id="ltext"
							value="{{from_class}}" /></td>
					</tr>
				</table>
			</div>
			<div class="col-lg-3">
				<p align="center">
					<button class="btn btn-info" ng-click="getData()">Get Data</button>
					<select ng-model="currentMap"
						ng-options="map.id for map in mappings"></select>
					<button class="btn btn-info" ng-click="loadMap()">Load Map</button>
					<button class="btn btn-info" ng-click="genCode()">
						<span class="glyphicon glyphicon-export"></span>Gen Code
					</button>
				</p>
			</div>
			<div class="col-lg-4">
				<table width="100%">
					<tr>
						<td>To:</td>
						<td>
							<p class="form-control-static">{{to_class}}</p>
						</td>
					</tr>
				</table>
			</div>
			
		</div>

		<div class="row">
			<div class="col-lg-6">
				<div id="ltree">
					Filter: <input class="form-control" ng-model="searchText">
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
			Filter: 
				<table width="100%">
				<tr>
				    <td width="90%"><input class="form-control" ng-model="searchTextRight"></td>
				    <td width="10%" align="right"><button class="{{guessClass}}" ng-click="guessOnOff()">{{guessStatus}}</button></td>
				    </tr></table>
				<table class="table table-condensed table-striped table-hover"
					style="table-layout: fixed;" id="rtable">
					<tr>
						<th style="width: 80%">Name</th>
						<th style="width: 10%">Status</th>
						<th style="width: 10%">Action</th>
					</tr>
					<tr ng-click="setSelectedRight()" class="{{item.highlight_class}}"
						ng-repeat="item in mappingRight | filter:searchTextRight">
						<td title="Type: {{item.type}}, Mapto: {{item.mapto}}">{{item.name}}</td>
						<td title="Linked To: {{item.mapto}}"><span
							class="label {{item.link_class}}">{{item.link}}</span></td>
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
