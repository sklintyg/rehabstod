describe('Controller: BootCtrl', function() {
    'use strict';

    var succeed = true;
    var user = {};
    var error = {};

    // load the controller's module
    beforeEach(angular.mock.module('privatlakareApp', function($provide) {
        $provide.value('UserProxy', {
            getUser: function() {
                return {
                    then: function(onSuccess, onError) {
                        if(succeed) {
                            onSuccess(user);
                        } else {
                            onError(error);
                        }
                    }
                };
            }
        });
    }));

    var scope, $controller, $state;

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_$controller_, $rootScope, _$state_) {
        $controller = _$controller_;
        scope = $rootScope.$new();
        $state = _$state_;
    }));

    it('should redirect to step 1 if targetId parameter is "new"', function() {
        succeed = true;
        user = {name:'Nisse', status: 'NOT_STARTED'};
        $state.params.targetId = 'new';
        spyOn($state, 'go').and.stub();
        $controller('BootLinkCtrl', { $scope: scope });
        expect($state.go).toHaveBeenCalledWith('app.register.step1');
    });

    it('should show error if invalid targetId is supplied', function() {
        succeed = true;
        user = {name:'Nisse', status: 'NOT_STARTED'};
        $state.params.targetId = '';
        spyOn($state, 'go').and.stub();
        $controller('BootLinkCtrl', { $scope: scope });
        expect(scope.errorMessage).not.toBe(null);
    });

    it('should show error if a user is not received', function() {
        succeed = false;
        error = {};
        $state.params.targetId = '';
        $controller('BootLinkCtrl', { $scope: scope });
        expect(scope.errorMessage).not.toBe(null);
    });

});
