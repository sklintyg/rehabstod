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
    function($timeout, patientHistoryViewState) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            patientSjfMetaData: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedList/rhsUnblockedList.directive.html',
        link: function($scope) {

            $scope.patientHistoryViewState = patientHistoryViewState;

            $scope.patientHistoryViewState.getSjfMetaData().samtyckeFinns.map(function(vg) {
                vg.loading = false;
                vg.fetched = false;
                return vg;
            });

            $scope.fetch = function(vardgivareId) {

                var vardgivare = patientHistoryViewState.getSjfMetaData().samtyckeFinns.filter(function(item){
                    return item.id === vardgivareId;
                });

                if(vardgivare.length > 0){
                    vardgivare[0].loading = true;

                    // fake backend fetch
                    $timeout(function(){
                        vardgivare[0].loading = false;
                        vardgivare[0].fetched = true;
                    }, 1000);
                }

            };
        }
    };
});
