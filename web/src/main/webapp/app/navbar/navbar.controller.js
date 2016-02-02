angular.module('rehabstodApp')
    .controller('NavbarCtrl', function($scope, $state, $location, UserModel) {
        'use strict';
        $scope.menu = [
            {
                'title': 'Start',
                'link': '/#/'
            },
            {
                title: 'Pågående sjukskrivningar',
                link: '/#/sjukfall',
                disabled: function() {
                    return !UserModel.isUrvalSet();
                }
            },
            {
                title: 'Om Rehabstöd',
                link: '/#/about'
            }
        ];

        $scope.isCollapsed = true;

        $scope.isActive = function(page) {
            if (!page) {
                return false;
            }

            page = page.substr(page.lastIndexOf('/') + 1);
            if (($state.current.data && angular.isString($state.current.data.defaultActive)) &&
                (page === $state.current.data.defaultActive)) {
                return true;
            }

            var currentRoute = $location.path().substr($location.path().lastIndexOf('/') + 1);
            return page === currentRoute;
        };
    });