/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

describe('Directive: RhsDateRangePicker', function() {
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

        $scope.slutdatum = {from: null, to: null};

        element =
            $compile(' <rhs-date-range-picker model="slutdatum" />')(
                $scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));

    it('should be empty when no date is selected', function() {
        // Assert
        expect(element.find('input').val()).toEqual('');
    });

    it('Show one date', function() {

        $scope.slutdatum.from = new Date(2017, 1, 1);
        $scope.slutdatum.to = new Date(2017, 1, 1);

        $scope.$digest();

        // Assert
        expect(element.find('input').val()).toEqual('2017-02-01');
    });

    it('Show interval', function() {

        $scope.slutdatum.from = new Date(2017, 1, 1);
        $scope.slutdatum.to = new Date(2017, 3, 1);

        $scope.$digest();

        // Assert
        expect(element.find('input').val()).toEqual('Intervall');
    });

    it('Show reset value', function() {

        $scope.slutdatum.from = new Date(2017, 1, 1);
        $scope.slutdatum.to = new Date(2017, 3, 1);

        $scope.$digest();

        // Assert
        expect(element.find('input').val()).toEqual('Intervall');


        // Reset
        $scope.slutdatum.from = null;
        $scope.slutdatum.to = null;

        $scope.$digest();

        // Assert
        expect(element.find('input').val()).toEqual('');

    });

});
