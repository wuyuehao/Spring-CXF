var app = angular.module('myApp', ['ui.bootstrap']);

function mapCtrl($scope, $modal, $http, $log) {
	
	$scope.from_class = "com.paypal.api.platform.riskprofileapi.IdentityProfileDecisionRequest";

	$scope.mapping = [];
	$scope.to_class;
	$scope.mappingRight = [];
	$scope.mappings = [];
	$scope.currentMap;
	$scope.guessStatus = "Guess On";
	$scope.guessClass = "btn btn-success";
	
	$scope.guessOnOff = function() {
		if($scope.guessStatus == "Guess On"){
			$scope.guessStatus = "Guess Off";
			$scope.guessClass = "btn btn-default";
		}else{
			$scope.guessStatus = "Guess On";
			$scope.guessClass = "btn btn-success";
		}
	}

	$scope.setSelected = function() {
		$scope.selected = this.item;
		angular.forEach($scope.mapping, function(obj) {
			obj.highlight_class = "";
		});
		$scope.selected.highlight_class = "info";
		if ($scope.guessStatus == "Guess On") {
			if (this.item.mapto) {
				$scope.searchTextRight = this.item.mapto;
			} else {
				$scope.searchTextRight = this.item.name;
			}
		}
		console.log($scope.selected);
		$scope.checkSelected();
	};

	$scope.setSelectedRight = function() {
		$scope.selectedRight = this.item;
		angular.forEach($scope.mappingRight, function(obj) {
			obj.highlight_class = "";
		});
		$scope.selectedRight.highlight_class = "info";

		// $scope.searchText = this.item.mapfrom;
		console.log($scope.selectedRight);
		$scope.checkSelected();
	};

	$scope.checkSelected = function() {
		if ($scope.selectedRight && $scope.selected) {
			angular.forEach($scope.mappingRight, function(obj) {
				if (obj.action_class.indexOf("disabled") < 0) {
					obj.action_class += " disabled";
				}
			});

			if ($scope.selectedRight.action == "Link"
					|| $scope.selected.mapto == $scope.selectedRight.name) {
				var index = $scope.selectedRight.action_class.lastIndexOf(" ")
				$scope.selectedRight.action_class = $scope.selectedRight.action_class
						.substring(0, index);
			}
		}
	};

	$scope.loadMappings = function() {
		var httpRequest = $http({
			method : 'GET',
			url : '/rs/mapping/class/' + $scope.from_class,
			contentType : "application/json",
			accepts : "application/json"
		}).success(function(data, status) {
			$scope.mappings = data;
			$scope.currentMap = data[data.length - 1];
		});
	};

	$scope.getData = function(id) {
		if (!id) {
			id = 0;
		}
		var httpRequest = $http({
			method : 'GET',
			url : '/rs/inspector/data/' + $scope.from_class + "?map_id=" + id,
			contentType : "application/json",
			accepts : "application/json"
		}).success(function(data, status) {
			$scope.mapping = data.mapping;
			angular.forEach($scope.mapping, function(obj) {
				if (obj.mapto != undefined) {
					obj.link = "Linked";
					obj.link_class = "label-success";
				} else {
					obj.link = "Unlinked";
					obj.link_class = "label-default";
				}

			});
			$scope.to_class = data.to_class;
			$scope.getDataRight(id);
			$scope.loadMappings();
		});
	};

	$scope.getDataRight = function(id) {
		if (!id) {
			id = 0;
		}
		var httpRequest = $http({
			method : 'GET',
			url : '/rs/inspector/data/' + $scope.to_class + "?map_id=" + id,
			contentType : "application/json",
			accepts : "application/json"
		}).success(function(data, status) {
			$scope.mappingRight = data.mapping;
			angular.forEach($scope.mappingRight, function(obj) {
				if (obj.mapto != undefined) {
					obj.action = "Unlink";
					obj.action_class = "btn-xs btn-danger disabled";
					obj.link = "Linked";
					obj.link_class = "label-success";
				} else {
					obj.action = "Link";
					obj.action_class = "btn-xs btn-success disabled";
					obj.link = "Unlinked";
					obj.link_class = "label-default";
				}
			});
		});
	};

	$scope.link = function() {
		var url = '/rs/mapping/' + $scope.currentMap.id + '?key='
				+ $scope.selected.name + "&value=" + $scope.selectedRight.name
				+ "&type=" + $scope.selected.type + "&to_type="
				+ $scope.selectedRight.type;
		if (this.item.action == "Unlink") {
			url = url + "&remove=true";
		}
		var httpRequest = $http({
			method : 'PATCH',
			url : url,
			contentType : "application/json",
			accepts : "application/json"
		}).success(function(data, status) {
			if ($scope.selectedRight.action == "Link") {
				// just linked
				$scope.selected.to_type = $scope.selectedRight.type;
				$scope.selected.mapto = $scope.selectedRight.name;
				$scope.selected.link = "Linked";
				$scope.selected.link_class = "label-success";
				$scope.selectedRight.link = "Linked";
				$scope.selectedRight.link_class = "label-success";
				$scope.selectedRight.action = "Unlink";
				$scope.selectedRight.action_class = "btn-xs btn-danger";
			} else {
				// just unlinked
				$scope.selected.to_type = "";
				$scope.selected.mapto = "";
				$scope.selected.link = "Unlinked";
				$scope.selected.link_class = "label-default";
				$scope.selectedRight.link = "Unlinked";
				$scope.selectedRight.link_class = "label-default";
				$scope.selectedRight.action = "Link";
				$scope.selectedRight.action_class = "btn-xs btn-success";
			}
		});
	};

	$scope.loadMap = function() {
		$scope.getData($scope.currentMap.id);
	};

	$scope.genCode = function() {
		var url = '/rs/codegen/' + $scope.currentMap.id;
		var httpRequest = $http({
			method : 'GET',
			url : url,
			contentType : "application/json",
			accepts : "application/json",
			data : $scope.mapping
		}).success(function(data, status) {
			$scope.open('lg', data);
		});
	};
	
	
	 $scope.open = function (size, data) {
		    var modalInstance = $modal.open({
		      templateUrl: 'myModalContent.html',
		      controller: ModalInstanceCtrl,
		      size: size,
		      resolve: {
		        items: function () {
		          return data;
		        }
		      }
		    });

		    modalInstance.result.then(function (selectedItem) {
		      $scope.selected = selectedItem;
		    }, function () {
		      $log.info('Modal dismissed at: ' + new Date());
		    });
		  };

}

var ModalInstanceCtrl = function ($scope, $modalInstance, items) {

	  $scope.gencode = items;

	  $scope.ok = function () {
	    $modalInstance.close($scope.selected.item);
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };
	};