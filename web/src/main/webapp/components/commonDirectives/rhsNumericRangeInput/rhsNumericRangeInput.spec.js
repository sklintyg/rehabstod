describe('Directive: RhsNumericRangeInput', function() {
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
        $scope.max = 10;

        element =
            $compile(
                angular.element(' <rhs-numeric-range-input external-model="externalModel" min="0" max="10" display-max-value-as="Mer än 10"/>'))(
                $scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();
    }));

    it('should correctly initialize internal model', function() {

        // Assert
        expect(elementScope.inputModel).toEqual(elementScope.externalModel);


    });

    it('should increase when UP is clicked', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.max = 10;

        //Act
        elementScope.onClickUp();

        // Assert
        expect(elementScope.externalModel).toEqual(6);
    });

    it('should NOT increase MAX is reached and UP is clicked', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 10;
        elementScope.max = 10;

        //Act
        elementScope.onClickUp();

        // Assert
        expect(elementScope.externalModel).toEqual(10);
    });

    it('should decrease when DOWN is clicked', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.max = 10;

        //Act
        elementScope.onClickDown();

        // Assert
        expect(elementScope.externalModel).toEqual(4);
    });

    it('should NOT decrease MIN is reached and DOWN is clicked', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 0;
        elementScope.max = 10;

        //Act
        elementScope.onClickDown();

        // Assert
        expect(elementScope.externalModel).toEqual(0);
    });

    it('should display max replacement when max is reached', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 10;
        elementScope.displayMaxValueAs = 'ett värde';
        elementScope.max = 10;
        $scope.$digest();

        // Assert
        expect(elementScope.inputModel).toEqual('ett värde');
    });

    it('should update when manually entering a valid range value', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.max = 10;


        //Act
        elementScope.inputModel = '8';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.inputModel).toEqual(8);
        expect(elementScope.externalModel).toEqual(8);
    });

    it('should NOT update when manually entering a invalid range value', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.max = 10;


        //Act
        elementScope.inputModel = '118';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.inputModel).toEqual(5);
        expect(elementScope.externalModel).toEqual(5);
    });

    it('should handle showmaxreplace value as MAX update when manually entering', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 8;
        elementScope.inputModel = 5;
        elementScope.displayMaxValueAs = 'Mer';
        elementScope.max = 10;

        //Act
        elementScope.inputModel = 'Mer';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.externalModel).toEqual(10);
    });

});