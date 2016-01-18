angular.module('privatlakareApp')
    .controller('ErrorCtrl', function ($scope, $sessionStorage, $stateParams,
        APP_CONFIG) {
        'use strict';

        $scope.webcertUrl = APP_CONFIG.webcertUrl;

        if ($stateParams.errorMessage) {
            $sessionStorage.errorMessage = $scope.errorMessage = $stateParams.errorMessage;
        }
        else {
            $scope.errorMessage = $sessionStorage.errorMessage;
        }
    });
