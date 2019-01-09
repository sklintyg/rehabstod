/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

describe('Directive: rhsDaysInRehabChain', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp', function() {
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

    function compileDirective(items) {
        $scope.items = items;
        element = $compile('<rhs-day-in-rehab-chain items="items"/>')($scope);
        $scope.$digest();

        return element.isolateScope() || element.scope();
    }

    function assertText(expected) {
        expect($(element).find('#day-in-rehab-chain-text').text()).toEqual(expected);
    }

    it('Should not render anything if isActive is false', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().subtract(20, 'days').format('YYYY-MM-DD')
            },
            'isActive': false
        } ];

        compileDirective(items);

        //Assert
        assertText('');

    });

    it('Should handle duration under 1 year correctly', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().subtract(364, 'days').format('YYYY-MM-DD')
            },
            'isActive': true
        } ];

        compileDirective(items);

        //Assert
        assertText('364 dagar');

    });

    it('Should handle duration of exactly 1 year correctly', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().subtract(1, 'years').format('YYYY-MM-DD')
            },
            'isActive': true
        } ];

        compileDirective(items);

        //Assert
        assertText('365 dagar');

    });

    it('Should handle duration of one year + one day correctly', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().subtract(1, 'years').subtract(1, 'days').format('YYYY-MM-DD')
            },
            'isActive': true
        } ];

        compileDirective(items);

        //Assert
        assertText('1 år 1 dagar');

    });

    it('Should handle duration over 1 year correctly', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().subtract(2, 'years').subtract(19, 'days').format('YYYY-MM-DD')
            },
            'isActive': true
        } ];

        compileDirective(items);

        //Assert
        assertText('2 år 19 dagar');

    });

    it('Should handle starting today correctly', function() {
        var items = [ {
            'sjukfall': {
                'start': moment().format('YYYY-MM-DD')
            },
            'isActive': true
        } ];

        compileDirective(items);

        //Assert
        assertText('0 dagar');

    });

});
