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

angular.module('rehabstodApp').filter('rhsUnique', [
  '$parse',
  function($parse) {
    'use strict';
    return function(items, propertyExpression) {
      if (propertyExpression === false) {
        return items;
      }
      if ((propertyExpression || angular.isUndefined(propertyExpression)) && angular.isArray(items)) {
        var newItems = [];
        //get via parse or otherwise just return the expression
        var get = angular.isString(propertyExpression) ? $parse(propertyExpression) : function(item) {
          return item;
        };
        var extractPropertyFilteredOn = function(item) {
          //if the property is an object, parse it, otherwise just return it.
          return angular.isObject(item) ? get(item) : item;
        };

        angular.forEach(items, function(item) {
          var isDuplicate = false;
          //check if we already have collected this value...
          for (var i = 0; i < newItems.length; i++) {
            if (angular.equals(extractPropertyFilteredOn(newItems[i]), extractPropertyFilteredOn(item))) {
              isDuplicate = true;
              break;
            }
          }
          if (!isDuplicate) {
            newItems.push(extractPropertyFilteredOn(item));
          }
        });
        return newItems;
      }
      return items;
    };
  }
]);