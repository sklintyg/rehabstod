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
angular.module('rehabstodApp').directive('rhsUnblockedList',
    function($rootScope, $timeout, patientHistoryProxy, patientHistoryViewState) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            patient: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedList/rhsUnblockedList.directive.html',
        link: function($scope) {

            $scope.patientHistoryViewState = patientHistoryViewState;

            patientHistoryViewState.vgMedSparrBoxState.skipStart = true;

            patientHistoryViewState.setVgMedSparrViewState($scope.patientHistoryViewState.getSjfMetaData().samtyckeFinns);

            $scope.fetch = function(vardgivareId) {

                var vardgivare = patientHistoryViewState.getVgMedSparrViewStateById(vardgivareId);
                vardgivare.loading = true;

                patientHistoryProxy.getFromVG($scope.patient, vardgivareId).then(function() {
                    vardgivare.loading = false;
                    vardgivare.includedInSjukfall = true;
                    $rootScope.$broadcast('patientHistory.update');
                }, function() {
                    vardgivare.loading = false;
                });
            };
        }
    };
});
