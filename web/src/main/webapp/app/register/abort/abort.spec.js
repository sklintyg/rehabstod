describe('Router: Abort', function() {
    'use strict';

    var rootScope;
    var state;
    var dialogService;

    // load the controller's module
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
    }));

    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(function(_$rootScope_, _$state_, _dialogService_) {
        rootScope = _$rootScope_;
        state = _$state_;
        dialogService = _dialogService_;
        spyOn(dialogService, 'open').and.callThrough();
        spyOn(dialogService, 'close').and.callThrough();
    }));

    it('should open dialog when entering state "app.register.step1.abort" and close it when leaving the state', function() {
        state.go('app.register.step1');
        rootScope.$apply();

        state.go('app.register.step1.abort');
        rootScope.$apply();
        expect(dialogService.open).toHaveBeenCalled();

        state.go('app.register.step1');
        rootScope.$apply();
        expect(dialogService.close).toHaveBeenCalled();
    });

});