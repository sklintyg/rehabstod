angular.module('rehabstodApp')
    .controller('RhsSettingsCtrl',
        function($scope, SjukfallFilterViewState, $uibModal, SjukfallService) {
            'use strict';

            $scope.filterViewState = SjukfallFilterViewState;

            $scope.openSettings = function() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'components/appDirectives/sjukfall/rhsSettings/rhsSettingsModal/rhsSettingsModal.html',
                    controller: 'RhsSettingsModalCtrl',
                    size: 'lg',
                    backdrop: 'static'
                });

                modalInstance.result.then(function(value) {
                    SjukfallFilterViewState.get().glapp = value;
                    SjukfallService.loadSjukfall(true, true);
                }, function() {

                });
            };
        }
    )
    .directive('rhsSettings',
        function() {
            'use strict';

            return {
                restrict: 'E',
                replace: true,
                scope: {},
                controller: 'RhsSettingsCtrl',
                templateUrl: 'components/appDirectives/sjukfall/rhsSettings/rhsSettings.directive.html'
            };
        });
