angular.module('rehabstodApp').factory('wcModalService',
    function($rootScope, $timeout, $window, $modal, $templateCache, $http, $q) {
        'use strict';

        function loadTemplates(modal, modalBodyTemplateUrl) {
            function getTemplatePromise(options) {
                return options.template ? $q.when(options.template) :
                    $http.get(angular.isFunction(options.templateUrl) ? (options.templateUrl)() : options.templateUrl,
                        {cache: $templateCache}).then(function (result) {
                            return result.data;
                        });
            }

            var def = $q.defer();
            var templatePromise = def.promise;
            getTemplatePromise({templateUrl:modal.templateUrl}).then(function(modalTemplate){
                getTemplatePromise({templateUrl:modalBodyTemplateUrl}).then(function(modalBody){
                    // the compiling is done in ui bootstrap
                    // so lets just replace the placeholder in the modal templates html with that
                    // of the modal body
                    var res = modalTemplate.replace('<!-- modalBody -->', modalBody);
                    modal.template = res;
                    modal.templateUrl = undefined;
                    def.resolve();
                });
            });

            return templatePromise;
        }

        function createModal($scope, modal) {
            $scope.modal = modal;
            return $modal.open(
                {
                    backdrop: 'static',
                    keyboard: false,
                    modalFade: false,

                    controller: modal.controller,
                    templateUrl: modal.templateUrl,
                    template: modal.template,
                    windowTemplateUrl: modal.windowTemplateUrl,
                    scope: $scope,
                    //size: size,   - overwritten by the extraDlgClass below (use 'modal-lg' or 'modal-sm' if desired)

                    extraDlgClass: modal.extraDlgClass,

                    width: modal.width,
                    height: modal.height,
                    maxWidth: modal.maxWidth,
                    maxHeight: modal.maxHeight,
                    minWidth: modal.minWidth,
                    minHeight: modal.minHeight
                });
        }

        function createBootstrapModalOptions(options) {
            var contentTemplate = 'components/commonDirectives/modal/wcModal.content.html',
                windowTemplate = 'components/commonDirectives/modal/wcModal.window.html';
            var modal = {
                controller: options.controller,
                titleId : options.titleId,
                extraDlgClass : options.extraDlgClass,
                width : options.width,
                height : options.height,
                maxWidth : options.maxWidth,
                maxHeight : options.maxHeight,
                minWidth : options.minWidth,
                minHeight : options.minHeight,
                contentHeight: options.contentHeight,
                contentOverflowY : options.contentOverflowY,
                contentMinHeight : options.contentMinHeight,
                bodyOverflowY: options.bodyOverflowY,
                templateUrl: options.templateUrl === undefined ? contentTemplate : options.templateUrl,
                windowTemplateUrl: options.windowTemplateUrl === undefined ? windowTemplate : options.windowTemplateUrl,
                showClose: options.showClose
            };

            if(modal.bodyOverflowY !== undefined){
                modal.bodyOuterStyle = 'height: 76%;' + 'overflow-y: ' + options.bodyOverflowY + ';';
            }

            if(options.buttons !== undefined && options.buttons.length > 0){
                modal.buttons = [];
                for(var i=0; i< options.buttons.length; i++){
                    var button = options.buttons[i];
                    var className = button.className === undefined ? 'btn-info' : button.className;
                    var mb = {
                        text:button.text,
                        id:button.id,
                        className:className,
                        clickFnName : 'modal.'+ button.name + '()',
                        clickFn : button.clickFn
                    };
                    modal[button.name] = button.clickFn;
                    modal.buttons.push(mb);
                }
            }

            return modal;
        }

        function _open(options, $scope){

            if($scope === undefined) {
                $scope = $rootScope.$new();
            }

            if(options === undefined){
                return;
            }

            var modalInstance = null;
            var bootstrapModalOptions = createBootstrapModalOptions(options);
            loadTemplates(bootstrapModalOptions, options.modalBodyTemplateUrl).then(function(){
                modalInstance = createModal($scope, bootstrapModalOptions);
                options.modalInstance = modalInstance;
            });

            return modalInstance;
        }

        return {
            open: _open
        };
    });
