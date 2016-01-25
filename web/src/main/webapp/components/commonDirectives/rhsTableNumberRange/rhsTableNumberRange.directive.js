angular.module('rehabstodApp').directive('rhsTableNumberRange',
    ['$timeout',
        function($timeout) {
            'use strict';

            return {
                restrict: 'E',
                require: '^stTable',
                scope: {
                    lower: '=',
                    higher: '='
                },
                link: function ($scope, element, attr, table) {
                    var predicateName = attr.predicate;

                    var runQuery = function() {
                        var query = {};

                        if ($scope.lower) {
                            query.lower = $scope.lower;
                        }

                        if ($scope.higher) {
                            query.higher = $scope.higher;
                        }

                        $timeout(function() {
                            table.search(query, predicateName);
                        });
                    };

                    $scope.$watch('lower', runQuery);
                    $scope.$watch('higher', runQuery);
                }
            };
        }]);
