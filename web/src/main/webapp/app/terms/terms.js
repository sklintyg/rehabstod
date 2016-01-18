angular.module('privatlakareApp')
    .config(function($stateProvider) {
        'use strict';

        function openExternalAppTerms(AppTermsModalModel, TermsService) {
            var modalModel = AppTermsModalModel.init();
            modalModel.options.controller = 'WebcertTermsCtrl';
            modalModel.options.titleId = 'label.modal.title.webcertvillkor';
            TermsService.openTerms(modalModel);
        }

        function openPortalTerms(AppTermsModalModel, TermsService) {
            var modalModel = AppTermsModalModel.init();
            modalModel.options.controller = 'PortalTermsCtrl';
            modalModel.options.titleId = 'label.modal.title.portalvillkor';
            TermsService.openTerms(modalModel);
        }

        function closeTerms(AppTermsModalModel) {
            var modal = AppTermsModalModel.get().modalInstance;
            if(modal) {
                modal.dismiss('cancel');
            }
        }

        $stateProvider
            .state('app.start.terms', {
                url: '/terms',
                onEnter: openExternalAppTerms,
                onExit: closeTerms,
                params: {
                    terms: null,
                    termsData: null
                },
                data: {
                    rule: function(fromState) {
                        if (fromState.name !== 'app.start') {
                            return {
                                to: 'app.start'
                            };
                        }
                    }
                }
            }).state('app.register.step3.terms', {
                url: '/terms',
                onEnter: openPortalTerms,
                onExit: closeTerms,
                params: {
                    terms: null,
                    termsData: null
                },
                data: {
                    rule: function(fromState) {
                        if (fromState.name !== 'app.register.step3') {
                            return {
                                to: 'app.register.step3'
                            };
                        }
                    }
                }
            });
    });
