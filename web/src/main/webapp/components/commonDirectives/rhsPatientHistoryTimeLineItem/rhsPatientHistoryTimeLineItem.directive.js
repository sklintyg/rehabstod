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
angular.module('rehabstodApp').directive('rhsPatientHistoryTimeLineItem', function() {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            historyItem: '=',
            index: '=',
            onSelect: '&'
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTimeLineItem/rhsPatientHistoryTimeLineItem.directive.html',
        link: function($scope) {

            $scope.radius = 26;
            $scope.center = $scope.radius + 2;

            $scope.getCircleY = function() {
                return $scope.radius + 30;
            };
            $scope.getCircleTextY1 = function() {
                return $scope.getCircleY() - 4;
            };
            $scope.getCircleTextY2 = function() {
                return $scope.getCircleTextY1() + 12;
            };

            $scope.showYear = function() {
                return $scope.historyItem.year !== 0;
            };

        }
    };
});