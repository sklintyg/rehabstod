describe('Viewstate: AppNavViewstate', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var AppNavViewstate;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_AppNavViewstate_) {
        AppNavViewstate = _AppNavViewstate_;
    }));

    describe('reset', function() {
        it('should reset correctly', function() {
            AppNavViewstate.reset();
            expect(AppNavViewstate.get().visningsLage).toBeUndefined();
        });
    });

    describe('setVisningsLage', function() {
        it('should setVisningsLage correctly', function() {
            AppNavViewstate.setVisningsLage('TEST');
            expect(AppNavViewstate.isVisningsLageValt()).toBeTruthy();
            expect(AppNavViewstate.get().visningsLage).toEqual('TEST');
        });
    });

});
