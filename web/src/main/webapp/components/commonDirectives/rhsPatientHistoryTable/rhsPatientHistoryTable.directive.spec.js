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
        for(; i < 20; i++){
            sjfMetaData.vardenheterInomVGMedSparr.push('Vardenhetmedlangtnamnochmassaandraproblem ' + (i + 1));
        }

        sjfMetaData.andraVardgivareMedSparr = [];
        for(i = 0; i < 20; i++){
            sjfMetaData.andraVardgivareMedSparr.push('Vardgivaremedlangtnamnochmassaandraproblem ' + (i + 1));
        }

        sjfMetaData.samtyckeFinns = [];
        for(i = 0; i < 20; i++){
            sjfMetaData.samtyckeFinns.push({
                id: i + 1,
                name: 'Vardgivaremedlangtnamnochmassaandraproblem ' + (i + 1),
                loading: false,
                fetched: false
            });
        }
        sjfMetaData.samtyckeSaknas = [];
        for(i = 0; i < 20; i++){
            sjfMetaData.samtyckeSaknas.push({
                id: i + 1,
                name: 'Vardgivaremedlangtnamnochmassaandraproblem ' + (i + 1),
                loading: false,
                fetched: false
            });
        }

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



});
