angular.module('showcase').controller('showcase.NavigationCtrl',
    ['$scope', '$window', 'UserModel', '$localStorage',
        function($scope, $window, UserModel, $localStorage) {
            'use strict';

            $scope.showCookieBanner = false;
            $scope.doShowCookieBanner = function() {
                $localStorage.rhsCookieConsentGiven = false;
                $scope.showCookieBanner = !$scope.showCookieBanner;
            };

            //Header state
            $scope.today = new Date();
            $scope.user = UserModel.get();


            // Navigation bar
            $scope.menu = [
                {
                    title: 'Pågående sjukfall',
                    link: 'app.sjukfall',
                    id: 'navbar-link-sjukfall'
                },
                {
                    title: 'Om Rehabstöd',
                    link: 'showcase.navigation',
                    id: 'navbar-link-about'
                }
            ];
            $scope.isCollapsed = true;
        }]);
