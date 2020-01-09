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

describe('Directive: RhsVardenhetSelector', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));
  beforeEach(module('htmlTemplates'));

  var $compile;
  var $scope;
  var element;
  var elementScope;

  var userJson = {
    'valdVardenhet': {
      id: 'm21'
    },
    'vardgivare': [

      {
        'id': 'TSTNMT2321000156-105M22',
        'namn': 'Rehabstöd Vårdgivare 2',
        'vardenheter': [{
          'id': 'TSTNMT2321000156-105N22',
          'namn': 'Rehabstöd Enhet 2.1',
          'mottagningar': [{
            'id': 'mottagning 1',
            'namn': 'mottagning 1',
            'parentHsaId': 'linkoping'
          }, {
            'id': 'mottagning 2',
            'namn': 'mottagning 2',
            'parentHsaId': 'linkoping'
          }]
        }, {
          'id': 'TSTNMT2321000156-105N222B',
          'namn': 'Rehabstöd Enhet 2.2',
          'mottagningar': [{
            'id': 'm21',
            'namn': 'mottagning 1',
            'parentHsaId': 'linkoping'
          }]
        }]
      }]
  };

  // Store references to $scope and $compile
  // so they are available to all tests in this describe block
  beforeEach(inject(function(_$compile_, $rootScope) {
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $compile = _$compile_;
    $scope = $rootScope.$new();
    //Setup common prerequsites for all tests
    $scope.userParameter = userJson;
    element = $compile('<rhs-vardenhet-selector user="userParameter" expand-vardgivare="true"/> roleSwitchMessageKey="temp"')($scope);
    $scope.$digest();

    elementScope = element.isolateScope() || element.scope();

  }));

  it('should have correct nodes expanded for valdVardenhet', function() {
    // Assert
    expect(elementScope.model.vardgivare[0].expanded).toBeTruthy();
    expect(elementScope.model.vardgivare[0].vardenheter[0].expanded).toBeFalsy();
    expect(elementScope.model.vardgivare[0].vardenheter[1].expanded).toBeTruthy();
  });

  it('should have calculated correct number of units', function() {
    // Assert
    expect(elementScope.getTotalVECount(elementScope.model.vardgivare)).toBe(2);

  });

});
