describe('Service: minsidaViewstate', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('privatlakareApp', function(/*$provide*/) {
    }));

    var MinsidaViewState;

    beforeEach(inject(function(_MinsidaViewState_) {
        MinsidaViewState = _MinsidaViewState_;
    }));

    describe('changesRequireLogout', function() {

        it('should check if changes have been made to fields requiring logout', function() {

            MinsidaViewState.infoMessage.save = 'Logout please text';
            expect(MinsidaViewState.changesRequireLogout()).toBe(true);

            MinsidaViewState.infoMessage.save = null;
            expect(MinsidaViewState.changesRequireLogout()).toBe(false);
        });
    });

    describe('checkFieldsRequiringLogout should tell user logout is required if changes have been made to fields requiring logout', function() {

        var registerForm;

        beforeEach(function() {
            registerForm = {
                '$dirty': false,
                'arbetsplatskod': {$dirty: false},
                'verksamhetensnamn' : {$dirty: false},
                'telefonnummer' : {$dirty: false},
                'adress' : {$dirty: false},
                'postnummer' : {$dirty: false}
            };
        });

        it('should update message if form is dirty and a required field has changed', function() {
            registerForm.$dirty = true;
            registerForm.verksamhetensnamn.$dirty = true;
            MinsidaViewState.checkFieldsRequiringLogout(registerForm);
            expect(MinsidaViewState.infoMessage.save).not.toBe(null);
        });

        it('should NOT update message if form is dirty and only a voluntary field has changed', function() {
            registerForm.$dirty = true;
            registerForm.arbetsplatskod.$dirty = true;
            MinsidaViewState.checkFieldsRequiringLogout(registerForm);
            expect(MinsidaViewState.infoMessage.save).toBe(null);
        });
    });
});
