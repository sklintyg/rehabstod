describe('Directive: RhsStatPanel', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var scope, compile;
    var SjukfallSummaryModel;
    var SjukfallSummaryProxy;
    var testData = {total: 100, men: 0, women: 100};



    // Initialize the controller and a mock scope
    beforeEach(inject(function($compile, $rootScope, _SjukfallSummaryModel_, _SjukfallSummaryProxy_) {
        compile = $compile;
        scope = $rootScope.$new();
        SjukfallSummaryModel = _SjukfallSummaryModel_;
        SjukfallSummaryProxy = _SjukfallSummaryProxy_;
    }));


    it('should not reload data if already loaded', function() {

        // Arrange
        spyOn(SjukfallSummaryModel, 'get').and.callFake(function() {
            return testData;
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
        expect(elementScope.today).toBeDefined();
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
