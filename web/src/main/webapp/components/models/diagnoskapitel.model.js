/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

angular.module('rehabstodApp').factory('DiagnosKapitelModel',
    function() {
      'use strict';

      var data = [];

      function _init() {
        data.splice(0, data.length);
        return data;
      }

      function _reset() {
        angular.forEach(data, function(value) {
          value.selected = false;
          value.disabled = false;
        });
        return data;
      }

      function _createDisplayValue(kapitel) {
        return kapitel.id + ((kapitel.id !== '') ? ': ' : '') + kapitel.name;
      }

      return {

        reset: _reset,

        set: function(diagnosKapitel) {
          _init();
          angular.forEach(diagnosKapitel, function(value) {
            data.push({
              _definition: value,
              id: value.id,
              displayValue: _createDisplayValue(value),
              selected: false,
              disabled: false
            });
          });

        },
        get: function() {
          return data;
        },
        getSelected: function() {
          var selected = [];
          angular.forEach(data, function(value) {
            if (value.selected) {
              selected.push(value);
            }
          });
          return selected;
        },
        setActivDiagnosKapitelIdlist: function(activeKapitelIds) {
          angular.forEach(data, function(kapitel) {
            kapitel.disabled = activeKapitelIds.indexOf(kapitel.id) === -1;
            if (kapitel.disabled) {
              kapitel.selected = false;
            }
          });
        }
      };
    }
);