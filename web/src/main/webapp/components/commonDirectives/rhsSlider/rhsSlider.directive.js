angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    
                },
                controller: function($scope) {
                    $scope.slider = {
                        value: [1, 366],
                        model: [],
                        min: 1,
                        max: 366,
                        step: 1,
                        formatterFn: function(value) {
                            var text;
                            if (value === $scope.slider.model[1]) {
                                text  = 'Till';
                            } else {
                                text = 'Från';
                            }

                            text += '\n';

                            if (value === $scope.slider.max) {
                                text += '365+';
                            } else {
                                text += value;
                            }

                            if (value === 1) {
                                text += ' dag';
                            } else {
                                text += ' dagar';
                            }

                            return text;
                        }
                    };
                },
                templateUrl: 'components/commonDirectives/rhsSlider/rhsSlider.directive.html'
            };
        }]);