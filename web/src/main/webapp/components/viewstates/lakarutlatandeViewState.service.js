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

angular.module('rehabstodApp').factory('LakarutlatandeViewState', [
  function() {
    'use strict';

    var state = {
      kompletteringInfoError: false
    };

    function _reset() {
      state.kompletteringInfoError = false;
    }

    function _getState() {
      return state;
    }

    function _setKompletteringInfoError(_kompletteringInfoError) {
      state.kompletteringInfoError = _kompletteringInfoError;
    }

    _reset();

    return {
      reset: _reset,
      get: _getState,
      setKompletteringInfoError: _setKompletteringInfoError
    };
  }])
;
