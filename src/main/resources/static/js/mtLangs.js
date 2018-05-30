mtApp.controller('mtLangCtrl', ['$scope', '$http', '$compile', '$document', '$location', 'dialogService', 'alertService',
	function ($scope, $http, $compile, $document, $location, dialogService, alertService) {

	$scope.langs = {};
	$scope.langs.selectedLang = {};
	$scope.langs.workingLang = {};
	$scope.langs.mtLangs = [];

	var theHost = $location.host();

	$scope.langs.serviceHost = "http://"+theHost+":8080/mt/";
	$scope.langs.isLangValid = false;
	$scope.langs.mtKey = "";
	
	$scope.langs.textCreateNewPair = "--- CREATE NEW MT LANGUAGE ---";
	$scope.langs.textCreateNewId = "Auto-Generated";
	
	$scope.langs.emptyLang = {
		Id: $scope.langs.textCreateNewId,
		Pair: $scope.langs.textCreateNewPair,
		Source: null,
		Target: null
	};

	$scope.checkLangValid = function () {
		var isValid = true;
		for ( var i in $scope.langs.workingLang ) {
			if ( $scope.langs.workingLang[i] === null || $scope.langs.workingLang[i] === "" ) {
				isValid = false;
				break;
			}
		}
		$scope.langs.isLangValid = isValid;
	}
	
	$scope.selectedLangChanged = function () {
		$scope.langs.workingLang = angular.copy($scope.langs.selectedLang);
		$scope.checkLangValid();
	}
	
	$scope.cloneLang = function () {
		// Capture the orig/starting lang
		var origLang = angular.copy($scope.langs.selectedLang);
		
		// Need to set selected lang to 'create new'
		for ( var i in $scope.langs.mtLangs ) {
			if ( $scope.langs.mtLangs[i].Id == $scope.langs.textCreateNewId ) {
				$scope.langs.selectedLang = $scope.langs.mtLangs[i];
			}
		}
		
		// Now set the working lang to be the starting lang
		$scope.langs.workingLang = origLang;
		$scope.langs.workingLang.Id = $scope.langs.textCreateNewId;
		
		$scope.checkLangValid();
	}
	
	// CRUD operations // 
	
	$scope.getMTLangs = function (langId) {
		$http({
			url: $scope.langs.serviceHost+"langs",
			method: 'GET',
			headers: { 'MT-Key': $scope.langs.mtKey }
		})
		.then(function (response) {
			var tmpArr = response.data;
			tmpArr.sort (function(a, b) {
				if (a.Id < b.Id) { return -1; }
				if (a.Id > b.Id) { return 1; }
				return 0;
			});

			// By default select first in list
			$scope.langs.selectedLang = tmpArr[0];

			for ( var i in tmpArr ) {
				var tmpLang = tmpArr[i];
				
				var srcTargLangs = tmpLang.Pair.split(":");
				tmpLang.Source = srcTargLangs[0];
				tmpLang.Target = srcTargLangs[1];
				
				// Check if we were updating an existing lang
				if ( langId && langId == tmpLang.Id ) {
					$scope.langs.selectedLang = tmpLang;
				}
			}
			
			tmpArr.push($scope.langs.emptyLang);
			$scope.langs.mtLangs = tmpArr;
			$scope.selectedLangChanged();
		})
		.catch(function (response) {
			console.log(response);
			var msg = "Sorry, an error occurred when fetching langs: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	$scope.getMTLangs();

	$scope.deleteLang = function () {
		$http({
			url: $scope.langs.serviceHost+"lang/"+$scope.langs.workingLang.Id,
			method: 'DELETE',
			headers: { 'MT-Key': $scope.langs.mtKey },
			data: ''
		})
		.then(function (response) {
			$scope.getMTLangs();
		})
		.catch(function (response) { // error
			console.log(response);
			var msg = "Sorry, an error occurred when deleting lang: "+response.data.message;
			dialogService.warningPrompt(msg, 'OK', 'md', false)
			.then(function (response) {
				return;
			})
			.catch(function (response) {
				return;
			});
		})
	}
	
	$scope.deleteLangDialog = function () {
		dialogService.deleteLangDialog($scope.langs.workingLang.Id)
		.then(function (response) {
			$scope.deleteLang();
		})
		.catch(function (response) {
			return;
		});
	};

	$scope.saveLang = function (clickEvent) {

		// Copy the working lang to payload lang so we can adjust things without affecting the view
		$scope.langs.payloadLang = angular.copy($scope.langs.workingLang);
		
		// Set the lang Pair with src and trg
		$scope.langs.payloadLang.Pair = $scope.langs.payloadLang.Source + ":" + $scope.langs.payloadLang.Target;
		
		var methodType = "PUT";
		if ( $scope.langs.payloadLang.Id == $scope.langs.textCreateNewId ) {
			methodType = "POST";
			$scope.langs.payloadLang.Id = null;
		}

		// Do the save
		$http({
			url: $scope.langs.serviceHost+"lang",
			method: methodType,
			headers: { 'MT-Key': $scope.langs.mtKey },
			data: $scope.langs.payloadLang
		})
		.then(function (response) {
			var savedLang = response.data;
			alertService.showSuccessBox(clickEvent, savedLang.Id, $scope);
			$scope.getMTLangs(savedLang.Id);
		})
		.catch(function (response) { // error
			console.log(response);
			var msg = "Sorry, an error occurred when saving lang: "+response.data.message;
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