describe('Controller: SelectionCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope, state, appNavViewstate;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, _$state_, _AppNavViewstate_) {
        scope = $rootScope.$new();
        state = _$state_;
        appNavViewstate = _AppNavViewstate_;

        $controller('SelectionCtrl', {
            $scope: scope
        });
    }));


    it('should setVisningsLage and then change state to sjukfall', function() {

        spyOn(appNavViewstate, 'setVisningsLage');
        spyOn(state, 'go');

        scope.onSelectUrval('alla');

        expect(appNavViewstate.setVisningsLage).toHaveBeenCalledWith('alla');
        expect(state.go).toHaveBeenCalledWith('app.sjukfall');

    });


});
