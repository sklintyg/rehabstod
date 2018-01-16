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

describe('Directive: rhsExport', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var SjukfallService;

    // Store references to $rootScope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope, _SjukfallService_){
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
        SjukfallService = _SjukfallService_;


    }));

    it('sort ASC start', function () {
        var rows = [{
            patient : {
                id: '123'
            }
        },{
            patient : {
                id: '234'
            }
        }];

        var sortColumn = 'start';
        var sortOrder = false;

        var elementScope = setupElement(rows, sortColumn, sortOrder);

        elementScope.exportExcel();

        // Assert
        var expextedColumn = 'start';
        var expectedOrder = 'asc';
        var expextedPatient = ['123', '234'];
        expect(SjukfallService.exportResult).toHaveBeenCalledWith('xlsx', expextedPatient, {kolumn: expextedColumn, order: expectedOrder});
    });

    it('sort DESC patient.id', function () {
        var rows = [{
            patient : {
                id: '123'
            }
        },{
            patient : {
                id: '234'
            }
        }];

        var sortColumn = 'patient.id';
        var sortOrder = true;

        var elementScope = setupElement(rows, sortColumn, sortOrder);

        elementScope.exportExcel();

        // Assert
        var expextedColumn = 'patient.id';
        var expectedOrder = 'desc';
        var expextedPatient = ['123', '234'];
        expect(SjukfallService.exportResult).toHaveBeenCalledWith('xlsx', expextedPatient, {kolumn: expextedColumn, order: expectedOrder});
    });

    it('no sort', function () {
        var rows = [{
            patient : {
                id: '989'
            }
        },{
            patient : {
                id: '222'
            }
        },{
            patient : {
                id: '333'
            }
        }];

        var sortColumn = null;
        var sortOrder = true;

        var elementScope = setupElement(rows, sortColumn, sortOrder);

        elementScope.exportExcel();

        // Assert
        var expextedColumn = null;
        var expectedOrder = null;
        var expextedPatient = ['989', '222', '333'];
        expect(elementScope.disabled).toBeFalsy();
        expect(SjukfallService.exportResult).toHaveBeenCalledWith('xlsx', expextedPatient, {kolumn: expextedColumn, order: expectedOrder});
    });

    it('no sort patients', function () {
        var rows = [];

        var sortColumn = null;
        var sortOrder = true;

        var elementScope = setupElement(rows, sortColumn, sortOrder);

        elementScope.exportExcel();

        // Assert
        var expextedColumn = null;
        var expectedOrder = null;
        var expextedPatient = [];
        expect(elementScope.disabled).toBeTruthy();
        expect(SjukfallService.exportResult).toHaveBeenCalledWith('xlsx', expextedPatient, {kolumn: expextedColumn, order: expectedOrder});
    });


    function setupElement(rows, sortColumn, sortOrder) {
        // Arrange
        var tableCtrl = {
            getFilteredCollection: function() { return rows; },
            tableState: function() {
                return {
                    sort: {
                        predicate: sortColumn,
                        reverse: sortOrder
                    }
                };
            }
        };

        spyOn(tableCtrl, 'getFilteredCollection').and.callThrough();
        spyOn(SjukfallService, 'exportResult').and.callFake(function() {});

        var element = angular.element('<rhs-export></rhs-export>');
        element.data('$stTableController', tableCtrl);

        var rowElement = $compile(element)($scope);

        // Act
        $scope.$digest();

        return rowElement.isolateScope() || rowElement.scope();
    }
});


