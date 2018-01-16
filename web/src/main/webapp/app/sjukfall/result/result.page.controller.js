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
    function ($scope, $rootScope, $state, SjukfallService, UserModel, SjukfallViewState) {
        'use strict';

        $scope.sjukfallService = SjukfallService;
        $scope.SjukfallViewState = SjukfallViewState;
        $scope.user = UserModel.get();
        $scope.srsError = false;

        loadSjukfall();

        var unregisterFn = $rootScope.$on('SelectedUnitChanged', function (/*event, value*/) {
            loadSjukfall();
        });
        //rootscope on event listeners aren't unregistered automatically when 'this' directives
        //scope is destroyed, so let's take care of that.
        $scope.$on('$destroy', unregisterFn);

        $scope.goBack = function () {
            $state.go('app.sjukfall.start');
        };

        $scope.$watch('sjukfallService.isLoading()', function (val) {
            $scope.showSpinner = val;
        });

        $scope.$watch('SjukfallViewState.get().srsError', function (val) {
            $scope.srsError = val;
        });

        function loadSjukfall() {
            $scope.showSpinner = true;
            SjukfallService.loadSjukfall(true);
        }
    });
