/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').factory('patientHistoryViewState', [ '$filter', function($filter) {
    'use strict';

    /**
     * Holds a custom array model representing the opened tabs and their state
     */
    var _tabs = [];

    /**
     * Holds a custom array model based on the supplied sjukfall array
     * decorated with presentation attributes.
     */
    var _timeline = [];

    var _defaultTab = null;

    var _sjfMetaData = {};

    function _setSjfMetaData(sjfMetaData) {
        _sjfMetaData = sjfMetaData;

        // FAKE DATA WHILE BACKEND IS BUILT
        _sjfMetaData.vardenheterInomVGMedSparr = [];
        var i = 0;
        for(; i < 20; i++){
            _sjfMetaData.vardenheterInomVGMedSparr.push('Vardenhetmedlangtnamnochmassaandraproblem ' + (i + 1));
        }

        _sjfMetaData.andraVardgivareMedSparr = [];
        for(i = 0; i < 20; i++){
            _sjfMetaData.andraVardgivareMedSparr.push('Vardgivaremedlangtnamnochmassaandraproblem ' + (i + 1));
        }

        _sjfMetaData.andraVardgivareUtanSparr = [];
        for(i = 0; i < 20; i++){
            _sjfMetaData.andraVardgivareUtanSparr.push('Vardgivaremedlangtnamnochmassaandraproblem ' + (i + 1));
        }
    }

    function _getSjfMetaData() {
        return _sjfMetaData;
    }

    // Timeline ---------------

    /* Build a custom array based on the supplied sjukfall array
     decorated with presentation attributes.

     The first item is assumed to be the active one,
     and array is assumed to be ordered chronologically in descending order.
     */
    function _setTimelineItems(sjukfall) {
        //Can't do anything without any data..
        if (!angular.isArray(sjukfall) || sjukfall.length < 1) {
            return;
        }
        _timeline = [];
        var historicalMarked = false;
        var previousYear;
        angular.forEach(sjukfall, function(sjukfall, index) {
            var thisYear = _getYearFromDate(sjukfall.start);

            if (previousYear && ((previousYear - thisYear) > 1)) {
                //we have a gap between years: insert empty timeline entry
                _timeline.push({
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
            _timeline.push({
                year: thisYear !== previousYear ? thisYear : 0,
                sjukfall: sjukfall,
                isFirstHistorical: isFirstHistorical,
                expanded: index === 0,
                selected: index === 0
            });
            previousYear = thisYear;
        });
    }

    function _getYearFromDate(dateString) {
        var date = new Date(dateString);
        return date.getFullYear();
    }

    function _selectTimelineItem(newItem) {
        //Deselect all..
        angular.forEach(_timeline, function(item) {
            item.selected = false;
        });
        //.. and mark the new one as selected (and expand it too)
        newItem.selected = true;
        newItem.expanded = true;
    }

    function _getTimelineItems() {
        return _timeline;
    }


    //Tabs --------------------
    function _reset() {
        _tabs = [];
        _timeline = [];
    }

    function _selectTab(tab) {
        //deselect all tabs..
        _tabs.forEach(function(tab) {
            tab.active = false;
        });
        //..and mark new one
        tab.active = true;
    }

    function _getTabById(id) {
        return $filter('filter')(_tabs, {
            intygsId: id
        })[0];
    }

    function _getTabs() {
        return _tabs;
    }

    function _addTab(intygsId, title, isFixed, isActive) {

        var newTab = {
            intygsId: intygsId,
            title: title,
            fixed: isFixed,
            active: isActive
        };

        _tabs.push(newTab);

        //Select new one by default
        _selectTab(newTab);
        if (isFixed) {
            _defaultTab = newTab;
        }
    }

    function _closeTab(tab) {
        _tabs.pop(tab);

        //select the (unclosable) default fixed tab.
        _selectTab(_defaultTab);

    }

    // Return public API for the factory
    return {
        reset: _reset,
        addTab: _addTab,
        closeTab: _closeTab,
        getTabs: _getTabs,
        selectTab: _selectTab,
        getTabById: _getTabById,
        setTimelineItems: _setTimelineItems,
        getTimelineItems: _getTimelineItems,
        selectTimelineItem: _selectTimelineItem,
        setSjfMetaData: _setSjfMetaData,
        getSjfMetaData: _getSjfMetaData
    };
} ]);
