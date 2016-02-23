angular.module('rehabstodApp')
    .directive('rhsExport',
        function(SjukfallService) {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                require: '^stTable',
                scope: {},
                link: function($scope, element, attr, table) {

                    function getPersonnummer() {
                        var data = [];
                        angular.forEach(table.getFilteredCollection(), function(row) {
                            data.push(row.patient.id);
                        });

                        return data;
                    }

                    $scope.exportExcel = function() {
                        SjukfallService.exportResult('xlsx', getPersonnummer());
                    };

                    $scope.exportPDF = function() {
                        SjukfallService.exportResult('pdf', getPersonnummer());
                    };
                },
                templateUrl: 'components/appDirectives/sjukfall/rhsExport/rhsExport.directive.html'
            };
        });
