describe('Controller: RegisterAbortCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
    }));

    var scope, UserModel, RegisterModel;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, _UserModel_, _RegisterModel_) {
        scope = $rootScope.$new();
        $controller('RegisterAbortCtrl', {
            $scope: scope
        });

        UserModel = _UserModel_;
        RegisterModel = _RegisterModel_;
        RegisterModel.reset();
    }));

    it('should clear user data and logout on confirm abort', function() {
        spyOn(UserModel, 'logout').and.callFake(function() {});

        var model = RegisterModel.init();
        model.verksamhetensNamn = 'Kliniken';

        scope.abort();

        // RegisterModel and sessionstorage
        expect(RegisterModel.get().verksamhetensNamn).toEqual(null);
        expect(UserModel.logout).toHaveBeenCalled();
    });

    it('should not clear user data on close', function() {

        scope.$dismiss = jasmine.createSpy('$dismiss');

        var model = RegisterModel.init();
        model.verksamhetensNamn = 'Kliniken';

        scope.dismiss();

        // RegisterModel and sessionstorage
        expect(RegisterModel.get().verksamhetensNamn).toEqual('Kliniken');
        expect(scope.$dismiss).toHaveBeenCalled();
    });

});
