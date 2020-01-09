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

angular.module('rehabstodApp').directive('setClassWhenAtTop', function($window) {
  'use strict';
  var $win = angular.element($window); // wrap window object as jQuery object

  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      var topClass = attrs.setClassWhenAtTop, // get CSS class from directive's attribute value
          parent = element.parent(),
          useHide = attrs.useHide === 'true';

      var paddingTop = 0;
      if (attrs.paddingTop) {
        paddingTop = parseInt(attrs.paddingTop, 10);
      }

      var onScroll = function() {
        var elementHeight = element.outerHeight();
        var offsetTop = parent.offset().top - paddingTop;

        if ($win.scrollTop() >= offsetTop) {
          if (!useHide) {
            parent.height(elementHeight);
          }

          element.css('top', paddingTop);
          element.addClass(topClass);
        } else {
          element.removeClass(topClass);
          if (!useHide) {
            parent.css('height', 'auto');
          }
        }
      };

      $win.on('scroll', onScroll);

      scope.$on('$destroy', function() {
        $win.unbind('scroll', onScroll);
      });
    }
  };
});