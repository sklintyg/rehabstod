angular.module('privatlakareApp').factory('ModalViewService',
    function($window, $state, $log, $timeout) {
        'use strict';

        function getWindowSize() {
            var docEl = document.documentElement,
                IS_BODY_ACTING_ROOT = docEl && docEl.clientHeight === 0;

            // Used to feature test Opera returning wrong values 
            // for documentElement.clientHeight. 
            function isDocumentElementHeightOff () {
                var d = document,
                    div = d.createElement('div');
                div.style.height = '2500px';
                d.body.insertBefore(div, d.body.firstChild);
                var r = d.documentElement.clientHeight > 2400;
                d.body.removeChild(div);
                return r;
            }

            if (typeof document.clientWidth === 'number') {
                return function () {
                    return { width: document.clientWidth, height: document.clientHeight };
                };
            } else if (IS_BODY_ACTING_ROOT || isDocumentElementHeightOff()) {
                var b = document.body;
                return function () {
                    return { width: b.clientWidth, height: b.clientHeight };
                };
            } else {
                return function () {
                    return { width: docEl.clientWidth, height: docEl.clientHeight };
                };
            }
        }
        
        function _updateModalBodyHeight(modalOptions){
            $timeout(function(){
                var header = angular.element('.modal-header').outerHeight();
                var footer = angular.element('.modal-footer').outerHeight();
                var modalcontent = angular.element('.modal-content').height();
                var modalBodyElement = angular.element('.modal-body');
                var modalHeight = getWindowSize()().height;

                if(header === null || footer === null || modalcontent === null || modalBodyElement === null) {
                    $log.info('content or DOM was not loaded yet. waiting...');
                } else {
                    var modalBody = modalHeight - header - footer - 110;
                    $log.info('header:' + header + ',footer:' + footer + ',modal:' + modalHeight + ',modalcontent:' +
                        modalcontent + ',modalBody:' + modalBody);
                    modalBodyElement.height(modalBody);
                }

                if(modalOptions !== undefined && modalOptions.bodyOverflowY !== undefined){
                    modalBodyElement.css('overflow-y', modalOptions.bodyOverflowY);
                } else {
                    $log.debug('invalid modal options.');
                }
            });
        }

        function _decorateModalScope($scope, $modalInstance) {

            _updateModalBodyHeight($scope.modal);

            $scope.close = function($event){
                if ($event) {
                    $event.preventDefault();
                }
                $modalInstance.close('cancel');
            };

            $scope.cancel = function($event)
            {
                if ($event) {
                    $event.preventDefault();
                }
                $modalInstance.dismiss('cancel');
            };

            $scope.modal.buttonAction = function(index){
                $scope.modal.buttons[index].clickFn($modalInstance, $scope.content);
            };

            var w = angular.element($window);
            w.bind('resize', function() {
                _updateModalBodyHeight($scope.modal);
            });

            $modalInstance.rendered.then(function() {
                $log.info('Modal rendered at: ' + new Date());
                _updateModalBodyHeight($scope.modal);
            }, function() {
                $log.info('Modal failed render? at: ' + new Date());
            });

            $modalInstance.result.then(function () {
                $log.info('Modal closed at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

        return {
            updateModalBodyHeight: _updateModalBodyHeight,
            decorateModalScope: _decorateModalScope
        };
    });
