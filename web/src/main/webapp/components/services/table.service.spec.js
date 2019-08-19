/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

describe('Service: TableService', function() {
  'use strict';

  // load the controller's module
  beforeEach(angular.mock.module('rehabstodApp'));

  var TableService;
  var UserModel;
  var SjukfallFilterViewState;

  beforeEach(inject(function(_TableService_, _UserModel_, _SjukfallFilterViewState_) {
    TableService = _TableService_;
    UserModel = _UserModel_;
    SjukfallFilterViewState = _SjukfallFilterViewState_;
  }));

  var preferenceNyckel = 'tabelNyckel';
  var columns = [{
    id: 'ny'
  }, {
    id: 'column2'
  }, {
    id: 'patientId'
  }];

  describe('all', function() {
    var expected = [{
      id: 'ny'
    }, {
      id: 'column2'
    }, {
      id: 'patientId'
    }];

    it('ingen vald', function() {
      var valda = TableService.getSelectedColumns(columns, preferenceNyckel);

      expect(valda.length).toBe(3);
      expect(valda).toEqual(expected);
    });

    it('alla valda', function() {
      UserModel.get().preferences[preferenceNyckel] = 'ny|column2|patientId';

      var valda = TableService.getSelectedColumns(columns, preferenceNyckel);

      expect(valda.length).toBe(3);
      expect(valda).toEqual(expected);
    });
  });

  it('en vald', function() {
    var expected = [{
      id: 'ny'
    }];

    UserModel.get().preferences[preferenceNyckel] = 'ny';

    var valda = TableService.getSelectedColumns(columns, preferenceNyckel);

    expect(valda.length).toBe(1);
    expect(valda).toEqual(expected);
  });

  it('sortering', function() {
    var expected = [{
      id: 'column2'
    }, {
      id: 'patientId'
    }, {
      id: 'ny'
    }];

    UserModel.get().preferences[preferenceNyckel] = 'column2|patientId|ny';

    var valda = TableService.getSelectedColumns(columns, preferenceNyckel);

    expect(valda.length).toBe(3);
    expect(valda).toEqual(expected);
  });

  describe('filtering', function() {
    it('filtering', function() {
      var expected = [{
        id: 'ny'
      }, {
        id: 'column2'
      }];

      SjukfallFilterViewState.get().showPatientId = false;

      var valda = TableService.getSelectedColumns(columns, preferenceNyckel);

      expect(valda.length).toBe(2);
      expect(valda).toEqual(expected);
    });

    it('strunta i filtret', function() {
      var expected = [{
        id: 'ny'
      }, {
        id: 'column2'
      }, {
        id: 'patientId'
      }];

      SjukfallFilterViewState.get().showPatientId = false;

      var valda = TableService.getSelectedColumns(columns, preferenceNyckel, true);

      expect(valda.length).toBe(3);
      expect(valda).toEqual(expected);
    });
  });

});
