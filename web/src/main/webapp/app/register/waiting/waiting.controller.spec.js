describe('Controller: WaitingCtrl', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('privatlakareApp'));

    var scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope) {
        scope = $rootScope.$new();
        $controller('WaitingCtrl', {
            $scope: scope
        });
    }));

    it('should ...', function() {
        expect(1).toEqual(1);
    });
});
