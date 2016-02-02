describe('Controller: SelectionCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope, state, userModel, userProxy;
    var succeed = true;
    var user = {};
    var error = {};


    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _UserProxy_) {
        scope = $rootScope.$new();
        state = _$state_;
        userModel = _UserModel_;
        userProxy = _UserProxy_;
        $controller('SelectionCtrl', {
            $scope: scope,
            UserModel: userModel,
            UserProxy: userProxy

        });
    }));


    it('should setUrval on user and then change state to sjukfall', function() {
        user = {urval: 'ALL'};
        spyOn(state, 'go');
        spyOn(userModel, 'set');

        spyOn(userProxy, 'changeUrval').and.callFake(function() {
            return {
                then: function(onSuccess, onError) {
                    if (succeed) {
                        onSuccess(user);
                    } else {
                        onError(error);
                    }
                }
            };
        });

        scope.onSelectUrval(user.urval);

        expect(userProxy.changeUrval).toHaveBeenCalledWith('ALL');
        expect(userModel.set).toHaveBeenCalledWith(user);
        expect(state.go).toHaveBeenCalledWith('app.sjukfall');

    });


});
