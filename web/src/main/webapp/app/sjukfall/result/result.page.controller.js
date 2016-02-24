angular.module('rehabstodApp')
    .controller('SjukfallResultPageCtrl',
        function($scope, $state, $rootScope, SjukfallService, UserModel, UserProxy) {
            'use strict';

            $scope.user = UserModel.get();
            $scope.showSpinner = true;
            $scope.sjukfallService = SjukfallService;

            SjukfallService.loadSjukfall();

            var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
                SjukfallService.loadSjukfall(true);
            });
            //rootscope on event listeners aren't unregistered automatically when 'this' directives
            //scope is destroyed, so let's take care of that.
            $scope.$on('$destroy', unregisterFn);

            $scope.goBack = function() {
                UserProxy.changeUrval(null).then(function(updatedUserModel) {
                    UserModel.set(updatedUserModel);

                    $state.go('app.sjukfall.start');

                }, function() {
                    //Handle errors
                });
            };

            $scope.$watch('sjukfallService.isLoading()', function(val)  {
               $scope.showSpinner = val;
            });
        });