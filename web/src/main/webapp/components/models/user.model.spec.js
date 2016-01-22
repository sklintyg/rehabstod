describe('Model: UserModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var UserModel;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_UserModel_) {
        UserModel = _UserModel_;
    }));

    describe('set', function() {
        it('should add dash personnummer without', function() {
            UserModel.set({namn: 'Kalle'});
            expect(UserModel.get().name).toEqual('Kalle');
        });
    });

});
