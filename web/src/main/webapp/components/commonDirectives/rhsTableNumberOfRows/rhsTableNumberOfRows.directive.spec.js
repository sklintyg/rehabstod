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

    it('ensures callFoo does whatever it is supposed to', function() {
        // Arrange

        var tableCtrl = {
            getFilteredCollection: function() { return [1,2,3]; }
        };

        spyOn(tableCtrl, 'getFilteredCollection').and.callThrough();

        var element = angular.element('<rhs-table-number-of-rows></rhs-table-number-of-rows>');
        element.data('$stTableController', tableCtrl);

        var rowElement = $compile(element)($scope);
        
        // Act
        $scope.$digest();

        // Assert
        expect(rowElement.html()).toContain("Antal poster: 3");
        expect(tableCtrl.getFilteredCollection).toHaveBeenCalled();
    });
});
