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
angular.module('rehabstodApp').directive('rhsUnblockedConsent',
    function($rootScope, patientHistoryViewState, patientHistoryProxy) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            patient: '=',
            patientSjfMetaData: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedConsent/rhsUnblockedConsent.directive.html',
        link: function($scope) {

            $scope.vardgivareUtanSamtycke = patientHistoryViewState.getSjfMetaData().samtyckeSaknas;

            var vardgivareUtanSamtyckeNames = $scope.vardgivareUtanSamtycke.map(function(vg) {
                return vg.vardgivareNamn;
            });

            $scope.vardgivareMedInfo = vardgivareUtanSamtyckeNames.join(', ');

            $scope.consent = {
                confirm: false,
                onlyCurrentUser: 'ONLYCURRENT',
                days: 7
            };

            $scope.next = function() {
                patientHistoryProxy.giveConsent({
                    patientId: $scope.patient.id,
                    onlyCurrentUser: $scope.consent.onlyCurrentUser === 'ONLYCURRENT',
                    days: $scope.consent.days
                }).then(function(response) {

                    if(response.responseCode === 'OK')
                    {
                        var patientSjfMeta = patientHistoryViewState.getSjfMetaData();
                        patientSjfMeta.samtyckeFinns = patientSjfMeta.samtyckeFinns.concat(patientSjfMeta.samtyckeSaknas);
                        patientSjfMeta.samtyckeSaknas = [];
                        $rootScope.$broadcast('rhsUnblockedFlow.next');
                    }
                });
            };
        }
    };
});
