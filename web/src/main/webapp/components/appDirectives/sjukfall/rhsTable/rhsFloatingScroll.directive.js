/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').directive('rhsFloatingScroll', function() {
  'use strict';
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {

      function _requestUpdate() {
        $(el).floatingScroll('update');
      }

      $(el).floatingScroll('init');
      scope.element = el[0];

      // We need to update the floating scrollbar when height changes.
      scope.$watch('element.clientHeight', function(newVal, oldVal) {
        if (newVal !== oldVal) {
          _requestUpdate();
        }
      });

      // Keep reference to element that scrolls page
      // We also need to update the floating scrollbar when that happens.
      var scrollSourceElement = $('#' + attrs.scrollParentElementId);
      scrollSourceElement.on('scroll', _requestUpdate);

      //Clean up jquery component and event listeners
      scope.$on('$destroy', function() {
        $(el).floatingScroll('destroy');
        scrollSourceElement.off('scroll');
      });
    }
  };
});
