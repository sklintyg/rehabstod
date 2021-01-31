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

angular.module('rehabstodApp').directive('rhsMultiSelect',
    function($window) {
      'use strict';

      var $win = angular.element($window);

      return {
        restrict: 'E',
        scope: {
          noneSelectedTitle: '@',
          highLightEnabled: '=',
          itemsModel: '=',
          xorMode: '=',
          controlDisabled: '='
        },

        templateUrl: '/components/commonDirectives/rhsMultiSelect/rhsMultiSelect.directive.html',
        link: function($scope, element) {

          var calculateDropdownHeight = function() {
            if ($scope.isOpen) {
              var dropdown = element.find('.dropdown-menu');

              var offsetTop = dropdown.offset().top - $win.scrollTop();

              var height = $window.innerHeight - offsetTop - 20;

              dropdown.css('max-height', Math.max(height, 100));

              if ($scope.items.length === 0) {
                dropdown.css('min-height', 35);
                dropdown.css('width', 160);
              }
            }
          };

          $scope.isOpen = false;

          //Template just need access to the array
          $scope.items = $scope.itemsModel.get();

          //Calculate title based on whether item(s) are selected or not.
          $scope.getTitle = function() {
            var nrItemsSelected = $scope.itemsModel.getSelected().length;
            if (nrItemsSelected === 0) {
              return $scope.noneSelectedTitle;
            } else if (nrItemsSelected === 1) {
              return $scope.itemsModel.getSelected()[0].displayValue;
            } else {
              return nrItemsSelected + ' valda';
            }
          };

          $scope.toggleItem = function(item) {
            if ($scope.xorMode) {
              angular.forEach($scope.items, function(it) {
                if (it !== item) {
                  it.selected = false;
                }
              });
            }
            item.selected = !item.selected;

            if ($scope.xorMode) {
              $scope.isOpen = false;
            }
          };

          $scope.dropdownToggle = function(open) {
            $scope.isOpen = open;
            calculateDropdownHeight();
          };

          $win.on('resize', calculateDropdownHeight);

          $scope.$on('$destroy', function() {
            $win.unbind('resize', calculateDropdownHeight);
          });

          //Listen to focusout events, that will bubble to this directives root element.
          //If the element receiveing the focus (event.relatedTarget) is not structurally under this directive
          //root node, we have for example tabbed to another component. In that case, we should close the dropdown.
          element.on('focusout', function(event) {
            if (!element[0].contains(event.relatedTarget)) {
              $scope.isOpen = false;
              //Wer'e not in angular land here(i.e within a digest loop) - so we need to inform
              //angular of this change.
              $scope.$apply();
            }
          });
        }
      };
    });


