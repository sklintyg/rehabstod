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
        'intyg': [ {'intygsId':'111'} ]
    };

    var sjukfall2016 = {
        'start': '2016-02-22',
        'slut': '2017-01-08',
        'diagnos': {
            'kod': 'J662',
            'beskrivning': 'desc2'
        },
        'intyg': [ {'intygsId':'222'} ]
    };
    var sjukfall2013 = {
        'start': '2013-02-22',
        'slut': '2013-05-08',
        'diagnos': {
            'kod': 'J663',
            'beskrivning': 'Småont i ryggen3'
        },
        'intyg': [ {'intygsId':'333'} ]
    };

    var scenario1 = [ sjukfall2017, sjukfall2016 ];
    var scenario2 = [ sjukfall2017, sjukfall2013 ];

    var scope, $httpBackend;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, $state, _$httpBackend_, _patientHistoryProxy_, _patientHistoryViewState_) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $controller('patientHistoryController', {
            $scope: scope,
            $uibModalInstance: {},
            $state: $state,
            patientHistoryProxy: _patientHistoryProxy_,
            patientHistoryViewState: _patientHistoryViewState_,
            patient: {}
        });
    }));

    it('should build correct timeline for scenario 1', function() {
        $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario1);

        scope.$digest();
        $httpBackend.flush();

        expect(scope.timeline.length).toBe(2);


    });

    it('should build correct timeline for scenario 2', function() {
        $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario2);

        scope.$digest();
        $httpBackend.flush();

        expect(scope.timeline.length).toBe(3);


    });

    it('should handle load intyg correctly', function() {
        $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario2);

        scope.$digest();
        $httpBackend.flush();

        expect(scope.timeline.length).toBe(3);
        expect(scope.tabs.length).toBe(1);

        scope.loadIntyg({intygsId: '333'});
        scope.$digest();

        expect(scope.tabs.length).toBe(2);

        scope.loadIntyg({intygsId: '333'});
        scope.$digest();
        //Should still be 2
        expect(scope.tabs.length).toBe(2);

    });



});
