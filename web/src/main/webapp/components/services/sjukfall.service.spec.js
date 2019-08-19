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

describe('Service: SjukfallService', function() {
  'use strict';

  // load the controller's module
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
  var SjukfallModel;
  var SjukfallService;
  var SjukfallProxy;
  var sjukFallData = [{patient: {namn: 'Hej', kon: null}}];
  var messageService;

  describe('Test Sjukfall', function() {
    // Initialize the controller and a mock scope
    beforeEach(inject(
        function(_SjukfallModel_, _messageService_, _SjukfallService_, _SjukfallProxy_, _SjukfallFilterViewState_, featureService) {
          SjukfallService = _SjukfallService_;
          SjukfallProxy = _SjukfallProxy_;
          SjukfallModel = _SjukfallModel_;
          SjukfallFilterViewState = _SjukfallFilterViewState_;
          messageService = _messageService_;

          featureService.hasFeature = function() {
            return false;
          };
        }));

    it('Success', function() {
      spyOn(SjukfallProxy, 'get').and.callFake(function() {
        return {
          then: function(success) {
            success({data: sjukFallData, srsError: false});
          }
        };
      });

      spyOn(SjukfallFilterViewState, 'reset');

      SjukfallService.loadSjukfall();

      expect(SjukfallFilterViewState.reset).toHaveBeenCalled();
      expect(SjukfallModel.get()).toEqual(sjukFallData);
    });

    it('should give correct export parameters', function() {

      spyOn(SjukfallProxy, 'exportResult');

      SjukfallFilterViewState.reset();
      var cfs = SjukfallFilterViewState.getCurrentFilterState();

      var type = 'pdf';
      var personnummer = ['1', '2'];
      var sortState = {
        kolumn: 'patient.namn',
        order: 'desc'
      };

      var expectedQuery = {
        sortering: {
          kolumn: messageService.getProperty('label.table.column.sort.' + sortState.kolumn),
          order: messageService.getProperty('label.table.column.sort.' + sortState.order)
        },
        maxIntygsGlapp: 5,
        fritext: cfs.freeText,
        showPatientId: cfs.showPatientId,
        langdIntervall: {
          min: '1',
          max: '365+'
        },
        aldersIntervall: {
          min: '0',
          max: '100+'
        },
        slutdatum: {
          min: '',
          max: ''
        },
        lakare: cfs.lakare,
        komplettering: null,
        diagnosGrupper: cfs.diagnosKapitel,
        personnummer: personnummer
      };

      SjukfallService.exportResult(type, personnummer, sortState);

      expect(SjukfallProxy.exportResult.calls.count()).toEqual(1);
      expect(SjukfallProxy.exportResult).toHaveBeenCalledWith(type, expectedQuery);
    });

    it('Failed', function() {
      spyOn(SjukfallProxy, 'get').and.callFake(function() {
        return {
          then: function(success, error) {
            error({error: 401});
          }
        };
      });

      SjukfallService.loadSjukfall();

      expect(SjukfallModel.get()).toEqual([]);
    });

    describe('Test reload of sjukfall', function() {
      beforeEach(inject(function() {
        spyOn(SjukfallProxy, 'get').and.callFake(function() {
          return {
            then: function(success) {
              success({data: sjukFallData, srsError: false});
            }
          };
        });

        spyOn(SjukfallFilterViewState, 'reset');

        SjukfallService.loadSjukfall();

        expect(SjukfallModel.get()).toEqual(sjukFallData);
      }));

      it('Not reload if loaded', function() {
        SjukfallService.loadSjukfall();

        expect(SjukfallFilterViewState.reset.calls.count()).toEqual(1);
        expect(SjukfallProxy.get.calls.count()).toEqual(1);
      });

      it('Load and reset if force', function() {
        SjukfallService.loadSjukfall(true);

        expect(SjukfallFilterViewState.reset.calls.count()).toEqual(2);
        expect(SjukfallProxy.get.calls.count()).toEqual(2);
      });
    });
  });
});
