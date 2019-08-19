describe('Controller: SjukfallStartSelectionCtrl', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  var scope, state, userModel, userProxy, sjukfallService;
  var user = {};

  // Initialize the controller and a mock scope
  beforeEach(inject(function($controller, $rootScope, _$state_, _UserModel_, _UserProxy_) {
    scope = $rootScope.$new();
    state = _$state_;
    userModel = _UserModel_;
    userProxy = _UserProxy_;
    sjukfallService = {
      loadSjukfall: function() {
      }
    };

    $controller('SjukfallStartSelectionCtrl', {
      $scope: scope,
      UserModel: userModel,
      UserProxy: userProxy,
      SjukfallService: sjukfallService
    });
  }));

  it('should setUrval on user and then change state to sjukfall', function() {
    user = {urval: 'ALL'};
    spyOn(state, 'go');
    spyOn(userModel, 'set');

    scope.onSelectUrval(user.urval);

    expect(state.go).toHaveBeenCalledWith('app.sjukfall.result');

  });

});
