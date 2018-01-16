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
    .controller('RhsSettingsCtrl',
        function($scope, SjukfallFilterViewState, $uibModal, SjukfallService) {
            'use strict';

            $scope.filterViewState = SjukfallFilterViewState;

            $scope.openSettings = function() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: '/components/appDirectives/sjukfall/rhsSettings/rhsSettingsModal/rhsSettingsModal.html',
                    controller: 'RhsSettingsModalCtrl',
                    size: 'md'
                });

                modalInstance.result.then(function(value) {
                    //Only reload if changed
                    if (SjukfallFilterViewState.get().glapp !== value) {
                        SjukfallFilterViewState.get().glapp = value;
                        SjukfallService.loadSjukfall(true, true);
                    }
                }, function() {

                });
            };
        }
    )
    .directive('rhsSettings',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                controller: 'RhsSettingsCtrl',
                templateUrl: '/components/appDirectives/sjukfall/rhsSettings/rhsSettings.directive.html'
            };
        });
