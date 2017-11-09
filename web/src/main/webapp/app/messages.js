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
        'label.sjukfall.start.lakare.header' : 'Översikt över mina pågående sjukfall på ',
        'label.sjukfall.start.lakare.subheader': 'Mina pågående sjukfall på enheten',
        'label.sjukfall.start.lakare.selectionpanel.header' : 'Vad kan jag se i Rehabstöd?',
        'label.sjukfall.start.lakare.selectionpanel.body' : 'Som läkare kan du ta del av de sjukfall där du själv utfärdat det nuvarande intyget.<br><br>' +
                                                    'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid och sjukskrivningsgrad. Om du har tillgång till flera enheter kan du se dina pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
                                                    'Informationen som visas loggas enligt Patientdatalagen (PDL).',
        'label.sjukfall.start.lakare.selectionpanel.urval.button' : 'Visa pågående sjukfall',

        // Sjukfall Start Rehab
        'label.sjukfall.start.rehab.header' : 'Översikt över alla pågående sjukfall på ',
        'label.sjukfall.start.rehab.subheader': 'Alla pågående sjukfall på enheten',
        'label.sjukfall.start.rehab.selectionpanel.header' : 'Vad kan jag se i Rehabstöd?',
        'label.sjukfall.start.rehab.selectionpanel.body' : 'Som rehabkoordinator kan du ta del av alla pågående sjukfall på enheten.<br><br>' +
                                                    'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid, sjukskrivningsgrad och läkare. Om du har tillgång till flera enheter kan du se pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
                                                    'Informationen som visas loggas enligt Patientdatalagen (PDL).',
        'label.sjukfall.start.rehab.selectionpanel.urval.button' : 'Visa pågående sjukfall',
        'label.sjukfall.start.merstatistik.header': 'Var kan jag hitta mer statistik?',
        'label.sjukfall.start.merstatistik.body': 'Om du vill se mer statistik för din enhet eller på nationell nivå kan du använda Statistiktjänsten.<br><br>När du klickar på länken nedan öppnas Statistiktjänsten i ett nytt fönster.',

        // Om SRS
        'label.sjukfall.start.srs.header': 'Var kan jag hitta mer information om Stöd för rätt sjukskrivning (SRS)?',
        'label.sjukfall.start.srs.body': 'Om du vill se mer information om SRS, t.ex. hur prediktionen räknas ut, fler åtgärder, mer nationell statistik så kan du gå till SRS webbplats. När du klickar på länken nedan öppnas SRS webbplats i ett nytt fönster.',


        // Sjukfall Stat
        'label.sjukfall.stat.totalt' : 'Antal sjukfall',
        'label.sjukfall.stat.gender' : 'Könsfördelning',
        'label.sjukfall.stat.diagnoses' : 'Diagnosfördelning',
        'label.sjukfall.stat.sickleavedegree' : 'Sjukskrivningsgrad',
        'label.stat.nosjukfall.rehab' : 'Det finns inga pågående sjukfall på enheten.',
        'label.stat.nosjukfall.lakare' : 'Du har inga pågående sjukfall på enheten.',



        // Sjukfall Result
        'label.sjukfall.result.lakare.header': 'Mina pågående sjukfall på ',
        'label.sjukfall.result.lakare.subheader': 'Mina pågående sjukfall på enheten',

        'label.sjukfall.result.rehab.header': 'Alla pågående sjukfall på ',
        'label.sjukfall.result.rehab.subheader': 'Alla pågående sjukfall på enheten',

        'label.sjukfall.result.back': 'Tillbaka till översiktssidan',

        // Filter
        'label.filter.show' : 'Visa sökfilter',
        'label.filter.hide' : 'Dölj sökfilter',
        'label.filter.diagnos' : 'Huvuddiagnosfilter',
        'label.filter.diagnos.help' : 'Filtrerar på huvuddiagnos uppdelat på kapitel. Diagnoskapitel som saknar data är inte valbara.',
        'label.filter.lakare' : 'Välj läkare',
        'label.filter.lakare.help' : 'Filtrerar på den läkare som har utfärdat det aktiva intyget. Endast läkare som utfärdat aktiva intyg visas i listan.',
        'label.filter.langd' : 'Välj sjukskrivningslängd',
        'label.filter.langd.help' : 'Filtrerar på total längd för det sjukfall som det aktiva intyget ingår i.',
        'label.filter.alder' : 'Välj åldersspann',
        'label.filter.alder.help' : 'Filtrerar på patientens nuvarande ålder.',
        'label.filter.slutdatum' : 'Välj slutdatum',
        'label.filter.slutdatum.help' : 'Filtrerar på slutdatum för det sjukfall som det aktiva intyget ingår i. Det är möjligt att välja ett intervall genom att klicka på två olika datum, eller ett enskilt datum genom att klicka på samma datum två gånger.',
        'label.filter.filter' : 'Fritextfilter',
        'label.filter.filter.help' : 'Filtrerar på all synlig text i tabellen.',
        'label.filter.filter.placeholder' : 'Hitta sjukfall som innehåller...',
        'label.filter.personuppgifter': 'Visa personuppgifter',
        'label.filter.personuppgifter.help': 'Visar eller döljer patienternas namn och personnummer i tabellen. ',
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
        'label.table.column.patient.alder': 'Ålder',
        'label.table.column.patient.konshow': 'Kön',
        'label.table.column.diagnos.intygsvarde': 'Diagnos',
        'label.table.column.diagnos.intygsvarde.help': 'Huvuddiagnos i nuvarande intyg. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.bidiagnosershow': 'Bidiagnoser',
        'label.table.column.bidiagnosershow.help': 'Bidiagnos(er) i nuvarande intyg. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.start': 'Startdatum',
        'label.table.column.start.help': 'Datum då sjukfallet började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukfall. Max antal dagars uppehåll mellan intyg kan ställas in i filtret.',
        'label.table.column.slut': 'Slutdatum',
        'label.table.column.slut.help': 'Slutdatum för sjukfallet, dvs. den sista dagen då det finns ett giltigt intyg.',
        'label.table.column.aktivgrad': 'Grad',
        'label.table.column.aktivgrad.help': 'Sjukskrivningsgrad i nuvarande intyg. Om det innehåller flera grader anges de ordnade i tidsföljd med markering av den just nu gällande graden.',
        'label.table.column.lakare.namn': 'Läkare',
        'label.table.column.lakare.namn.help': 'Läkaren som utfärdat nuvarande intyg. Namnet hämtas från HSA-katalogen. Om det inte går att slå upp läkaren i HSA-katalogen visas bara HSA-id.',
        'label.table.column.dagar': 'Längd',
        'label.table.column.dagar.help': 'Sjukfallets totala längd i dagar, från startdatum till slutdatum. Eventuella dagar mellan intyg räknas inte in.',
        'label.table.column.intyg': 'Antal',
        'label.table.column.intyg.help': 'Antalet intyg som ingår i sjukfallet.',
        'label.table.column.risksignal.riskkategori': 'Risk',
        'label.table.column.risksignal.riskkategori.help': 'Risksignalen för att sjukfallet varar mer än 90 dagar beräknas med matematiska metoder tillämpade på en rad variabler som till exempel ålder, kön, bostadsort och tidigare vårdkontakter för att försöka detektera om den aktuella individens risk skiljer sig från andra patienter inom samma diagnosgrupp. Metoden ska ses som ett komplement inför den egna professionella bedömningen.',


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
        'modal.pdlconsent.rehabkoordinator.body': '<b>Viktigt!</b><br><br>Väljer du att gå vidare kommer du få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL) (läs mer om loggning här: <a href="/#/app/about/rehabstod" class="external-link" rel="nofollow">Vad är Rehabstöd?</a>).<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
        'modal.pdlconsent.lakare.body': '<b>Viktigt!</b><br><br>Väljer du att gå vidare kommer du få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL) (läs mer om loggning här: <a href="/#/app/about/rehabstod" class="external-link" rel="nofollow">Vad är Rehabstöd?</a>).<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
        'modal.pdlconsent.userapproval': 'Jag förstår och godkänner.',

        // Patient History Dialog
        'label.patienthistory.loading' : 'Hämtar sjukfall för patient...',

        //Rest 500 exception error messages:
        'server.error.default.title' : 'Ett fel uppstod',
        'server.error.default.text': 'Tyvärr har ett tekniskt problem uppstått i tjänsten. Kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',

        'server.error.getsjukfall.title' : 'Sjukfall för enheten kunde inte hämtas',
        'server.error.changeunit.title' : 'Byte av enhet misslyckades',
        'server.error.changeurval.title' : 'Kunde inte visa sjukfall',
        'server.error.getsummary.text': 'Statistik för enheten kan inte visas',
        'server.error.giveconsent.title' : 'Kunde inte lämna medgivande om PDL-loggning',
        'server.error.loadpatienthistory.text': 'Sjukfallshistorik kan inte visas på grund av ett tekniskt fel. Försök igen om en liten stund. Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.'



    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
