describe('Directive: RhsStatPanel', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var scope, compile;
    var UnitCertificateSummaryModel;
    var UnitCertificateSummaryProxy;
    var testData = {total: 100, men: 0, women: 100};



    // Initialize the controller and a mock scope
    beforeEach(inject(function($compile, $rootScope, _UnitCertificateSummaryModel_, _UnitCertificateSummaryProxy_) {
        compile = $compile;
        scope = $rootScope.$new();
        UnitCertificateSummaryModel = _UnitCertificateSummaryModel_;
        UnitCertificateSummaryProxy = _UnitCertificateSummaryProxy_;
    }));


    it('should not reload data if already loaded', function() {

        // Arrange
        spyOn(UnitCertificateSummaryModel, 'get').and.callFake(function() {
            return testData;
        });
        spyOn(UnitCertificateSummaryProxy, 'get');

        // Act
        var element = compile('<rhs-stat-panel></rhs-stat-panel>')(scope);
        scope.$digest();

        var elementScope = element.isolateScope() || element.scope();

        // Assert
        expect(UnitCertificateSummaryModel.get).toHaveBeenCalled();
        expect(UnitCertificateSummaryProxy.get).not.toHaveBeenCalled();
        expect(elementScope.model).toBe(testData);
    });


    describe('test load of data', function() {

        var elementScope;

        beforeEach(function() {
            // Arrange
            spyOn(UnitCertificateSummaryModel, 'set').and.callThrough();
            spyOn(UnitCertificateSummaryProxy, 'get').and.callFake(function() {
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
            expect(UnitCertificateSummaryProxy.get).toHaveBeenCalled();
            expect(UnitCertificateSummaryModel.set).toHaveBeenCalledWith(testData);
            expect(elementScope.model).toEqual(testData);
        });

        it('should load data on event', function() {
            scope.$emit('SelectedUnitChanged', {enhet: '123'});
            scope.$digest();

            // Assert
            expect(UnitCertificateSummaryProxy.get.calls.count()).toEqual(2);
            expect(UnitCertificateSummaryModel.set).toHaveBeenCalledWith(testData);
            expect(elementScope.model).toEqual(testData);
        });
    });
});
