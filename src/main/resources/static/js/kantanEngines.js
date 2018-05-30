mtApp.controller('kantanEngineCtrl', ['$scope', '$http', '$compile', '$document', '$location', 'dialogService', 'alertService',
	function ($scope, $http, $compile, $document, $location, dialogService, alertService) {

	$scope.engines = {};
	$scope.engines.selectedEngine = {};
	$scope.engines.workingEngine = {};
	$scope.engines.kantanEngines = [];

	var theHost = $location.host();

	$scope.engines.serviceHost = "http://"+theHost+":8080/mt/";
	$scope.engines.mtKey = "";
	
	$scope.selectedEngineChanged = function () {
		$scope.engines.workingEngine = angular.copy($scope.engines.selectedEngine);
	}
	
	$scope.getKantanEngines = function (engineId) {
		$http({
			url: $scope.engines.serviceHost+"kantan-engines",
			method: 'GET',
			headers: { 'MT-Key': $scope.engines.mtKey }
		})
		.then(function (response) {
			var tmpArr = response.data.engines;
			tmpArr.sort (function(a, b) {
				if (a.name < b.name) { return -1; }
				if (a.name > b.name) { return 1; }
				return 0;
			});

			// By default select first in list
			$scope.engines.selectedEngine = tmpArr[0];
			$scope.engines.kantanEngines = tmpArr;
			$scope.selectedEngineChanged();
		})
		.catch(function (response) {
			console.log(response);
			var msg = "Sorry, an error occurred when fetching Kantan Engines: "+response.data;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	$scope.getKantanEngines();

}]);