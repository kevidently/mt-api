(function () {
	"use strict";
	angular.module('services').factory('alertService', ['$document', '$compile', function ($document, $compile) {
		
		// Function to handle end of animation event
		function listener (event) {
			if ( event.type == "animationend" ) {
				$("div").remove(".successBox");
				$("div").remove(".errorBox");
			}
		}
		
		return {
			showSuccessBox: function ( event, itemId, curScope ) {
				var clickedX = event.originalEvent.pageX;
				var clickedY = event.originalEvent.pageY;
				if ( !itemId ) { itemId = ''};
				var successBox = 
					'<div class="successBox" id="successBox">' +
					'<div class="innerBoxText">' +
					'Item ID #' + itemId + ' Saved' +
					'</div>' +
					'</div>';
				this.addAlertBox(successBox, clickedX, clickedY, curScope);
			},
			showErrorBox: function ( event, curScope ) {
				var clickedX = event.originalEvent.pageX;
				var clickedY = event.originalEvent.pageY;
				var errorBox = 
					'<div class="errorBox" id="errorBox">' +
					'<div class="innerBoxText">' +
					'An Error Occurred' +
					'</div>' +
					'</div>';
				this.addAlertBox(errorBox, clickedX, clickedY, curScope);
			},
			addAlertBox: function ( alertBox, clickedX, clickedY, curScope ) {
				var alertBoxCompiled = angular.element( $compile( alertBox )( curScope ) );
				alertBoxCompiled[0].addEventListener("animationend", listener, false);
				if ( clickedX && clickedY ) {
					var offsetY = ( clickedY - 120 );
					alertBoxCompiled[0].style.left = clickedX+"px";
					alertBoxCompiled[0].style.top = offsetY+"px";
				}
				else {
					// If somehow we don't have the right values set, by default place alert in center of window
					alertBoxCompiled[0].style.left = "50%";
					alertBoxCompiled[0].style.top = "50%";
				}
				alertBoxCompiled[0].style.transform = "translate(-50%, -50%)";
				var body = $document.find('body').eq(0);
				body.append(alertBoxCompiled);
			}
		}
	}]);
}());