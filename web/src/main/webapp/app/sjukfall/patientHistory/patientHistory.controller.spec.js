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
        'intyg': [ {} ]
    };

    var sjukfall2016 = {
        'start': '2016-02-22',
        'slut': '2017-01-08',
        'diagnos': {
            'kod': 'J662',
            'beskrivning': 'desc2'
        },
        'intyg': [ {} ]
    };
    var sjukfall2013 = {
        'start': '2013-02-22',
        'slut': '2013-05-08',
        'diagnos': {
            'kod': 'J663',
            'beskrivning': 'Småont i ryggen3'
        },
        'intyg': [ {} ]
    };

    var scenario1 = [ sjukfall2017, sjukfall2016 ];
    var scenario2 = [ sjukfall2017, sjukfall2013 ];

    var scope, $httpBackend;

    // Initialize the controller and a mock scope
    beforeEach(inject(function($controller, $rootScope, $state, _$httpBackend_, _patientHistoryProxy_) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $controller('patientHistoryController', {
            $scope: scope,
            $uibModalInstance: {},
            $state: $state,
            patientHistoryProxy: _patientHistoryProxy_,
            patient: {}
        });
    }));

    it('should build correct timeline for scenario 1', function() {
        //TODO: replace expectGET with real path when backend exists
        $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario1);

        scope.$digest();
        $httpBackend.flush();

        expect(scope.timeline.length).toBe(2);
        verifyFirstYear();
        expect(scope.timeline[1].year).toBe(2016);
        expect(scope.timeline[1].selected).toBeUndefined();
        expect(scope.timeline[1].expanded).toBeUndefined();
        expect(scope.timeline[1].isFirstHistorical).toBe(true);
        expect(scope.timeline[1].sjukfall).toEqual(sjukfall2016);

    });

    it('should build correct timeline for scenario 2', function() {
        //TODO: replace expectGET with real path when backend exists
        $httpBackend.expectPOST('/api/sjukfall/patient').respond(scenario2);

        scope.$digest();
        $httpBackend.flush();

        expect(scope.timeline.length).toBe(3);
        verifyFirstYear();
        expect(scope.timeline[1].year).toBe(0);
        expect(scope.timeline[1].sjukfall).toBeUndefined();

        expect(scope.timeline[2].year).toBe(2013);
        expect(scope.timeline[2].selected).toBeUndefined();
        expect(scope.timeline[2].expanded).toBeUndefined();
        expect(scope.timeline[2].isFirstHistorical).toBe(true);
        expect(scope.timeline[2].sjukfall).toEqual(sjukfall2013);

    });

    function verifyFirstYear() {

        expect(scope.timeline[0].year).toBe(2017);
        expect(scope.timeline[0].selected).toBe(true);
        expect(scope.timeline[0].expanded).toBe(true);
        expect(scope.timeline[0].isFirstHistorical).toBe(false);
        expect(scope.timeline[0].sjukfall).toEqual(sjukfall2017);
    }

});
