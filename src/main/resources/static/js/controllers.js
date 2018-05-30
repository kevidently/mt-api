mtApp.controller('WarningDialogCtrl', function ($scope, $uibModalInstance, message, okBtnText, needsUserInput) {
	$scope.message = message;
	$scope.okBtnText = okBtnText;
	$scope.needsUserInput = needsUserInput;
	$scope.userInput = null;
	
	$scope.ok = function () {
		if ( $scope.needsUserInput ) {
			$uibModalInstance.close($scope.userInput);
		}
		else {
			$uibModalInstance.close('ok');
		}
	};
	
	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});