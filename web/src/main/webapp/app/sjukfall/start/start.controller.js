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

angular.module('rehabstodApp')
.controller('SjukfallStartCtrl', function($scope, $state, $http,
    UserModel, SjukfallSummaryModel, SjukfallSummaryProxy) {
  'use strict';

  function _init() {
    SjukfallSummaryModel.reset();
    SjukfallSummaryProxy.get().then(
        function(data) {
          SjukfallSummaryModel.set(data);
          _proceed();
        }, function() {
          //for some reason, we failed to get sjukfallSummary
          SjukfallSummaryModel.get().hasError = true;
          _proceed();
        });
  }

  function _proceed() {
    if (SjukfallSummaryModel.get().total === 0) {
      //No data to show - go to show 'no data for unit' state
      $state.go('app.sjukfall.start.nodata');
    } else if (UserModel.get().isLakare) {
      //Show lakares selection view (we know we have some summaryData)
      $state.go('app.sjukfall.start.lakare');
    } else {
      //Show rehab selection view (we know we have some summaryData)
      $state.go('app.sjukfall.start.rehabkoordinator');
    }
  }

  _init();
});
