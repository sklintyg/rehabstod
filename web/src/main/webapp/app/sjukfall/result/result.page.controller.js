angular.module('rehabstodApp')
    .controller('SjukfallResultPageCtrl',
        function($scope, SjukfallService, UserModel) {
            'use strict';

            $scope.user = UserModel.get();

            SjukfallService.loadSjukfall();
        });