angular.module('rehabstodApp').controller('SelectCareUnitCtrl',
    function($scope, $uibModalInstance, UserModel) {
        'use strict';
        /**
         * Private functions
         */


        /**
         * Exposed scope properties
         */
        $scope.vardgivare = UserModel.get().vardgivare;
        $scope.currentId = UserModel.get().valdVardenhet.id;


        /**
         * Exposed scope interaction functions
         */

        $scope.toggle = function(enhet) {
            enhet.showMottagning = !enhet.showMottagning;
        };

        $scope.cancel = function() {
            $uibModalInstance.dismiss();
        };

        $scope.onSelectVardenhet = function(enhet) {
            //This is a qiuck&dirty hack-version of selecting new vardenehet. we should use a UserService that
            // calls the backend via proxy and then re-sets a user model with updated data and valdVardenhet etc
            // as we do in webcert. This is just local to the gui
            UserModel.get().valdVardenhet = enhet;
            $uibModalInstance.close(enhet);

        };
    }
);
