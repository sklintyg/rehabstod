/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//Register module
angular.module('rhsIndexApp', [
    'ngAnimate',
    'ui.bootstrap',
    'ngStorage',
    'rehabstodcommon.dynamiclink'
]);


angular.module('rhsIndexApp')
    .controller('IndexController', ['$scope', '$sce','$uibModal', function($scope, $sce, $uibModal) {
        'use strict';

        $scope.open = function (which) {

            $scope.modalInstance = $uibModal.open({
                templateUrl: which,
                scope: $scope,
                size: 'lg'
            });

            $scope.modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {
                // closed the modal
            });
        };

        $scope.ok = function () {
            $scope.modalInstance.close();
        };

    }]);


angular.module('rhsIndexApp').directive('rhsCookieBanner',
    ['$localStorage', '$http', 'dynamicLinkService',
        function($localStorage, $http, dynamicLinkService) {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                templateUrl: '/pubapp/rhsCookieBanner.directive.html',
                controller: function($scope, $timeout) {

                    $http.get('/api/config/links').then(function(links) {
                        dynamicLinkService.addLinks(links.data);
                    });

                    $scope.isOpen = false;
                    $scope.showDetails = false;
                    $scope.localStorage = $localStorage;

                    if (!$scope.localStorage.rhsCookieConsentGiven) {
                        $timeout(function() {
                            $scope.isOpen = true;
                        }, 500);
                    }

                    $scope.onCookieConsentClick = function() {
                        $scope.isOpen = false;
                        $scope.localStorage.rhsCookieConsentGiven = true;

                    };
                }
            };
        }]);

angular.module('rhsIndexApp').animation('.banner-slide-in', function() {
    'use strict';
    return {
        enter: function(element, done) {
            element.css({
                    opacity: 0,
                    top: '-80px'
                })
                .animate({
                    opacity: 1,
                    top: '0px'
                }, 500, done);
        },
        leave: function(element, done) {
            element.css({
                    opacity: 1,
                    top: '0px'
                })
                .animate({
                    opacity: 0,
                    top: '-80px'
                }, 500, done);
        }
    };
});