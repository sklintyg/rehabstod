describe('Directive: rhsTable', function () {
    'use strict';

    describe('Controller: RhsTableCtrl', function () {

        // load the controller's module
        beforeEach(angular.mock.module('rehabstodApp', function($provide) {
            $provide.value('APP_CONFIG', { });
        }));

        var scope;
        var SjukfallModel;
        var SjukfallFilterViewState;

        // Initialize the controller and a mock scope
        beforeEach(inject(function ($controller, $rootScope, _SjukfallModel_, _SjukfallFilterViewState_) {
            scope = $rootScope.$new();

            SjukfallModel = _SjukfallModel_;
            SjukfallFilterViewState = _SjukfallFilterViewState_;

            $controller('RhsTableCtrl', {
                $scope: scope,
                SjukfallModel: _SjukfallModel_,
                SjukfallFilterViewState: SjukfallFilterViewState
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
});


