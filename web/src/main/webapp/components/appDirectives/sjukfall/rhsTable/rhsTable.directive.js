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

angular.module('rehabstodApp')
    .controller('RhsTableCtrl', ['$scope', '$uibModal', 'SjukfallFilterViewState', 'SjukfallModel', 'UserModel', 'messageService',
        'featureService',
        function($scope, $uibModal, SjukfallFilterViewState, SjukfallModel, UserModel, messageService, featureService) {
            'use strict';

            $scope.filter = SjukfallFilterViewState;
            $scope.model = SjukfallModel;
            $scope.user = UserModel.get();

            $scope.noResultColspan = function() {
                return (($scope.user.urval !== 'ISSUED_BY_ME') ? 13 : 12) - (SjukfallFilterViewState.get().showPatientId ? 0 : 2);
            };

            $scope.displayedCollection = [].concat($scope.model.get());

            $scope.getToolTip = function(diagnos) {
                var desc = angular.isString(diagnos.beskrivning) ? diagnos.beskrivning :
                    messageService.getProperty('label.table.diagnosbeskrivning.okand', {'kod': diagnos.kod});
                return '<b>' + diagnos.kod + '</b><br>' + desc;
            };

            $scope.showPatientHistory = function(patientModel) {
                    $uibModal.open({
                        windowClass: 'patient-history-dialog',
                        templateUrl: '/app/sjukfall/patientHistory/patientHistory.dialog.html',
                        controller: 'patientHistoryController',
                        size: 'lg',
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            patient: function() {
                                return patientModel;
                            }
                        }
                    });
            };

            $scope.showMoreInTable = function() {
                $scope.limit += 50;
            };

            $scope.resetLimit = function() {
                $scope.limit = 100;
            };

            $scope.hasFeature = function(feature) {
                return featureService.hasFeature(feature);
            };

            $scope.resetLimit();

        }
    ])
    .directive('rhsTable',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                controller: 'RhsTableCtrl',
                templateUrl: '/components/appDirectives/sjukfall/rhsTable/rhsTable.directive.html'
            };
        });
