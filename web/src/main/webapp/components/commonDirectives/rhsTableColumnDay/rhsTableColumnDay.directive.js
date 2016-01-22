angular.module('rehabstodApp').directive('rhsTableColumnDay',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    days: '@'
                },
                controller: function($scope) {
                    $scope.$watch('days', function(val) {
                        if (val > 1) {
                            $scope.suffix = 'dagar';
                        } else {
                            $scope.suffix = 'dag';
                        }
                    });
                },
                templateUrl: 'components/commonDirectives/rhsTableColumnDay/rhsTableColumnDay.directive.html'
            };
        });
