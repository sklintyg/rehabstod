angular.module('rehabstodApp')
    .controller('NavbarCtrl', function($scope, $state, UserModel) {
        'use strict';
        $scope.menu = [
            {
                title: 'Start',
                link: 'app.start'
            },
            {
                title: 'Pågående sjukskrivningar',
                link: 'app.sjukfall',
                disabled: function() {
                    return !UserModel.isUrvalSet();
                }
            },
            {
                title: 'Om Rehabstöd',
                link: 'app.about'
            }
        ];

        $scope.isCollapsed = true;
    });