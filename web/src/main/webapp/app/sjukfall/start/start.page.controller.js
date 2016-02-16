angular.module('rehabstodApp')
    .controller('SjukfallStartPageCtrl', function($scope, UserModel) {
        'use strict';

        $scope.user = UserModel.get();

    });