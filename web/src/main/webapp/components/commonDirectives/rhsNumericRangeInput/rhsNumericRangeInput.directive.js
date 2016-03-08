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

    //Initialize internal model
    _updateInputModel();

    //Store originalMaxValue in case we need to set it
    var originalMaxValue = parseInt($scope.max);

    var isNumberPattern = /^\d+$/;

    function _updateInputModel() {
        $scope.inputModel = $scope.externalModel;

        //handle case when we should show a preconfigured value instead of actual maxvalue
        if (angular.isDefined($scope.displayMaxValueAs) && $scope.inputModel >= originalMaxValue) {
            $scope.inputModel = $scope.displayMaxValueAs;
        }
    }


    function _parseEnteredValue() {
        //Handle input of maxreplacevalue such as 366 => '365+'
        if ($scope.inputModel == $scope.displayMaxValueAs) {
            $scope.inputModel = $scope.originalMaxValue;
        } else if (isNumberPattern.test($scope.inputModel)) {
            //It's a number, is is in valid range
            var newValue = parseInt($scope.inputModel);
            if (newValue >= $scope.min && newValue <= $scope.max) {
                $scope.externalModel = newValue;
                return;
            }
        }
        //could not parse the value, or was out of range - revert to old
        _updateInputModel();
    }

    $scope.onManualChange = function() {
        _parseEnteredValue()
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
        },

        link: function(scope, elem, attrs) {

        }
    }
})
;
