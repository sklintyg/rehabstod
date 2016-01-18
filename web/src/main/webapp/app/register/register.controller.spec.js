describe('Controller: RegisterCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
    }));

    var scope, $rootScope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, _$rootScope_) {
        $rootScope = _$rootScope_;
        scope = $rootScope.$new();
        $controller('RegisterCtrl', {
            $scope: scope
        });
    }));

    it('should not step ahead if not allowed to change state', function() {
        $rootScope.$broadcast('$stateChangeStart', { data: {step: 1 }});
        expect(scope.step).toEqual(1);
        $rootScope.$broadcast('$stateChangeSuccess', { data: {step: 2 }});
        expect(scope.step).toEqual(2);
    });
});
