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

angular.module('rehabstodApp').value('mockResponse', {
  'hospOK': {
    'hospInformation': {
      'personalPrescriptionCode': '1234567',
      'specialityNames': ['specialityName1', 'specialityName2'],
      'hsaTitles': ['hsaTitle1', 'hsaTitle2']
    }
  },
  'hospModel': {
    'personalPrescriptionCode': '1234567',
    'specialityNames': ['specialityName1', 'specialityName2'],
    'hsaTitles': ['hsaTitle1', 'hsaTitle2']
  },
  'hospFail': {
    'hospInformation': null
  },
  'hospFailModel': {},
  'userModel': {
    'hsaId': 'IFV1239877878-1049',
    'namn': 'Jan Nilsson',
    'titel': '',
    'authenticationScheme': 'urn:inera:rehabstod:siths:fake',
    'vardgivare': [
      {
        '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
        'id': 'IFV1239877878-1041',
        'namn': 'WebCert-Vårdgivare1',
        'vardenheter': [
          {
            '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
            'id': 'IFV1239877878-1042',
            'namn': 'WebCert-Enhet1',
            'epost': 'enhet1@webcert.invalid.se',
            'postadress': 'Storgatan 1',
            'postnummer': '12345',
            'postort': 'Småmåla',
            'telefonnummer': '0101234567890',
            'arbetsplatskod': '1234567890',
            'start': null,
            'end': null,
            'mottagningar': []
          }
        ]
      }],
    'befattningar': [],
    'valdVardenhet': {
      '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
      'id': 'IFV1239877878-1042',
      'namn': 'WebCert-Enhet1',
      'epost': 'enhet1@webcert.invalid.se',
      'postadress': 'Storgatan 1',
      'postnummer': '12345',
      'postort': 'Småmåla',
      'telefonnummer': '0101234567890',
      'arbetsplatskod': '1234567890',
      'start': null,
      'end': null,
      'mottagningar': []
    }, 'valdVardgivare': {
      '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardgivare',
      'id': 'IFV1239877878-1041',
      'namn': 'WebCert-Vårdgivare1',
      'vardenheter': [
        {
          '@class': 'se.inera.intyg.infra.integration.hsa.model.Vardenhet',
          'id': 'IFV1239877878-1042',
          'namn': 'WebCert-Enhet1',
          'epost': 'enhet1@webcert.invalid.se',
          'postadress': 'Storgatan 1',
          'postnummer': '12345',
          'postort': 'Småmåla',
          'telefonnummer': '0101234567890',
          'arbetsplatskod': '1234567890',
          'start': null,
          'end': null,
          'mottagningar': []
        }]
    },
    'roles': {
      'LAKARE': {
        'name': 'LAKARE',
        'desc': 'Läkare',
        'privileges': []
      }
    },
    'totaltAntalVardenheter': 1
  }
});
