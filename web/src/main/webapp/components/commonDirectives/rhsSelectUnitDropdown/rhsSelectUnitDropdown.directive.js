/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
          config: '='
        },

        templateUrl: '/components/commonDirectives/rhsSelectUnitDropdown/rhsSelectUnitDropdown.directive.html',
        link: function($scope) {

          // Toggle display state for dropdown options in template
          $scope.isOpen = false;

          function buildItems(vardgivare) {
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

          //Template need access to a generic flat items array with id/label
          $scope.items = buildItems($scope.config.vardgivare);

          $scope.selectedItem = _.find($scope.items, function(item) {
            return item.id === $scope.value;
          });

          $scope.onSelectItem = function(item) {
            $scope.selectedItem = item;
            $scope.value = item.id;
          }
        }
      };
    });


