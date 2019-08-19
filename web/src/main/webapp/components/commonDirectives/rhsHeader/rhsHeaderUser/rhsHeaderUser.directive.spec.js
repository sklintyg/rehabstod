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

describe('Directive: rhsHeaderUser', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));
  beforeEach(module('htmlTemplates'));

  var $scope;
  var element;
  var elementScope;
  var UserModel;

  // Store references to $rootScope and $compile
  // so they are available to all tests in this describe block
  beforeEach(inject(function($compile, $rootScope, _UserModel_) {
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $scope = $rootScope.$new();
    UserModel = _UserModel_;

    element =
        $compile(' <rhs-header-user />')(
            $scope);
    $scope.$digest();

    elementScope = element.isolateScope() || element.scope();

  }));

  it('show description if user is lakare', function() {
    var role = {
      name: 'LAKARE',
      desc: 'Läkare'
    };

    expect(elementScope.showRoleDescription(role)).toBeTruthy();
  });

  it('hide description if user is REHABKOORDINATOR', function() {
    var role = {
      name: 'REHABKOORDINATOR',
      desc: 'Rehabkoordinator'
    };

    expect(elementScope.showRoleDescription(role)).toBeFalsy();
  });
});


