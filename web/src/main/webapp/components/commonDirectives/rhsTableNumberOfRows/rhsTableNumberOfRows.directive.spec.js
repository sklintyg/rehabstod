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

describe('Directive: RhsTableNumberOfRows', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;

    // Store references to $rootScope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope){
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
    }));

    it('should show the correct number of rows in table', inject(function (messageService) {
        // Arrange

        var tableCtrl = {
            getFilteredCollection: function() { return [1,2,3]; }
        };

        spyOn(tableCtrl, 'getFilteredCollection').and.callThrough();

        var element = angular.element('<rhs-table-number-of-rows total-rows="3"></rhs-table-number-of-rows>');
        element.data('$stTableController', tableCtrl);

        var rowElement = $compile(element)($scope);

        var html = messageService.getProperty('label.table.number.of.rows') + ' ' + 3 + ' ' +
            messageService.getProperty('label.table.number.of.rows.of') + ' ' + 3;

        // Act
        $scope.$digest();


        // Assert
        expect(rowElement.html()).toContain(html);
        expect(tableCtrl.getFilteredCollection).toHaveBeenCalled();
    }));
});
