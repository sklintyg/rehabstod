describe('Service: SjukfallService', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp', function($provide) {
        $provide.value('SjukfallFilterViewState', {
            reset: function(){}
        });
    }));

    var SjukfallFilterViewState;
    var SjukfallModel;
    var SjukfallService;
    var SjukfallProxy;
    var sjukFallData = [{patient: { namn: 'Hej', kon: null }}];

    describe('Test load of Sjukfall', function () {
        // Initialize the controller and a mock scope
        beforeEach(inject(function(_SjukfallModel_, _SjukfallService_, _SjukfallProxy_, _SjukfallFilterViewState_) {
            SjukfallService = _SjukfallService_;
            SjukfallProxy = _SjukfallProxy_;
            SjukfallModel = _SjukfallModel_;
            SjukfallFilterViewState = _SjukfallFilterViewState_;
        }));


        it('Success', function() {
            spyOn(SjukfallProxy, 'get').and.callFake(function() {
                return {
                    then : function(success) {
                        success(sjukFallData);
                    }
                };
            });

            spyOn(SjukfallFilterViewState, 'reset');

            SjukfallService.loadSjukfall();

            expect(SjukfallFilterViewState.reset).toHaveBeenCalled();
            expect(SjukfallModel.get()).toEqual(sjukFallData);
        });

        it('Failed', function() {
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

        describe('Test reload of sjukfall', function () {
            beforeEach(inject(function() {
                spyOn(SjukfallProxy, 'get').and.callFake(function() {
                    return {
                        then : function(success) {
                            success(sjukFallData);
                        }
                    };
                });

                spyOn(SjukfallFilterViewState, 'reset');

                SjukfallService.loadSjukfall();

                expect(SjukfallModel.get()).toEqual(sjukFallData);
            }));

            it('Not reload if loaded', function() {
                SjukfallService.loadSjukfall();

                expect(SjukfallFilterViewState.reset.calls.count()).toEqual(1);
                expect(SjukfallProxy.get.calls.count()).toEqual(1);
            });

            it('Load and reset if force', function() {
                SjukfallService.loadSjukfall(true);

                expect(SjukfallFilterViewState.reset.calls.count()).toEqual(2);
                expect(SjukfallProxy.get.calls.count()).toEqual(2);
            });
        });
    });
});
