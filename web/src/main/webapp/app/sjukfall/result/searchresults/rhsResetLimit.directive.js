angular.module('rehabstodApp').directive('rhsStTableSearchConnector',
        function() {
            'use strict';

            return {
                restrict: 'E',
                require: '^stTable',
                link: function($scope, element, attr, table) {
                    var changeLimit = function() {
                        $scope.resetLimit();
                    };

                    $scope.table  = table;
                    $scope.$watch('table.tableState().search', changeLimit, true);
                    $scope.$watch('table.tableState().sort', changeLimit, true);
                }
            };
        });
