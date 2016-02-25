describe('Model: SjukfallModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(angular.mock.module('rehabstodApp', function(/*$provide*/) {
        //$provide.value('APP_CONFIG', {});
    }));

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
        'start': '2016-02-01',
        'slut': '2016-03-01',
        'dagar': 84,
        'intyg': 4,
        'grader': [100, 50],
        'aktivGrad': 100,
        'lakare': {
            namn: 'Jan Nilsson'
        }
    }];

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_SjukfallModel_) {
        SjukfallModel = _SjukfallModel_;
    }));

    describe('set', function() {
        it('should decorate quickSearch property when set', function() {
            SjukfallModel.set(testJsonData);
            expect(SjukfallModel.get().length).toEqual(testJsonData.length);
            expect(SjukfallModel.get()[0].quickSearchString).toEqual(
                '19360721-7068:Förnamn-3607 Efternamn-21-7068:79:Kvinna:M16.0:2016-02-01:2016-03-01:84:4:100,50:Jan Nilsson:');
        });

        it('should decorate show properties when set', inject(function (messageService) {
            SjukfallModel.set(testJsonData);
            expect(SjukfallModel.get().length).toEqual(testJsonData.length);
            expect(SjukfallModel.get()[0].dagarShow).toEqual('84 dagar');
            expect(SjukfallModel.get()[0].gradShow).toEqual('<span class="rhs-table-grad-active">100% </span>50% ');
            expect(SjukfallModel.get()[0].patient.konShow).toEqual(messageService.getProperty('label.gender.female'));
        }));
    });

    describe('reset', function() {
        it('should reset correctly', function() {
            SjukfallModel.set(testJsonData);

            expect(SjukfallModel.get().length).toEqual(testJsonData.length);
            SjukfallModel.reset();
            expect(SjukfallModel.get().length).toEqual(0);

        });
    });

});
