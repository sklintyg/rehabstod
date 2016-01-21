/**
 * Enable tooltips for other components than wcFields
 */
angular.module('rehabstodApp').directive('rhsStatCircle',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                transclude: true,
                scope: {
                    size: '@',
                    backgroundColor: '@'
                },
                controller: function(/*scope*/) {

                },
                templateUrl: 'components/commonDirectives/rhsStatCircle/rhsStatCircle.directive.html'
            };
        }]);
