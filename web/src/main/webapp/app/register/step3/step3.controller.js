angular.module('privatlakareApp')
    .controller('Step3Ctrl',
    function($scope, $log, $state, $window, UserModel, RegisterModel, RegisterProxy, Step3ViewState,
        TermsProxy, WindowUnload) {
        'use strict';

        var user = UserModel.get();
        if (UserModel.isRegistered()) {
            $state.go('app.register.complete');
            return;
        }

        if (!RegisterModel.validForStep3()) {
            $state.go('app.register.step2');
            return;
        }

        var model = RegisterModel.init();
        var privatLakareDetails = Step3ViewState.getRegisterDetailsTableDataFromModel(user, model);
        $scope.uppgifter = privatLakareDetails.uppgifter;
        $scope.kontaktUppgifter = privatLakareDetails.kontaktUppgifter;
        $scope.viewState = Step3ViewState;
        $scope.registerModel = model;

        // Skapa konto button
        $scope.createAccount = function() {
            Step3ViewState.loading.register = true;

            var godkantMedgivandeVersion = null;
            if (Step3ViewState.godkannvillkor) {
                godkantMedgivandeVersion = $scope.terms.version;
            }

            RegisterProxy.registerPrivatlakare(model, godkantMedgivandeVersion).then(function(successData) {
                $log.debug('Registration complete - data:');
                $log.debug(successData);
                Step3ViewState.loading.register = false;
                Step3ViewState.errorMessage.register = null;
                user.status = successData.status;
                RegisterModel.reset();

                switch (user.status) {
                case 'AUTHORIZED':
                    $state.go('app.register.complete');
                    break;
                case 'NOT_AUTHORIZED':
                case 'WAITING_FOR_HOSP':
                    $state.go('app.register.waiting');
                    break;
                default: // NOT_STARTED, UNKNOWN or other unwanted values like null or undefined
                    Step3ViewState.errorMessage.register =
                        'Kunde inte registrera privatläkare på grund av tekniskt fel. Försök igen senare.';
                    $log.debug('Invalid user status in response:' + user.status);
                }

            }, function(errorData) {
                Step3ViewState.loading.register = false;
                Step3ViewState.errorMessage.register =
                    'Kunde inte registrera privatläkare på grund av tekniskt fel. Försök igen senare.';
                $log.debug('Failed to register errorCode:' + errorData.errorCode + ' reason:' + errorData.message);
            });
        };

        // Retrieve terms
        TermsProxy.getTerms().then(function(successData) {
            $scope.terms = {
                text : successData.text,
                date : successData.date,
                version : successData.version
            };
        }, function(errorData) {
            $log.debug('Failed to get terms.');
            $log.debug(errorData);
        });

        // Add browser dialog to ask if user wants to save before leaving if he closes the window on an edited form.
        WindowUnload.bindUnload($scope);
    });
