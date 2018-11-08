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

        'label.sjukfall.start.selectionpanel.header' : 'Vad kan jag se i Rehabstöd?',
        'label.sjukfall.start.selectionpanel.urval.button' : 'Visa pågående sjukfall',

        // Sjukfall Start Läkare
        'label.sjukfall.start.lakare.header' : 'Översikt över mina pågående sjukfall på ',
        'label.sjukfall.start.lakare.subheader': 'Mina pågående sjukfall på enheten',
        'label.sjukfall.start.lakare.selectionpanel.body' : 'Som läkare kan du ta del av de sjukfall där du själv utfärdat det nuvarande intyget.<br><br>' +
                                                    'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid och sjukskrivningsgrad. Om du har tillgång till flera enheter kan du se dina pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
                                                    'Informationen som visas loggas enligt Patientdatalagen (PDL).',

        // Sjukfall Start Rehab
        'label.sjukfall.start.rehab.header' : 'Översikt över alla pågående sjukfall på ',
        'label.sjukfall.start.rehab.subheader': 'Alla pågående sjukfall på enheten',

        'label.sjukfall.start.rehab.selectionpanel.body' : 'Som rehabkoordinator kan du ta del av alla pågående sjukfall på enheten.<br><br>' +
                                                    'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid, sjukskrivningsgrad och läkare. Om du har tillgång till flera enheter kan du se pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
                                                    'Informationen som visas loggas enligt Patientdatalagen (PDL).',
        'label.sjukfall.start.merstatistik.header': 'Var kan jag hitta mer statistik?',
        'label.sjukfall.start.merstatistik.body': 'Om du vill se mer statistik för din enhet eller på nationell nivå kan du använda Intygsstatistik.<br><br>När du klickar på länken nedan öppnas Intygsstatistik i ett nytt fönster.',

        // Om SRS
        'label.sjukfall.start.srs.header': 'Var kan jag hitta mer information om Stöd för rätt sjukskrivning (SRS)?',
        'label.sjukfall.start.srs.body': 'Om du vill se mer information om SRS, t.ex. hur prediktionen räknas ut, fler åtgärder, mer nationell statistik så kan du gå till SRS webbplats. När du klickar på länken nedan öppnas SRS webbplats i ett nytt fönster.',


        // Sjukfall Stat
        'label.sjukfall.stat.totalt' : 'Antal sjukfall',
        'label.sjukfall.stat.gender' : 'Könsfördelning',
        'label.sjukfall.stat.diagnoses' : 'Diagnosgrupp',
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
        'label.filter.diagnos' : 'Diagnosfilter',
        'label.filter.diagnos.help' : 'Filtrerar på den diagnos som skrivs ut först för sjukfallet uppdelat på kapitel. Diagnoskapitel som saknar data är inte valbara.',
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
        'settings.modal.header' : 'Inställningar',

        'settings.modal.maxantaldagarmellanintyg.title' : 'Antal dagar mellan intyg',
        'settings.modal.maxantaldagarmellanintyg.help' : 'Ställ in det antal dagar det maximalt får vara mellan två intyg för att de ska räknas till samma sjukfall. Välj 0-90 dagar.',
        'settings.modal.maxantaldagarmellanintyg.description' : 'Välj max antal dagars uppehåll mellan intygen:',

        'settings.modal.maxantaldagarsedansjukfallavslut.title' : 'Visa nyligen avslutade sjukfall',
        'settings.modal.maxantaldagarsedansjukfallavslut.help' : 'Välj maximalt antal dagar som får ha passerat efter ett sjukfalls slutdatum för att sjukfallet ska visas upp i sjukfallstabellen. Med denna funktion kan du bevaka de sjukfall som är nyligen avslutade. Välj 0-14 dagar.',
        'settings.modal.maxantaldagarsedansjukfallavslut.description' : 'Välj maximalt antal dagar efter slutdatum som ett sjukfall ska visas:',

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
        'label.table.column.diagnos.intygsvarde': 'Diagnos/diagnoser',
        'label.table.column.diagnos.intygsvarde.help': 'Diagnos/diagnoser i nuvarande intyg. Om det finns flera diagnoser så är den som anges först den som påverkar arbetsförmågan mest. För muspekaren över koden att se vilken diagnos den motsvarar.',
        'label.table.column.start': 'Startdatum',
        'label.table.column.start.help': 'Datum då sjukfallet började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukfall. Max antal dagars uppehåll mellan intyg kan ställas in i inställningar.',
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


        'label.table.no-result.rehab': 'Det finns inga pågående sjukfall på enheten',
        'label.table.no-result.lakare': 'Du har inga pågående sjukfall på enheten',
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
        'modal.pdlconsent.rehabkoordinator.body': 'Väljer du att gå vidare kommer du få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL). <LINK:lasMerOmLoggning>.<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget rent tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
        'modal.pdlconsent.lakare.body': 'Väljer du att gå vidare kommer du få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL). <LINK:lasMerOmLoggning>.<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget rent tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
        'modal.pdlconsent.userapproval': 'Jag förstår och godkänner.',

        // Role switching
        'label.role.switch.notloggedin': 'Du har behörigheten Rehabkoordinator på någon/några av dina enheter. Var uppmärksam om att din roll kommer skifta från Läkare till Rehabkoordinator när du väljer att logga in på en sådan enhet.',
        'label.role.switch.loggedin': 'Du har olika behörigheter på olika enheter. Var uppmärksam om att din roll kan komma att skifta mellan Läkare och Rehabkoordinator beroende på vilken enhet du väljer att byta till.',

        // Patient History Dialog
        'label.patienthistory.loading' : 'Hämtar sjukfall för patient...',

        'label.extradiagnoser.empty': 'Det finns ingenting att visa här.',

        'label.extradiagnoser.sparradinom.title' : 'Spärrad information inom din vårdgivare',
        'label.extradiagnoser.sparradinom' : '<p>Det finns spärrad intygsinformation hos en annan vårdenhet inom din vårdgivare som tillhör det aktuella sjukfallet.</p>Vill du häva spärren tillfälligt kan du göra det med patientens samtycke. Klicka på knappen nedan för att gå vidare.',
        'label.extradiagnoser.sparradinom.list.title': 'Vårdenheter',

        'label.extradiagnoser.osparradandra.title' : 'Ospärrad information hos annan vårdgivare',
        'label.extradiagnoser.osparradandra' : '<p>Det finns ospärrad intygsinformation hos annan vårdgivare som tillhör det aktuella sjukfallet.</p>Du kan klicka nedan för att visa vilka vårdgivare som har denna information. Men patientens samtycke krävs för att du ska kunna ta del av den faktiska intygsinformationen.',

        'label.extradiagnoser.sparradandra.title' : 'Spärrad information hos andra vårdgivare',
        'label.extradiagnoser.sparradandra' : '<p>Det finns spärrad intygsinformation hos andra vårdgivare som tillhör det aktuella sjukfallet.</p>Endast patienten kan få spärren hävd genom att kontakta den enhet där spärren sattes.',
        'label.extradiagnoser.sparradandra.list.title': 'Vårdgivare',

        //Rest 500 exception error messages:
        'server.error.default.title' : 'Ett fel uppstod',
        'server.error.default.text': 'Tyvärr har ett tekniskt problem uppstått i tjänsten. Kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',

        'server.error.getsjukfall.title' : 'Sjukfall för enheten kunde inte hämtas',
        'server.error.changeunit.title' : 'Byte av enhet misslyckades',
        'server.error.changeurval.title' : 'Kunde inte visa sjukfall',
        'server.error.getsummary.text': 'Statistik för enheten kan inte visas',
        'server.error.giveconsent.title' : 'Kunde inte lämna medgivande om PDL-loggning',
        'server.error.loadpatienthistory.text': 'Sjukfallshistorik kan inte visas på grund av ett tekniskt fel. Försök igen om en liten stund. Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',



        // FAQ
        'faq.sickness.1.title' : 'Vad är ett sjukfall?',
        'faq.sickness.1.body' : '<p>Ett sjukfall omfattar alla de elektroniska läkarintyg som utfärdats för en viss patient vid en sjukskrivning, ' +
            'där uppehållet mellan intygens giltighetstider inte överskrider ett angivet maximalt antal dagar. ' +
            'Det maximala antalet dagar är förinställt till 5 dagar, men du kan enkelt ändra det till vilket värde du vill mellan 0 och 90 dagar.</p>' +
            '<p>Exempel: Om max antal dagar är inställt på 5 dagar, och intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti, ' +
            'så räknas de båda intygen till samma sjukfall. Om intyg 2 istället hade varit giltigt från den 21 augusti så skulle intygen ha räknats som två separata sjukfall.</p>',

        'faq.sickness.2.title' : 'Vad är ett pågående sjukfall?',
        'faq.sickness.2.body' : '<p>Med pågående sjukfall menas de sjukfall som har ett giltigt intyg vid inloggningstillfället.</p>',

        'faq.sickness.3.title' : 'Varför ser jag fler sjukfall i <LINK:statistiktjansten> än i Rehabstöd?',
        'faq.sickness.3.body' : '<p>I Intygsstatistik redovisas totalt antal sjukfall för en månad åt gången. ' +
            'Alla sjukfall som pågått någon gång under månaden, även de som har avslutats tidigare under månaden, räknas med där. ' +
            'I Rehabstöd däremot visas bara sjukfall som pågår just nu och som inte har avslutats än.</p>' +
            '<p>Exempel: Det är den 23 mars och i Intygsstatistik ser man idag att en viss enhet har 120 sjukfall under mars månad. ' +
            'I Rehabstöd däremot ser man samma dag att enheten endast har 100 sjukfall. Det beror på att 20 sjukfall har avslutats mellan 1 och 22 mars, ' +
            'och dessa räknas med i Intygsstatistiks statistik för mars men inte i Rehabstöd som bara visar pågående sjukfall.</p>',

        'faq.sickness.4.title' : 'Vad kan jag göra åt sjukfall med felaktigt slutdatum?',
        'faq.sickness.4.body' : '<p>Informationen i Rehabstöd hämtas från de läkarintyg som är utfärdade på enheten. ' +
            'Om läkaren av misstag har angett ett slutdatum väldigt långt fram i tiden kommer sjukfallet att synas i Rehabstöd ända till intyget går ut, ' +
            'eftersom sjukfallet räknas som pågående under den tiden. För att rätta felet finns det två alternativ. ' +
            'Om intygsutfärdaren arbetar i Webcert kan denne ersätta det felaktiga intyget med ett nytt. ' +
            'Annars måste intyget makuleras i journalsystemet och ett nytt intyg skapas.</p>',

        'faq.sickness.5.title' : 'Varför kan jag inte få upp information om vad en diagnoskod i sjukfallstabellen betyder?',
        'faq.sickness.5.body' : '<p>Det finns två olika anledningar till att betydelsen av en diagnoskod inte kan visas när du för muspekaren över den:</p>' +
            '<ol>' +
            '<li>Diagnoskoden som läkaren har angett i intyget är inte giltig och går inte att slå upp i något av kodverken för diagnoser (ICD-10-SE och KSH97-P).</li>' +
            '<li>Läkaren har felaktigt angett flera diagnoskoder i ett och samma fält i intyget och det går därför inte att utläsa och slå upp kodernas betydelse.</li>' +
            '</ol>',

        'faq.sickness.6.title' : 'Varför ser rehabkoordinatorn fler sjukfall i Rehabstöd än vad läkaren kan se?',
        'faq.sickness.6.body' : '<p>Läkaren kan endast se de pågående intyg som läkaren själv utfärdat. Rehabkoordinatorn har behörighet att se samtliga intyg på vårdenheten och tillhörande underenheter.</p>',

        'faq.sickness.7.title' : 'Kan jag se sjukfall från två olika vårdenheter samtidigt?',
        'faq.sickness.7.body' : '<p>Nej, i dagsläget kan man inte det. Det är endast möjligt att se en vårdenhet åt gången. ' +
            'Om vårdenheten har underenheter upplagda i HSA-katalogen visas dock som standard alla sjukfall för dessa enheter samtidigt när man tittar på vårdenheten.</p>',

        'faq.sickness.8.title' : 'Kan jag se intyg från andra vårdgivare eller från andra vårdenheter inom samma vårdgivare?',
        'faq.sickness.8.body' : '<p>Nej, av juridiska skäl är det i dagsläget inte möjligt att se intygsinformation från andra vårdgivare eller från andra vårdenheter inom samma vårdgivare.</p>',

        'faq.sickness.9.title' : 'Kan jag se avslutade sjukfall för en patient?',
        'faq.sickness.9.body' : '<p>Ja, om patienten har ett pågående intyg på din vårdenhet eller om du i rollen som läkare har skrivit det pågående intyget för en patient finns det möjlighet att se alla patientens tidigare sjukfall på din vårdenhet. ' +
            'För att visa patientens sjukfallshistorik klicka på patientens rad i sjukfallstabellen som visas upp under fliken "Pågående sjukfall".</p>',

        'faq.certificate.1.title': 'Vad menas med nuvarande intyg?',
        'faq.certificate.1.body': '<p>Med nuvarande intyg menas det intyg som är giltigt just nu för en patient. ' +
            'I de fall det finns intyg med överlappande giltighetstid väljs det intyg som har det senaste signeringsdatumet.</p>',

        'faq.certificate.2.title': 'Vad gör jag om intyg från en viss läkare inte syns i Rehabstöd?',
        'faq.certificate.2.body': '<ul>' +
            '<li>Kontrollera att läkaren har utfärdat intyget på den vårdenhet du väljer att titta på och att intyget är pågående.</li>' +
            '<li>Ta reda på intygs-id för utfärdat intyg (går att få via journalsystemsleverantören) som inte syns i Rehabstöd, ' +
            'HSA-id för läkaren och HSA-id för enheten på vilken intyget utfärdats. Vänd dig sedan till <LINK:ineraNationellKundservice> för vidare felsökning.</li>' +
            '</ul>',

        'faq.certificate.3.title': 'Kan jag se de fullständiga intygen som sjukfallet består av i Rehabstöd?',
        'faq.certificate.3.body': '<p>Ja, genom att klicka upp patientens sjukfallshistorik kan du välja att "Visa intyg". ' +
            'Det är möjligt att ha flera intyg öppna samtidigt för en patient. Men det går inte att ha intyg för olika patienter öppna samtidigt.</p>',

        'faq.patient.1.title': 'Vad menas med sekretessmarkerad uppgift?',
        'faq.patient.1.body': '<p>Med sekretessmarkerad uppgift menas att Skatteverket har bedömt att patientens personuppgifter är extra viktiga att skydda. ' +
            'Det finns speciella riktlinjer om hur personuppgifter för invånare med sekretessmarkering ska hanteras. I Rehabstöd innebär det att:</p>' +
            '<ul>' +
            '<li>Namnet på patienten har bytts ut till "Sekretessmarkerad uppgift".</li>' +
            '<li>Endast läkare, inloggad på den vårdenhet där denne utfärdade intyget kan se sjukfallet.</li>' +
            '</ul>'

    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
