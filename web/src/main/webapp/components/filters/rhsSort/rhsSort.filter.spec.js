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

describe('Filter: RhsSortFilter', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    it('has a rhsSort filter', inject(function($filter) {
        expect($filter('rhsSort')).not.toBeNull();
    }));

    it('sort strings ascii', inject(function (rhsSortFilter) {
        var array = [{name: 'b'}, {name: 'a'}, {name: 'c'}];

        expect(rhsSortFilter(array, 'name', false)).toEqual([{name: 'a'}, {name: 'b'}, {name: 'c'}]);
        expect(rhsSortFilter(array, 'name', true)).toEqual([{name: 'c'}, {name: 'b'}, {name: 'a'}]);
    }));

    it('sort strings swedish', inject(function (rhsSortFilter) {
        var array = [{name: 'a'}, {name: 'ö'}, {name: 'ä'}, {name: 'å'}];

        expect(rhsSortFilter(array, 'name', false)).toEqual([{name: 'a'}, {name: 'å'}, {name: 'ä'}, {name: 'ö'}]);
        expect(rhsSortFilter(array, 'name', true)).toEqual([{name: 'ö'}, {name: 'ä'}, {name: 'å'}, {name: 'a'}]);
    }));

    it('sort number', inject(function (rhsSortFilter) {
        var array = [{name: 1}, {name: 12}, {name: 5}, {name: 20}];

        expect(rhsSortFilter(array, 'name', false)).toEqual([{name: 1}, {name: 5}, {name: 12}, {name: 20}]);
        expect(rhsSortFilter(array, 'name', true)).toEqual([{name: 20}, {name: 12}, {name: 5}, {name: 1}]);
    }));

    it('sort mix', inject(function (rhsSortFilter) {
        var array = [{name: '1åäö'}, {name: 'a12'}, {name: 'test'}, {name: 'b12'}];

        expect(rhsSortFilter(array, 'name', false)).toEqual([{name: '1åäö'}, {name: 'a12'}, {name: 'b12'}, {name: 'test'}]);
        expect(rhsSortFilter(array, 'name', true)).toEqual([{name: 'test'}, {name: 'b12'}, {name: 'a12'}, {name: '1åäö'}]);
    }));

});
