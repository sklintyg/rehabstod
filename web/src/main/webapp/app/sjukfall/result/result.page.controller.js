angular.module('rehabstodApp')
    .controller('SjukfallResultPageCtrl',
        function($scope, $rootScope, SjukfallService, UserModel) {
            'use strict';

            $scope.user = UserModel.get();

            SjukfallService.loadSjukfall();

            var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
                SjukfallService.loadSjukfall(true);
            });
            //rootscope on event listeners aren't unregistered automatically when 'this' directives
            //scope is destroyed, so let's take care of that.
            $scope.$on('$destroy', unregisterFn);
        });