/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
.directive('rhsPatientTableSelectColumns',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          labelKey: '@',
          nyligenAvslutat: '='
        },
        controller: function($scope, $uibModal, UserModel, TableService) {

          $scope.label = $scope.labelKey ? $scope.labelKey : 'label.table.anpassa.patient';

          var preferenceKey = TableService.patientTableKey;

          $scope.showIcon = $scope.labelKey ? false : !!UserModel.get().preferences[preferenceKey];

          $scope.openDialog = function() {
            $uibModal.open({
              templateUrl: '/components/commonDirectives/rhsSelectColumnsModal/rhsSelectColumns.modal.html',
              controller: 'rhsSelectColumnsModalController',
              size: 'md',
              resolve: {
                columns: function() {
                  return TableService.getAllPatientAndAgTableColumns($scope.nyligenAvslutat);
                },
                preferenceKey: function() {
                  return preferenceKey;
                },
                columnTranslationKey: function() {
                  return 'label.patient.table.column.';
                },
                modalTextTranslationKey: function() {
                  return 'label.table.custom.modal.patient.';
                }
              }
              // Removes angular error "Possibly unhandled rejection:
              // backdrop click" when clicking outside of modal
            }).result.then(function() {
              $scope.showIcon = !!UserModel.get().preferences[preferenceKey];
            }, function() {
            });
          };
        },
        templateUrl: '/components/commonDirectives/rhsPatientHistoryTable/' +
            'rhsPatientTableSelectColumns/rhsPatientTableSelectColumns.directive.html'
      };
    });
