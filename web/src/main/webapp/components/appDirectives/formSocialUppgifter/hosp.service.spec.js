describe('Service: HospService', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function($provide) {
        $provide.value('HospProxy', { 'getHospInformation' : function() {}});
    }));

    var HospViewState, HospService, HospModel, HospProxy, mockResponse, $rootScope, $httpBackend;

    var testModelNoHOSP = {'legitimeradYrkesgrupp':null,'specialitet':null,'forskrivarkod':null};

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _HospService_, _HospViewState_, _HospProxy_, _HospModel_, _mockResponse_) {
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        HospProxy = _HospProxy_;
        HospService = _HospService_;
        HospViewState = _HospViewState_;
        HospModel = _HospModel_;
        mockResponse = _mockResponse_;

        HospViewState.reset();
        HospModel.init();
        HospModel.set(testModelNoHOSP);
    }));

    describe('loadHosp', function() {

        // Success
        it('should decorate the model with hospinfo', function() {

            spyOn(HospProxy, 'getHospInformation').and.callFake(function() {
                return {
                    then: function(onSuccess) {
                        onSuccess(mockResponse.hospModel);
                    }
                };
            });

            HospService.loadHosp(HospViewState, HospModel);

            var model = HospModel.get();
            expect(model.legitimeradYrkesgrupp).toBe('hsaTitle1, hsaTitle2');
            expect(model.specialitet).toBe('specialityName1, specialityName2');
            expect(model.forskrivarkod).toBe('1234567');
        });

        // Fail

        it('should gracefully fail to decorate the model with hospinfo if it is not available', function() {

            spyOn(HospProxy, 'getHospInformation').and.callFake(function() {
                return {
                    then: function(onSuccess, onError) {
                        onError(mockResponse.hospFailModel);
                    }
                };
            });

            HospService.loadHosp(HospViewState, HospModel);

            var model = HospModel.get();
            expect(model.legitimeradYrkesgrupp).toBe(null);
            expect(model.specialitet).toBe(null);
            expect(model.forskrivarkod).toBe(null);
        });
    });
});
