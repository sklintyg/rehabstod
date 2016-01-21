angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(messageService) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    
                },
                controller: function($scope) {
                    $scope.slider = {
                        value: [15, 60],
                        model: [],
                        min: 1,
                        max: 365,
                        step: 1,
                        formatterFn: function(value) {
                            var text = "";
                            if (value == $scope.slider.model[1]) {
                                text  += "Till";
                            } else {
                                text += "Från";
                            }

                            text += "\nDag " + value;

                            if (value == $scope.slider.max) {
                                text += "+";
                            }

                            return text;
                        }
                    };
                },
                templateUrl: 'components/commonDirectives/rhsSlider/rhsSlider.directive.html'
            };
        }]);
