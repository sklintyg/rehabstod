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

angular.module('showcase').controller('showcase.BootstrapCtrl',
    ['$scope', '$timeout', '$uibModal',
        function($scope, $timeout, $uibModal) {
            'use strict';

            //Vidarebefodra knapp states
            $scope.cert1 = {
                vidarebefordrad: false
            };
            $scope.cert2 = {
                vidarebefordrad: true
            };

            //Visa/dölj länk knapp
            $scope.linkBtnState = false;


            $scope.fetchingMoreInProgress = false;
            $scope.simulateFetchMore = function() {
                $scope.fetchingMoreInProgress = true;
                $timeout(function() {
                    $scope.fetchingMoreInProgress = false;
                }, 2000);
            };

            $scope.acceptprogressdone = true;
            $scope.simulateProgress = function() {
                $scope.acceptprogressdone = false;
                $timeout(function() {
                    $scope.acceptprogressdone = true;
                }, 2000);
            };

            $scope.onHideAlert = function() {
                $scope.hideAlert = true;
                $timeout(function() {
                    $scope.hideAlert = false;
                }, 2000);
            };

            $scope.tableitems = [];
            for (var i = 0; i < 10; i++) {
                $scope.tableitems.push({id: i, value: 'Item ' + i});
            }

            //modal dialog sample
            $scope.showDialog = function() {
                $uibModal.open({
                    animation: true,
                    templateUrl: '/pubapp/showcase/views/modal-dialog.html',
                    size: 'md',
                    backdrop: 'static'
                });
            };

        }]);