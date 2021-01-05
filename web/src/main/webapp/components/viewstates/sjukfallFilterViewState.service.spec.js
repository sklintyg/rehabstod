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

describe('Viewstate: SjukfallFilterViewState', function() {
  'use strict';
  // Load the module and mock away everything that is not necessary.
  beforeEach(angular.mock.module('rehabstodApp', function($provide) {
    $provide.value('APP_CONFIG', {
      diagnosKapitelList: [{
        'from': {'letter': 'A', 'number': 0, 'id': 'A00'},
        'to': {'letter': 'B', 'number': 99, 'id': 'B99'},
        'name': 'Vissa infektionssjukdomar och parasitsjukdomar',
        'id': 'A00-B99'
      }]
    });
  }));

  var SjukfallFilterViewState;
  var DiagnosKapitelModel;
  var LakareModel;
  var QAModel;

  // Inject dependencies and mocks
  beforeEach(inject(function(_SjukfallFilterViewState_, _DiagnosKapitelModel_, _LakareModel_, _QAModel_) {
    SjukfallFilterViewState = _SjukfallFilterViewState_;
    DiagnosKapitelModel = _DiagnosKapitelModel_;
    LakareModel = _LakareModel_;
    QAModel = _QAModel_;
    SjukfallFilterViewState.initQAModel();

  }));

  describe('get', function() {
    it('should return initial state initially', function() {
      expect(SjukfallFilterViewState.get().diagnosKapitelModel.getSelected()).toEqual([]);
      expect(SjukfallFilterViewState.get().lakareModel.getSelected()).toEqual([]);
      expect(SjukfallFilterViewState.get().sjukskrivningslangdModel).toEqual([1, 366]);
      expect(SjukfallFilterViewState.get().aldersModel).toEqual([0, 101]);
      expect(SjukfallFilterViewState.get().slutdatumModel).toEqual({from: null, to: null});
      expect(SjukfallFilterViewState.get().freeTextModel).toEqual('');
      expect(SjukfallFilterViewState.get().qaModel.getSelected().length).toEqual(1);
      expect(SjukfallFilterViewState.get().showPatientId).toEqual(true);
    });
  });

  describe('getCurrentFilterState', function() {
    it('should return correct state', function() {
      //Arrange
      DiagnosKapitelModel.get()[0].selected = true;
      LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
      LakareModel.get()[0].selected = true;
      SjukfallFilterViewState.get().freeTextModel = 'anything';
      SjukfallFilterViewState.get().sjukskrivningslangdModel = [22, 77];
      SjukfallFilterViewState.get().aldersModel = [22, 77];
      SjukfallFilterViewState.get().slutdatumModel = {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)};
      SjukfallFilterViewState.get().showPatientId = false;

      var expectedFilter = {
        diagnosKapitel: [DiagnosKapitelModel.get()[0].id],
        lakare: [LakareModel.get()[0].id],
        sjukskrivningslangd: SjukfallFilterViewState.get().sjukskrivningslangdModel,
        alder: SjukfallFilterViewState.get().aldersModel,
        slutdatum: {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)},
        freeText: SjukfallFilterViewState.get().freeTextModel,
        qa: null,
        showPatientId: false
      };
      //Verify

      expect(SjukfallFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
    });
  });

  describe('reset', function() {
    it('should return correct reset selections when reset', function() {
      //Arrange
      DiagnosKapitelModel.get()[0].selected = true;
      LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
      LakareModel.get()[0].selected = true;
      SjukfallFilterViewState.get().freeTextModel = 'anything';
      SjukfallFilterViewState.get().sjukskrivningslangdModel = [22, 77];
      SjukfallFilterViewState.get().aldersModel = [22, 77];
      SjukfallFilterViewState.get().slutdatumModel = {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)};
      SjukfallFilterViewState.get().showPatientId = false;

      var expectedFilter = {
        diagnosKapitel: [],
        lakare: [],
        sjukskrivningslangd: [1, null],
        alder: [0, null],
        slutdatum: {from: null, to: null},
        freeText: '',
        qa: null,
        showPatientId: true
      };

      //Act
      SjukfallFilterViewState.reset();

      //Verify
      expect(SjukfallFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
    });
  });

});
