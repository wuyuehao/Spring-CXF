<html ng-app="myApp">

<head>
<!-- Required Stylesheets -->
<link href="./css/bootstrap.css" rel="stylesheet">
<!-- Required Javascript -->
<script src="./js/angular.min.js"></script>
<script src="./js/ui-bootstrap-tpls-0.11.0.min.js"></script>
<script src="./js/admin.js"></script>
<title>PPaas-ASF Mapping Viewer v0.1 - Admin Console</title>
</head>

<body>
	<h2>PPaas-ASF Mapping Viewer - Admin Console</h2>
	<div class="container-fluid" ng-controller="adminCtrl" ng-init="init()">
		<div class="row">
			<div class="col-lg-8">
				<table width="100%">
					<tr>
						<td>From:</td>
						<td><input class="form-control" type="text"
							ng-model="from_class" /></td>
					</tr>
					<tr>
						<td>To:</td>
						<td><input class="form-control" type="text"
							ng-model="to_class" /></td>
					</tr>
					<tr>
						<td>Package for code gen:</td>
						<td><input class="form-control" type="text"
							ng-model="pkg_code" /></td>
					</tr>
					<tr>
						<td>Package for UT:</td>
						<td><input class="form-control" type="text"
							ng-model="pkg_ut" /></td>
					</tr>
					<tr>
						<td colspan="2"><button class="btn btn-info"
								ng-click="createMapping()">Submit</button></td>
					</tr>

					<tr>
						<td colspan="2">{{result}}</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="row">
			<div class="col-lg-12">
				<table class="table table-condensed table-striped table-hover" width="100%">
					<tr>
						<th style="width: 4%">ID</th>
						<th style="width: 28%">From</th>
						<th style="width: 28%">To</th>
						<th style="width: 20%">Mapper Package</th>
						<th style="width: 20%">UT Package</th>
					</tr>
					<tr ng-click="edit()"
						ng-repeat="item in mappings">
						<td >{{item.id}}</td>
						<td >{{item.fromClass}}</td>
						<td >{{item.toClass}}</td>
						<td >{{item.pkg4Code}}</td>
						<td >{{item.pkg4UT}}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>
<footer id="footer"></footer>
</html>
