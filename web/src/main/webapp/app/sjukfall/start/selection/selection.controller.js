angular.module('rehabstodApp')
    .controller('SjukfallStartSelectionCtrl', function($scope, $state, $rootScope, UserModel, UserProxy, SjukfallService) {
        'use strict';

        $scope.user = UserModel.get();

        var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
            $state.go('app.sjukfall.start');
        });
        //rootscope on event listeners aren't unregistered automatically when 'this' directives
        //scope is destroyed, so let's take care of that.
        $scope.$on('$destroy', unregisterFn);

        $scope.onSelectUrval = function(urval) {
            UserProxy.changeUrval(urval).then(function(updatedUserModel) {
                UserModel.set(updatedUserModel);

                $state.go('app.sjukfall.result');

                SjukfallService.loadSjukfall(true);

            }, function() {
                //Handle errors
            });

        };
    });
