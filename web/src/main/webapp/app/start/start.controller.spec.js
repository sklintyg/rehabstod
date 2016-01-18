describe('Controller: MainCtrl', function () {
  'use strict';

  // load the controller's module
  beforeEach(module('privatlakareApp'));

  var scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function (_$httpBackend_, $controller, $rootScope) {
    scope = $rootScope.$new();
    $controller('MainCtrl', {
      $scope: scope
    });
  }));
});
