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

describe('Directive: RhsTooltip', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp', function($provide) {

    var messageServiceStup = {
      getProperty: function(key) {
        return key;
      },
      addResources: function() {
      },
      addLinks: function() {
      }
    };

    $provide.value('messageService', messageServiceStup);
  }));
  beforeEach(module('htmlTemplates'));

  var $compile;
  var $scope;
  var element;

  // Store references to $scope and $compile
  // so they are available to all tests in this describe block
  beforeEach(inject(function(_$compile_, $rootScope) {
    $compile = _$compile_;
    $scope = $rootScope.$new();
  }));

  function compileDirective(text, alignment) {

    var align = alignment ? 'alignment="' + alignment + '"' : '';

    element = $compile('<div rhs-tooltip field-help-text="' + text + '" ' + align + ' />')($scope);
    $scope.$digest();

    return element.isolateScope() || element.scope();
  }

  it('Should set alignment if not provided', function() {
    var scope = compileDirective();

    expect(scope.align).toEqual('auto top');
  });

  it('Should use the alignment provided', function() {
    var scope = compileDirective('', 'bottom');

    expect(scope.align).toEqual('bottom');
  });

  it('Get message', function() {
    var scope = compileDirective();

    var message = scope.getMessage('message.key');

    expect(message).toEqual('message.key');
  });

});
