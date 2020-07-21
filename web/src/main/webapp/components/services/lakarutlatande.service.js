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
    function($log, StringHelper, messageService, /*LakarutlatandeProxy,*/ LakarutlatandeModel, LakarutlatandeFilterViewState, LakarutlatandeViewState, _, UserModel) {
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

          var query = {
            LakarutlatandeFilterViewState
            // maxIntygsGlapp: UserModel.get().preferences.maxAntalDagarMellanIntyg
          };

          var testJsonData = [{
            'patient': {
              'id': '19360721-7068',
              'namn': 'Förnamn-3607 Efternamn-21-7068',
              'kon': 'F',
              'alder': 79
            },
            'diagnos': {
              'intygsVarde': 'M16.0',
              'kapitel': 'M00-M99',
              'kod': 'M160',
              'beskrivning': 'Primär koxartros, dubbelsidig'
            },
            'biDiagnoser': [{'intygsVarde': 'B1'}, {'intygsVarde': 'B2'}],
            'signDate': '2016-02-01',
            'qas': 1,
            'lakare': {
              namn: 'Jan Nilsson'
            },
            'certType': 'FK7800'
          }];
          LakarutlatandeModel.set(testJsonData);
          loading = false;

          return null;
          // return LakarutlatandeProxy.get(query).then(function(response) {
          //   LakarutlatandeViewState.setKompletteringInfoError(response.kompletteringInfoError);
          //   LakarutlatandeModel.set(response.data);
          //   loading = false;
          // }, function(errorData) {
          //   $log.debug('Failed to get sjukfall.');
          //   $log.debug(errorData);
          //
          //   LakarutlatandeModel.setError();
          //   loading = false;
          // });
        }
      }

      function _isLoading() {
        return loading;
      }

      return {
        loadLakarutlatande: _loadLakarutlatande,
        isLoading: _isLoading
      };
    });
