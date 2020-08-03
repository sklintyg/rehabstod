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

angular.module('rehabstodApp').directive('rhsLakarutlatanden',
    function(TableService, UserModel, lakarutlatandenProxy, patientHistoryViewState) {
      'use strict';
      return {
        restrict: 'E',
        scope: {
          activeUnit: '=',
          index: '=',
          patient: '=',
          onLoadIntyg: '&',
          getToolTip: '&'
        },
        templateUrl: '/components/commonDirectives/rhsLakarutlatanden/rhsLakarutlatanden.directive.html',
        link: function($scope) {
          $scope.patientHistoryViewState = patientHistoryViewState;
          $scope.showSpinner = true;
          $scope.user = UserModel.get();
          $scope.preferenceKey = TableService.lakarutlatandenTableKey;
          $scope.tableTextKey = 'label.lakarutlatanden.table.column';
          $scope.arendenErrorMessageKey = 'server.error.getarenden.text';

          $scope.allColumns = TableService.getAllLakarutlatandenTableColumns();
          $scope.$watch(function() {
            return UserModel.get().preferences[$scope.preferenceKey];
          }, function() {
            $scope.tableColumns = TableService.getSelectedLakarutlatandenTableColumns(true);
          }, true);

          $scope.getLakarutlatanden = function() {
            lakarutlatandenProxy.getLakarutlatandenForPatient($scope.patient, UserModel.valdVardenhet).then(function(lakarutlatandenResponse) {
              $scope.showSpinner = false;
              $scope.lakarutlatanden = lakarutlatandenResponse.certificates;
              $scope.lakarutlatanden.forEach(function(intyg) {
                intyg.highestNbrOfArenden = intyg.unAnsweredComplement ? intyg.unAnsweredComplement : intyg.unAnsweredOther;
                intyg.signingTimeStamp = intyg.signingTimeStamp.split('T')[0];
              });
            }, function() {
              $scope.showSpinner = false;
              $scope.errorMessageKey = 'server.error.loadlakarutlatanden.text';
            });
          };

          $scope.getLakarutlatanden();
        }
      };
    });