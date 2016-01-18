angular.module('privatlakareApp').factory('LinkBuilder',
    function($window, $log,
        messageService, APP_CONFIG, ObjectHelper) {
        'use strict';

        return {
            getExitLink: function(fromStateName, toStateName, userStatus, fromUrlPath) {
                var exitLink = {
                    name: '',
                    link: ''
                };

                if (toStateName === 'app.minsida') {
                    if (ObjectHelper.isEmpty(fromStateName) || fromStateName === 'app.boot') {
                        // If we had no valid state before we came from an external site, assuming webcert for now.
                        exitLink.name = messageService.getProperty('label.header.backtoapp');
                        // Build exit link with specific path in external application if available.
                        exitLink.link =  ObjectHelper.isDefined(fromUrlPath) ? APP_CONFIG.webcertUrl + fromUrlPath : APP_CONFIG.webcertUrl;
                        $log.debug('ExitLink:');
                        $log.debug(exitLink.link);
                    } else {
                        switch (userStatus) {
                        case 'AUTHORIZED':
                            if (fromStateName === 'app.register.complete') {
                                exitLink.name = messageService.getProperty('label.header.backtocomplete');
                                exitLink.link = '/#/registrera/klar';
                            }
                            break;
                        case 'NOT_AUTHORIZED':
                        case 'WAITING_FOR_HOSP':
                            exitLink.name = messageService.getProperty('label.header.backtocomplete');
                            exitLink.link = '/#/registrera/vanta';
                            break;
                        }
                    }
                } else { // complete, waiting. For start, register and every other state the link is hidden by logic.
                    exitLink.name = messageService.getProperty('label.header.changeaccount');
                    exitLink.link = '/#/minsida';
                }

                return exitLink;
            }
        };
    }
);