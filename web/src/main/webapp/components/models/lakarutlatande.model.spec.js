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

describe('Model: LakarutlatandeModel', function() {
  'use strict';

  // Load the module and mock away everything that is not necessary.
  beforeEach(module('rehabstodApp'));

  var LakarutlatandeModel;
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
    'signingTimeStamp': '2016-02-01T12:01:01.1111',
    'unAnsweredComplement': 1,
    'unAnsweredOther': 1,
    'lakare': {
      namn: 'Jan Nilsson'
    },
    'certtype': 'FK7800'
  }];

  // Initialize the controller and a mock scope
  beforeEach(inject(function(_LakarutlatandeModel_, featureService) {
    LakarutlatandeModel = _LakarutlatandeModel_;

    featureService.hasFeature = function() {
      return false;
    };
  }));

  describe('set', function() {
    it('should decorate quickSearch property when set', function() {
      LakarutlatandeModel.set(testJsonData);
      expect(LakarutlatandeModel.get().length).toEqual(testJsonData.length);
      expect(LakarutlatandeModel.get()[0].quickSearchString).toEqual(
          '19360721-7068:79:Förnamn-3607 Efternamn-21-7068:Kvinna:FK7800:M16.0:Primär koxartros, dubbelsidig:B1B2:2016-02-01:Jan Nilsson:');
    });

    it('should decorate show properties when set', inject(function(messageService) {
      LakarutlatandeModel.set(testJsonData);
      expect(LakarutlatandeModel.get().length).toEqual(testJsonData.length);
      expect(LakarutlatandeModel.get()[0].patient.konShow).toEqual(messageService.getProperty('label.gender.female'));
      expect(LakarutlatandeModel.hasError()).toBeFalsy();
    }));
  });

  describe('reset', function() {
    it('should reset correctly', function() {
      LakarutlatandeModel.set(testJsonData);

      expect(LakarutlatandeModel.get().length).toEqual(testJsonData.length);
      LakarutlatandeModel.reset();
      expect(LakarutlatandeModel.get().length).toEqual(0);
      expect(LakarutlatandeModel.hasError()).toBeFalsy();

    });
  });

  describe('setError', function() {
    it('should indicate error when set', function() {
      LakarutlatandeModel.setError();
      expect(LakarutlatandeModel.hasError()).toBeTruthy();

    });
  });

});
