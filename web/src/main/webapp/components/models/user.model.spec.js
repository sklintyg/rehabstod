describe('Model: UserModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('privatlakareApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

    var UserModel;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_UserModel_) {
        UserModel = _UserModel_;
    }));

    describe('set', function() {
        it('should add dash personnummer without', function() {
            UserModel.set({personalIdentityNumber: '191212121212'});
            expect(UserModel.get().personnummer).toEqual('19121212-1212');
        });
        it('should not add dash personnummer that already has a dash', function() {
            UserModel.set({personalIdentityNumber: '19121212-1212'});
            expect(UserModel.get().personnummer).toEqual('19121212-1212');
        });
    });

});
