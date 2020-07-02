angular.module('rehabstodApp')
.controller('Controller', ['$scope', function($scope) {
  'use strict';
  $scope.toggleDisplayAgTable = 'false';

    }])
  .directive('rhsPatientAgTableCheckbox', function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {},

        templateUrl: '/components/commonDirectives/rhsPatientAgTable/rhsPatientAgTableCheckbox/rhsPatientAgTableCheckbox.directive.html'
      };
    });