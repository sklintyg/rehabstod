angular.module('rehabstodApp').directive('setClassWhenAtTop', function ($window) {
    'use strict';
    var $win = angular.element($window); // wrap window object as jQuery object

    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var topClass = attrs.setClassWhenAtTop, // get CSS class from directive's attribute value
                parent = element.parent();

            $win.on('scroll', function () {
                var offsetTop = parent.offset().top;
                if ($win.scrollTop() >= offsetTop) {
                    element.addClass(topClass);
                    parent.height(element.height());
                } else {
                    element.removeClass(topClass);
                    parent.css('height', 'auto');
                }
            });

            scope.$on('$destroy', function() {
                $win.unbind('scroll');
            });
        }
    };
});