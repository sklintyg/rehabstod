/* globals window */
describe('Service: TermsService', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('privatlakareApp', function($provide) {
        $provide.value('$state', { params: { terms: null, termsData: null }});
    }));

    var $rootScope, $httpBackend, $state, $timeout, wcModalService, $window, AppTermsModalModel, TermsModel, TermsService;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _$state_, _$timeout_,
        _TermsService_, _AppTermsModalModel_, _wcModalService_, _$window_, _TermsModel_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        TermsService = _TermsService_;
        $state = _$state_;
        $timeout = _$timeout_;
        AppTermsModalModel = _AppTermsModalModel_;
        TermsModel = _TermsModel_;
        wcModalService = _wcModalService_;
        $window = _$window_;
    }));

    describe('openTerms', function() {
        it('should call the wcModalService to open a dialog', function() {
            spyOn(wcModalService, 'open').and.stub();
            var modalModel = AppTermsModalModel.init();
            TermsService.openTerms(modalModel);
            expect(wcModalService.open).toHaveBeenCalled();
        });
    });
    describe('loadTerms', function() {
        it('should fetch terms from webcert if stateparams.terms are set to "webcert"', function() {
            $httpBackend.expectGET('/api/terms/webcert').respond(200, { terms: {}});
            $state.params.terms = 'webcert';
            TermsService.loadTerms().then(function(terms) {
                expect(terms).not.toBeNull();
                expect(terms.loadedOK).toBeTruthy();
            });
            $httpBackend.flush();
            $rootScope.$apply();
        });
        it('should fetch terms from portal if stateparams.termsData are set', function() {
            $state.params.termsData = {};
            TermsService.loadTerms().then(function(terms) {
                expect(terms).not.toBeNull();
                expect(terms.loadedOK).toBeTruthy();
            });
            $rootScope.$apply();
        });
        it('should report error if neither stateparams.termsData or stateparams.terms are set', function() {
            TermsService.loadTerms().then(function(terms) {
                expect(terms).not.toBeNull();
                expect(terms.loadedOK).toBeFalsy();
            });
            $rootScope.$apply();
        });
        it('should report error if server returns error', function() {
            $httpBackend.expectGET('/api/terms/webcert').respond(500, null);
            $state.params.terms = 'webcert';
            TermsService.loadTerms().then(function(terms) {
                expect(terms).not.toBeNull();
                expect(terms.loadedOK).toBeFalsy();
            });
            $httpBackend.flush();
            $rootScope.$apply();
        });
    });
   describe('printTerms', function() {

        var oldUserAgent;

        function setUserAgent(window, userAgent) {
           var oldUserAgent = window.navigator.userAgent;
           if (window.navigator.userAgent !== userAgent) {
               var userAgentProp = { get: function () { return userAgent; } };
               try {
                   Object.defineProperty(window.navigator, 'userAgent', userAgentProp);
               } catch (e) {
                   window.navigator = Object.create(navigator, {
                       userAgent: userAgentProp
                   });
               }
           }
           return oldUserAgent;
        }

        it('should call the wcModalService to open a dialog (not chrome)', function() {
            spyOn($window, 'open').and.returnValue({
                window: { focus: function() {}},
                document: { write: function() {}, open: function() {}, close: function() {}},
                close: function() {},
                open: function() {}
            });
            var content = {
                terms: { termsModel: TermsModel.init() },
                absUrl: 'url',
                titleId: 'label.modal.content.title.portalvillkor',
                logoImage: null
            };

            TermsService.printTerms(content);
            expect($window.open).toHaveBeenCalled();
        });
       it('should call the wcModalService to open a dialog (chrome)', function() {
           oldUserAgent = setUserAgent(window,'chrome');
           spyOn($window, 'open').and.returnValue({
               window: { focus: function() {}},
               document: { write: function() {}, open: function() {}, close: function() {}},
               close: function() {},
               open: function() {}
           });
           var content = {
               terms: { termsModel: TermsModel.init() },
               absUrl: 'url',
               titleId: 'label.modal.content.title.portalvillkor',
               logoImage: 'testimage.png'
           };

           TermsService.printTerms(content);
           $timeout.flush();
           expect($window.open).toHaveBeenCalled();
           setUserAgent(window,oldUserAgent);
       });
    });

});
