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
.controller('RhsLakarutlatandeFilterCtrl',
    function($scope, $rootScope, $timeout, $filter, $log, LakarutlatandeService, LakarutlatandeFilterViewState, LakarutlatandeModel,
        DiagnosKapitelModel, LakareModel, UserModel, StringHelper, TableService, _) {
      'use strict';

      $scope.filterViewState = LakarutlatandeFilterViewState;
      $scope.user = UserModel.get();
      $scope.showSearchFilter = true;
      $scope.model = LakarutlatandeModel;
      var columns;
      $scope.showDatePicker = true;

      $scope.toggleDatePicker = function () {
        $scope.showDatePicker = false;
        $timeout(function(){
          $scope.showDatePicker = true;
        });
      };

      $scope.$on('rhsLakarutlatandeFilter.toggleDatePicker', $scope.toggleDatePicker);


      var unregisterFn = $rootScope.$on('SelectedUnitChanged', function() {
        columns = TableService.getSelectedLakarutlatandeUnitColumns();
      });
      $scope.$on('$destroy', unregisterFn);

      $scope.onResetFilterClick = function() {
        $scope.filterViewState.reset();
      };

      $scope.onSearchFilterClick = function() {
        $scope.searchFn();
      };

      $scope.$watch(function() {
        return UserModel.get().preferences[TableService.lakarutlatandeUnitTableKey];
      }, function() {
        columns = TableService.getSelectedLakarutlatandeUnitColumns();
      }, true);

      $scope.filterInactive = function(field, field2) {
        var column = _.find(columns, function(column) {
          return column.id === field || (field2 && column.id === field2);
        });

        return !column;
      };
    }
)
.directive('rhsLakarutlatandeFilter',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: { searchFn: '&' },
        controller: 'RhsLakarutlatandeFilterCtrl',
        templateUrl: '/components/appDirectives/sjukfall/rhsLakarutlatandeFilter/rhsLakarutlatandeFilter.directive.html',
      };
    });
