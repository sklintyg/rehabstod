angular.module('rehabstodApp')
    .controller('StartHeaderCtrl', function($scope, $state, UserModel) {
        'use strict';

        /**
         * Exposed scope properties
         */
        $scope.user = UserModel.get();

    });
