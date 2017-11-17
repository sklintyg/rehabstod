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
        function($scope, _) {
            'use strict';


            function getSicknessQuestions() {
                var questions = [];
                var numberOfQuestions = 9;

                for (var i = 1; i <= numberOfQuestions; i++) {
                    questions.push({
                        title: 'faq.sickness.' + i + '.title',
                        closed: true,
                        body: 'faq.sickness.' + i + '.body'
                    });
                }

                return questions;
            }

            function getCertificateQuestions() {
                var questions = [];
                var numberOfQuestions = 3;

                for (var i = 1; i <= numberOfQuestions; i++) {
                    questions.push({
                        title: 'faq.certificate.' + i + '.title',
                        closed: true,
                        body: 'faq.certificate.' + i + '.body'
                    });
                }

                return questions;
            }

            function getPatientQuestions() {
                var questions = [];
                var numberOfQuestions = 1;

                for (var i = 1; i <= numberOfQuestions; i++) {
                    questions.push({
                        title: 'faq.patient.' + i + '.title',
                        closed: true,
                        body: 'faq.patient.' + i + '.body'
                    });
                }

                return questions;
            }

            var faq = [];

            faq.push({
                title: 'Sjukfall',
                icon: 'fa-stethoscope',
                questions: getSicknessQuestions()
            });

            faq.push({
                title: 'Intyg',
                icon: 'fa-file-text-o',
                questions: getCertificateQuestions()
            });

            faq.push({
                title: 'Patient',
                icon: 'fa-user-o',
                questions: getPatientQuestions()
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
