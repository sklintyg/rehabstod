angular.module('rehabstodApp')
    .controller('IndexCtrl',
        function ($scope, $cookies, APP_CONFIG) {
            'use strict';

            $scope.resetCookie = function() {
                $cookies.remove('selectedSambiIdp');
                $scope.refresh();
            };

            $scope.refresh = function() {
                var storedIdpEntityId = $cookies.get('selectedSambiIdp');
                $scope.defaultUrl = '/saml/login/alias/defaultAlias?idp=' + APP_CONFIG.defaultIDP;
                $scope.hasStoredLogin = typeof storedIdpEntityId !== 'undefined';

                if ($scope.hasStoredLogin) {
                    $scope.sambiUrl = '/saml/login/alias/defaultAlias?idp=' + storedIdpEntityId;
                    $scope.sambiLabel = APP_CONFIG.idpMap[storedIdpEntityId];
                } else {
                    $scope.sambiUrl = '/saml/login/discovery';
                    $scope.sambiLabel = 'Logga in via Sambi';
                }
            };

            $scope.refresh();
        });