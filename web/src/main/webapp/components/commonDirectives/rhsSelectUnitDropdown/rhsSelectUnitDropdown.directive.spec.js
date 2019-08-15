describe('Directive: RhsSelectUnitDropdown', function() {
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
  beforeEach(inject(function(_$compile_, $rootScope) {
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $compile = _$compile_;
    $scope = $rootScope.$new();

    $scope.setting = {
      value: 'vg1ve2m1',
      typeConfig: {
        vardgivare: [
          {
            id: 'vg1',
            name: 'vg1name',
            vardenheter: [
              {
                id: 'vg1ve1',
                namn: 'vg1ve1name'
              },
              {
                id: 'vg1ve2',
                namn: 'vg1ve2name',
                mottagningar: [
                  {
                    id: 'vg1ve2m1',
                    namn: 'vg1ve2m1name'
                  }
                ]
              }
            ]
          }
        ]
      }
    };

    element =
        $compile('<rhs-select-unit-dropdown value="setting.value" config="setting.typeConfig"/>')($scope);
    $scope.$digest();

    elementScope = element.isolateScope() || element.scope();

  }));

  it('should display initial value selected', function() {

    $scope.$digest();

    //Assert
    expect(elementScope.items.length).toEqual(4);
    expect(elementScope.selectedItem.id).toEqual('vg1ve2m1');
    expect(elementScope.value).toEqual('vg1ve2m1');

    expect(element.find('#rhs-select-unit-dropdown-btn').text()).toContain('vg1ve2m1name');

  });

  it('should display correct title when re-selecting to first "Ingen standardenhet"', function() {

    // Act
    elementScope.onSelectItem(elementScope.items[0]);
    $scope.$digest();

    //Assert
    expect(elementScope.selectedItem.id).toEqual(null);
    expect(elementScope.value).toEqual(null);
    expect(element.find('#rhs-select-unit-dropdown-btn').text()).toContain('Ingen standardenhet');

  });

});
