/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
angular.module('rehabstodApp').factory('patientHistoryViewState', ['$filter', function($filter) {
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

  var _kraverSamtyckeViewState = {};

  var _kraverInteSamtyckeViewState = {};

  var _extraBoxStates = {
    sparradInom: {
      skipStart: false
    },
    osparradInom: {
      skipStart: false
    },
    osparradAndra: {
      skipStart: false
    },
    sparradAndra: {
      skipStart: false
    }
  };
  var _isKompletteringInfoError = false;

  function _hasSamtycke() {
    return _sjfMetaData.samtyckeFinns;
  }

  function _setKraverSamtyckeViewState(kraverSamtycke) {
    _kraverSamtyckeViewState = kraverSamtycke;
    _kraverSamtyckeViewState = _kraverSamtyckeViewState.map(function(item) {
      item.loading = false;
      return item;
    });
  }

  function _getKraverSamtyckeViewState() {
    return _kraverSamtyckeViewState;
  }

  function _setKraverInteSamtyckeViewState(kraverInteSamtycke) {
    _kraverInteSamtyckeViewState = kraverInteSamtycke;
    _kraverInteSamtyckeViewState = _kraverInteSamtyckeViewState.map(function(item) {
      item.loading = false;
      return item;
    });
  }

  function _getKraverInteSamtyckeViewState() {
    return _kraverInteSamtyckeViewState;
  }

  function _setKompletteringInfoError(isError) {
    _isKompletteringInfoError = isError;
  }

  function _getKompletteringInfoError() {
    return _isKompletteringInfoError;
  }

  function _setSjfMetaData(sjfMetaData) {
    _sjfMetaData = sjfMetaData;

    _setKraverSamtyckeViewState(sjfMetaData.kraverSamtycke);
    _setKraverInteSamtyckeViewState(sjfMetaData.kraverInteSamtycke);

    var kraverInteSamtyckeLength = _sjfMetaData.kraverInteSamtycke.filter(function(value) {
      return value.includedInSjukfall;
    }).length;

    if (kraverInteSamtyckeLength > 0) {
      _extraBoxStates.osparradInom.skipStart = true;
    }

    var kraverSamtyckeLength = _sjfMetaData.kraverSamtycke.filter(function(value) {
      return value.includedInSjukfall;
    }).length;

    if (kraverSamtyckeLength > 0) {
      _extraBoxStates.osparradAndra.skipStart = true;
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

      var isActive = false;
      angular.forEach(sjukfall.intyg, function(intyg) {
        isActive = isActive || intyg.aktivtIntyg;
      });

      var isFirstHistorical = !isActive;
      if (!historicalMarked && isFirstHistorical) {
        historicalMarked = true;
      } else {
        isFirstHistorical = false;
      }

      //Add sjukfall for this year.
      _timeline.push({
        year: thisYear !== previousYear ? thisYear : 0,
        sjukfall: sjukfall,
        isActive: isActive,
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
    _extraBoxStates.sparradInom.skipStart = false;
    _extraBoxStates.osparradInom.skipStart = false;
    _extraBoxStates.osparradAndra.skipStart = false;
    _extraBoxStates.sparradAndra.skipStart = false;
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

  function _addTab(intygsId, title, isFixed, isActive, accessToken) {

    var newTab = {
      accessToken: accessToken,
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
    _tabs.pop();

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
    getSjfMetaData: _getSjfMetaData,
    getKraverSamtyckeViewState: _getKraverSamtyckeViewState,
    getKraverInteSamtyckeViewState: _getKraverInteSamtyckeViewState,
    extraBoxStates: _extraBoxStates,
    hasSamtycke: _hasSamtycke,
    setKompletteringInfoError: _setKompletteringInfoError,
    getKompletteringInfoError: _getKompletteringInfoError
  };
}]);
