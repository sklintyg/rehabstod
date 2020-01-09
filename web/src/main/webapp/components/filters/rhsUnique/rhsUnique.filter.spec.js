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

describe('Filter: rhsUniqueFilter', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  it('has a rhsUniqueFilter filter', inject(function($filter) {
    expect($filter('rhsUnique')).not.toBeNull();
  }));

  it('should handle plain array using true as propertyExpression', inject(function(rhsUniqueFilter) {

        var dupArray = ['1', 2, 'A', 2, 'A', 'B', 'A'];
        expect(rhsUniqueFilter(dupArray, true)).toEqual(['1', 2, 'A', 'B']);
      })
  );

  it('should handle nested object properties ', inject(function(rhsUniqueFilter) {

        var dupArray = [
          {
            root: {
              propA: {
                propA1: 'a1'
              },
              propB: 'b1'
            }
          },
          {
            root: {
              propA: {
                propA1: 'a1'
              },
              propB: 'b2'
            }
          },
          {
            root: {
              propA: {
                propA1: 'a1'
              },
              propB: 'b3'
            }
          }

        ];
        expect(rhsUniqueFilter(dupArray, 'root.propA.propA1').length).toEqual(1);
        expect(rhsUniqueFilter(dupArray, 'root.propB')).toEqual(['b1', 'b2', 'b3']);
      })
  );

});