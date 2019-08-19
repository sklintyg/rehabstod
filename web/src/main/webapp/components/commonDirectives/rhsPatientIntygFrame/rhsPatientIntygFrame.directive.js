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

angular.module('rehabstodApp').directive('rhsPatientIntygFrame',
    ['$sce', 'APP_CONFIG', 'UserModel',
      function($sce, APP_CONFIG, UserModel) {
        'use strict';

        return {
          restrict: 'E',
          scope: {
            tab: '='
          },
          templateUrl: '/components/commonDirectives/rhsPatientIntygFrame/rhsPatientIntygFrame.directive.html',
          link: function($scope, $element) {

            var url = $sce.trustAsResourceUrl(APP_CONFIG.webcertViewIntygTemplateUrl);
            var enhet = UserModel.get().valdVardenhet.id;
            var frameName = 'h_' + $scope.tab.intygsId.replace(/-/g, '');

            // var iframeDiv = $element[0].querySelector('.wc_iframe');
            var iframeDiv = angular.element($element[0].querySelector('.wc_iframe'));

            var form = document.createElement('form');

            form.method = 'post';
            form.action = url;
            form.target = frameName;

            var nodeCertId = document.createElement('input');
            nodeCertId.name = 'certId';
            nodeCertId.value = $scope.tab.intygsId;
            nodeCertId.type = 'hidden';
            form.appendChild(nodeCertId);

            var nodeEnhet = document.createElement('input');
            nodeEnhet.name = 'enhet';
            nodeEnhet.value = enhet;
            nodeEnhet.type = 'hidden';
            form.appendChild(nodeEnhet);

            var nodeAccessToken = document.createElement('input');
            nodeAccessToken.name = 'access_token';
            nodeAccessToken.value = $scope.tab.accessToken;
            nodeAccessToken.type = 'hidden';
            form.appendChild(nodeAccessToken);

            // To be sent, the form needs to be attached to the main document.
            form.style.display = 'none';
            iframeDiv.append(form);

            // This iframe needs to be added in this awkward way due to limitations in dynamic name attribute for this element.
            // @See https://mudge.name/2012/01/29/naming-dynamically-created-iframes.html
            var iframeTemp = document.createElement('div');
            iframeTemp.innerHTML = '<iframe name="' + frameName + '"></iframe>';
            iframeDiv.append(iframeTemp.firstChild);

            form.submit();
          }
        };
      }]);
