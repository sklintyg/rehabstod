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

angular.module('rehabstodApp').directive('rhsSelectUnitDropdown',
    function(_) {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          value: '=',
          vardgivare: '='
        },

        templateUrl: '/components/commonDirectives/rhsSelectUnitDropdown/rhsSelectUnitDropdown.directive.html',
        link: function($scope) {

          function buildItems(vardgivare) {
            //Add initial "none selected" option
            var items = [{id: null, label: 'Ingen standardenhet'}];

            _.each(vardgivare, function(vg) {
              _.each(vg.vardenheter, function(ve) {
                items.push({id: ve.id, label: ve.namn});
                _.each(ve.mottagningar, function(m) {
                  items.push({id: m.id, label: m.namn});
                });
              });
            });
            return items;
          }

          //Template expects a generic flat id/label items array
          $scope.items = buildItems($scope.vardgivare);

          $scope.selectedItem = _.find($scope.items, function(item) {
            return item.id === $scope.value;
          });
          if (!$scope.selectedItem) {
            $scope.value = null;
            $scope.selectedItem = $scope.items[0];
          }

          $scope.onSelectItem = function(item) {
            $scope.selectedItem = item;
            $scope.value = item.id;
          };
        }
      };
    });


