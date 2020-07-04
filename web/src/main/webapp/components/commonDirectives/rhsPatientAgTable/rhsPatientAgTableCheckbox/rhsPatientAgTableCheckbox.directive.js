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
angular.module('rehabstodApp')/*
.controller('rhsPatientAgTableCheckboxCtrl', ['$scope', function($scope) {
  'use strict';
    $scope.displayAgTable = '';
    //$scope.remote = 'false';
    //$scope.displayAgTable.test = '';
    //patientAgViewState.updateDisplayAgTable(true);

    //$scope.remote = '';

    $scope.$watch('displayAgTable', function(a) {
      alert('yes ' + a);
      patientAgViewState.updateDisplayAgTable(a);
      $scope.remote = patientAgViewState.getDisplayAgTable();
    }, true);

}])*/
  .directive('rhsPatientAgTableCheckbox',  function(patientAgViewState) {
      'use strict';

      return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/rhsPatientAgTable/rhsPatientAgTableCheckbox/rhsPatientAgTableCheckbox.directive.html',
        link: function($scope) {
          $scope.patientAgTableCheckbox = false;
          $scope.$watch('patientAgTableCheckbox', function(checked) {
            patientAgViewState.updateDisplayAgTable(checked);
          });
        }
      };
    });