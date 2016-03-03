/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

/* jshint maxlen: false, unused: false */
var rhsMessages = {
    'sv': {

        'common.logout': 'Logga ut',
        'common.yes': 'Ja',
        'common.no': 'Nej',
        'common.yes.caps': 'JA',
        'common.no.caps': 'NEJ',
        'common.ok': 'OK',
        'common.cancel': 'Avbryt',
        'common.goback': 'Tillbaka',
        'common.save': 'Spara',
        'common.change': 'Ändra',
        'common.print': 'Skriv ut',
        'common.close': 'Stäng',
        'common.date': 'Datum',
        'common.reset': 'Återställ',

        'common.label.loading': 'Laddar',


        // General form errors

        // Header

        // Start
        'label.start.header': 'Rehabstöd startsida',

        // Sjukfall Start
        // Sjukfall Start Läkare
        'label.sjukfall.start.lakare.header' : 'Dina sjukfall på enhet %0',
        'label.sjukfall.start.lakare.statheader' : 'Översikt - alla dina pågående sjukfall på enheten',
        'label.sjukfall.start.lakare.selectionpanel.header' : 'Endast de sjukfall där jag utfärdat det aktiva intyget',
        'label.sjukfall.start.lakare.selectionpanel.body' : 'Som läkare kan du ta del av de sjukskrivningar där du själv utfärdat det senaste intyget.<br><br>' +
                                                    'När du går vidare och klickar på "Visa mina sjukfall" nedan kommer du att få se de pågående sjukfall där du har utfärdat det senaste intyget för den aktuella enheten. ' +
                                                    'Om du sedan byter enhet kommer du få se dina pågående sjukfall för den enheten istället.',
        'label.sjukfall.start.lakare.selectionpanel.urval.button' : 'Visa mina sjukfall',

        // Sjukfall Start Rehab
        'label.sjukfall.start.rehab.header' : 'Sjukfall på enhet %0',
        'label.sjukfall.start.rehab.statheader' : 'Översikt - alla pågående sjukfall på enheten',
        'label.sjukfall.start.rehab.selectionpanel.header' : 'Alla pågående sjukfall på enheten',
        'label.sjukfall.start.rehab.selectionpanel.body' : 'Som rehabkoordinator tar du del av alla pågående sjukskrivningar på enheten.<br><br>' +
        'När du går vidare och klickar på "Visa alla sjukfall" nedan kommer du att få se alla pågående sjukfall för den aktuella enheten. ' +
        'Om du sedan byter enhet kommer du få se alla pågående sjukfall för den enheten istället.',
        'label.sjukfall.start.rehab.selectionpanel.urval.button' : 'Visa alla sjukfall',



        // Sjukfall Stat
        'label.sjukfall.stat.ongoing' : 'Antalet pågående sjukfall på',
        'label.sjukfall.stat.gender' : 'Könsfördelning',
        'label.sjukfall.stat.male' : 'Män',
        'label.sjukfall.stat.female' : 'Kvinnor',

        // Sjukfall Result
        'label.sjukfall.result.lakare.header': 'Lista på mina pågående sjukfall',
        'label.sjukfall.result.lakare.subheader': ' - En lista över de sjukfall där du utfärdat det senaste intyget',

        'label.sjukfall.result.rehab.header': 'Lista på alla pågående sjukfall',
        'label.sjukfall.result.rehab.subheader': ' - En lista över alla enhetens pågående sjukfall',

        'label.sjukfall.result.back': 'Ändra urval',

        //
        'label.gender.male': 'Man',
        'label.gender.female': 'Kvinna',
        'label.gender.undefined': '-',

        // Table (NOTE: the parts after label.table.column must match sjukfallmodels json, as we use this to get sortorder descriptions)
        'label.table.column.patient.id': 'Person&shy;nummer',
        'label.table.column.patient.namn': 'Namn',
        'label.table.column.patient.konshow': 'Kön',
        'label.table.column.diagnos.intygsvarde': 'Nuvarande diagnos',
        'label.table.column.diagnos.help': 'Huvuddiagnosen i det intyg som är giltigt just nu. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.start': 'Startdatum',
        'label.table.column.start.help': 'Datum då sjukskrivningen började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukskrivning. Max antal dagars uppehåll mellan intyg kan ställas in i filter.',
        'label.table.column.slut': 'Slutdatum',
        'label.table.column.slut.help': 'Slutdatum för det intyg som är giltigt just nu, dvs. det datum då sjukskrivningen beräknas avslutas om den inte förlängs.',
        'label.table.column.aktivgrad': 'Sjukskrivnings&shy;grad',
        'label.table.column.aktivgrad.help': 'Sjukskrivningsgraden i det intyg som är giltigt just nu. Om det innehåller flera grader anges de ordnade i tidsföljd med den just nu gällande graden fetstilsmarkerad.',
        'label.table.column.lakare.namn': 'Nuvarande läkare',
        'label.table.column.lakare.namn.help': 'Läkaren som utfärdat det intyg som är giltigt just nu.',
        'label.table.column.dagar': 'Sjukskrivnings&shy;längd',
        'label.table.column.dagar.help': 'Sjukskrivningens totala längd i dagar, från ”Startdatum vårdenhet” till ”Slutdatum”. Dagar då patienten inte haft något giltigt intyg räknas inte.',

        'label.table.number.of.rows' : 'Visar',
        'label.table.number.of.rows.of' : 'av',
        'label.table.column.sort.desc': 'Fallande',
        'label.table.column.sort.asc': 'Stigande',


        // Filter
        'label.filter.diagnos' : 'Välj nuvarande diagnos',
        'label.filter.lakare' : 'Välj nuvarande läkare',
        'label.filter.langd' : 'Välj sjukskrivningslängd',
        'label.filter.show' : 'Visa sökfilter',
        'label.filter.hide' : 'Dölj sökfilter',
        'label.filter.filter' : 'Filter',
        'label.filter.allselected' : 'Alla valda',

        // Settings
        'label.settings.header' : 'Inställning',
        'label.settings.help' : 'En hjälpande text...',
        'label.settings.info' : 'Max antal dagar uppehåll mellan intyg är satt till:',

        'label.settings.modal.body' : 'Sätt det intervall som du vill att maxantalet dagar mellan två intyg ska vara för att det ska räknas som ett sjukfall och inte två.',
        'label.settings.modal.label' : 'Välj max antal dagars uppehåll mellan två intyg',

        // Export
        'label.export.button' : 'Exportera',
        'label.export.pdf' : 'Exportera sida till PDF',
        'label.export.excel' : 'Exportera sida till Excel'


    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
