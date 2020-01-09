/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

angular
.module('rehabstodApp')
.run(function($log, $rootScope, $state, $window, messageService, dynamicLinkService, UserProxy, UserModel, USER_DATA, LINKS,
    $uibModalStack, monitoringLogService) {
  'use strict';

  // Always scroll to top
  $rootScope.$on('$stateChangeSuccess', function() {
    $('html, body').animate({
      scrollTop: 0
    }, 200);
  });

  $rootScope.lang = 'sv';
  $rootScope.DEFAULT_LANG = 'sv';

  // Populate user with resolved user state.
  // NOTE: new properties from backend need to manually be added in UserModel.set to be accessible to the app
  UserModel.init();
  if (angular.isDefined(USER_DATA)) {
    UserModel.set(USER_DATA);
  }

  /* jshint -W117 */
  messageService.addResources(rhsMessages);// jshint ignore:line
  messageService.addLinks(LINKS);

  dynamicLinkService.addLinks(LINKS);

  //Log startup screen resolution (server also uses userAgent header to detect and log other browser info)
  monitoringLogService.screenResolution($window.innerWidth, $window.innerHeight);

  //Configure app wide routing rules
  $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState/*, fromParams*/) {

    //Close any open dialogs on state change
    $uibModalStack.dismissAll();

    $log.debug('$stateChangeStart: from "' + fromState.name + '" to "' + toState.name + '"');

    if (!UserModel.get().loggedIn && toState.name !== 'app.index') {
      // app.index is the only valid route when not authenticated
      _redirect($state, toState.name, event, 'app.index', {}, {
        location: 'replace'
      });
    } else if (UserModel.get().loggedIn && toState.name === 'app.index') {
      // app.index is the only valid route when not authenticated
      _redirect($state, toState.name, event, 'app.sjukfall', {}, {
        location: 'replace'
      });
    } else if (UserModel.get().loggedIn && UserModel.get().valdVardenhet === null && toState.name !== 'app.selectunit') {
      // app.selectunit is the only valid route when no vardenhet selected
      _redirect($state, toState.name, event, 'app.selectunit', {}, {
        location: false
      });
    } else if ((toState.name === 'app.sjukfall.result') && !UserModel.isPdlConsentGiven()) {
      // app.sjukfall.result can only be visited if pdlConsent has been given
      event.preventDefault();

      // The if-statement is for the corner case where someone tries to access /index.html#/sjukfall directly
      // without having a valid fromState (e.g. using bookmark or similar while already being logged in)
      if (fromState['abstract']) {
        $state.go('app.sjukfall.start');
      } else {
        $log.debug('PDL logging consent not given - redirecting to give consent page!');

        var msgConfig = {
          bodyTextKey: 'modal.pdlconsent.' + (UserModel.isLakare() ? 'lakare' : 'rehabkoordinator') + '.body'
        };
        $rootScope.$emit('show.pdl.consent', msgConfig);

        // This is a workaround so the "Pågående sjukfall" tab doesn't stay selected if the user was redirected
        // to the PDL dialog and then chose to Avbryt.
        $state.reload();
      }
    } else if (toState.data && angular.isFunction(toState.data.rule)) {
      var result = toState.data.rule(fromState, toState, UserModel);
      if (result && result.to) {
        _redirect($state, toState.name, event, result.to, result.params, result.options);
      }
    }
  });

  function _redirect($state, originalTo, event, to, params, options) {
    $log.debug('Overriding ' + originalTo + ' --> ' + to);
    event.preventDefault();
    $state.go(to, params, options);
  }

  $rootScope.$on('$stateChangeError', function(event, toState/*, toParams, fromState, fromParams, error*/) {
    $log.log('$stateChangeError');
    $log.log(toState);
  });
});
