/**
 * Enable tooltips for other components than wcFields
 */
angular.module('rehabstodApp').directive('rhsTooltip',
    [ 'messageService',
        function(messageService) {
            'use strict';

            return {
                restrict: 'A',
                transclude: true,
                scope: {
                    fieldHelpText: '@',
                    msgParams: '=',
                    tooltipClass: '@'
                },
                controller: function($scope) {
                    $scope.getMessage = function(key, msgParams) {
                        return messageService.getProperty(key, msgParams);
                    };
                },
                templateUrl: 'components/commonDirectives/rhsTooltip/rhsTooltip.directive.html'
            };
        }]);
