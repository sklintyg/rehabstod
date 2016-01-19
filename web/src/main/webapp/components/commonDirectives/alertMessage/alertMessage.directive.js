/**
 * Show an alert that can be closed
 */
angular.module('rehabstodApp').directive('alertMessage',
    [
        function() {
            'use strict';

            return {
                restrict: 'A',
                scope: {
                    'alertShow' : '=',
                    'alertMessageId' : '@',
                    'alertSeverity' : '@'
                },
                controller: function($scope) {
                    if($scope.alertShow === undefined) {
                        $scope.alertShow = true;
                    }
                },
                templateUrl: 'components/commonDirectives/alertMessage/alertMessage.directive.html'
            };
        }]);
