angular.module('rehabstodApp').service('AppNavViewstate',
    function() {
        'use strict';

        this.reset = function() {
            this.state = {};
            this.state.visningsLage = undefined;
            return this;
        };

        this.get = function() {
            return this.state;
        };

        this.isVisningsLageValt = function() {
            //var value =  ((UserModel.get().role.name === 'LAKARE') && (angular.isUndefined(this.vardenhetsUrval)));
            return !angular.isUndefined(this.state.visningsLage);
        };

        this.setVisningsLage = function(lage) {
            this.state.visningsLage = lage;
        };

        this.reset();
    }
);
