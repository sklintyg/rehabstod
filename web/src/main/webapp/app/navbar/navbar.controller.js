angular.module('rehabstodApp')
    .controller('NavbarCtrl', function($scope) {
        'use strict';
        $scope.menu = [
            {
                title: 'Pågående sjukfall',
                link: 'app.sjukfall'
            },
            {
                title: 'Om Rehabstöd',
                link: 'app.about'
            }
        ];

        $scope.isCollapsed = true;
    });