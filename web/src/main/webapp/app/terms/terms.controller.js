angular.module('privatlakareApp')
    .controller('PortalTermsCtrl', function($scope, $http, $state, $templateCache, $window, $log, $location, $q, $timeout,
        $modalInstance, TermsModel, TermsProxy, TermsService, ModalViewService) {
        'use strict';

        TermsService.loadTerms().then(function(terms) {
            $scope.content = {
                terms: terms,
                absUrl: $location.absUrl(),
                titleId: 'label.modal.content.title.portalvillkor',
                logoImage: null
            };
            ModalViewService.decorateModalScope($scope, $modalInstance);
        });
    });
