angular.module('rehabstodApp').controller('SelectCareUnitCtrl',
    function($scope, $uibModalInstance, UserModel) {
        'use strict';
        /**
         * Private functions
         */


        /**
         * Exposed scope properties
         */
        $scope.user = UserModel.get();

        /**
         * Exposed scope interaction functions
         */

        $scope.cancel = function() {
            $uibModalInstance.dismiss();
        };

        $scope.onSelectVardenhet = function(enhet) {
             $uibModalInstance.close(enhet);

        };
    }
);
