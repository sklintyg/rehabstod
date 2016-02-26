angular.module('rehabstodApp')
    .directive('rhsExport',
        function(SjukfallService) {
            'use strict';

            function getPersonnummer(table) {
                var data = [];
                angular.forEach(table.getFilteredCollection(), function(row) {
                    data.push(row.patient.id);
                });

                return data;
            }

            function getSort(table) {
                var state = table.tableState();


                var sortOrder = null;
                var column = state.sort.predicate;
                if (column) {
                    sortOrder = state.sort.reverse ? 'desc' : 'asc';
                }

                return {
                    kolumn: column,
                    order: sortOrder
                };
            }

            return {
                restrict: 'E',
                replace: true,
                require: '^stTable',
                scope: {},
                link: function($scope, element, attr, table) {
                    $scope.disabled = false;
                    $scope.table = table;

                    $scope.$watch('table.getFilteredCollection().length', function(val) {
                        $scope.disabled = val < 1;
                    });

                    $scope.exportExcel = function() {
                        SjukfallService.exportResult('xlsx', getPersonnummer(table), getSort(table));
                    };

                    $scope.exportPDF = function() {
                        SjukfallService.exportResult('pdf', getPersonnummer(table), getSort(table));
                    };
                },
                templateUrl: 'components/appDirectives/sjukfall/rhsExport/rhsExport.directive.html'
            };
        });
