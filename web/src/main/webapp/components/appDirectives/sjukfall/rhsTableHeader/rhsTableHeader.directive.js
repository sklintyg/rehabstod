angular.module('rehabstodApp').directive('rhsTableHeader',
    function() {
        'use strict';

        return {
            restrict: 'A',
            scope: {
                user : '=',
                glapp : '='
            },
            templateUrl: 'components/appDirectives/sjukfall/rhsTableHeader/rhsTableHeader.directive.html'
        };
    });