describe('Service: LinkBuilder', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {webcertUrl: 'starturl'});
    }));

    var LinkBuilder, $rootScope, $httpBackend;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$rootScope_, _$httpBackend_, _LinkBuilder_) {
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        LinkBuilder = _LinkBuilder_;
    }));

    describe('getExitLink', function() {
        it('should link to minsida page if user is on complete page', function() {
            var exitLink = LinkBuilder.getExitLink('app.register.step3', 'app.register.complete', 'AUTHORIZED');
            expect(exitLink.name).toBe('Ändra uppgifter');
            expect(exitLink.link).toBe('/#/minsida');
        });
        it('should link to minsida page if user is on waiting page', function() {
            var exitLink = LinkBuilder.getExitLink('app.register.step3', 'app.register.waiting', 'WAITING_FOR_HOSP');
            expect(exitLink.name).toBe('Ändra uppgifter');
            expect(exitLink.link).toBe('/#/minsida');
        });
        it('should link to complete page if user is ready to use webcert', function() {
            var exitLink = LinkBuilder.getExitLink('app.register.complete', 'app.minsida', 'AUTHORIZED');
            expect(exitLink.name).toBe('Tillbaka');
            expect(exitLink.link).toBe('/#/registrera/klar');
        });
        it('should link to waiting page if user is not ready to use webcert', function() {
            var exitLink = LinkBuilder.getExitLink('app.register.waiting', 'app.minsida', 'WAITING_FOR_HOSP');
            expect(exitLink.name).toBe('Tillbaka');
            expect(exitLink.link).toBe('/#/registrera/vanta');

            exitLink = LinkBuilder.getExitLink('app.register.waiting', 'app.minsida', 'NOT_AUTHORIZED');
            expect(exitLink.name).toBe('Tillbaka');
            expect(exitLink.link).toBe('/#/registrera/vanta');
        });
        it('should link to webcert if user came from webcert', function() {
            var exitLink = LinkBuilder.getExitLink('', 'app.minsida', 'NOT_AUTHORIZED');
            expect(exitLink.name).toBe('Tillbaka');
            expect(exitLink.link).toBe('starturl');
        });
    });
});
