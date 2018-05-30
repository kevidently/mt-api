(function () {
	"use strict";
	angular.module('services').factory('dialogService', ['$http', '$httpParamSerializer', '$uibModal',
		function ($http, $httpParamSerializer, $uibModal) {
		
		return {
			deleteConfigDialog: function (configId) {
				var message = "Are you sure you want to delete the MT config with ID #"+configId+"?";
				var okBtnText = "Delete Config";
				var dlgSize = 'sm';
				return this.warningPrompt(message, okBtnText, dlgSize, false);
			},
			deleteLangDialog: function (langId) {
				var message = "Are you sure you want to delete the MT language with ID #"+langId+"?";
				var okBtnText = "Delete Language";
				var dlgSize = 'sm';
				return this.warningPrompt(message, okBtnText, dlgSize, false);
			},
			warningPrompt:  function (message, okBtnText, dlgSize, needsUserInput) {
				var modalInstance = $uibModal.open({
					templateUrl: 'views/DLG_warning.html',
					controller: 'WarningDialogCtrl',
					size: dlgSize,
					backdrop: 'static',
					resolve: {
						message: function () { return message; },
						okBtnText: function () { return okBtnText; },
						needsUserInput: function () { return needsUserInput; }
					}
				});
				return modalInstance.result;
			}
		}
	}]);
}());