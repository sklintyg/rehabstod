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

describe('Service: LakarutlatandeService', function() {
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

  var LakarutlatandeFilterViewState;
  var LakarutlatandeModel;
  var LakarutlatandeService;
  var LakarutlatandeProxy;
  var lakarutlatandeData = [{patient: {namn: 'Hej', kon: null}, signingTimeStamp: '2020-01-01T01:01:01.000'}];
  var messageService;


  describe('Test Lakarutlatande', function() {
    // Initialize the controller and a mock scope
    beforeEach(inject(
        function(_LakarutlatandeModel_, _messageService_, _LakarutlatandeService_, _lakarutlatandenProxy_,
            _LakarutlatandeFilterViewState_, featureService) {
          LakarutlatandeService = _LakarutlatandeService_;
          LakarutlatandeProxy = _lakarutlatandenProxy_;
          LakarutlatandeModel = _LakarutlatandeModel_;
          LakarutlatandeFilterViewState = _LakarutlatandeFilterViewState_;
          messageService = _messageService_;

          LakarutlatandeFilterViewState.reset();

          featureService.hasFeature = function() {
            return false;
          };
        }));

    it('Success', function() {
      spyOn(LakarutlatandeProxy, 'getLakarutlatandenForUnit').and.callFake(function() {
        return {
          then: function(success) {
            success({certificates: lakarutlatandeData, questionAndAnswersError:false});
          }
        };
      });

      spyOn(LakarutlatandeFilterViewState, 'reset');

      LakarutlatandeService.loadLakarutlatande();

      expect(LakarutlatandeFilterViewState.reset).toHaveBeenCalled();
      expect(LakarutlatandeModel.get()).toEqual(lakarutlatandeData);
    });

    it('Failed', function() {
      spyOn(LakarutlatandeProxy, 'getLakarutlatandenForUnit').and.callFake(function() {
        return {
          then: function(success, error) {
            error({error: 401});
          }
        };
      });

      LakarutlatandeService.loadLakarutlatande();

      expect(LakarutlatandeModel.get()).toEqual([]);
    });

    describe('Test reload of Lakarutlatande', function() {
      beforeEach(inject(function() {
        spyOn(LakarutlatandeProxy, 'getLakarutlatandenForUnit').and.callFake(function() {
          return {
            then: function(success) {
              success({certificates: lakarutlatandeData, questionAndAnswersError:false});
            }
          };
        });

        spyOn(LakarutlatandeFilterViewState, 'reset');

        LakarutlatandeService.loadLakarutlatande();

        expect(LakarutlatandeModel.get()).toEqual(lakarutlatandeData);
      }));

      it('Not reload if loaded', function() {
        LakarutlatandeService.loadLakarutlatande();

        expect(LakarutlatandeFilterViewState.reset.calls.count()).toEqual(1);
        expect(LakarutlatandeProxy.getLakarutlatandenForUnit.calls.count()).toEqual(1);
      });

      it('Load and reset if force', function() {
        LakarutlatandeService.loadLakarutlatande(true);

        expect(LakarutlatandeFilterViewState.reset.calls.count()).toEqual(2);
        expect(LakarutlatandeProxy.getLakarutlatandenForUnit.calls.count()).toEqual(2);
      });
    });
  });
});
