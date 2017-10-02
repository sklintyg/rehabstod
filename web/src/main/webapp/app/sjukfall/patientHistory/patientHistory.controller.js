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
    function($scope, $uibModalInstance, $state, patientHistoryProxy, SjukfallFilterViewState, patient) {
    'use strict';
    $scope.errorMessageKey  = '';
    $scope.patient = patient;
    $scope.showPatientId = SjukfallFilterViewState.get().showPatientId;
    $scope.showSpinner = true;
    //Constant needed in template
    $scope.radius = 30;

    function getYearFromDate(dateString) {
        var date = new Date(dateString);
        return date.getFullYear();
    }

    /* Build a custom array based on the supplied sjukfall array
     decorated with presentation attributes.

     The first item is assumed to be the active one,
     and array is assumed to be ordered chronologically in descending order.
     */
    function buildTimeline(sjukfall) {
        //Can't do anything without any data..
        if (!angular.isArray(sjukfall) || sjukfall.length < 1) {
            return [];
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
        //Deselect all..
        angular.forEach($scope.timeline, function(item) {
            item.selected = false;
        });
        //.. and mark the new one as selected (and expand it too)
        timelineItem.selected = true;
        timelineItem.expanded = true;
    };

    $scope.close = function() {
        $uibModalInstance.close();
    };

    //Start by requesting data
    patientHistoryProxy.get(patient).then(function(sjukfallResponse) {
        $scope.showSpinner = false;
        $scope.timeline = buildTimeline(sjukfallResponse);

        //The first item (assumed to be the ongoing sjukfall) should be expanded by default
        if ($scope.timeline.length > 0) {
            $scope.timeline[0].expanded = true;
            $scope.timeline[0].selected = true;

        }
    }, function() {
        $scope.showSpinner = false;
        $scope.errorMessageKey = 'server.error.loadpatienthistory.text';
    });

});
