describe('Controller: CompleteCtrl', function () {
  'use strict';

  // load the controller's module
  beforeEach(module('privatlakareApp', function($provide) {
      $provide.value('APP_CONFIG', {});
  }));

  var scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    $controller('CompleteCtrl', {
      $scope: scope
    });
  }));

  it('should ...', function () {
    expect(1).toEqual(1);
  });
});
