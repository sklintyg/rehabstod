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
                    fieldHelpText: '@'
                },
                controller: function($scope) {
                    $scope.getMessage = function(key) {
                        return messageService.getProperty(key);
                    };
                },
                templateUrl: 'components/commonDirectives/rhsTooltip/rhsTooltip.directive.html'
            };
        }]);
