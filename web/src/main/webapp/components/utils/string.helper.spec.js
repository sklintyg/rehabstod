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

describe('Helper: StringHelper', function() {
  'use strict';

  beforeEach(module('rehabstodApp'));

  var StringHelper;

  // Inject dependencies and mocks
  beforeEach(inject(function(_StringHelper_) {
    StringHelper = _StringHelper_;
  }));

  it('replaceAll', function() {
    expect(StringHelper.replaceAll('A+B', '+', ' ')).toEqual('A B');
    expect(StringHelper.replaceAll('A+B+C', '+', ' ')).toEqual('A B C');
    expect(StringHelper.replaceAll('AB', '+', ' ')).toEqual('AB');
    expect(StringHelper.replaceAll('A B', '+', ' ')).toEqual('A B');
    expect(StringHelper.replaceAll('', '+', ' ')).toEqual('');
    expect(StringHelper.replaceAll('+', '+', ' ')).toEqual(' ');
  });

  it('should handle empty or undefined array ', function() {

    var arr = [];
    arr.sort(StringHelper.swedishStringSortImpl(true, false));
    expect(arr).toEqual([]);
  });

  it('should respect swedish alphabet order - asc', function() {

    var arr = ['Åa', 'Bö', 'Ab', 'David', 'aröööäåöåå'];
    arr.sort(StringHelper.swedishStringSortImpl(true, false));
    expect(arr[0]).toEqual('Ab');
    expect(arr[1]).toEqual('aröööäåöåå');
    expect(arr[2]).toEqual('Bö');
    expect(arr[3]).toEqual('David');
    expect(arr[4]).toEqual('Åa');

  });

  it('should respect swedish alphabet order - desc', function() {

    var arr = ['Åsa', 'Börje', 'Alfons', 'David'];
    arr.sort(StringHelper.swedishStringSortImpl(false, true));
    expect(arr[3]).toEqual('Alfons');
    expect(arr[2]).toEqual('Börje');
    expect(arr[1]).toEqual('David');
    expect(arr[0]).toEqual('Åsa');
  });

  it('sort unknown characters last - asc', function() {

    var arr = ['?', '_', 'é', 'e'];
    arr.sort(StringHelper.swedishStringSortImpl(true, false));
    expect(arr[0]).toEqual('e');
    expect(arr[1]).toEqual('?');
    expect(arr[2]).toEqual('_');
    expect(arr[3]).toEqual('é');
  });

  it('sort case sensitive', function() {

    var arr = ['a', 'ö', 'A', 'Ö'];
    arr.sort(StringHelper.swedishStringSortImpl(true, true));
    expect(arr[0]).toEqual('A');
    expect(arr[1]).toEqual('a');
    expect(arr[2]).toEqual('Ö');
    expect(arr[3]).toEqual('ö');
  });
});