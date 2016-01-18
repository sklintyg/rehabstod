angular.module('privatlakareApp').service('FormGrundUppgifterViewState',
    function(APP_CONFIG) {
        'use strict';

        this.reset = function() {
            function processListOptions(data) {
                var list = [];
                angular.forEach(data, function(value, key) {
                    this.push({
                        'id':key,
                        'label':value
                    });
                }, list);
                return list;
            }

            this.befattningList = processListOptions(APP_CONFIG.befattningar);
            this.vardformList = processListOptions(APP_CONFIG.vardformer);
            this.verksamhetstypList = processListOptions(APP_CONFIG.verksamhetstyper);
            return this;
        };

        this.reset();
    }
);
