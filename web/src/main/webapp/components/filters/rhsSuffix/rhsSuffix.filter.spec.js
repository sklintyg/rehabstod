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

describe('Filter: RhsSuffixFilter', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  it('has a rhsSuffix filter', inject(function($filter) {
    expect($filter('rhsSuffix')).not.toBeNull();
  }));

  it('should return "..." if no value ', inject(function(rhsSuffixFilter) {
    var noValue = '...';

    expect(rhsSuffixFilter(null, 'st', noValue)).toEqual(noValue);
    expect(rhsSuffixFilter(null, null, noValue)).toEqual(noValue);
    expect(rhsSuffixFilter('', null, noValue)).toEqual(noValue);
    expect(rhsSuffixFilter(false, null, noValue)).toEqual(noValue);
    expect(rhsSuffixFilter(null, null, null)).toBeNull();
  }));

  it('should return value with suffix', inject(function(rhsSuffixFilter) {

    expect(rhsSuffixFilter(123, null, null)).toEqual(123);
    expect(rhsSuffixFilter(123, 'st', null)).toEqual('123 st');
    expect(rhsSuffixFilter(123, 'st', '...')).toEqual('123 st');
    expect(rhsSuffixFilter(0, 'st', '...')).toEqual('0 st');
  }));

});
