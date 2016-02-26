describe('Viewstate: SjukfallFilterViewState', function() {
    'use strict';
    // Load the module and mock away everything that is not necessary.
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
    var DiagnosKapitelModel;
    var LakareModel;

    // Inject dependencies and mocks
    beforeEach(inject(function(_SjukfallFilterViewState_, _DiagnosKapitelModel_, _LakareModel_) {
        SjukfallFilterViewState = _SjukfallFilterViewState_;
        DiagnosKapitelModel = _DiagnosKapitelModel_;
        LakareModel = _LakareModel_;


    }));

    describe('get', function() {
        it('should return initial state initially', function() {
            expect(SjukfallFilterViewState.get().diagnosKapitelModel.getSelected()).toEqual([]);
            expect(SjukfallFilterViewState.get().lakareModel.getSelected()).toEqual([]);
            expect(SjukfallFilterViewState.get().sjukskrivningslangdModel).toEqual([1, 366]);
            expect(SjukfallFilterViewState.get().freeTextModel).toEqual('');
        });
    });

    describe('getCurrentFilterState', function() {
        it('should return correct state', function() {
            //Arrange
            DiagnosKapitelModel.get()[0].selected = true;
            LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
            LakareModel.get()[0].selected = true;
            SjukfallFilterViewState.get().freeTextModel = 'anything';
            SjukfallFilterViewState.get().sjukskrivningslangdModel = [22, 77];
            SjukfallFilterViewState.get().glapp = 55;


            var expectedFilter = {
                diagnosKapitel: [DiagnosKapitelModel.get()[0].id],
                lakare: [LakareModel.get()[0].id],
                sjukskrivningslangd: SjukfallFilterViewState.get().sjukskrivningslangdModel,
                freeText: SjukfallFilterViewState.get().freeTextModel,
                glapp: 55
            };
            //Verify

            expect(SjukfallFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
        });
    });

    describe('reset', function() {
        it('should return correct reset selections when reset', function() {
            //Arrange
            DiagnosKapitelModel.get()[0].selected = true;
            LakareModel.set(['Dr Jan Itor', 'Mr Nobody']);
            LakareModel.get()[0].selected = true;
            SjukfallFilterViewState.get().freeTextModel = 'anything';
            SjukfallFilterViewState.get().sjukskrivningslangdModel = [22, 77];
            SjukfallFilterViewState.get().glapp = 55;

            var expectedFilter = {
                diagnosKapitel: [],
                lakare: [],
                sjukskrivningslangd: [1, null],
                freeText: '',
                glapp: 55
            };

            //Act
            SjukfallFilterViewState.reset();

            //Verify
            expect(SjukfallFilterViewState.getCurrentFilterState()).toEqual(expectedFilter);
        });
    });


});
