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

describe('Directive: RhsStatPanel', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var scope, compile;
    var SjukfallSummaryModel;
    var SjukfallSummaryProxy;
    var UserModel;
    var testData = {total: 100, men: 0, women: 100, groups: [], diagnoseGroupData: [] };




    // Initialize the controller and a mock scope
    beforeEach(inject(function($compile, $rootScope, _SjukfallSummaryModel_, _SjukfallSummaryProxy_, _UserModel_) {
        compile = $compile;
        scope = $rootScope.$new();
        SjukfallSummaryModel = _SjukfallSummaryModel_;
        SjukfallSummaryProxy = _SjukfallSummaryProxy_;
        UserModel = _UserModel_;
    }));


    it('should not reload data if already loaded', function() {

        // Arrange
        spyOn(SjukfallSummaryModel, 'get').and.callFake(function() {
            return testData;
        });

        spyOn(UserModel, 'get').and.callFake(function() {
            return { isLakare: true };
        });


        spyOn(SjukfallSummaryProxy, 'get');

        // Act
        var element = compile('<rhs-stat-panel></rhs-stat-panel>')(scope);
        scope.$digest();

        var elementScope = element.isolateScope() || element.scope();

        // Assert
        expect(SjukfallSummaryModel.get).toHaveBeenCalled();
        expect(SjukfallSummaryProxy.get).not.toHaveBeenCalled();
        expect(elementScope.model).toBe(testData);
        expect(elementScope.isLakare).toBeTruthy();
    });


    describe('test load of data', function() {

        var elementScope;

        beforeEach(function() {
            // Arrange
            spyOn(SjukfallSummaryModel, 'set').and.callThrough();
            spyOn(SjukfallSummaryProxy, 'get').and.callFake(function() {
                return {
                    then : function(onSuccess) {
                        onSuccess(testData);
                    }
                };
            });

            // Act
            var element = compile('<rhs-stat-panel></rhs-stat-panel>')(scope);
            scope.$digest();
            elementScope = element.isolateScope() || element.scope();
        });


        it('should load data if not loaded', function() {
            // Assert
            expect(SjukfallSummaryProxy.get).toHaveBeenCalled();
            expect(SjukfallSummaryModel.set).toHaveBeenCalledWith(testData);
            expect(elementScope.model).toEqual(testData);
        });

        it('should load data on event', function() {
            scope.$emit('SelectedUnitChanged', {enhet: '123'});
            scope.$digest();

            // Assert
            expect(SjukfallSummaryProxy.get.calls.count()).toEqual(2);
            expect(SjukfallSummaryModel.set).toHaveBeenCalledWith(testData);
            expect(elementScope.model).toEqual(testData);
        });
    });
});
