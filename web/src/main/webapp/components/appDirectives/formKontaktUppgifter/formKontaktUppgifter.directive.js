/**
 * Form directive to enter kontaktuppgifter
 */
angular.module('privatlakareApp').directive('formKontaktUppgifter',

        function($log, $timeout, $sessionStorage, $state,
            ObjectHelper, FormKontaktUppgifterViewState) {
            'use strict';

            return {
                restrict: 'A',
                transclude: true,
                scope: {
                    'registerModel': '='
                },
                controller: function($scope) {

                    if(!ObjectHelper.isDefined($scope.registerModel) || !ObjectHelper.isDefined($scope.viewState)) {
                        $log.debug('formKontaktUppgifter requires parameters register-model and view-state');
                    }

                    if($state.current.name !== 'app.minsida') {
                        $scope.focusTelefonnummer = true;
                    }
                    $scope.viewState = FormKontaktUppgifterViewState;
                    $scope.preventPaste = function(e, fieldName){
                        e.preventDefault();
                        FormKontaktUppgifterViewState.errorMessage['paste'+fieldName] = true;
                        return false;
                    };
                },
                templateUrl: 'components/appDirectives/formKontaktUppgifter/formKontaktUppgifter.directive.html'
            };
        });