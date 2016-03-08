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
    .controller('RhsFilterCtrl',
        function($scope, $filter, $log, SjukfallFilterViewState, SjukfallModel, DiagnosKapitelModel, LakareModel,
            UserModel) {
            'use strict';

            $scope.filterViewState = SjukfallFilterViewState;
            $scope.user = UserModel.get();

            $scope.sjukfallsLangder = angular.copy($scope.filterViewState.get().sjukskrivningslangdModel);

            //Forward changes from FilterViewState to directive
            $scope.$watch('filterViewState.get().sjukskrivningslangdModel', function(newValue) {
                //Make sure the model that the NumericRangeInput's boxes are bound to have correct order.
                //The dragging one slider point ofter the other will otherwise swap hi/lo
                $scope.sjukfallsLangder[0] = Math.min(newValue[0], newValue[1]);
                $scope.sjukfallsLangder[1] = Math.max(newValue[0], newValue[1]);

            });

            //Forward changes from NumericRangeInput directives to viewState (update slider from NumericInputRange directives)
            $scope.$watchCollection('sjukfallsLangder', function(newValue) {
                $scope.filterViewState.get().sjukskrivningslangdModel[0] = newValue[0];
                $scope.filterViewState.get().sjukskrivningslangdModel[1] = newValue[1];
            });


            $scope.showSearchFilter = true;
            $scope.position = 'bottom';
            $scope.model = SjukfallModel;

            $scope.$watchCollection('model.get()', function(value) {
                //Update contents on those models of filtercomponents that depends on the searchresults contents, i.e
                // uniqueness of lakare diagnoskapitel.
                var lakare = $filter('rhsUnique')(value, 'lakare.namn');
                if (!angular.equals($scope.filterViewState.get().lakareModel.getNames(), lakare)) {
                    $scope.filterViewState.get().lakareModel.set(lakare);
                }

                $scope.filterViewState.get().diagnosKapitelModel.setActivDiagnosKapitelIdlist(
                    $filter('rhsUnique')(value, 'diagnos.kapitel'));

            });

            $scope.onResetFilterClick = function() {
                $scope.filterViewState.reset();
                $log.debug('reset filterViewState');
            };
        }
    )
    .directive('rhsFilter',
        function() {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                controller: 'RhsFilterCtrl',
                templateUrl: 'components/appDirectives/sjukfall/rhsFilter/rhsFilter.directive.html'
            };
        });
