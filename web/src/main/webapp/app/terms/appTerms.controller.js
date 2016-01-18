angular.module('privatlakareApp')
    .controller('WebcertTermsCtrl', function($scope, $http, $state, $templateCache, $window, $log, $location, $q, $timeout,
        $modalInstance, TermsModel, TermsProxy, TermsService, ModalViewService) {
        'use strict';

        TermsService.loadTerms().then(function(terms) {
            $scope.content = {
                terms: terms,
                absUrl: $location.absUrl(),
                titleId: 'label.modal.content.title.webcertvillkor',
                logoImage: 'assets/images/webcert_black.png'
            };
            ModalViewService.decorateModalScope($scope, $modalInstance);
        });
    });
