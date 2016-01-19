angular.module('rehabstodApp').directive('wcModal',
    function(wcModalService) {
        'use strict';

        return {
            restrict: 'A',
            replace: true,
            scope: {
                options:'='
            },
            controller: function($scope){
                wcModalService.open($scope.options, $scope);
            }
        };
    });
