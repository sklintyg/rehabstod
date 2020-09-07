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

describe('Viewstate: LakarutlatandeFilterViewState', function() {

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

  var LakarutlatandeFilterViewState;
  var DiagnosKapitelModel;
  var LakareModel;
  var QAModel;
  var CertTypeModel;

  // Inject dependencies and mocks
  beforeEach(inject(function(_LakarutlatandeFilterViewState_, _DiagnosKapitelModel_, _LakareModel_, _QAModel_, _CertTypeModel_) {
    LakarutlatandeFilterViewState = _LakarutlatandeFilterViewState_;
    DiagnosKapitelModel = _DiagnosKapitelModel_;
    LakareModel = _LakareModel_;
    QAModel = _QAModel_;
    CertTypeModel = _CertTypeModel_;
    LakarutlatandeFilterViewState.initQAModel();
  }));

  describe('get', function() {
    it('should return initial state initially', function() {
      expect(LakarutlatandeFilterViewState.get().diagnosKapitelModel.getSelected()).toEqual([]);
      expect(LakarutlatandeFilterViewState.get().lakareModel.getSelected()).toEqual([]);
      expect(LakarutlatandeFilterViewState.get().aldersModel).toEqual([0, 101]);
      expect(LakarutlatandeFilterViewState.get().signDateModel).toEqual({from: null, to: null});
      expect(LakarutlatandeFilterViewState.get().freeTextModel).toEqual('');
      expect(LakarutlatandeFilterViewState.get().qaModel.getSelected().length).toEqual(1);
      expect(LakarutlatandeFilterViewState.get().showPatientId).toEqual(true);
      expect(LakarutlatandeFilterViewState.get().certTypeModel.getSelected().length).toEqual(0);
    });
  });

  describe('getCurrentFilterState', function() {
    it('should return correct state', function() {
      //Arrange
      DiagnosKapitelModel.get()[0].selected = true;
      LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
      LakareModel.get()[0].selected = true;
      LakarutlatandeFilterViewState.get().freeTextModel = 'anything';
      LakarutlatandeFilterViewState.get().aldersModel = [22, 77];
      LakarutlatandeFilterViewState.get().signDateModel = {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)};
      LakarutlatandeFilterViewState.get().showPatientId = false;
      CertTypeModel.get()[0].selected = true;

      var expectedFilter = {
        diagnosKapitel: [DiagnosKapitelModel.get()[0].id],
        lakare: [LakareModel.get()[0].id],
        alder: LakarutlatandeFilterViewState.get().aldersModel,
        signDate: {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)},
        freeText: LakarutlatandeFilterViewState.get().freeTextModel,
        komplettering: null,
        showPatientId: false,
        certType: ['FK7800']
      };
      //Verify

      expect(LakarutlatandeFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
    });
  });

  describe('reset', function() {
    it('should return correct reset selections when reset', function() {
      //Arrange
      DiagnosKapitelModel.get()[0].selected = true;
      LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
      LakareModel.get()[0].selected = true;
      LakarutlatandeFilterViewState.get().freeTextModel = 'anything';
      LakarutlatandeFilterViewState.get().aldersModel = [22, 77];
      LakarutlatandeFilterViewState.get().signDateModel = {from: new Date(2017, 1, 9), to: new Date(2017, 1, 11)};
      LakarutlatandeFilterViewState.get().showPatientId = false;
      CertTypeModel.get()[0].selected = true;

      var expectedFilter = {
        diagnosKapitel: [],
        lakare: [],
        alder: [0, null],
        signDate: {from: null, to: null},
        freeText: '',
        komplettering: null,
        showPatientId: true,
        certType: []
      };

      //Act
      LakarutlatandeFilterViewState.reset();

      //Verify
      expect(LakarutlatandeFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
    });
  });

});
