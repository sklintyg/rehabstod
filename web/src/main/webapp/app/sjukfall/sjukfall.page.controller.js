angular.module('rehabstodApp')
    .controller('SjukfallPageCtrl', function($scope, $log, UserModel) {
        'use strict';
        $log.debug('SjukfallPageCtrl init');
        $scope.user = UserModel.get();

    });
