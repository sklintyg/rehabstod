angular.module('rehabstodApp')
    .controller('NavbarCtrl', function($scope) {
        'use strict';
        $scope.menu = [
            {
                title: 'Pågående sjukfall',
                link: 'app.sjukfall',
                id: 'navbar-link-sjukfall'
            },
            {
                title: 'Om Rehabstöd',
                link: 'app.about',
                id: 'navbar-link-about'
            }
        ];

        $scope.isCollapsed = true;
    });