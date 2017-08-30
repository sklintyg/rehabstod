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

            // The first item is assuemd to be the active one,
            // and array is assumed to be ordered chronologically in descending order.
            function buildTimeline(sjukfall) {
                //Cant do anything without any data
                if (!angular.isArray(sjukfall) || sjukfall.length < 1) {
                    return;
                }
                var historicalMarked = false;
                var timeline = [];
                var previousYear;
                angular.forEach(sjukfall, function(sjukfall, index) {
                    var thisYear = getYearFromDate(sjukfall.start);

                    if (previousYear && ((previousYear - thisYear) > 1)) {
                        //we have a gap between years: insert empty timeline entry
                        timeline.push({
                            year: 0
                        });
                    }
                    var isFirstHistorical = false;
                    if (!historicalMarked && index > 0) {
                        historicalMarked = true;
                        isFirstHistorical = true;
                    } else {
                        isFirstHistorical = false;
                    }
                    //Add sjukfall for this year.
                    timeline.push({
                        year: thisYear !== previousYear ? thisYear : 0,
                        sjukfall: sjukfall,
                        isFirstHistorical: isFirstHistorical
                    });
                    previousYear = thisYear;
                });

                return timeline;
            }

            $scope.onSelectSjukfall = function(timelineItem) {
                angular.forEach($scope.timeline, function(item) {
                    item.selected = false;
                });
                timelineItem.selected = true;
                timelineItem.expanded = true;
            };

            $scope.close = function() {
                $uibModalInstance.close();
            };

            //Initialize
            patientHistoryProxy.get(patient).then(function(sjukfallResponse) {
                $scope.showSpinner = false;
                $scope.timeline = buildTimeline(sjukfallResponse);

                //The first (assumed to be active) should be expanded by default
                if ($scope.timeline.length > 0) {
                    $scope.timeline[0].expanded = true;
                    $scope.timeline[0].selected = true;

                }
            });
            //TODO: maybe use som other form of constant for this?
            $scope.radius = 26;

        });
