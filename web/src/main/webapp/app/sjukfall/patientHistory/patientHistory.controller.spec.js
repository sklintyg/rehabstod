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

describe('Controller: PatientHistoryController', function() {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  beforeEach(module('htmlTemplates'));

  var sjukfall2017 = {
    'start': '2017-06-22',
    'slut': '2017-12-08',
    'diagnos': {
      'kod': 'J661',
      'beskrivning': 'desc1'
    },
    'intyg': [{'intygsId': '111'}]
  };

  var sjukfall2016 = {
    'start': '2016-02-22',
    'slut': '2017-01-08',
    'diagnos': {
      'kod': 'J662',
      'beskrivning': 'desc2'
    },
    'intyg': [{'intygsId': '222'}]
  };
  var sjukfall2013 = {
    'start': '2013-02-22',
    'slut': '2013-05-08',
    'diagnos': {
      'kod': 'J663',
      'beskrivning': 'Småont i ryggen3'
    },
    'intyg': [{'intygsId': '333'}]
  };

  var scenario1 = {
    sjukfallList: [sjukfall2017, sjukfall2016],
    sjfMetaData: {
      vardenheterInomVGMedSparr: ['Vardenhet 1', 'Vardenhet 2'],
      andraVardgivareMedSparr: ['Vardgivare 1', 'Vardgivare 2'],
      andraVardgivareUtanSparr: ['Vardgivare 1', 'Vardgivare 2'],
      kraverSamtycke: [],
      kraverInteSamtycke: []
    }
  };
  var scenario2 = {
    sjukfallList: [sjukfall2017, sjukfall2013],
    sjfMetaData: {
      vardenheterInomVGMedSparr: ['Vardenhet 1', 'Vardenhet 2'],
      andraVardgivareMedSparr: ['Vardgivare 1', 'Vardgivare 2'],
      andraVardgivareUtanSparr: ['Vardgivare 1', 'Vardgivare 2'],
      kraverSamtycke: [],
      kraverInteSamtycke: []
    }
  };

  var accessToken = {
    accessToken: 'abc123'
  };

  var scope, $httpBackend;

  // Initialize the controller and a mock scope
  beforeEach(inject(
      function($controller, $rootScope, $state, _$httpBackend_, _patientHistoryProxy_, _patientHistoryViewState_, _featureService_) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        _featureService_.hasFeature = function() {
          return false;
        };

        $controller('patientHistoryController', {
          $scope: scope,
          $uibModalInstance: {},
          $state: $state,
          patientHistoryProxy: _patientHistoryProxy_,
          patientHistoryViewState: _patientHistoryViewState_,
          patient: {},
          nyligenAvslutat: false,
          openLU: false,
          certificate: undefined
        });
      }));

  it('should build correct timeline for scenario 1', function() {
    $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario1);
    $httpBackend.expectGET('/api/user/accesstoken').respond(accessToken);

    scope.$digest();
    $httpBackend.flush();

    expect(scope.timeline.length).toBe(2);

  });

  it('should build correct timeline for scenario 2', function() {
    $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario2);
    $httpBackend.expectGET('/api/user/accesstoken').respond(accessToken);

    scope.$digest();
    $httpBackend.flush();

    expect(scope.timeline.length).toBe(3);

  });

  it('should handle load intyg correctly', function() {
    $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario2);
    $httpBackend.expectGET('/api/user/accesstoken').respond(accessToken);

    scope.$digest();
    $httpBackend.flush();

    expect(scope.timeline.length).toBe(3);
    expect(scope.tabs.length).toBe(2);

    scope.loadIntyg({intygsId: '333'});
    scope.$digest();

    expect(scope.tabs.length).toBe(3);

    scope.loadIntyg({intygsId: '333'});
    scope.$digest();
    //Should still be 2
    expect(scope.tabs.length).toBe(3);

  });

});
