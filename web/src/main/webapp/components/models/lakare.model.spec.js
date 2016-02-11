describe('Model: LakareModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var LakareModel;
    var testJsonData = ['KALLE','OLLE','NISSE'];

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_LakareModel_) {
        LakareModel = _LakareModel_;
    }));

    describe('set', function() {
        it('should set internal array correctly', function() {
            LakareModel.set(testJsonData);
            expect(LakareModel.get().length).toEqual(testJsonData.length);
            expect(LakareModel.get()[0].id).toEqual(testJsonData[0]);
            expect(LakareModel.get()[0].displayValue).toEqual(testJsonData[0]);
            expect(LakareModel.get()[0].selected).toBeFalsy();
            expect(LakareModel.get()[0].disabled).toBeFalsy();
        });
    });

    describe('getSelected', function() {
        it('should return selected items correctly', function() {
            LakareModel.set(testJsonData);
            expect(LakareModel.getSelected().length).toEqual(0);

            LakareModel.get()[0].selected = true;
            expect(LakareModel.getSelected().length).toEqual(1);
            expect(LakareModel.getSelected()[0].id).toEqual(testJsonData[0]);
            expect(LakareModel.getSelected()[0].displayValue).toEqual(testJsonData[0]);

        });
    });

    describe('reset', function() {
        it('should reset correctly', function() {
            LakareModel.set(testJsonData);

            expect(LakareModel.getSelected().length).toEqual(0);
            LakareModel.get()[0].selected = true;

            expect(LakareModel.getSelected().length).toEqual(1);
            LakareModel.reset();
            expect(LakareModel.getSelected().length).toEqual(0);

        });
    });

});
