describe('Directive: rhsPatientHistoryTable', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var element;
    var elementScope;

    // Store references to $rootScope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope, _patientHistoryViewState_) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $scope = $rootScope.$new();
        $scope.item = {};

        var sjfMetaData = {};

        // FAKE DATA WHILE BACKEND IS BUILT
        sjfMetaData.vardenheterInomVGMedSparr = [];
        var i = 0;
        for(; i < 5; i++){
            sjfMetaData.vardenheterInomVGMedSparr.push('Vardenhet-med-sparr-' + (i + 1));
        }

        sjfMetaData.andraVardgivareMedSparr = [];
        for(i = 0; i < 3; i++){
            sjfMetaData.andraVardgivareMedSparr.push('Vardgivare-med-sparr-' + (i + 1));
        }

        sjfMetaData.kraverSamtycke = [];
        for(i = 0; i < 3; i++){
            sjfMetaData.kraverSamtycke.push({
                vardenhetId: i + 1,
                vardgivareNamn: 'Vardgivare-som-kraver-samtycke-' + (i + 1),
                includedInSjukfall: false,
                bidrarTillAktivtSjukfall: false,
                loading: false,
                fetched: false
            });
        }

        sjfMetaData.samtyckeFinns = false;

        _patientHistoryViewState_.setSjfMetaData(sjfMetaData);

        element = $compile(' <rhs-patient-history-table history-item="item" index="1" on-select=""/>')($scope);
        $scope.$digest();

        elementScope = element.isolateScope() || element.scope();

    }));

    it('should render correct number of sjukskrivningsgrader', function() {

        // Assert
        expect(elementScope.formatGrader([100])).toEqual('100%');
        expect(elementScope.formatGrader([100,25])).toEqual('100% &#10142; 25%');
        expect(elementScope.formatGrader([100,25,50])).toEqual('100% &#10142; 50%');
        expect(elementScope.formatGrader([100,25,75,50])).toEqual('100% &#10142; 50%');

    });

    it('should render correct number of sjukskrivningsgrader', function() {

        // Assert
        expect(elementScope.formatGrader([100])).toEqual('100%');
        expect(elementScope.formatGrader([100,25])).toEqual('100% &#10142; 25%');
        expect(elementScope.formatGrader([100,25,50])).toEqual('100% &#10142; 50%');
        expect(elementScope.formatGrader([100,25,75,50])).toEqual('100% &#10142; 50%');

    });



});
