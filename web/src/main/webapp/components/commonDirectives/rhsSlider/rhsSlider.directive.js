angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    sliderModel: '='
                },
                controller: function($scope) {
                    $scope.value = $scope.sliderModel;
                    $scope.min =  1;
                    $scope.max = 366;
                    $scope.step = 1;
                    $scope.formatterFn =  function(value) {
                        var text;
                        if (value === $scope.sliderModel[1]) {
                            text  = 'Till';
                        } else {
                            text = 'Från';
                        }

                        text += '\n';

                        if (value === $scope.max) {
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
                    };
                },
                templateUrl: 'components/commonDirectives/rhsSlider/rhsSlider.directive.html'
            };
        }]);