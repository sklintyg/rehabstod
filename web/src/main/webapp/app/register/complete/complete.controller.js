angular.module('privatlakareApp')
    .controller('CompleteCtrl', function($scope, $window,
        APP_CONFIG) {
        'use strict';

        $scope.goToApp = function() {
            $window.location = APP_CONFIG.webcertStartUrl;
        };
    });
