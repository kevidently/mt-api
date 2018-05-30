mtApp.controller('mtConfigCtrl', ['$scope', '$http', '$compile', '$document', '$location', 'dialogService', 'alertService',
	function ($scope, $http, $compile, $document, $location, dialogService, alertService) {

	$scope.data = {};
	$scope.data.selectedConfig = {};
	$scope.data.workingConfig = {};
	$scope.data.mtConfigs = [];

	var theHost = $location.host();

	$scope.data.serviceHost = "http://"+theHost+":8080/mt/";
	$scope.data.isConfigValid = false;
	$scope.data.mtKey = '010448c6-b350-41fd-93cb-5bcb2a317057';
	
	$scope.data.textCreateNewDescription = "--- CREATE NEW MT CONFIGURATION ---";
	$scope.data.textCreateNewId = "Auto-Generated";
	$scope.data.textNeedNewSequence = "*** ENTER NEW SEQUENCE ***";
	
	$scope.data.emptyConfig = {
		ClientId: null,
		ConnectorId: null,
		Description: $scope.data.textCreateNewDescription,
		Id: $scope.data.textCreateNewId,
		Params: null,
		Source: null,
		Target: null,
		Variant: null,
		Sequence: null,
		Site: null,
	};

	$scope.checkConfigValid = function () {
		var isValid = true;

		if ( $scope.data.workingConfig.Sequence == $scope.data.textNeedNewSequence ) { 
			$(document).find('#Sequence').addClass('borderRed');
			isValid = false; 
		}
		else {
			$(document).find('#Sequence').removeClass('borderRed');
			for ( var i in $scope.data.workingConfig ) {
				if ( i == "Variant" || i == "Site") { continue; } // These can be null
				if ( $scope.data.workingConfig[i] === null || $scope.data.workingConfig[i] === "" ) {
					isValid = false;
					break;
				}
			}
		}
		$scope.data.isConfigValid = isValid;
	}
	
	$scope.selectedConfigChanged = function () {
		$scope.data.workingConfig = angular.copy($scope.data.selectedConfig);
		if ( $scope.data.workingConfig.Id == $scope.data.textCreateNewId ) {
			$scope.data.workingConfig.Description = null;
		}
		$scope.checkConfigValid();
	}
	
	$scope.cloneConfig = function () {
		// Capture the orig/starting config
		var origConfig = angular.copy($scope.data.selectedConfig);
		
		// Need to set selected config to 'create new'
		for ( var i in $scope.data.mtConfigs ) {
			if ( $scope.data.mtConfigs[i].Id == $scope.data.textCreateNewId ) {
				$scope.data.selectedConfig = $scope.data.mtConfigs[i];
			}
		}
		
		// Now set the working config to be the starting config
		$scope.data.workingConfig = origConfig;
		$scope.data.workingConfig.Id = $scope.data.textCreateNewId;
		$scope.data.workingConfig.Sequence = $scope.data.textNeedNewSequence;
		$scope.data.workingConfig.Description += " -- COPY";
		
		$scope.checkConfigValid();
	}
	
	// CRUD operations // 
	
	$scope.getMTConfigs = function (configId) {
		$http({
			url: $scope.data.serviceHost+"configs",
			method: 'GET',
			headers: { 'MT-Key': $scope.data.mtKey }
		})
		.then(function (response) {
			var tmpArr = response.data;
			tmpArr.sort (function(a, b) {
				if (a.Sequence < b.Sequence) { return -1; }
				if (a.Sequence > b.Sequence) { return 1; }
				return 0;
			});

			// By default select first in list
			$scope.data.selectedConfig = tmpArr[0];

			for ( var i in tmpArr ) {
				var tmpConfig = tmpArr[i];

				// Try better formatting for the Params section
				var tmpParams = JSON.parse(tmpConfig.Params);
				tmpConfig.Params = JSON.stringify(tmpParams, null, 1);
				
				// Check if we were updating an existing config
				if ( configId && configId == tmpConfig.Id ) {
					$scope.data.selectedConfig = tmpConfig;
				}
			}
			
			tmpArr.push($scope.data.emptyConfig);
			$scope.data.mtConfigs = tmpArr;
			$scope.selectedConfigChanged();
		})
		.catch(function (response) {
			console.log(response);
			var msg = "Sorry, an error occurred when fetching configs: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	$scope.getMTConfigs();

	$scope.deleteConfig = function () {
		$http({
			url: $scope.data.serviceHost+"config/"+$scope.data.workingConfig.Id,
			method: 'DELETE',
			headers: { 'MT-Key': $scope.data.mtKey },
			data: ''
		})
		.then(function (response) {
			$scope.getMTConfigs();
		})
		.catch(function (response) { // error
			console.log(response);
			var msg = "Sorry, an error occurred when deleting config: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	
	$scope.deleteConfigDialog = function () {
		dialogService.deleteConfigDialog($scope.data.workingConfig.Id)
		.then(function (response) {
			$scope.deleteConfig();
		})
		.catch(function (response) {
			return;
		});
	};

	$scope.saveConfig = function (clickEvent) {

		// Copy the working config to payload config so we can adjust things without affecting the view
		$scope.data.payloadConfig = angular.copy($scope.data.workingConfig);
		
		var methodType = "PUT";
		if ( $scope.data.payloadConfig.Id == $scope.data.textCreateNewId ) {
			methodType = "POST";
			$scope.data.payloadConfig.Id = null;
		}

		// Do the save
		$http({
			url: $scope.data.serviceHost+"config",
			method: methodType,
			headers: { 'MT-Key': $scope.data.mtKey },
			data: $scope.data.payloadConfig
		})
		.then(function (response) {
			var savedConfig = response.data;
			alertService.showSuccessBox(clickEvent, savedConfig.Id, $scope);
			$scope.getMTConfigs(savedConfig.Id);
		})
		.catch(function (response) { // error
			console.log(response);
			var msg = "Sorry, an error occurred when saving config: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	
	$scope.synchronizeKantan = function () {
		$http({
			url: $scope.data.serviceHost+"synchronized-configs",
			method: 'GET',
			headers: { 'MT-Key': $scope.data.mtKey }
		})
		.then(function (response) {
			console.log(response.data.engines);
			$scope.getMTConfigs();
		})
		.catch(function (response) {
			console.log(response);
			var msg = "Sorry, an error occurred when fetching Kantan engines: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
}]);