/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').factory('ShowPatientIdViewState', function($window) {
      'use strict';

      var showPatientId = _getShowPatientId();

      function _showPatientId() {
        return showPatientId;
      }

      function _setShowPatientId(value) {
        showPatientId = value;
        $window.sessionStorage.setItem('showPatientId', value.toString());
      }

      function _toggleShowPatientId() {
        var showPatientId = _getShowPatientId();
        _setShowPatientId(!showPatientId);
      }

      function _getShowPatientId() {
        var storeValue = $window.sessionStorage.getItem('showPatientId');

        if (storeValue === null && showPatientId === undefined) {
          _setShowPatientId(true);
          return true;
        }
        if (storeValue === null || storeValue === undefined) {
          return showPatientId;
        }

        return storeValue === 'true';
      }

      return {
        showPatientId: _showPatientId,
        setShowPatientId: _setShowPatientId,
        toggleShowPatientId: _toggleShowPatientId
      };
    });