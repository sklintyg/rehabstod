angular.module('rehabstodApp')
    .controller('RhsExportCtrl',
        function($scope, SjukfallService) {
            'use strict';

            $scope.exportExcel = function() {
                SjukfallService.exportResult('xlsx');
            };

            $scope.exportPDF = function() {
                SjukfallService.exportResult('pdf');
            };
        }
    )
    .directive('rhsExport',
        function() {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {},
                controller: 'RhsExportCtrl',
                templateUrl: 'components/appDirectives/sjukfall/rhsExport/rhsExport.directive.html'
            };
        });
