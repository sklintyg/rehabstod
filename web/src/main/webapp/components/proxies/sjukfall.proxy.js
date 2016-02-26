angular.module('rehabstodApp').factory('SjukfallProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig, $window, $httpParamSerializer, StringHelper) {
        'use strict';

        var timeout = networkConfig.defaultTimeout;

        /*
         * Get statistic for selected Vardenhet
         */
        function _get(query) {

            var promise = $q.defer();

            var restPath = '/api/sjukfall';
            $http.post(restPath, query, {timeout: timeout}).success(function(data) {
                if(!ObjectHelper.isDefined(data)) {
                    promise.reject({ errorCode: data, message: 'invalid data'});
                } else {
                    promise.resolve(data);
                }
            }).error(function(data, status) {
                $log.error('error ' + status);
                // Let calling code handle the error of no data response
                if(data === null) {
                    promise.reject({errorCode: data, message: 'no response'});
                } else {
                    promise.reject(data);
                }
            });

            return promise.promise;
        }

        function _exportResult(type, query) {

            var restPath = '/api/sjukfall/' + type;
            var inputs = '';
            
            inputs+='<input type="hidden" name="langdIntervall.max" value="' + query.langdIntervall.max + '" />';
            inputs+='<input type="hidden" name="langdIntervall.min" value="' + query.langdIntervall.min + '" />';
            inputs+='<input type="hidden" name="sortering.kolumn" value="' + query.sortering.kolumn + '" />';
            inputs+='<input type="hidden" name="sortering.order" value="' + query.sortering.order + '" />';
            delete query.langdIntervall;
            delete query.sortering;

            var data = $httpParamSerializer(query);
            //split params into form inputs

            angular.forEach(data.split('&'), function(item){
                var pair = item.split('=');
                inputs+='<input type="hidden" name="' + decodeURIComponent(pair[0]) + '" value="' +
                    StringHelper.replaceAll(decodeURIComponent(pair[1]), '+', ' ') + '" />';
            });

            //send request
            $window.jQuery('<form action="'+ restPath +'" target="_blank" method="post">'+inputs+'</form>')
                .appendTo('body').submit().remove();
        }

        // Return public API for the service
        return {
            get: _get,
            exportResult: _exportResult
        };
    });
