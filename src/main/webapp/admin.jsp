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
	<div class="container-fluid" ng-controller="adminCtrl">
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
						<td colspan="2"><button class="btn btn-info" ng-click="createMapping()">Submit</button></td>
					</tr>
					
					<tr><td colspan="2">{{result}}</td></tr>
				</table>
			</div>
		</div>
	</div>
</body>
<footer id="footer"></footer>
</html>
