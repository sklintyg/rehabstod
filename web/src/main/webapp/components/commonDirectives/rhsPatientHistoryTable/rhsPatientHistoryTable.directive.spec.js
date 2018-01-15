/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
describe('Directive: rhsPatientHistoryTable', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var element;
    var elementScope;

    // Store references to $rootScope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
        $scope.item = {};
        element = $compile(' <rhs-patient-history-table history-item="item" index="1" on-select=""/>')($scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));

    it('should render correct number of sjukskrivningsgrader', function() {

        // Assert
        expect(elementScope.formatGrader([100])).toEqual('100%');
        expect(elementScope.formatGrader([100,25])).toEqual('100% &#10142; 25%');
        expect(elementScope.formatGrader([100,25,50])).toEqual('100% &#10142; 50%');
        expect(elementScope.formatGrader([100,25,75,50])).toEqual('100% &#10142; 50%');

    });



});
