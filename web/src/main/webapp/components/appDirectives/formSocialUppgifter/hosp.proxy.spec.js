describe('Proxy: HospProxy', function() {
    'use strict';

    beforeEach(module('htmlTemplates'));

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('privatlakareApp', function(/*$provide*/) {

    }));

    var HospProxy, mockResponse, $rootScope, $httpBackend;
    
    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _HospProxy_, _mockResponse_) {
        $httpBackend = _$httpBackend_;
        HospProxy = _HospProxy_;
        $rootScope = _$rootScope_;
        mockResponse = _mockResponse_;
    }));

    describe('getHospInformation', function() {
        it('should get the hosp info for the logged in privatlakare', function() {
            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');
            $httpBackend.expectGET('/api/registration/hospInformation').respond(mockResponse.hospOK);

            HospProxy.getHospInformation().then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalledWith(mockResponse.hospModel);
            expect(onError).not.toHaveBeenCalled();
        });
    });
});
