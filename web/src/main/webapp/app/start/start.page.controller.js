angular.module('rehabstodApp')
    .controller('StartPageCtrl', function($scope, $log, UserModel) {
        'use strict';
        $log.debug('StartPageCtrl init');
        $scope.user = UserModel.get();

    });
