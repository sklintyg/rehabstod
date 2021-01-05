/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
      var $rhsBody = $('.rhs-body');
      var topClass = attrs.rhsTableFixedHeader,
          fixedHeader = element.find('#' + attrs.fixedHeader),
          normalHeader = element.find('#' + attrs.normalHeader);
      var tableBody = $('#rhs-table-body');
      var tableScrollContainer = $('.floating-scroll-container');
      var isFixed = false;
      var createdWrappingDiv = false;

      var syncFixedHeaderHorizontalScroll = function() {
        var baseOffset = tableScrollContainer.offset().left;
        var currentOffset = tableBody.offset().left;
        var currentWidth = normalHeader.width() - baseOffset + currentOffset;
        $('.outer-div-fixed-header').width(currentWidth);
      };

      var setColumnWidths = function() {
        fixedHeader.height(normalHeader.outerHeight());
        var tableWidth = normalHeader.parent().outerWidth();
        var tableScrollContainerWidth = tableScrollContainer.outerWidth();
        fixedHeader.width(tableWidth < tableScrollContainerWidth ? tableWidth : tableScrollContainerWidth);

        var fixedColumns = fixedHeader.find('th');
        normalHeader.find('th').each(function(index, column) {
          $(fixedColumns[index]).width($(column).width());
        });

        if (createdWrappingDiv) {
          syncFixedHeaderHorizontalScroll();
        }

      };

      var onScroll = function() {
        fixedHeader = element.find('#' + attrs.fixedHeader);
        normalHeader = element.find('#' + attrs.normalHeader);
        var offsetTop = normalHeader.offset().top; // - paddingTop;

        if (offsetTop < 0) {
          if (!isFixed) {
            if (!createdWrappingDiv) {
              fixedHeader.wrapInner('<div class="inner-div-fixed-header"></div>');
              var outerDiv = fixedHeader.wrapInner('<div class="outer-div-fixed-header"></div>');
              outerDiv.width(normalHeader.width());
              createdWrappingDiv = true;
            }
            fixedHeader.addClass(topClass);
            fixedHeader.removeClass('hidden');
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

      $win.on('resize', setColumnWidths); //To catch horizontal scroll
      $rhsBody.on('resize', setColumnWidths);
      $rhsBody.on('scroll', onScroll);
      tableScrollContainer.on('scroll', syncFixedHeaderHorizontalScroll);

      scope.$on('$destroy', function() {
        tableScrollContainer.unbind('scroll', syncFixedHeaderHorizontalScroll);
        $rhsBody.unbind('scroll', onScroll);
        $rhsBody.unbind('resize', setColumnWidths);
        $win.unbind('resize', setColumnWidths);
      });
    }
  };
});