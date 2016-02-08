describe('Model: DiagnosGruppModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var DiagnosGruppModel;
    var testJsonData = [
        {id: 'A00-B99', name: 'Vissa infektionssjukdomar och parasitsjukdomar'},
        {id: 'C00-D48', name: 'Tumörer- disabled'},
        {id: 'D50-D89', name: 'Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet'},
        {id: 'E00-E90', name: 'Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar'}
    ];

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_DiagnosGruppModel_) {
        DiagnosGruppModel = _DiagnosGruppModel_;
    }));

    describe('set', function() {
        it('should set internal array correctly', function() {
            DiagnosGruppModel.set(testJsonData);
            expect(DiagnosGruppModel.get().length).toEqual(testJsonData.length);
            expect(DiagnosGruppModel.get()[0].id).toEqual(testJsonData[0].id);
            expect(DiagnosGruppModel.get()[0].displayValue).toEqual(testJsonData[0].id + ': ' + testJsonData[0].name);
            expect(DiagnosGruppModel.get()[0].selected).toBeFalsy();
            expect(DiagnosGruppModel.get()[0].disabled).toBeFalsy();
        });
    });

    describe('getSelected', function() {
        it('should return selected items correctly', function() {
            DiagnosGruppModel.set(testJsonData);
            expect(DiagnosGruppModel.getSelected().length).toEqual(0);

            DiagnosGruppModel.get()[0].selected = true;

            expect(DiagnosGruppModel.getSelected().length).toEqual(1);
            expect(DiagnosGruppModel.getSelected()[0]).toEqual(testJsonData[0].id);

        });
    });

});
