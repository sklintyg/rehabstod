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
describe('DatePickerFieldDirective', function() {
  'use strict';

  var $scope, tpl, inputElement;

  function _applyValue(value) {
    angular.element(inputElement).val(value).trigger('input');
    $scope.$apply();
  }

  function _validateOutcome(value) {
    expect($scope.testForm.test.$viewValue).toBe(value);
    expect($scope.testForm.test.$modelValue).toBe(value);
  }

  beforeEach(angular.mock.module('htmlTemplates'));
  beforeEach(angular.mock.module('rehabstodApp'), function() {
  });
  beforeEach(angular.mock.inject(['$rootScope', '$compile',
    function($rootScope, $compile) {

      tpl = angular.element(
          '<div ng-form="testForm">' +
          '<rhs-date-picker target-model="model" dom-id="test" add-date-parser="loose"></rhs-date-picker>' +
          '</div>'
      );

      $scope = $rootScope.$new();
      $scope.model = null;
      $compile(tpl)($scope);
      $scope.$digest();

      // Find the input control:
      inputElement = tpl.find('input');
    }
  ]));

  it('should allow 2016-09-12', function() {
    _applyValue('2016-09-12');
    _validateOutcome('2016-09-12');
  });

  it('should allow 2016-0912', function() {
    _applyValue('2016-0912');
    _validateOutcome('2016-09-12');
  });

  it('should allow 201609-12', function() {
    _applyValue('201609-12');
    _validateOutcome('2016-09-12');
  });

  it('should allow 2016/09/12', function() {
    _applyValue('2016/09/12');
    _validateOutcome('2016-09-12');
  });

  it('should allow 2016/0912', function() {
    _applyValue('2016/0912');
    _validateOutcome('2016-09-12');
  });

  it('should allow 201609/12', function() {
    _applyValue('201609/12');
    _validateOutcome('2016-09-12');
  });

  it('should allow 20160912', function() {
    _applyValue('20160912');
    _validateOutcome('2016-09-12');
  });

});
