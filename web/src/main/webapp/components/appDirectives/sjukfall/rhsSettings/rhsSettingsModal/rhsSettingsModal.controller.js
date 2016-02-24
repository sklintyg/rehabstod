angular.module('rehabstodApp').controller('RhsSettingsModalCtrl',
    function($scope, $uibModalInstance, SjukfallFilterViewState) {
        'use strict';
        /**
         * Private functions
         */


        /**
         * Exposed scope properties
         */

        $scope.glapp = SjukfallFilterViewState.get().glapp;
        $scope.ticks = [0, 7, 14, 30, 60, 90];
        $scope.ticksLabels = ['0 dagar', '7 dagar', '14 dagar', '30 dagar', '60 dagar', '90 dagar'];
        $scope.ticksPositions = [0, 20, 40,60, 80, 100];

        /**
         * Exposed scope interaction functions
         */

        $scope.cancel = function() {
            $uibModalInstance.dismiss();
        };

        $scope.save = function() {
            $uibModalInstance.close($scope.glapp);

        };
    }
);
