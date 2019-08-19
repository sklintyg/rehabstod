/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

describe('Model: SjukfallModel', function() {
  'use strict';

  // Load the module and mock away everything that is not necessary.
  beforeEach(module('rehabstodApp'));

  var SjukfallModel;
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
    'start': '2016-02-01',
    'slut': '2016-03-01',
    'dagar': 84,
    'intyg': 4,
    'obesvaradeKompl': 1,
    'grader': [100, 50],
    'aktivGrad': 100,
    'lakare': {
      namn: 'Jan Nilsson'
    }
  }];

  // Initialize the controller and a mock scope
  beforeEach(inject(function(_SjukfallModel_, featureService) {
    SjukfallModel = _SjukfallModel_;

    featureService.hasFeature = function() {
      return false;
    };
  }));

  describe('set', function() {
    it('should decorate quickSearch property when set', function() {
      SjukfallModel.set(testJsonData);
      expect(SjukfallModel.get().length).toEqual(testJsonData.length);
      expect(SjukfallModel.get()[0].quickSearchString).toEqual(
          '19360721-7068:79:Förnamn-3607 Efternamn-21-7068:Kvinna:M16.0:Primär koxartros, dubbelsidig:B1B2:2016-02-01:2016-03-01:84 dagar' +
          ':4:100%,50%:Obesvarad (1):Jan Nilsson:');
    });

    it('should decorate show properties when set', inject(function(messageService) {
      SjukfallModel.set(testJsonData);
      expect(SjukfallModel.get().length).toEqual(testJsonData.length);
      expect(SjukfallModel.get()[0].dagarShow).toEqual('84 dagar');
      expect(SjukfallModel.get()[0].gradShow).toEqual('<span class="rhs-table-grad-active">100% </span> &#10142; 50% ');
      expect(SjukfallModel.get()[0].patient.konShow).toEqual(messageService.getProperty('label.gender.female'));
      expect(SjukfallModel.hasError()).toBeFalsy();
    }));
  });

  describe('reset', function() {
    it('should reset correctly', function() {
      SjukfallModel.set(testJsonData);

      expect(SjukfallModel.get().length).toEqual(testJsonData.length);
      SjukfallModel.reset();
      expect(SjukfallModel.get().length).toEqual(0);
      expect(SjukfallModel.hasError()).toBeFalsy();

    });
  });

  describe('setError', function() {
    it('should indicate error when set', function() {
      SjukfallModel.setError();
      expect(SjukfallModel.hasError()).toBeTruthy();

    });
  });

});
