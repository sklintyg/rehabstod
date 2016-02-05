describe('Directive: RhsTableGrad', function () {
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

    it('should show grad active', function() {
        var activeGard = 25;
        var grader = [100, 50, 25];

        $scope.active = activeGard;
        $scope.grader = grader;


        var element = $compile('<rhs-table-grad active="active" grader="grader"></rhs-table-grad>')($scope);
        $scope.$digest();

        var elementScope = element.isolateScope() || element.scope();

        // Assert
        expect(elementScope.isActive(activeGard)).toBeTruthy();
        expect(elementScope.isActive(50)).toBeFalsy();
        expect(elementScope.isActive(10)).toBeFalsy();
    });

    it('should return false on isActive', function() {
        var grader = [100, 50, 25];

        $scope.grader = grader;

        var element = $compile('<rhs-table-grad grader="grader"></rhs-table-grad>')($scope);
        $scope.$digest();

        var elementScope = element.isolateScope() || element.scope();

        // Assert
        expect(elementScope.isActive(100)).toBeFalsy();
        expect(elementScope.isActive(50)).toBeFalsy();
        expect(elementScope.isActive(10)).toBeFalsy();
    });
});
