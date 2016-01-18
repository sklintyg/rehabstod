describe('Proxy: TermsProxy', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('privatlakareApp', function(/*$provide*/) {

    }));

    var TermsProxy, mockResponse, $rootScope, $httpBackend;
    
    // Initialize the controller and a mock scope

    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _TermsProxy_, _mockResponse_) {
        $httpBackend = _$httpBackend_;
        TermsProxy = _TermsProxy_;
        $rootScope = _$rootScope_;
        mockResponse = _mockResponse_;
    }));

    describe('TermsProxy', function() {
        it('should get the logged in user', function() {

            var onSuccess = jasmine.createSpy('onSuccess');
            var onError = jasmine.createSpy('onError');

            $httpBackend.expectGET('/api/terms/webcert').respond(mockResponse.termsOK);

            TermsProxy.getTerms('webcert').then(onSuccess, onError);
            $httpBackend.flush();
            // promises are resolved/dispatched only on next $digest cycle
            $rootScope.$apply();

            expect(onSuccess).toHaveBeenCalledWith(mockResponse.termsModel);
            expect(onError).not.toHaveBeenCalled();
        });
    });
});
