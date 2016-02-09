angular.module('rehabstodApp').directive('rhsTableGrad',
    [
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    active: '=',
                    grader: '='
                },
                link: function ($scope) {


                    $scope.isActive = function(grad) {

                        return $scope.active === grad;
                    };
                },
                templateUrl: 'components/commonDirectives/rhsTableGrad/rhsTableGrad.directive.html'
            };
        }]);
