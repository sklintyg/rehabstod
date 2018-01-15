/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').filter('rhsSort', function($filter, $parse, StringHelper) {
    'use strict';

    var orderBy = $filter('orderBy');

    var customSort = function(predicte, reverse) {
        return function(a, b) {
            a = $parse(predicte)(a);
            b = $parse(predicte)(b);
            
            return StringHelper.compareSwedishString(!reverse, false, a, b);
        };
    };
    
    
    return function(array, predicate, reverse) {
        if (array.length > 0) {
            if (angular.isString($parse(predicate)(array[0]))) {
                return array.sort(customSort(predicate, reverse));
            }
        }

        return orderBy(array, predicate, reverse);
    };
});
