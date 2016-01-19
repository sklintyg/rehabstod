/**
 * Show an alert that can be closed
 */
angular.module('rehabstodApp').directive('alertSingle',
    [
        function() {
            'use strict';

            return {
                restrict: 'A',
                transclude: true,
                scope: {
                    'alertModel' : '=',
                    'alertMessageId' : '@'
                },
                controller: function($scope) {
                    $scope.close = function() {
                        $scope.alertModel = false;
                    };
                },
                templateUrl: 'components/commonDirectives/alertSingle/alertSingle.directive.html'
            };
        }]);
