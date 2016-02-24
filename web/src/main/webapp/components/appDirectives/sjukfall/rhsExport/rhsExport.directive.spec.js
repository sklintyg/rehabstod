describe('Directive: rhsExport', function () {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('rehabstodApp', function($provide) {
        $provide.value('APP_CONFIG', { });
    }));
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
        var expectedOrder = 'ASC';
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
        var expectedOrder = 'DESC';
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


