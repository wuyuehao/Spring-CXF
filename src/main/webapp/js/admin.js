var app = angular.module('myApp', []);

function adminCtrl($scope, $http, $log) {

	$scope.from_class = "com.paypal.api.platform.riskprofileapi.IdentityProfileDecisionRequest";

	$scope.to_class = "com.paypal.riskprofilechanges.EvaluateIdentityProfileChangeRequest";
	$scope.pkg_code = "com.paypal.services.rs.riskprofile";
	$scope.pkg_ut = "com.paypal.services.rs.riskprofile";

	$scope.result = "Result";

	$scope.createMapping = function() {
		var httpRequest = $http({
			method : 'POST',
			url : '/rs/mapping/',
			contentType : "application/json",
			accepts : "application/json",
			data : {
				fromClass : $scope.from_class,
				toClass : $scope.to_class,
				pkg4Code : $scope.pkg_code,
				pkg4UT : $scope.pkg_ut
			}
		}).success(function(data, status) {
			$scope.result = "mapping created with id : " + data.idCreated;
		});
	}

	$scope.init = function() {

		var httpRequest = $http({
			method : 'GET',
			url : '/rs/mapping/',
			contentType : "application/json",
			accepts : "application/json"
		}).success(function(data, status) {
			$scope.mappings = data;
		});

	}

}