describe('Controller: MinsidaCtrl', function() {
    'use strict';

    var succeed = true;
    var succeedSave = true;
    var privatlakare = {};
    var error = {};

    // load the controller's module
    beforeEach(module('privatlakareApp', function($provide) {
        $provide.value('APP_CONFIG', {});
        $provide.value('RegisterProxy', {
            getPrivatlakare: function() {
                return {
                    then: function(onSuccess, onError) {
                        if (succeed) {
                            onSuccess(privatlakare);
                        } else {
                            onError(error);
                        }
                    }
                };
            },
            savePrivatlakare: function() {
                return {
                    then: function(onSuccess, onError) {
                        if (succeedSave) {
                            onSuccess(privatlakare);
                        } else {
                            onError(error);
                        }
                    }
                };
            }
        });
    }));

    var scope;

    describe('When getPrivatlakare succeeds', function() {

        var MinsidaViewState;

        beforeEach(inject(function($controller, $rootScope, $window, _MinsidaViewState_) {
            succeed = true;
            scope = $rootScope.$new();
            scope.registerModel = { telefonnummer: '0123456789' };
            scope.registerForm = {
                $dirty: true,
                $setPristine: function() {}
            };
            MinsidaViewState = _MinsidaViewState_;
            $controller('MinsidaCtrl', {
                $scope: scope,
                MinsidaViewState: _MinsidaViewState_
            });
            $window.onbeforeunload = function() {};
        }));

        it('should save data when user saves', function() {
            expect(MinsidaViewState.errorMessage.load).toBe(null);
            succeedSave = true;
            scope.save();
            expect(MinsidaViewState.errorMessage.save).toBe(null);
        });

        it('should save data when user saves', function() {
            expect(MinsidaViewState.errorMessage.load).toBe(null);
            succeedSave = false;
            scope.save();
            expect(MinsidaViewState.errorMessage.save).not.toBe(null);
        });

        it('should reset data when user resets', function() {
            MinsidaViewState.reset();
            MinsidaViewState.undoModel.telefonnummer = '0123456789';
            scope.registerModel = { telefonnummer: '0123456789' };
            expect(scope.registerModel.telefonnummer).toBe('0123456789');
            scope.registerModel.telefonnummer = '12345';
            scope.reset();
            expect(scope.registerModel.telefonnummer).toBe('0123456789');
        });
    });

    describe('When getPrivatlakare fails', function() {

        var MinsidaViewState;

        beforeEach(inject(function($controller, $rootScope, $window, _MinsidaViewState_) {
            succeed = false;
            scope = $rootScope.$new();
            scope.registerForm = {
                $dirty: true
            };
            MinsidaViewState = _MinsidaViewState_;
            $controller('MinsidaCtrl', {
                $scope: scope,
                MinsidaViewState: _MinsidaViewState_
            });
            $window.onbeforeunload = function() {};
        }));

        it('Should show error message', function() {
            expect(MinsidaViewState.errorMessage.load).not.toBe(null);
        });

    });
});
