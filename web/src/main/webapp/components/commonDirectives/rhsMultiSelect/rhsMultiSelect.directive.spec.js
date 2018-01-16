describe('Directive: RhsMultiSelect', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var DiagnosKapitelModel;
    var testJsonData = [
        {id: 'A00-B99', name: 'Namn 1', displayValue: 'displayValue1'},
        {id: 'C00-D48', name: 'Namn 2', displayValue: 'displayValue2'}
    ];
    var element;
    var elementScope;

    // Store references to $rootScope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, _DiagnosKapitelModel_, $rootScope) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
        DiagnosKapitelModel = _DiagnosKapitelModel_;

        //Setup common prerequisites for all tests
        DiagnosKapitelModel.set(testJsonData);

        $scope.diagnosKapitelModel = DiagnosKapitelModel;

        element =
            $compile(' <rhs-multi-select items-model="diagnosKapitelModel" none-selected-title="inget-valt-titel"/>')(
                $scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));

    it('should show correct title when nothing is selected', function() {

        // Assert
        expect(elementScope.items.length).toEqual(2);
        expect(elementScope.getTitle()).toEqual('inget-valt-titel');

    });

    it('should show correct title when 1 item is selected', function() {

        // Act
        elementScope.toggleItem(elementScope.items[0]);

        //Assert
        expect(elementScope.getTitle()).toEqual(elementScope.items[0].displayValue);

    });

    it('should show correct title when 2 items are selected', function() {

        //Act
        elementScope.toggleItem(elementScope.items[0]);
        elementScope.toggleItem(elementScope.items[1]);

        //Assert
        expect(elementScope.getTitle()).toEqual('2 valda');
    });

});
