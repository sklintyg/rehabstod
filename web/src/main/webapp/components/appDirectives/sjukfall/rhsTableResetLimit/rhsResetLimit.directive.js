angular.module('rehabstodApp').directive('rhsResetLimit',
        function($window) {
            'use strict';

            var $win = angular.element($window);

            return {
                restrict: 'E',
                require: '^stTable',
                scope: {
                    onChange: '&',
                    scrollElement: '@',
                    paddingTop: '@'
                },
                link: function($scope, element, attr, table) {
                    var changeLimit = function() {
                        $scope.onChange();
                    };

                    $scope.table  = table;
                    $scope.$watch('table.tableState().search', changeLimit, true);
                    $scope.$watch('table.tableState().sort', function() {

                        var position = $($scope.scrollElement).offset().top - $scope.paddingTop;
                        if ($win.scrollTop() > position) {
                            $('html, body').animate({scrollTop: position}, 200);
                        }

                        changeLimit();
                    }, true);
                }
            };
        });
