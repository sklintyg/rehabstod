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

        var selectedColumnsString = selectedColumns.join('|');
        var defaultColumnsString = columns.map(function(column) {
          return column.id;
        }).join('|');

        settingsToSave[preferenceKey] = selectedColumnsString === defaultColumnsString  ?  '' : selectedColumnsString;

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

      $scope.moveUp = function(column) {
        var index = _.findIndex($scope.columns, ['id', column.id]);

        if (index === 0) {
          return;
        }

        move($scope.columns, index, index - 1);
      };

      $scope.moveDown = function(column) {
        var index = _.findIndex($scope.columns, ['id', column.id]);

        if (index === $scope.columns.length - 1) {
          return;
        }

        move($scope.columns, index, index + 1);
      };

      function move(arr, oldIndex, newIndex) {
        while (oldIndex < 0) {
          oldIndex += arr.length;
        }
        while (newIndex < 0) {
          newIndex += arr.length;
        }
        if (newIndex >= arr.length) {
          var k = newIndex - arr.length;
          while ((k--) + 1) {
            arr.push(undefined);
          }
        }
        arr.splice(newIndex, 0, arr.splice(oldIndex, 1)[0]);
        return arr;
      }

      $scope.getLabel = function(column) {
        return columnTranslationKey + column.toLowerCase();
      };

      function getColumns() {
        return $scope.columns.filter(function(column) {
          return column.checked;
        }).map(function(column) {
          return column.id;
        });
      }

      function init() {
        $scope.columns = _.cloneDeep(columns);
        var selectedColumns = UserModel.get().preferences[preferenceKey] || '';
        var selectAll = selectedColumns === '';

        if (selectAll) {
          _.each($scope.columns, function(column) {
            column.checked = true;
          });
        } else {
          var selectedColumnsArray = selectedColumns.split('|');
          var selectedColumnsIndexByKey = {};
          _.each(selectedColumnsArray, function(column, index) {
            selectedColumnsIndexByKey[column] = index;
          });

          $scope.columns.sort(function(c1, c2) {
            var index1 = selectedColumnsIndexByKey[c1.id];
            var index2 = selectedColumnsIndexByKey[c2.id];

            index1 = index1 === undefined ? 1000 : index1;
            index2 = index2 === undefined ? 1000 : index2;

            return index1 - index2;
          });

          _.each($scope.columns, function(column) {
            column.checked = selectedColumnsIndexByKey[column.id] !== undefined;
          });
        }

        $scope.checkSelected();
      }

      $scope.checkSelected = function() {
        $scope.nonSelected = $scope.columns.filter(function(column) {
          return column.checked;
        }).length === 0;
      };

      $scope.translationBaseKey = modalTextTranslationKey;
      $scope.nonSelected = true;

      init();
    }
);
