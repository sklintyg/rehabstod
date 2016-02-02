angular.module('rehabstodApp')
    .controller('StatisticsCtrl', function($scope, $state, UserModel) {
        'use strict';

        /**
         * Exposed scope properties
         */
        $scope.user = UserModel.get();

    });
