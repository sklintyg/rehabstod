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
    function($rootScope, $uibModal, $timeout, patientHistoryProxy, patientHistoryViewState) {
    'use strict';

        function _getVgSomKraverSamtycke() {
            return patientHistoryViewState.getKraverSamtyckeViewState().filter(function(item) {
                return item.itemType === 'VARDGIVARE';
            });
        }

        function _getVgSomKraverSamtyckeById(vgId) {
            var result = null;
            _getVgSomKraverSamtycke().forEach(function(item){
                if(item.itemId === vgId){
                    result = item;
                }
            });

            return result;
        }

        function _getVeInomSammaVardgivare() {
            return patientHistoryViewState.getKraverInteSamtyckeViewState().filter(function(item) {
                return item.itemType === 'VARDENHET';
            });
        }

        function _getVeInomSammaVardgivareById(veId) {
            var result = null;
            _getVeInomSammaVardgivare().forEach(function(item){
                if(item.itemId === veId){
                    result = item;
                }
            });

            return result;
        }

        return {
            restrict: 'E',
            scope: {
                patient: '=',
                mustHaveConsent: '='
            },
            templateUrl: '/components/commonDirectives' +
                '/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedList/rhsUnblockedList.directive.html',
            link: function($scope) {

                $scope.patientHistoryViewState = patientHistoryViewState;

                patientHistoryViewState.setKraverSamtyckeViewState($scope.patientHistoryViewState.getSjfMetaData().kraverSamtycke);

                patientHistoryViewState.setKraverInteSamtyckeViewState($scope.patientHistoryViewState.getSjfMetaData().kraverInteSamtycke);

                $scope.sjfMetaDataItems = $scope.mustHaveConsent ? _getVgSomKraverSamtycke() : _getVeInomSammaVardgivare();

                $scope.fetchItem = function(itemId) {
                    if ($scope.mustHaveConsent) {
                        $scope.fetchVg(itemId);
                    } else {
                        $scope.fetchVe(itemId);
                    }
                };

                $scope.fetchVg = function(vardgivareId) {
                    var vardgivare = _getVgSomKraverSamtyckeById(vardgivareId);
                    vardgivare.loading = true;

                    patientHistoryProxy.getFromVG($scope.patient, vardgivareId).then(function() {
                        vardgivare.loading = false;
                        vardgivare.includedInSjukfall = true;
                        $rootScope.$broadcast('patientHistory.update');
                    }, function() {
                        vardgivare.loading = false;
                    });
                };

                $scope.fetchVe = function(vardenhetId) {
                    var vardenhet = _getVeInomSammaVardgivareById(vardenhetId);
                    vardenhet.loading = true;

                    patientHistoryProxy.getFromVE($scope.patient, vardenhetId).then(function() {
                        vardenhet.loading = false;
                        vardenhet.includedInSjukfall = true;
                        $rootScope.$broadcast('patientHistory.update');
                    }, function() {
                        vardenhet.loading = false;
                    });
                };

                $scope.openFetchDialog = function() {
                    $uibModal.open({
                        templateUrl: '/components/commonDirectives' +
                            '/rhsPatientHistoryTable/rhsUnblockedFlow/rhsUnblockedList/rhsUnblockedList.dialog.html',
                        keyboard: true,
                        size: 'md'
                    }).result.then(
                        function(){}, function(){}
                    );
                };
            }
        };
});
