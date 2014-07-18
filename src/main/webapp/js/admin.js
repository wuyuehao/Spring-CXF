var app = angular.module('myApp', []);

function adminCtrl($scope, $http, $log) {
	
	$scope.from_class = "com.paypal.api.platform.riskprofileapi.IdentityProfileDecisionRequest";
	
	$scope.to_class = "com.paypal.riskprofilechanges.EvaluateIdentityProfileChangeRequest";
	
	
	$scope.result = "Result";
	
	$scope.createMapping = function() {
		var httpRequest = $http({
			method : 'POST',
			url : '/rs/mapping/',
			contentType : "application/json",
			accepts : "application/json",
			data: {
                fromClass: $scope.from_class,
                toClass: $scope.to_class
            }
		}).success(function(data, status) {
			$scope.mappings = data;
			angular.forEach(data, function(obj) {
				$scope.result = "mapping created with id : " + data.idCreated;
			});
		});
	}
}