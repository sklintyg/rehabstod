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

angular.module('rehabstodApp').controller('rhsNumericRangeInputCtrl', ['$scope', function($scope) {
    'use strict';
    //Initialize internal model
    _updateInputModel();

    var isNumberPattern = /^[0-9]+$/;

    //Store originalMaxValue in case we need to set it
    $scope.originalMaxValue = parseInt($scope.max, 10);


    function _updateInputModel() {
        $scope.inputModel = $scope.externalModel;

        //handle case when we should show a preconfigured (displayMaxValueAs) value instead of actual maxvalue
        if (angular.isDefined($scope.displayMaxValueAs) && $scope.inputModel >= $scope.originalMaxValue) {
            $scope.inputModel = $scope.displayMaxValueAs;
        }
    }

    function withinRangeNow(enteredValue) {
        return (enteredValue >= $scope.min && enteredValue <= $scope.max);
    }

    function _parseEnteredValue() {
        $scope.error = false;

        //Handle input of displayMaxValueAs (inversed) such as '365+' => 366
        if ($scope.inputModel === $scope.displayMaxValueAs) {
            $scope.inputModel = $scope.originalMaxValue;
            if (withinRangeNow($scope.inputModel)) {
                $scope.externalModel = $scope.inputModel;
            }
        } else if (isNumberPattern.test($scope.inputModel)) {
            //convert to a number
            $scope.inputModel = parseInt($scope.inputModel, 10);

            if ($scope.inputModel < $scope.min) {
                $scope.inputModel = $scope.min;
            }
            else if ($scope.inputModel > $scope.max) {
                $scope.inputModel = $scope.max;
            }

            $scope.externalModel = $scope.inputModel;

        } else if ($scope.inputModel) {
            $scope.error = true;
        }

        if (!$scope.error) {
            _updateInputModel();
        }
    }

    $scope.onManualChange = function() {
        _parseEnteredValue();
    };


    $scope.onClickUp = function() {
        if ($scope.externalModel < $scope.max) {
            $scope.externalModel++;
            _updateInputModel();
        }
    };

    $scope.onClickDown = function() {
        if ($scope.externalModel > $scope.min) {
            $scope.externalModel--;
            _updateInputModel();
        }
    };

    //Select text in input when clicked
    $scope.onfocus = function(event) {
        event.target.select();
    };

    $scope.$watch('externalModel', function() {
        _updateInputModel();
    });


}]).directive('rhsNumericRangeInput', function() {
    'use strict';
    return {
        restrict: 'E',
        replace: true,
        controller: 'rhsNumericRangeInputCtrl',
        templateUrl: 'components/commonDirectives/rhsNumericRangeInput/rhsNumericRangeInput.directive.html',
        scope: {
            'externalModel': '=',
            'min': '=',
            'max': '=',
            'displayMaxValueAs': '@'
        }
    };
});
