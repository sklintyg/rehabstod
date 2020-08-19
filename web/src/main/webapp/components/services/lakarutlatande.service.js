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

angular.module('rehabstodApp').factory('LakarutlatandeService',
    function($log, StringHelper, messageService, lakarutlatandenProxy, LakarutlatandeModel, LakarutlatandeFilterViewState,
        LakarutlatandeViewState) {
      'use strict';

      var loading = false;

      function _loadLakarutlatande(force, skipReset) {

        if (loading) {
          return;
        }

        var empty = LakarutlatandeModel.get().length === 0;

        if (force || empty) {
          loading = true;
          if (!skipReset) {
            LakarutlatandeFilterViewState.reset();
            LakarutlatandeModel.reset();
          }

          var filterState = LakarutlatandeFilterViewState.getCurrentFilterState();

          var query = {
            qas: filterState.komplettering,
            certTypes: filterState.certType,
            fromDate: filterState.signDate.from,
            toDate: filterState.signDate.to,
            diagnoses: filterState.diagnosKapitel,
            doctors: filterState.lakare,
            searchText: filterState.freeText,
            fromAge: filterState.alder[0],
            toAge: filterState.alder[1]
          };

          return lakarutlatandenProxy.getLakarutlatandenForUnit(query).then(function(response) {
            LakarutlatandeViewState.setKompletteringInfoError(response.qaError);
            LakarutlatandeModel.set(response.certificates);
            loading = false;
          }, function(errorData) {
            $log.debug('Failed to get lakarutlatanden.');
            $log.debug(errorData);

            LakarutlatandeModel.setError();
            loading = false;
          });
        }
      }

      function _isLoading() {
        return loading;
      }

      function _reset() {
        LakarutlatandeFilterViewState.reset();
        LakarutlatandeModel.reset();
      }

      return {
        loadLakarutlatande: _loadLakarutlatande,
        isLoading: _isLoading,
        reset: _reset
      };
    });
