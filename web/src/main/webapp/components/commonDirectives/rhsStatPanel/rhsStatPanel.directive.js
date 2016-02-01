angular.module('rehabstodApp').directive('rhsStatPanel',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    $scope.men = 36;
                    $scope.women = 64;
                    $scope.total = 1100;

                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
