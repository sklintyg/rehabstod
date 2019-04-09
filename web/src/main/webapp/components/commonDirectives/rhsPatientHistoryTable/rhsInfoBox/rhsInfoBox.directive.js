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
angular.module('rehabstodApp').directive('rhsInfoBox',
    function() {
    'use strict';

    return {
        restrict: 'E',
        transclude: true,
        scope: {
            infoExpr: '=',
            boxTitle: '@',
            labelTruthy: '@',
            labelFalsy: '@',
            labelError: '@?',
            serviceError: '@?', // if true the call to the consent service failed
            boxState: '=?' // allows to set skipStart to skip the "Visa mig" section
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsInfoBox/rhsInfoBox.directive.html',
        link: function($scope) {

            if(!$scope.labelError){
                $scope.labelError = '';
            }

            if(!$scope.serviceError){
                $scope.serviceError = false;
            }

            // Box state is optional so set defaults if its not provided
            if(!$scope.boxState){
                $scope.boxState = { skipStart: false };
            }

            if(!$scope.boxState.skipStart){
                $scope.boxState.skipStart = false;
            }

            $scope.next = function() {
                $scope.boxState.skipStart = true;
            };
        }
    };
});
