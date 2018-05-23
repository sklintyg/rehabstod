/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('rehabstodApp')
    .controller('AboutFaqPageCtrl',
        function($scope, _, messageService, $window, smoothScroll) {
            'use strict';

            function getQuestions(prefix, idPrefix) {
                var questions = [];
                var numberOfQuestions = 1;

                while(hasQuestion(prefix, numberOfQuestions)) {
                    questions.push({
                        id: 'faq-' + idPrefix + '-' + numberOfQuestions,
                        title: prefix + numberOfQuestions + '.title',
                        closed: true,
                        body: prefix + numberOfQuestions + '.body'
                    });

                    numberOfQuestions++;
                }

                return questions;
            }

            function hasQuestion(prefix, index) {
                var key = prefix + index + '.title';

                return messageService.propertyExists(key);
            }

            var faq = [];

            faq.push({
                title: 'Sjukfall',
                icon: 'fa-stethoscope',
                questions: getQuestions('faq.sickness.', 'sickness')
            });

            faq.push({
                title: 'Intyg',
                icon: 'fa-file-text-o',
                questions: getQuestions('faq.certificate.', 'certificate')
            });

            faq.push({
                title: 'Patient',
                icon: 'fa-user-o',
                questions: getQuestions('faq.patient.', 'patient')
            });


            $scope.faq = faq;

            $scope.openAll = function() {
                toggleQuestions(false);
            };

            $scope.closeAll = function() {
                toggleQuestions(true);
            };

            $scope.toggleQuestion = function(question) {
                question.closed = !question.closed;

                if (!question.closed) {
                    var elementToScrollTo = $('#' + question.id);

                    var windowElement = $($window);
                    var windowHeight = windowElement.height() / 2;
                    var bodyElement = $('#rhs-body');
                    var scrollTop = bodyElement.scrollTop();
                    var elementPosition = findElementPosition(elementToScrollTo[0], 'rhs-body');

                    if (elementPosition - scrollTop > windowHeight) {
                        var offset = 100;
                        var options = {
                            duration: 500,
                            easing: 'easeInOutQuart',
                            offset: offset,
                            containerId: 'rhs-body'
                        };

                        //scroll to this questions panel heading, centered vertically
                        smoothScroll(elementToScrollTo[0], options);
                    }
                }
            };

            function findElementPosition(element, containerId) {
                var location = 0;
                do {
                    location += element.offsetTop;
                    element = element.offsetParent;
                    // NOTE: This is a patch made to fix a bug when scrolling within a container. In the container case, we dont wan't to iterate further up than the container wer'e scrolling in!
                } while (element &&  element.id !== containerId);

                return location;
            }

            function toggleQuestions(closed) {
                _.each(faq, function(category) {
                    _.each(category.questions, function(question) {
                        question.closed = closed;
                    });
                });
            }


        });
