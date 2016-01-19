/**
 * Form directive to show socialstyrelsenuppgifter
 */
angular.module('rehabstodApp').directive('formSocialUppgifter',
    function(HospService, HospViewState, HospModel, ObjectHelper) {
        'use strict';

        return {
            restrict: 'A',
            scope: {
                'autoload': '='
            },
            controller: function($scope) {
                $scope.viewState = HospViewState;
                $scope.model = HospModel.init();
                if(!ObjectHelper.isDefined($scope.autoload)) {
                    $scope.autoload = false;
                }
                if($scope.autoload) {
                    HospService.loadHosp(HospViewState, HospModel);
                }
            },
            templateUrl: 'components/appDirectives/formSocialUppgifter/formSocialUppgifter.directive.html'
        };
    });
