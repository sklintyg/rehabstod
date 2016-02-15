describe('Controller: SearchResultsCtrl', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();

        $controller('SearchResultsCtrl', {
            $scope: scope
        });
    }));


    it('should return toolTip', function() {
        var diagnos = {
            kod: 123,
            beskrivning: 'Hej'
        };

        expect(scope.getToolTip(diagnos)).toEqual('<b>' + diagnos.kod + '</b><br>' + diagnos.beskrivning);
    });
});
