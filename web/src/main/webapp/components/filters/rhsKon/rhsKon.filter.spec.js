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

describe('Filter: RhsKonFilter', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  it('has a rhsKon filter', inject(function($filter) {
    expect($filter('rhsKon')).not.toBeNull();
  }));

  it('should return male', inject(function(rhsKonFilter, messageService) {
    var returnValue = messageService.getProperty('label.gender.male');

    expect(rhsKonFilter('m')).toEqual(returnValue);
    expect(rhsKonFilter('M')).toEqual(returnValue);
  }));

  it('should return female', inject(function(rhsKonFilter, messageService) {
    var returnValue = messageService.getProperty('label.gender.female');

    expect(rhsKonFilter('f')).toEqual(returnValue);
    expect(rhsKonFilter('F')).toEqual(returnValue);
  }));

  it('should return -', inject(function(rhsKonFilter, messageService) {
    var returnValue = messageService.getProperty('label.gender.undefined');

    expect(rhsKonFilter(null)).toEqual(returnValue);
    expect(rhsKonFilter(undefined)).toEqual(returnValue);
    expect(rhsKonFilter()).toEqual(returnValue);
    expect(rhsKonFilter('x')).toEqual(returnValue);
  }));

});
