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

describe('Directive: rhsTableHeaderColumn', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var $timeout;
    var elementScope;
    var element;

    // Initialize the controller and a mock scope
    beforeEach(inject(function (_$compile_, $rootScope, _$timeout_) {
        $scope = $rootScope.$new();
        $compile = _$compile_;
        $timeout = _$timeout_;

        var tableCtrl = {
            getFilteredCollection: function() { return []; },
            tableState: function() {
                return {
                    sort: {
                        predicate: null,
                        reverse: true
                    }
                };
            }
        };

        element = angular.element('<th rhs-table-header-column label-key="sortValue" column="sortValue"></th>');
        element.data('$stTableController', tableCtrl);
        var rowElement = $compile(element)($scope);

        // Act
        $scope.$digest();

        elementScope = rowElement.isolateScope() || rowElement.scope();
    }));

    it('should set label', function() {
        expect(elementScope.label).toEqual('label.table.column.sortvalue');
    });

    it('should add header class', function() {
        $timeout.flush();
        $scope.$digest();
        expect(element.hasClass('rhs-table-head')).toBeTruthy();
    });
});


