/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('rehabstodApp')
    .controller('AppPageCtrl',
        function($scope, $rootScope, $uibModal, messageService) {
            'use strict';

            $scope.showErrorDialog = function(msgConfig) {
                $uibModal.open({
                    templateUrl: '/app/error/restErrorDialog.html',
                    controller: 'restErrorDialogCtrl',
                    size: 'md',
                    resolve: {
                        msgConfig: function() {
                            return msgConfig;
                        }
                    }
                });
            };

            $scope.showPdlConsentDialog = function() {
                $uibModal.open({
                    templateUrl: '/app/pdlconsent/pdlconsentdialog.html',
                    size: 'md'
                    // resolve: {
                    //     msgConfig: function() {
                    //         return msgConfig;
                    //     }
                    // }
                });
            };

            var unregisterFn = $rootScope.$on('rehab.rest.exception', function(event, msgConfig) {
                 var texts = {
                     title: messageService.getProperty(msgConfig.errorTitleKey),
                     body: messageService.getProperty(msgConfig.errorTextKey)
                 };
                 $scope.showErrorDialog(texts);
             });

            var unregisterFn2 = $rootScope.$on('show.pdl.consent', function() {
                var texts = {
                    title: messageService.getProperty('modal.pdlconsent.title'),
                    body: messageService.getProperty('modal.pdlconsent.lakare.body')
                };
                $scope.showPdlConsentDialog(texts);
            });
             //rootscope on event listeners aren't unregistered automatically when 'this' directives
             //scope is destroyed, so let's take care of that.
             $scope.$on('$destroy', unregisterFn);
             $scope.$on('$destroy', unregisterFn2);

        });