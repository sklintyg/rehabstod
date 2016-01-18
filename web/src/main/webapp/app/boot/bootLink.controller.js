angular.module('privatlakareApp')
    .controller('BootLinkCtrl', function($scope, $timeout, $state,
        UserProxy, UserModel) {
        'use strict';
        $scope.loading = true;
        $scope.errorMessage = null;

        UserProxy.getUser().then(function(successData) {
            $scope.loading = false;
            $scope.errorMessage = null;
            UserModel.set(successData);

            if($state.params.targetId === 'new') {
                $state.go('app.register.step1');
            } else {
                $scope.errorMessage = 'Kunde inte länka rätt. Kontrollera parametern:' + $state.params.targetId;
            }
        }, function() {
            $scope.loading = false;
            $scope.errorMessage = 'Kunde inte hämta användare. Har du loggat in?';
        });
    });
