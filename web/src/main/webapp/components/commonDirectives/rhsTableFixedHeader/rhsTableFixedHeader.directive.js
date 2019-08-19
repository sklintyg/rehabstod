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

angular.module('rehabstodApp').directive('rhsTableFixedHeader', function($window) {
  'use strict';
  var $win = angular.element($window); // wrap window object as jQuery object

  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      var topClass = attrs.rhsTableFixedHeader,
          fixedHeader = element.find('#' + attrs.fixedHeader),
          normalHeader = element.find('#' + attrs.normalHeader);

      var paddingTop = 0;
      if (attrs.paddingTop) {
        paddingTop = parseInt(attrs.paddingTop, 10);
      }

      var isFixed = false;

      var setColumnWidths = function() {
        fixedHeader.height(normalHeader.outerHeight());

        var fixedColumns = fixedHeader.find('th');
        normalHeader.find('th').each(function(index, column) {
          $(fixedColumns[index]).width($(column).width());
        });
      };

      var onScroll = function() {
        fixedHeader = element.find('#' + attrs.fixedHeader);
        normalHeader = element.find('#' + attrs.normalHeader);
        var offsetTop = element.offset().top - paddingTop;

        if ($win.scrollTop() >= offsetTop) {
          if (!isFixed) {
            fixedHeader.addClass(topClass);
            fixedHeader.removeClass('hidden');
            fixedHeader.css('top', paddingTop);
            setColumnWidths();
            isFixed = true;
          }
        } else {
          if (isFixed) {
            fixedHeader.removeClass(topClass);
            fixedHeader.addClass('hidden');
          }
          isFixed = false;
        }
      };

      $win.on('resize', setColumnWidths);
      $win.on('scroll', onScroll);

      scope.$on('$destroy', function() {
        $win.unbind('scroll', onScroll);
        $win.unbind('resize', setColumnWidths);
      });
    }
  };
});