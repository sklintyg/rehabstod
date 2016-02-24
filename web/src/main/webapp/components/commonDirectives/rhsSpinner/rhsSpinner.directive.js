angular.module('rehabstodApp').directive('rhsSpinner',
    ['$timeout','$window', function() {
        'use strict';

        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                label: '@',
                showSpinner: '='
            },
            templateUrl: 'components/commonDirectives/rhsSpinner/rhsSpinner.directive.html'
        };
    }]);
