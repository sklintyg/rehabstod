describe('Proxy: OmradeProxy', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('privatlakareApp', function(/*$provide*/) {

    }));

    var OmradeProxy, mockResponse, $rootScope, $httpBackend, PostnummerHelper;
    
    // Initialize the controller and a mock scope

    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _OmradeProxy_, _mockResponse_, _PostnummerHelper_) {
        $httpBackend = _$httpBackend_;
        OmradeProxy = _OmradeProxy_;
        $rootScope = _$rootScope_;
        mockResponse = _mockResponse_;
        PostnummerHelper = _PostnummerHelper_;
    }));

    describe('OmradeProxy', function() {
        it('should get the omrade list info for the supplied postnummer', function() {

            function testOmradeProxySuccess(postnummer) {
                var onSuccess = jasmine.createSpy('onSuccess');
                var onError = jasmine.createSpy('onError');

                var cleanedpostnr = PostnummerHelper.cleanPostnummer(postnummer);

                $httpBackend.expectGET('/api/registration/omrade/' + cleanedpostnr).respond(mockResponse.omradeOK);

                OmradeProxy.getOmradeList(cleanedpostnr).then(onSuccess, onError);
                $httpBackend.flush();
                // promises are resolved/dispatched only on next $digest cycle
                $rootScope.$apply();

                expect(onSuccess).toHaveBeenCalledWith(mockResponse.omradeModel);
                expect(onError).not.toHaveBeenCalled();
            }

            // Success
            testOmradeProxySuccess('13155');
            testOmradeProxySuccess('131 55');
            testOmradeProxySuccess(' 131 55 ');

            // Fail
            function testOmradeProxyFail(postnummer) {
                var onSuccess = jasmine.createSpy('onSuccess2');
                var onError = jasmine.createSpy('onError2');

                OmradeProxy.getOmradeList(postnummer).then(onSuccess, onError);
                // promises are resolved/dispatched only on next $digest cycle
                $rootScope.$apply();

                expect(onSuccess).not.toHaveBeenCalled();
                expect(onError).toHaveBeenCalledWith(null);
            }

            testOmradeProxyFail(13155);
            testOmradeProxyFail('asf öaösff');
            testOmradeProxyFail(null);
            testOmradeProxyFail(undefined);
        });
    });
});
