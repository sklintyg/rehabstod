angular.module('rehabstodApp').service('SjukfallFilterViewState',
    function() {
        'use strict';


        this.reset = function() {

            this.sjukskrivningslangd =  {
                    high: null,
                    low: null
                };
            this.lakare = null;
            this.diagnos = null;

            return this;
        };

        this.reset();
    }
);