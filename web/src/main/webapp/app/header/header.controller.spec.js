describe('Controller: HeaderController', function () {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('rehabstodApp', function($provide) {
        $provide.value('APP_CONFIG', { });
    }));

    var scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();

        $controller('HeaderController', {
            $scope: scope
        });
    }));


    it('show description if user is lakare', function() {
        var role = {
            name: 'LAKARE',
            desc: 'Läkare'
        };

        expect(scope.showRoleDescription(role)).toBeTruthy();
    });

    it('hide description if user is REHABKOORDINATOR', function() {
        var role = {
            name: 'REHABKOORDINATOR',
            desc: 'Rehabkoordinator'
        };

        expect(scope.showRoleDescription(role)).toBeFalsy();
    });
});


