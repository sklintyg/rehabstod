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
.directive('rhsTableSelectColumns',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          labelKey: '@',
          preferenceKey: '=',
          columns: '=',
          tableTextKey: '@',
          noFilter: '=',
          modalTextKey: '@'
        },
        controller: function($scope, $uibModal, UserModel) {

          $scope.label = $scope.labelKey ? $scope.labelKey : 'label.table.anpassa';
          showIcon();

          if($scope.noFilter) {
            $scope.tooltipLabel = 'label.table.anpassa.help.nofilter';
          } else {
            $scope.tooltipLabel = 'label.table.anpassa.help';
          }

          $scope.modalTextKey = $scope.modalTextKey ? $scope.modalTextKey : 'label.table.custom.modal.sjukfall.';

          $scope.openDialog = function() {
            $uibModal.open({
              templateUrl: '/components/commonDirectives/rhsSelectColumnsModal/rhsSelectColumns.modal.html',
              controller: 'rhsSelectColumnsModalController',
              size: 'md',
              resolve: {
                columns: function() {
                  return $scope.columns;
                },
                preferenceKey: function() {
                  return $scope.preferenceKey;
                },
                columnTranslationKey: function() {
                  return $scope.tableTextKey + '.';
                },
                modalTextTranslationKey: function() {
                  return $scope.modalTextKey;
                }
              }
              // Removes angular error "Possibly unhandled rejection:
              // backdrop click" when clicking outside of modal
            }).result.then(function() {
              showIcon();
            }, function() {
            });
          };

          function showIcon() {
            $scope.showIcon = $scope.labelKey ? false : !!UserModel.get().preferences[$scope.preferenceKey];
          }
        },
        templateUrl: '/components/appDirectives/sjukfall/rhsTableSelectColumns/rhsTableSelectColumns.directive.html'
      };
    });
