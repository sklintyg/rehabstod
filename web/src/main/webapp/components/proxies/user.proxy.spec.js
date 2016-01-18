describe('Proxy: UserProxy', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('privatlakareApp', function(/*$provide*/) {

    }));

    var UserProxy, mockResponse, $rootScope, $httpBackend;
    
    // Initialize the controller and a mock scope

    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _UserProxy_, _mockResponse_) {
        $httpBackend = _$httpBackend_;
        UserProxy = _UserProxy_;
        $rootScope = _$rootScope_;
        mockResponse = _mockResponse_;
    }));

    describe('UserProxy', function() {
        it('should get the logged in user', function() {

            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');

            $httpBackend.expectGET('/api/user').respond(mockResponse.userOK);

            UserProxy.getUser().then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalledWith(mockResponse.userModel);
            expect(onError).not.toHaveBeenCalled();
        });
    });
});
