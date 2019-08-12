/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').controller('rhsSelectColumnsModalController',
    function($scope, $uibModalInstance, UserProxy, UserModel, _, columns, preferenceKey, columnTranslationKey, modalTextTranslationKey) {
      'use strict';

      $scope.save = function() {
        $scope.saving = true;
        $scope.error = false;

        var settingsToSave = UserModel.get().preferences;
        var selectedColumns = getColumns();
        settingsToSave[preferenceKey] = selectedColumns.length === columns.length  ?  '' : getColumns().join('|');

        UserProxy.saveSettings(settingsToSave).then(
            function(preferences) {
              UserModel.get().preferences = preferences;
              $uibModalInstance.close(preferences);
              $scope.saving = false;
            },
            function() {
              $scope.error = true;
              $scope.saving = false;
            }
        );
      };

      $scope.getLabel = function(column) {
        return columnTranslationKey + column.toLowerCase();
      };

      $scope.$watch('columns', function() {
        checkSelected();
      }, true);

      function getColumns() {
        return $scope.columns.filter(function(column) {
          return column.checked;
        }).map(function(column) {
          return column.id;
        });
      }

      function init() {
        var selectedColumns = UserModel.get().preferences[preferenceKey] || '';

        var selectAll = selectedColumns === '';
        selectedColumns = selectedColumns.split('|');

        _.each($scope.columns, function(column) {
          column.checked = selectAll || selectedColumns.indexOf(column.id) !== -1;
        });

        checkSelected();
      }

      function checkSelected() {
        $scope.nonSelected = $scope.columns.filter(function(column) {
          return column.checked;
        }).length === 0;
      }

      $scope.translationBaseKey = modalTextTranslationKey;
      $scope.nonSelected = true;
      $scope.columns = columns;

      init();
    }
);
