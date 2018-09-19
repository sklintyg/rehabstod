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
angular.module('rehabstodApp').directive('rhsHeaderUser',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                templateUrl: '/components/commonDirectives/rhsHeader/rhsHeaderUser/rhsHeaderUser.directive.html',
                controller: function($scope, $log, UserModel, $uibModal) {
                    /**
                     * Private functions
                     */

                    /**
                     * Exposed scope interaction functions
                     */
                    $scope.user = UserModel.get();
                    $scope.userExpanded = false;

                    $scope.toggleMenu = function($event) {
                        $event.stopPropagation();
                        $scope.userExpanded = !$scope.userExpanded;
                    };

                    $scope.showRoleDescription = function(role) {
                        return role.name === 'LAKARE';
                    };
                    $scope.openSettingsDialog = function() {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            templateUrl: '/components/commonDirectives/rhsHeader/rhsHeaderUser/rhsSettingsModal/rhsSettingsModal.html',
                            controller: 'RhsSettingsModalCtrl',
                            size: 'md',
                            windowClass: 'settings-modal'
                        });

                        modalInstance.result.then(function() {
                            $log.debug('Settings Modal closed with a selection');
                        }, function() {
                            $log.debug('Settings Modal cancelled');
                        });
                    };
                }
            };
        });
