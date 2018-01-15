/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
describe('Viewstate: patientHistoryViewState', function() {
    'use strict';

    var viewState;
    // load the controller's module
    beforeEach(module('rehabstodApp'));

    var sjukfall2017 = {
        'start': '2017-06-22',
        'slut': '2017-12-08',
        'diagnos': {
            'kod': 'J661',
            'beskrivning': 'desc1'
        },
        'intyg': [ {} ]
    };

    var sjukfall2016 = {
        'start': '2016-02-22',
        'slut': '2017-01-08',
        'diagnos': {
            'kod': 'J662',
            'beskrivning': 'desc2'
        },
        'intyg': [ {} ]
    };
    var sjukfall2013 = {
        'start': '2013-02-22',
        'slut': '2013-05-08',
        'diagnos': {
            'kod': 'J663',
            'beskrivning': 'Småont i ryggen3'
        },
        'intyg': [ {} ]
    };

    var scenario1 = [ sjukfall2017, sjukfall2016 ];
    var scenario2 = [ sjukfall2017, sjukfall2013 ];

    // Initialize the service
    beforeEach(inject(function($filter, _patientHistoryViewState_) {
        viewState = _patientHistoryViewState_;
    }));

    it('should build correct timeline for scenario 1', function() {
        viewState.setTimelineItems(scenario1);

        var timeline = viewState.getTimelineItems();
        expect(timeline.length).toBe(2);
        verifyFirstYear(timeline[0]);
        expect(timeline[1].year).toBe(2016);
        expect(timeline[1].selected).toBe(false);
        expect(timeline[1].expanded).toBe(false);
        expect(timeline[1].isFirstHistorical).toBe(true);
        expect(timeline[1].sjukfall).toEqual(sjukfall2016);

    });

    it('should build correct timeline for scenario 2', function() {

        viewState.setTimelineItems(scenario2);

        var timeline = viewState.getTimelineItems();

        expect(timeline.length).toBe(3);
        verifyFirstYear(timeline[0]);
        expect(timeline[1].year).toBe(0);
        expect(timeline[1].sjukfall).toBeUndefined(); // gap item

        expect(timeline[2].year).toBe(2013);
        expect(timeline[2].selected).toBe(false);
        expect(timeline[2].expanded).toBe(false);
        expect(timeline[2].isFirstHistorical).toBe(true);
        expect(timeline[2].sjukfall).toEqual(sjukfall2013);

        //verify selection
        viewState.selectTimelineItem(timeline[2]);
        expect(timeline[0].selected).toBe(false);
        expect(timeline[2].selected).toBe(true);


    });

    function verifyFirstYear(item) {

        expect(item.year).toBe(2017);
        expect(item.selected).toBe(true);
        expect(item.expanded).toBe(true);
        expect(item.isFirstHistorical).toBe(false);
        expect(item.sjukfall).toEqual(sjukfall2017);
    }

    it('should handle getTabById correctly', function() {
        viewState.reset();
        viewState.addTab('intygsId', 'title', false, false);
        expect(viewState.getTabById('intygsId')).toEqual(viewState.getTabs()[0]);

    });

    it('should handle selectTab correctly', function() {
        viewState.reset();
        viewState.addTab('', 'title', true, true);
        viewState.addTab('intygsId', 'title', false, false);

        expect(viewState.getTabs().length).toEqual(2);

        viewState.selectTab(viewState.getTabs()[1]);

        expect(viewState.getTabs()[0].active).toBe(false);
        expect(viewState.getTabs()[1].active).toBe(true);

        viewState.selectTab(viewState.getTabs()[0]);

        expect(viewState.getTabs()[0].active).toBe(true);
        expect(viewState.getTabs()[1].active).toBe(false);

    });

    it('should handle close tab correctly', function() {
        viewState.reset();
        viewState.addTab('', 'title', true, true);
        viewState.addTab('intygsId', 'title', false, false);

        expect(viewState.getTabs().length).toEqual(2);

        viewState.selectTab(viewState.getTabs()[1]);

        expect(viewState.getTabs()[0].active).toBe(false);
        expect(viewState.getTabs()[1].active).toBe(true);

        viewState.closeTab(viewState.getTabs()[1]);

        expect(viewState.getTabs().length).toEqual(1);
        expect(viewState.getTabs()[0].active).toBe(true);

    });


});
