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

angular.module('rehabstodApp').filter('rhsSearchfilter', function(moment, _) {
  'use strict';

  var standardComparator = function standardComparator(obj, text) {
    text = ('' + text).toLowerCase();
    return ('' + obj).toLowerCase().indexOf(text) > -1;
  };

  return function(array, expression) {
    function customComparator(actual, filterParams, slutDatum) {

      //DiagnosKapitel
      if (!matchAny(actual.diagnos.kapitel, filterParams.diagnosKapitel)) {
        return false;
      }

      //Lakare
      if (!matchAny(actual.lakare.namn, filterParams.lakare)) {
        return false;
      }

      //Kompletteringsstatus (null = don't filter at all, 0 = must be exactly 0 to pass, otherwise must be > 0)
      if (angular.isNumber(filterParams.komplettering) &&
          (((filterParams.komplettering === 0) && actual.obesvaradeKompl > 0) ||
              ((filterParams.komplettering > 0) && actual.obesvaradeKompl < 1))) {
        return false;
      }

      //Sjukskrivningslangd
      if (!range(actual.dagar, filterParams.sjukskrivningslangd[0], filterParams.sjukskrivningslangd[1])) {
        return false;
      }

      // Ålder
      if (!range(actual.patient.alder, filterParams.alder[0], filterParams.alder[1])) {
        return false;
      }

      // Slutdatum
      if (!range(actual.slutOmDagar, slutDatum.from, slutDatum.to)) {
        return false;
      }

      if (filterParams.freeText.length > 0 && !passWildCardSearch(actual, filterParams.freeText)) {
        return false;
      }

      return true;
    }

    function passWildCardSearch(actual, wildCard) {
      return standardComparator(actual.quickSearchString, wildCard);
    }

    function range(actual, lower, higher) {
      //number range
      if (angular.isNumber(lower)) {
        if (actual < lower) {
          return false;
        }
      }

      if (angular.isNumber(higher)) {
        if (actual > higher) {
          return false;
        }
      }

      return true;
    }

    function matchAny(actual, matchAny) {
      if (angular.isUndefined(matchAny) || matchAny.length === 0) {
        return true;
      }

      if (angular.isUndefined(actual)) {
        return false;
      }

      for (var i = 0; i < matchAny.length; i++) {
        if (actual.toLowerCase() === matchAny[i].toLowerCase()) {
          return true;
        }
      }

      return false;
    }

    function processItems(array, filterParam) {

      var slutDatum = {};

      if (moment.isDate(filterParam.customSearch.slutdatum.from) && moment.isDate(filterParam.customSearch.slutdatum.to)) {

        // Remove time from diff calculation.
        filterParam.customSearch.slutdatum.from.setHours(0, 0, 0, 0);
        filterParam.customSearch.slutdatum.to.setHours(0, 0, 0, 0);
        var today = moment().hour(0).minute(0).second(0).millisecond(0);

        slutDatum = {
          from: moment(filterParam.customSearch.slutdatum.from).diff(today, 'days'),
          to: moment(filterParam.customSearch.slutdatum.to).diff(today, 'days')
        };
      }

      return _.filter(array, function(item) {
        return customComparator(item, filterParam.customSearch, slutDatum);
      });
    }

    return processItems(array, expression);
  };

});
