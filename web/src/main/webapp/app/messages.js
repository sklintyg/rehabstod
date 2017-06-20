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
        'common.approve': 'Godkänn',

        'common.label.loading': 'Laddar',


        // General form errors

        // Header

        // Start
        'label.start.header': 'Rehabstöd startsida',

        // Sjukfall Start
        'label.loading.sjukfallssummary' : 'Hämtar sjukfallsöversikt...',
        // Sjukfall Start Läkare
        'label.sjukfall.start.lakare.header' : 'Översikt över pågående sjukfall på ',
        'label.sjukfall.start.lakare.subheader': 'Mina pågående sjukfall på enheten',
        'label.sjukfall.start.lakare.selectionpanel.header' : 'Vad kan jag se i Rehabstöd?',
        'label.sjukfall.start.lakare.selectionpanel.body' : 'Som läkare kan du ta del av dina pågående sjukfall på den enhet du är inloggad på.<br><br>' +
                                                    'Rehabstöd presenterar även statistik över det nuläge som råder för dina pågående sjukfall, det vill säga antalet sjukskrivna, könsfördelning och diagnosfördelning.<br><br>' +
                                                    'Gå till Pågående sjukfall i menyn eller via länken nedan för att se detaljerad information om dina sjukfall.',
        'label.sjukfall.start.lakare.selectionpanel.urval.button' : 'Visa pågående sjukfall',

        // Sjukfall Start Rehab
        'label.sjukfall.start.rehab.header' : 'Översikt över pågående sjukfall på ',
        'label.sjukfall.start.rehab.subheader': 'Alla pågående sjukfall på enheten',
        'label.sjukfall.start.rehab.selectionpanel.header' : 'Vad kan jag se i Rehabstöd?',
        'label.sjukfall.start.rehab.selectionpanel.body' : 'Som rehabkoordinator kan du ta del av alla pågående sjukfall på den enhet du är inloggad på.<br><br>' +
                                                    'Rehabstöd presenterar även statistik över det nuläge som råder för alla pågående sjukfall, det vill säga antalet sjukskrivna, könsfördelning och diagnosfördelning.<br><br>' +
                                                    'Gå till Pågående sjukfall i menyn eller via länken nedan för att se detaljerad information om sjukfallen på din enhet.',
        'label.sjukfall.start.rehab.selectionpanel.urval.button' : 'Visa pågående sjukfall',



        // Sjukfall Stat
        'label.sjukfall.stat.totalt' : 'Antal sjukfall',
        'label.sjukfall.stat.gender' : 'Könsfördelning',
        'label.sjukfall.stat.diagnoses' : 'Diagnosfördelning',
        'label.sjukfall.stat.sickleavedegree' : 'Sjukskrivningsgrad',
        'label.stat.nosjukfall.rehab' : 'Det finns inga pågående sjukfall på enheten.',
        'label.stat.nosjukfall.lakare' : 'Du har inga pågående sjukfall på enheten.',



        // Sjukfall Result
        'label.sjukfall.result.lakare.header': 'Pågående sjukfall på ',
        'label.sjukfall.result.lakare.subheader': 'Mina pågående sjukfall på enheten',

        'label.sjukfall.result.rehab.header': 'Pågående sjukfall på ',
        'label.sjukfall.result.rehab.subheader': 'Alla pågående sjukfall på enheten',

        'label.sjukfall.result.back': 'Tillbaka till översiktssidan',

        // Filter
        'label.filter.show' : 'Visa sökfilter',
        'label.filter.hide' : 'Dölj sökfilter',
        'label.filter.diagnos' : 'Välj diagnos',
        'label.filter.lakare' : 'Välj läkare',
        'label.filter.langd' : 'Välj sjukskrivningslängd',
        'label.filter.filter' : 'Fritextfilter',
        'label.filter.filter.placeholder' : 'Hitta sjukfall som innehåller...',
        'label.filter.allselected' : 'Alla valda',

        // Settings
        'label.settings.header' : 'Antal dagar mellan intyg',
        'label.settings.help' : 'Ställ in det antal dagar det maximalt får vara mellan två intyg för att de ska räknas till samma sjukfall.',
        'label.settings.info' : 'Max antal dagar uppehåll mellan intyg är satt till:',

        'label.settings.modal.body' : 'Välj hur många dagars uppehåll det maximalt får vara mellan två intyg för att de ska räknas till samma sjukfall.',
        'label.settings.modal.label' : 'Välj max antal dagars uppehåll mellan intygen',



        //
        'label.gender.male': 'Man',
        'label.gender.male.plural': 'Män',
        'label.gender.female': 'Kvinna',
        'label.gender.female.plural': 'Kvinnor',
        'label.gender.undefined': '-',

        // Table (NOTE: the parts after label.table.column must match sjukfallmodels json, as we use this to get sortorder descriptions)
        'label.table.column.patient.id': 'Person&shy;nummer',
        'label.table.column.patient.namn': 'Namn',
        'label.table.column.patient.konshow': 'Kön',
        'label.table.column.diagnos.intygsvarde': 'Diagnos',
        'label.table.column.diagnos.help': 'Huvuddiagnos i nuvarande intyg. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.start': 'Startdatum',
        'label.table.column.start.help': 'Datum då sjukfallet började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukfall. Max antal dagars uppehåll mellan intyg kan ställas in i filtret.',
        'label.table.column.slut': 'Slutdatum',
        'label.table.column.slut.help': 'Slutdatum för sjukfallet, dvs. den sista dagen då det finns ett giltigt intyg.',
        'label.table.column.aktivgrad': 'Sjukskrivnings&shy;grad',
        'label.table.column.aktivgrad.help': 'Sjukskrivningsgrad i nuvarande intyg. Om det innehåller flera grader anges de ordnade i tidsföljd med markering av den just nu gällande graden.',
        'label.table.column.lakare.namn': 'Läkare',
        'label.table.column.lakare.namn.help': 'Läkaren som utfärdat nuvarande intyg. Namnet hämtas från HSA-katalogen. Om det inte går att slå upp läkaren i HSA-katalogen visas bara HSA-id.',
        'label.table.column.dagar': 'Sjukskrivnings&shy;längd',
        'label.table.column.dagar.help': 'Sjukfallets totala längd i dagar, från startdatum till slutdatum. Eventuella dagar mellan intyg räknas inte in.',

        'label.table.no-result': 'Det finns inga pågående sjukfall på ',
        'label.table.no-filter-result': 'Inga sjukfall matchade filtreringen.',

        'label.table.number.of.rows' : 'Visar',
        'label.table.number.of.rows.of' : 'av',
        'label.table.column.sort.desc': 'Fallande',
        'label.table.column.sort.asc': 'Stigande',
        'label.table.diagnosbeskrivning.okand': 'Diagnoskod ${kod} är okänd och har ingen beskrivning',

        // Export
        'label.export.button' : 'Spara som',
        'label.export.pdf' : 'PDF',
        'label.export.excel' : 'Excel',

        // Modal
        'modal.pdlconsent.title': 'Samtycke krävs',
        'modal.pdlconsent.rehabkoordinator.body': '<b>Viktigt!</b><br><br>Väljer du att gå vidare kommer du få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid, sjukskrivningsgrad och läkare.<br><br>Informationen som kommer visas loggas enligt Patientdatalagen (PDL) (läs mer här: <a href="/app.html#/about/rehabstod" target="_blank">Loggning</a>).<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker.<br><br>Detta godkännande gäller för alla enheter som du har behörighet till<br><br>Detta godkännande kommer endast krävas en gång.',
        'modal.pdlconsent.lakare.body': '<b>Viktigt!</b><br><br>Väljer du att gå vidare kommer du få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid och sjukskrivningsgrad.<br><br>Informationen som kommer visas loggas enligt Patientdatalagen (PDL) (läs mer här: <a href="/app.html#/about/rehabstod" target="_blank">Loggning</a>).<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. <br><br>Detta godkännande gäller för alla enheter som du har behörighet till<br><br>Detta godkännande kommer endast krävas en gång.',
                                                                                                                                                                                                                                                                                        

        //Rest 500 exception error messages:
        'server.error.default.title' : 'Ett fel uppstod',
        'server.error.default.text': 'Tyvärr har ett tekniskt problem uppstått i tjänsten. Kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',

        'server.error.getsjukfall.title' : 'Sjukfall för enheten kunde inte hämtas',
        'server.error.changeunit.title' : 'Byte av enhet misslyckades',
        'server.error.changeurval.title' : 'Kunde inte visa sjukfall',
        'server.error.getsummary.text': 'Statistik för enheten kan inte visas',
        'server.error.giveconsent.title' : 'Kunde inte lämna medgivande om PDL-loggning'



    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
