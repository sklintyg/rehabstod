/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').directive('rhsOverviewMoreStatistic',
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/rhsOverviewMoreStatistic/rhsOverviewMoreStatistic.directive.html',
        controller: function($scope, UserProxy, UserModel, dynamicLinkService, $window) {
          var link = dynamicLinkService.getLink('statistiktjanstenTooltip');

          $scope.linkText = link.text;
          $scope.linkTooltip = link.tooltip;

          $scope.openStatistik = function() {
            UserProxy.fetchAccessToken().then(function(token) {
              if (token) {
                formLogin(token);
              } else {
                openLink();
              }
            }, function() {
              openLink();
            });
          };

          function openLink() {
            $window.open(link.url);
          }

          function formLogin(accessToken) {
            var enhet = UserModel.get().valdVardenhet.id;
            var form = document.createElement('form');

            form.method = 'post';
            form.action = link.url;
            form.target = '_blank';

            var nodeEnhet = document.createElement('input');
            nodeEnhet.name = 'enhet';
            nodeEnhet.value = enhet;
            nodeEnhet.type = 'hidden';
            form.appendChild(nodeEnhet);

            var nodeAccessToken = document.createElement('input');
            nodeAccessToken.name = 'access_token';
            nodeAccessToken.value = accessToken;
            nodeAccessToken.type = 'hidden';
            form.appendChild(nodeAccessToken);

            // To be sent, the form needs to be attached to the main document.
            form.style.display = 'none';

            $window.jQuery(form).appendTo('body').submit().remove();
          }
        }
      };
    });
