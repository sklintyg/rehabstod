angular.module('rehabstodApp').directive('rhsResetLimit',
        function() {
            'use strict';

            return {
                restrict: 'E',
                require: '^stTable',
                scope: {
                    onChange: '&'
                },
                link: function($scope, element, attr, table) {
                    var changeLimit = function() {
                        $scope.onChange();
                    };

                    $scope.table  = table;
                    $scope.$watch('table.tableState().search', changeLimit, true);
                    $scope.$watch('table.tableState().sort', changeLimit, true);
                }
            };
        });
