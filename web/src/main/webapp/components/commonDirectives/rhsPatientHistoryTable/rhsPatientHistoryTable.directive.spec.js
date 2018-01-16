describe('Directive: rhsPatientHistoryTable', function() {
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
        $scope.item = {};
        element = $compile(' <rhs-patient-history-table history-item="item" index="1" on-select=""/>')($scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));

    it('should render correct number of sjukskrivningsgrader', function() {

        // Assert
        expect(elementScope.formatGrader([100])).toEqual('100%');
        expect(elementScope.formatGrader([100,25])).toEqual('100% &#10142; 25%');
        expect(elementScope.formatGrader([100,25,50])).toEqual('100% &#10142; 50%');
        expect(elementScope.formatGrader([100,25,75,50])).toEqual('100% &#10142; 50%');

    });



});
