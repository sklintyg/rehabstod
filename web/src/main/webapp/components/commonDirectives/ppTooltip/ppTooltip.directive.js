/**
 * Enable tooltips for other components than wcFields
 */
angular.module('privatlakareApp').directive('ppTooltip',
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
                templateUrl: 'components/commonDirectives/ppTooltip/ppTooltip.directive.html'
            };
        }]);
