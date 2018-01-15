/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
                templateUrl: '/components/appDirectives/sjukfall/rhsExport/rhsExport.directive.html'
            };
        });
