angular.module('rehabstodApp')
    .controller('StartPageCtrl', function($scope, $state, $log, UserModel) {
            'use strict';
            $log.debug('StartPageCtrl init');

            if (UserModel.isLakare()) {
                $state.go('app.start.lakare', {}, {location: 'replace'});
            } else {
                $state.go('app.start.rehabkoordinator', {}, {location: 'replace'});
            }
        }
    );
