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
    .controller('SjukfallResultPageCtrl',
    function ($scope, $rootScope, SjukfallService, SjukfallSummaryModel, UserModel, UserProxy) {
        'use strict';
        if (_proceed()) {
            UserProxy.changeUrval(UserModel.isLakare() ? 'ISSUED_BY_ME' : 'ALL').then(function (updatedUserModel) {
                UserModel.set(updatedUserModel);
                $scope.user = UserModel.get();
                $scope.showSpinner = true;
                $scope.sjukfallService = SjukfallService;
                SjukfallService.loadSjukfall(true);
            });

        }


        var unregisterFn = $rootScope.$on('SelectedUnitChanged', function (/*event, value*/) {
            SjukfallService.loadSjukfall(true);
        });
        //rootscope on event listeners aren't unregistered automatically when 'this' directives
        //scope is destroyed, so let's take care of that.
        $scope.$on('$destroy', unregisterFn);

        $scope.goBack = function () {
            UserProxy.changeUrval(null).then(function (updatedUserModel) {
                UserModel.set(updatedUserModel);
            }, function () {
                //Handle errors
            });
        };

        $scope.$watch('sjukfallService.isLoading()', function (val) {
            $scope.showSpinner = val;
        });

        function _proceed() {
            if (SjukfallSummaryModel.get().total === 0) {
                //No data to show - go to show 'no data for unit' state
                return false;
            } else {
                return true;
            }

        }

    });