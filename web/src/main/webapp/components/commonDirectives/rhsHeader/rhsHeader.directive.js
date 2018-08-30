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
angular.module('rehabstodApp').directive('rhsHeader',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                templateUrl: '/components/commonDirectives/rhsHeader/rhsHeader.directive.html',
                controller: function($scope, UserModel) {
                    /**
                     * Private functions
                     */

                    /**
                     * Exposed scope interaction functions
                     */

                    $scope.user = UserModel.get();

                    $scope.logoutLocation = UserModel.getLogoutLocation();

                    $scope.showRoleDescription = function(role) {
                        return role.name === 'LAKARE';
                    };

                    $scope.showUnit = function() {
                        var user = UserModel.get();

                        return user.loggedIn && user.valdVardenhet;
                    };
                }
            };
        });