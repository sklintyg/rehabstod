angular.module('rehabstodApp')
    .controller('IndexCtrl',
        function ($scope, $cookies, APP_CONFIG) {
            'use strict';

            var SELECTED_SAMBI_IDP_KEY = 'selectedSambiIdp';
            var DEFAULT_LOGIN_URL = '/saml/login/alias/' + APP_CONFIG.defaultAlias;

            $scope.resetCookie = function () {
                $cookies.remove(SELECTED_SAMBI_IDP_KEY);
                $scope.refresh();
            };

            $scope.refresh = function () {
                var storedIdpEntityId = $cookies.get(SELECTED_SAMBI_IDP_KEY);

                $scope.defaultUrl = DEFAULT_LOGIN_URL + '?idp=' + APP_CONFIG.defaultIDP;
                $scope.hasStoredLogin = typeof storedIdpEntityId !== 'undefined';

                if ($scope.hasStoredLogin) {
                    $scope.sambiUrl = DEFAULT_LOGIN_URL + '?idp=' + storedIdpEntityId;
                    $scope.sambiLabel = resolveIdpName(storedIdpEntityId);
                } else {
                    $scope.sambiUrl = '/saml/login/discovery';
                    $scope.sambiLabel = 'Logga in via Sambi';
                }
            };

            $scope.refresh();

            function resolveIdpName(entityId) {
                if (typeof APP_CONFIG.idpMap[entityId] !== 'undefined') {
                    return APP_CONFIG.idpMap[entityId];
                }
                return entityId;
            }
        });