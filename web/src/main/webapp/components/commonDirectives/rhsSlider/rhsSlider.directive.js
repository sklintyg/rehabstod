angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    
                },
                controller: function($scope) {
                    $scope.slider = {
                        value: [15, 60],
                        model: [],
                        min: 1,
                        max: 365,
                        step: 1,
                        formatterFn: function(value) {
                            var text;
                            if (value === $scope.slider.model[1]) {
                                text  = 'Till';
                            } else {
                                text = 'Från';
                            }

                            text += '\nDag ' + value;

                            if (value === $scope.slider.max) {
                                text += '+';
                            }

                            return text;
                        }
                    };
                },
                templateUrl: 'components/commonDirectives/rhsSlider/rhsSlider.directive.html'
            };
        }]);


/* jshint ignore:start */
Slider.prototype._layout = function() {
    var positionPercentages;

    if (this.options.reversed) {
        positionPercentages = [100 - this._state.percentage[0],
            this.options.range ? 100 - this._state.percentage[1] : this._state.percentage[1]];
    }
    else {
        positionPercentages = [this._state.percentage[0], this._state.percentage[1]];
    }

    this.handle1.style[this.stylePos] = positionPercentages[0] + '%';
    this.handle1.setAttribute('aria-valuenow', this._state.value[0]);

    this.handle2.style[this.stylePos] = positionPercentages[1] + '%';
    this.handle2.setAttribute('aria-valuenow', this._state.value[1]);

    /* Position ticks and labels */
    if (Array.isArray(this.options.ticks) && this.options.ticks.length > 0) {

        var styleSize = this.options.orientation === 'vertical' ? 'height' : 'width';
        var styleMargin = this.options.orientation === 'vertical' ? 'marginTop' : 'marginLeft';
        var labelSize = this._state.size / (this.options.ticks.length - 1);

        if (this.tickLabelContainer) {
            var extraMargin = 0;
            if (this.options.ticks_positions.length === 0) {
                if (this.options.orientation !== 'vertical') {
                    this.tickLabelContainer.style[styleMargin] = -labelSize / 2 + 'px';
                }

                extraMargin = this.tickLabelContainer.offsetHeight;
            } else {
                /* Chidren are position absolute, calculate height by finding the max offsetHeight of a child */
                for (i = 0; i < this.tickLabelContainer.childNodes.length; i++) {
                    if (this.tickLabelContainer.childNodes[i].offsetHeight > extraMargin) {
                        extraMargin = this.tickLabelContainer.childNodes[i].offsetHeight;
                    }
                }
            }
            if (this.options.orientation === 'horizontal') {
                this.sliderElem.style.marginBottom = extraMargin + 'px';
            }
        }
        for (var i = 0; i < this.options.ticks.length; i++) {

            var percentage = this.options.ticks_positions[i] || this._toPercentage(this.options.ticks[i]);

            if (this.options.reversed) {
                percentage = 100 - percentage;
            }

            this.ticks[i].style[this.stylePos] = percentage + '%';

            /* Set class labels to denote whether ticks are in the selection */
            this._removeClass(this.ticks[i], 'in-selection');
            if (!this.options.range) {
                if (this.options.selection === 'after' && percentage >= positionPercentages[0]) {
                    this._addClass(this.ticks[i], 'in-selection');
                } else if (this.options.selection === 'before' && percentage <= positionPercentages[0]) {
                    this._addClass(this.ticks[i], 'in-selection');
                }
            } else if (percentage >= positionPercentages[0] && percentage <= positionPercentages[1]) {
                this._addClass(this.ticks[i], 'in-selection');
            }

            if (this.tickLabels[i]) {
                this.tickLabels[i].style[styleSize] = labelSize + 'px';

                if (this.options.orientation !== 'vertical' &&
                    this.options.ticks_positions[i] !== undefined) {
                    this.tickLabels[i].style.position = 'absolute';
                    this.tickLabels[i].style[this.stylePos] = percentage + '%';
                    this.tickLabels[i].style[styleMargin] = -labelSize / 2 + 'px';
                } else if (this.options.orientation === 'vertical') {
                    this.tickLabels[i].style['marginLeft'] = this.sliderElem.offsetWidth + 'px';
                    this.tickLabelContainer.style['marginTop'] =
                        this.sliderElem.offsetWidth / 2 * -1 + 'px';
                }
            }
        }
    }

    var formattedTooltipVal;

    if (this.options.range) {
        formattedTooltipVal = this.options.formatter(this._state.value);
        this._setText(this.tooltipInner, formattedTooltipVal);
        this.tooltip.style[this.stylePos] = (positionPercentages[1] + positionPercentages[0]) / 2 + '%';

        if (this.options.orientation === 'vertical') {
            this._css(this.tooltip, 'margin-top', -this.tooltip.offsetHeight / 2 + 'px');
        } else {
            this._css(this.tooltip, 'margin-left', -this.tooltip.offsetWidth / 2 + 'px');
        }

        if (this.options.orientation === 'vertical') {
            this._css(this.tooltip, 'margin-top', -this.tooltip.offsetHeight / 2 + 'px');
        } else {
            this._css(this.tooltip, 'margin-left', -this.tooltip.offsetWidth / 2 + 'px');
        }

        var innerTooltipMinText = this.options.formatter(this._state.value[0]);
        this._setText(this.tooltipInner_min, innerTooltipMinText);

        var innerTooltipMaxText = this.options.formatter(this._state.value[1]);
        this._setText(this.tooltipInner_max, innerTooltipMaxText);

        this.tooltip_min.style[this.stylePos] = positionPercentages[0] + '%';

        if (this.options.orientation === 'vertical') {
            this._css(this.tooltip_min, 'margin-top', -this.tooltip_min.offsetHeight / 2 + 'px');
        } else {
            this._css(this.tooltip_min, 'margin-left', -this.tooltip_min.offsetWidth / 2 + 'px');
        }

        this.tooltip_max.style[this.stylePos] = positionPercentages[1] + '%';

        if (this.options.orientation === 'vertical') {
            this._css(this.tooltip_max, 'margin-top', -this.tooltip_max.offsetHeight / 2 + 'px');
        } else {
            this._css(this.tooltip_max, 'margin-left', -this.tooltip_max.offsetWidth / 2 + 'px');
        }
    } else {
        formattedTooltipVal = this.options.formatter(this._state.value[0]);
        this._setText(this.tooltipInner, formattedTooltipVal);

        this.tooltip.style[this.stylePos] = positionPercentages[0] + '%';
        if (this.options.orientation === 'vertical') {
            this._css(this.tooltip, 'margin-top', -this.tooltip.offsetHeight / 2 + 'px');
        } else {
            this._css(this.tooltip, 'margin-left', -this.tooltip.offsetWidth / 2 + 'px');
        }
    }

    if (this.options.orientation === 'vertical') {
        this.trackLow.style.top = '0';
        this.trackLow.style.height = Math.min(positionPercentages[0], positionPercentages[1]) + '%';

        this.trackSelection.style.top = Math.min(positionPercentages[0], positionPercentages[1]) + '%';
        this.trackSelection.style.height = Math.abs(positionPercentages[0] - positionPercentages[1]) + '%';

        this.trackHigh.style.bottom = '0';
        this.trackHigh.style.height = (100 - Math.min(positionPercentages[0], positionPercentages[1]) -
            Math.abs(positionPercentages[0] - positionPercentages[1])) + '%';
    }
    else {
        this.trackLow.style.left = '0';
        this.trackLow.style.width = Math.min(positionPercentages[0], positionPercentages[1]) + '%';

        this.trackSelection.style.left = Math.min(positionPercentages[0], positionPercentages[1]) + '%';
        this.trackSelection.style.width = Math.abs(positionPercentages[0] - positionPercentages[1]) + '%';

        this.trackHigh.style.right = '0';
        this.trackHigh.style.width = (100 - Math.min(positionPercentages[0], positionPercentages[1]) -
            Math.abs(positionPercentages[0] - positionPercentages[1])) + '%';

        var offset_min = this.tooltip_min.getBoundingClientRect();
        var offset_max = this.tooltip_max.getBoundingClientRect();

        if (this.options.tooltip_position === 'bottom') {
            if (offset_min.right > offset_max.left) {
                this._removeClass(this.tooltip_max, 'bottom');
                this._addClass(this.tooltip_max, 'top');
                this.tooltip_max.style.top = '';
                this.tooltip_max.style.bottom = 22 + 'px';
            } else {
                this._removeClass(this.tooltip_max, 'top');
                this._addClass(this.tooltip_max, 'bottom');
                this.tooltip_max.style.top = this.tooltip_min.style.top;
                this.tooltip_max.style.bottom = '';
            }
        }
        else {
            if (offset_min.right > offset_max.left) {
                this._removeClass(this.tooltip_max, 'top');
                this._addClass(this.tooltip_max, 'bottom');
                this.tooltip_max.style.top = 18 + 'px';
            } else {
                this._removeClass(this.tooltip_max, 'bottom');
                this._addClass(this.tooltip_max, 'top');
                this.tooltip_max.style.top = this.tooltip_min.style.top;
            }
        }
    }
};
/* jshint ignore:end */