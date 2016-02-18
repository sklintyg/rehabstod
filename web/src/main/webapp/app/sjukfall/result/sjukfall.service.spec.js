describe('Service: SjukfallService', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var SjukfallModel;
    var SjukfallService;
    var SjukfallProxy;
    var sjukFallData = [{patient: { namn: 'Hej', kon: null }}];

    describe('Test load of Sjukfall', function () {
        // Initialize the controller and a mock scope
        beforeEach(inject(function(_SjukfallModel_, _SjukfallService_, _SjukfallProxy_) {
            SjukfallService = _SjukfallService_;
            SjukfallProxy = _SjukfallProxy_;
            SjukfallModel = _SjukfallModel_;

        }));


        it('Success', function() {
            spyOn(SjukfallProxy, 'get').and.callFake(function() {
                return {
                    then : function(success) {
                        success(sjukFallData);
                    }
                };
            });

            SjukfallService.loadSjukfall();

            expect(SjukfallModel.get()).toEqual(sjukFallData);
        });

        it('Faled', function() {
            spyOn(SjukfallProxy, 'get').and.callFake(function() {
                return {
                    then : function(success, error) {
                        error({error: 401});
                    }
                };
            });

            SjukfallService.loadSjukfall();

            expect(SjukfallModel.get()).toEqual([]);
        });

    });
});
