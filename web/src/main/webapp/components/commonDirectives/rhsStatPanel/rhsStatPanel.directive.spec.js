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

describe('Directive: RhsStatPanel', function() {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('rehabstodApp', function($provide) {
        $provide.value('pieChartBaseConfig', { base: 'base'});
    }));
    beforeEach(module('htmlTemplates'));

    var scope, compile;
    var SjukfallSummaryModel;
    var UserModel;
    var pieChartBaseConfig;
    var messageService;
    var testData = {
        total: 100, hasError: false, genders: [], groups: [], diagnoseGroupData: [], genderData: [], totalData: [{
            name: '',
            y: 100
        }]
    };


    // Initialize the controller and a mock scope
    beforeEach(inject(function($compile, $rootScope, _SjukfallSummaryModel_, _UserModel_,
        _pieChartBaseConfig_, _messageService_) {
        compile = $compile;
        scope = $rootScope.$new();
        SjukfallSummaryModel = _SjukfallSummaryModel_;
        UserModel = _UserModel_;
        pieChartBaseConfig = _pieChartBaseConfig_;
        messageService = _messageService_;

    }));


    it('should extend base piechart config', function() {

        // Arrange
        spyOn(SjukfallSummaryModel, 'get').and.callFake(function() {
            return testData;
        });

        spyOn(UserModel, 'get').and.callFake(function() {
            return {isLakare: true};
        });


        // Act
        var element = compile('<rhs-stat-panel></rhs-stat-panel>')(scope);
        scope.$digest();

        var elementScope = element.isolateScope() || element.scope();

        // Assert
        expect(SjukfallSummaryModel.get).toHaveBeenCalled();
        expect(elementScope.model).toBe(testData);
        expect(elementScope.totalPaEnhetStatConfig).toBeDefined();
        expect(elementScope.genderStatConfig).toBeDefined();
        expect(elementScope.diagnoseStatConfig).toBeDefined();
        expect(elementScope.diagnoseStatConfig.base).toEqual('base');
        expect(elementScope.diagnoseStatConfig.legend.labelFormatter).toBeDefined();
    });



});
