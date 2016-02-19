angular.module('rehabstodApp').directive('rhsTableFixedHeader', function ($window) {
    'use strict';
    var $win = angular.element($window); // wrap window object as jQuery object

    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var topClass = attrs.rhsTableFixedHeader,
                fixedHeader = element.find('#' + attrs.fixedHeader),
                normalHeader = element.find('#' + attrs.normalHeader);

            var paddingTop = 0;
            if (attrs.paddingTop) {
                paddingTop = parseInt(attrs.paddingTop, 10);
            }

            var isFixed =  false;

            $win.on('scroll', function () {
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
                        isFixed = false;
                    }
                }
            });

            $win.on('resize', function() {
                setColumnWidths();
            });


            var setColumnWidths = function() {
                fixedHeader.height(normalHeader.outerHeight());

                var fixedColumns = fixedHeader.find('th');
                normalHeader.find('th').each(function(index, column) {
                    $(fixedColumns[index]).width($(column).width());
                });
            };

            scope.$on('$destroy', function() {
                $win.unbind('scroll');
                $win.unbind('resize');
            });
        }
    };
});