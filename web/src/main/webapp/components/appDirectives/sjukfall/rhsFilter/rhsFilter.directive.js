/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
    function($scope, $rootScope, $timeout, $filter, $log, SjukfallFilterViewState, SjukfallModel, DiagnosKapitelModel, LakareModel,
        UserModel, StringHelper, TableService, _) {
      'use strict';

      $scope.filterViewState = SjukfallFilterViewState;
      $scope.user = UserModel.get();
      $scope.showSearchFilter = true;
      $scope.model = SjukfallModel;
      var columns;
      $scope.showDatePicker = true;

      $scope.filterViewState.initQAModel();

      $scope.toggleDatePicker = function () {
        $scope.showDatePicker = false;
        $timeout(function(){
          $scope.showDatePicker = true;
        });
      };

      $scope.$on('rhsFilter.toggleDatePicker', $scope.toggleDatePicker);

      $scope.$watchCollection('model.get()', function(value) {
        //Update contents on those models of filtercomponents that depends on the searchresults contents, i.e
        // uniqueness of lakare diagnoskapitel.
        var lakare = $filter('rhsUnique')(value, 'lakare.namn');
        if (!angular.equals($scope.filterViewState.get().lakareModel.getNames(), lakare)) {
          $scope.filterViewState.get().lakareModel.set(lakare.sort(StringHelper.swedishStringSortImpl(true, false)));
        }

        $scope.filterViewState.get().diagnosKapitelModel.setActivDiagnosKapitelIdlist(
            $filter('rhsUnique')(value, 'diagnos.kapitel'));

      });

      var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
        columns = TableService.getSelectedSjukfallColumns(true);
      });
      $scope.$on('$destroy', unregisterFn);

      $scope.onResetFilterClick = function() {
        $scope.filterViewState.reset();
      };

      $scope.$watch(function() {
        return UserModel.get().preferences[TableService.sjukfallTableKey];
      }, function() {
        columns = TableService.getSelectedSjukfallColumns(true);
      }, true);

      $scope.filterInactive = function(field, field2) {
        var column = _.find(columns, function(column) {
          return column.id === field || (field2 && column.id === field2);
        });

        return !column;
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
        templateUrl: '/components/appDirectives/sjukfall/rhsFilter/rhsFilter.directive.html'
      };
    });
