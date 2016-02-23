angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    sliderModel: '=',
                    range: '=',
                    min: '=',
                    max: '=',
                    ticksPositions: '=',
                    ticksLabels: '=',
                    ticks: '=',
                    position: '='
                },
                controller: function($scope) {

                    $scope.value = $scope.sliderModel;
                    $scope.step = 1;
                    $scope.ticksSnapBounds = 1;

                    $scope.formatterFn =  function(value) {
                        var text;

                        if ($scope.range) {
                            if (value === $scope.sliderModel[1]) {
                                text = 'Till';
                            } else {
                                text = 'Från';
                            }

                            text += '\n';

                            if (value === $scope.max) {
                                text += '365+';
                            } else {
                                text += value;
                            }
                        }
                        else {
                            text = value;
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