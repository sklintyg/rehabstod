/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

    it('should set max value when a value above max is entered', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.displayMaxValueAs = 'Mer';
        elementScope.max = 10;


        //Act
        elementScope.inputModel = '118';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.inputModel).toEqual('Mer');
        expect(elementScope.externalModel).toEqual(10);
    });

    it('should set min value when a value below min is entered', function() {
        //Arrange
        elementScope.min = 2;
        elementScope.externalModel = 5;
        elementScope.displayMaxValueAs = 'Mer';
        elementScope.max = 10;


        //Act
        elementScope.inputModel = '1';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.inputModel).toEqual(2);
        expect(elementScope.externalModel).toEqual(2);
    });

    it('remove non numeric values in input', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 5;
        elementScope.max = 10;


        //Act
        elementScope.inputModel = 'osk';
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

    it('should handle show previous value when entering only a "-"', function() {
        //Arrange
        elementScope.min = 0;
        elementScope.externalModel = 8;
        elementScope.inputModel = 5;
        elementScope.displayMaxValueAs = 'Mer';
        elementScope.max = 10;

        //Act
        elementScope.inputModel = '-';
        elementScope.onManualChange();
        $scope.$digest();

        // Assert
        expect(elementScope.externalModel).toEqual(8);
        expect(elementScope.inputModel).toEqual(8);
    });

});