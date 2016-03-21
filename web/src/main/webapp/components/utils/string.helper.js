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

angular.module('rehabstodApp').factory('StringHelper',
    function() {
        'use strict';
        var swedishAlphabet = '-.()0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖabcedefghijklmnopqrstuvwxyzåäö';

        function escapeRegExp(str) {
            return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, '\\$1');
        }

        function swedishStringSorter(dir, caseSensitive) {
            return function(a, b) {
                return compareSwedishString(dir, caseSensitive, a, b);
            };
        }

        function compareSwedishString(dir, caseSensitive, a, b) {
            var pos = 0,
                min = Math.min(a.length, b.length);
            dir = dir || 1;
            caseSensitive = caseSensitive || false;
            if (!caseSensitive) {
                a = a.toLowerCase();
                b = b.toLowerCase();
            }
            while (a.charAt(pos) === b.charAt(pos) && pos < min) {
                pos++;
            }
            return swedishAlphabet.indexOf(a.charAt(pos)) > swedishAlphabet.indexOf(b.charAt(pos)) ?
                dir : -dir;
        }
        
        return {
            replaceAll: function(str, search, replacement) {
                return str.replace(new RegExp(escapeRegExp(search), 'g'), replacement);
            },
            swedishStringSortImpl: function(asc, caseSensitive) {
                return swedishStringSorter(asc === true ? 1 : -1, caseSensitive);
            },
            compareSwedishString: function(asc, caseSensitive, a, b) {
                return compareSwedishString(asc === true ? 1 : -1, caseSensitive, a, b);
            }
        };

    }
);
