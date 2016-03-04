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

angular.module('rehabstodApp').directive('rhsStatPanel',
    ['SjukfallSummaryModel', 'SjukfallSummaryProxy', 'UserModel', '$rootScope',
        function(SjukfallSummaryModel, SjukfallSummaryProxy, UserModel, $rootScope) {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    /**
                     * Private functions
                     */
                    function _loadData() {
                        SjukfallSummaryModel.reset();
                        SjukfallSummaryProxy.get().then(function(data) {
                            data.women = Math.round(data.women * 10) / 10;
                            data.men = Math.round(data.men * 10) / 10;
                            SjukfallSummaryModel.set(data);
                        });
                    }

                    var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
                        _loadData();
                    });
                    //rootscope on event listeners aren't unregistered automatically when 'this' directives
                    //scope is destroyed, so let's take care of that.
                    $scope.$on('$destroy', unregisterFn);

                    /**
                     * Exposed scope properties
                     */
                    $scope.model = SjukfallSummaryModel.get();

                    if ($scope.model.total === null) {
                        _loadData();
                    }

                    $scope.isLakare = UserModel.get().isLakare;

                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
