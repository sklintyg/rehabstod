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
        function($scope, _, messageService) {
            'use strict';

            function getQuestions(prefix) {
                var questions = [];
                var numberOfQuestions = 1;

                while(hasQuestion(prefix, numberOfQuestions)) {
                    questions.push({
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
                questions: getQuestions('faq.certificate.')
            });

            faq.push({
                title: 'Intyg',
                icon: 'fa-file-text-o',
                questions: getQuestions('faq.sickness.')
            });

            faq.push({
                title: 'Patient',
                icon: 'fa-user-o',
                questions: getQuestions('faq.patient.')
            });


            $scope.faq = faq;

            $scope.openAll = function() {
                toggleQuestions(false);
            };

            $scope.closeAll = function() {
                toggleQuestions(true);
            };

            function toggleQuestions(closed) {
                _.each(faq, function(category) {
                    _.each(category.questions, function(question) {
                        question.closed = closed;
                    });
                });
            }


        });
