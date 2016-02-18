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

        var html = messageService.getProperty('label.table.number.of.rows') + ' ' + 3 + ' ' + messageService.getProperty('label.table.number.of.rows.of') + ' ' + 3;

        // Act
        $scope.$digest();


        // Assert
        expect(rowElement.html()).toContain(html);
        expect(tableCtrl.getFilteredCollection).toHaveBeenCalled();
    }));
});
