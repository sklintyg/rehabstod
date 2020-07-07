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
  .directive('rhsPatientAgTable',
    function(UserModel) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          historyItem: '=',
          patient: '=',
          index: '=',
          onLoadIntyg: '&',
          columns: '='
        },
        templateUrl: '/components/commonDirectives/rhsPatientAgTable/rhsPatientAgTable.directive.html',

        controller: function($scope, patientAgViewState) {
          $scope.showAgTable = patientAgViewState;
        },
        link: function($scope) {

          $scope.$watchCollection('columns', function() {
            $scope.filteredColumns = $scope.columns.filter(function(column ) {
              return column.id !== 'obesvaradekompl' && column.id !== 'risk';
            });
          });

          $scope.getEffectiveVardenhetUnitName = function() {
            var user = UserModel.get();
            if (user.valdVardenhet) {
              //Is valdvardenhet actually a mottagning?
              if (user.valdVardenhet.parentHsaId) {
                //return parent unit name, since data is always returned for unit level (even if mottagning is selected)
                return UserModel.getUnitNameById(user.valdVardenhet.parentHsaId);
              }
              return user.valdVardenhet.namn;
            }
            return '';
          };
        }
      };
    });