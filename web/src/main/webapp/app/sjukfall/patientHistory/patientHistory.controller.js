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

angular.module('rehabstodApp').controller('patientHistoryController',
        function($scope, $uibModalInstance, $state, patientHistoryProxy, UserProxy, UserModel, patient) {
            'use strict';

            $scope.patient = patient;
            $scope.user = UserModel;
            $scope.showSpinner = true;

            function getYearFromDate(dateString) {
                var date = new Date(dateString);
                return date.getFullYear();
            }

            //Find the first and last year for all sjukfalls. The first item is assuemd to be the active one,
            // and array is assumed to be ordered chronologically in descending order.
            function buildTimelineYears(sjukfall) {
                //Cant do anything without any data
                if (!angular.isArray(sjukfall) || sjukfall.length < 1) {
                    return;
                }

                var newestYear = getYearFromDate(sjukfall[0].start), oldestYear = newestYear;

                //If any historical sjukfalls exists, the oldest one must be the last one
                if (sjukfall.length > 1) {
                    oldestYear = getYearFromDate(sjukfall[sjukfall.length - 1].start);
                }
                var timelineYears = [];
                for (var i = newestYear; i >= oldestYear; i--) {
                    timelineYears.push(i);
                }
                return timelineYears;
            }

            $scope.getSjukFallsForYear = function(year) {
                var result = [];
                angular.forEach($scope.sjukfall, function(sjukfall) {
                    if (getYearFromDate(sjukfall.start) === year) {
                        result.push(sjukfall);
                    }
                });
                return result;
            };

            $scope.close = function() {
                $uibModalInstance.close();
            };

            //Initialize
            patientHistoryProxy.get(patient).then(function(sjukfall) {
                $scope.showSpinner = false;
                $scope.sjukfall = sjukfall;
                $scope.timeline = buildTimelineYears(sjukfall);

                //The first (assumed to be active) should be expanded by default
                if ($scope.sjukfall.length > 0) {
                    $scope.sjukfall[0].expanded = true;
                    $scope.sjukfall[0].selected = true;

                }
            });

        });
