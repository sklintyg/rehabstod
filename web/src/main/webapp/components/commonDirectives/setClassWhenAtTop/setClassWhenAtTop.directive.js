angular.module('rehabstodApp').directive('setClassWhenAtTop', function ($window) {
    'use strict';
    var $win = angular.element($window); // wrap window object as jQuery object

    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var topClass = attrs.setClassWhenAtTop, // get CSS class from directive's attribute value
                offsetTop = element.offset().top; // get element's offset top relative to document

            $win.on('scroll', function () {
                if ($win.scrollTop() >= offsetTop) {
                    element.addClass(topClass);
                } else {
                    element.removeClass(topClass);
                }
            });

            scope.$on('$destroy', function() {
                $win.unbind('scroll');
            });
        }
    };
});