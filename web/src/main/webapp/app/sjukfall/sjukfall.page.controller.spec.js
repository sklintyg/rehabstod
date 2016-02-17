describe('Controller: SjukfallPageCtrl', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var scope;
    var SjukfallModel;
    var SjukfallService;
    var sjukFallData = [{patient: { namn: 'Hej', kon: null }}];

    describe('Load succeeded ', function () {
        // Initialize the controller and a mock scope
        beforeEach(inject(function($controller, $rootScope, $q, _SjukfallModel_, _SjukfallService_) {
            scope = $rootScope.$new();

            SjukfallService = _SjukfallService_;
            SjukfallModel = _SjukfallModel_;

            spyOn(SjukfallService, 'loadSjukfall').and.callFake(function() {
                var promise = $q.defer();

                promise.resolve(sjukFallData);

                return promise.promise;
            });


            $controller('SjukfallPageCtrl', {
                $scope: scope,
                SjukfallModel: SjukfallModel,
                SjukfallService: SjukfallService
            });

            scope.$digest();
        }));


        it('Test load of Sjukfall', function() {
            expect(SjukfallModel.get()).toEqual(sjukFallData);
        });

    });

    describe('Load failed ', function () {
        // Initialize the controller and a mock scope
        beforeEach(inject(function($controller, $rootScope, $q, _SjukfallModel_, _SjukfallService_) {
            scope = $rootScope.$new();

            SjukfallService = _SjukfallService_;
            SjukfallModel = _SjukfallModel_;

            SjukfallModel.set(sjukFallData);

            spyOn(SjukfallService, 'loadSjukfall').and.callFake(function() {
                var promise = $q.defer();

                promise.reject();

                return promise.promise;
            });


            $controller('SjukfallPageCtrl', {
                $scope: scope,
                SjukfallModel: SjukfallModel,
                SjukfallService: SjukfallService
            });

            scope.$digest();
        }));


        it('Test load of Sjukfall', function() {
            expect(SjukfallModel.get()).toEqual([]);
        });

    });
});
