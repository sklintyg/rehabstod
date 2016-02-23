angular.module('rehabstodApp').factory('SjukfallProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig, $window) {
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

        function _download(url, data) {
            if( url && data ){
                //data can be string of parameters or array/object
                data = typeof data === 'string' ? data : $window.jQuery.param(data);
                //split params into form inputs
                var inputs = '';
                $window.jQuery.each(data.split('&'), function(){
                    var pair = this.split('=');
                    inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
                });
                //send request
                $window.jQuery('<form action="'+ url +'" method="post">'+inputs+'</form>')
                    .appendTo('body').submit().remove();
            }
        }

        function _exportResult(type, query) {

            var restPath = '/api/sjukfall/' + type;

            _download(restPath, query);
        }

        // Return public API for the service
        return {
            get: _get,
            exportResult: _exportResult
        };
    });
