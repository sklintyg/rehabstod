/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').directive('rhsDatePicker',['$rootScope', '$timeout', 'datePickerOpenService', 'dateUtilsService',
    function($rootScope, $timeout, datePickerOpen, dateUtils) {
        'use strict';

        return {
            restrict: 'E',
            replace: false,
            scope: {
                targetModel: '=',
                targetModelOptions: '=?',
                format: '@',
                domId: '@',
                invalid: '=',
                onChange: '&',
                maxDate: '@',
                minDate: '@',
                addDateParser: '@',
                dateOptions: '@',
                onBlur: '&'
            },
            templateUrl: '/components/commonDirectives/rhsDatePicker/rhsDatePickerField.directive.html',
            require:'rhsDatePicker',
            controller: function($scope) {
                var activeDate = new Date();

                function setActiveDate(date) {
                    activeDate = date;
                }

                function getActiveDate() {
                    return activeDate;
                }

                $scope.dateOptions = {
                        minDate: undefined,
                        maxDate: undefined,
                        maxMode: 'day',
                        setActiveDate: setActiveDate,
                        getActiveDate: getActiveDate
                };

                if (!$scope.targetModelOptions) {
                    $scope.targetModelOptions = {};
                }
                $scope.targetModelOptions.allowInvalid = true;

                if ($scope.minDate !== undefined) {
                    $scope.dateOptions.minDate = new Date($scope.minDate);
                }

                if ($scope.maxDate !== undefined) {
                    $scope.dateOptions.maxDate = new Date($scope.maxDate);
                }

                if ($scope.format === undefined) {
                    $scope.format = 'yyyy-MM-dd';
                }

                function updateOptions() {
                    if ($scope.minDate !== undefined) {
                        $scope.dateOptions.minDate = new Date($scope.minDate);
                    }
    
                    if ($scope.maxDate !== undefined) {
                        $scope.dateOptions.maxDate = new Date($scope.maxDate);
                    }
                }

                var PickerState = {
                    isOpen: false
                };
                $scope.pickerState = PickerState;

                $scope.toggleOpen = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    updateOptions();
                    $timeout(function() {
                        $scope.pickerState.isOpen = !$scope.pickerState.isOpen;
                        datePickerOpen.update($scope.pickerState);
                    });
                };

                $scope.isFocused = false;
                $scope.toggleFocus = function() {
                    if ($scope.isFocused) {
                        $scope.isFocused = false;
                    } else {
                        $scope.isFocused = true;
                    }
                };

                $scope.onDatepickerInputFieldBlur = function() {
                    $scope.toggleFocus();
                    $scope.onBlur();
                };

                $scope.focused = function() {
                    $scope.toggleFocus();
                };
            },
            link: function(scope/*, element, attrs, ctrl*/) {
                scope.$watch('targetModel', function(newVal, oldVal) {
                    if (newVal || newVal !== oldVal) {
                        if(dateUtils.isDate(newVal)) {
                            scope.dateOptions.initDate = new Date(newVal);
                        } else {
                            scope.dateOptions.initDate = new Date();
                        }
                        scope.dateOptions.setActiveDate(scope.dateOptions.initDate);
                    }
                });
            }
        };
    }])
    .directive('rhsDatePickerFieldInput', ['$log', 'dateUtilsService',
    function($log, dateUtils ) {
        'use strict';
        return {
            priority: 1,
            restrict: 'A',
            require:['ngModel'],
            link: function(scope, element, attrs, ctrls) {
                var ngModel = ctrls[0];

                if (scope.addDateParser) {
                    if (scope.addDateParser === 'loose') {
                        dateUtils.addLooseDateParser(ngModel);
                    }
                    else if (scope.addDateParser === 'strict') {
                        dateUtils.addStrictDateParser(ngModel);
                    }
                    else {
                        $log.error('unknown dateparser method ' + scope.addDateParser);
                    }
                }

                var maximumDate = '2099-12-12';
                var minimumDate = '1900-01-01';

                ngModel.$validators.maxDate = function() {
                    return dateUtils.isDateEmptyOrValidAndBefore(maximumDate, ngModel.$viewValue);
                };

                ngModel.$validators.minDate = function() {
                    return dateUtils.isDateEmptyOrValidAndBefore(ngModel.$viewValue, minimumDate);
                };

            }
        };
    }]);
