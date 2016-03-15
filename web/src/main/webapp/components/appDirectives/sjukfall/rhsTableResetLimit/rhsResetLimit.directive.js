/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

                    $scope.$watchCollection('table.getFilteredCollection()', function() {
                        $('body, thead *').css('cursor', '');
                    });
                }
            };
        });
