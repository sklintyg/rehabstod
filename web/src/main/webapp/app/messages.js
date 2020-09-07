/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
    'common.approve': 'Godkänn',
    'common.reset': 'Återställ',

    'common.label.loading': 'Laddar',

    // General form errors

    // Header

    // Start
    'label.start.header': 'Rehabstöd startsida',

    // Sjukfall Start
    'label.loading.sjukfallssummary': 'Hämtar sjukfallsöversikt...',

    'label.sjukfall.start.selectionpanel.header': 'Vad kan jag se i Rehabstöd?',
    'label.sjukfall.start.selectionpanel.urval.button': 'Visa pågående sjukfall',

    // Sjukfall Start Läkare
    'label.sjukfall.start.lakare.header': 'Översikt över mina pågående sjukfall på ',
    'label.sjukfall.start.lakare.subheader': 'Mina pågående sjukfall på enheten',
    'label.sjukfall.start.lakare.selectionpanel.body': 'Som läkare kan du ta del av de sjukfall där du själv utfärdat det nuvarande intyget.<br><br>' +
        'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid och sjukskrivningsgrad. Om du har tillgång till flera enheter kan du se dina pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
        'Informationen som visas loggas enligt Patientdatalagen (PDL).',

    // Sjukfall Start Rehab
    'label.sjukfall.start.rehab.header': 'Översikt över alla pågående sjukfall på ',
    'label.sjukfall.start.rehab.subheader': 'Alla pågående sjukfall på enheten',

    'label.sjukfall.start.rehab.selectionpanel.body': 'Som rehabkoordinator kan du ta del av alla pågående sjukfall på enheten.<br><br>' +
        'När du klickar på "Visa pågående sjukfall" nedan kommer du att få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid, sjukskrivningsgrad och läkare. Om du har tillgång till flera enheter kan du se pågående sjukfall för en annan enhet genom att byta enhet i sidhuvudet.<br><br>' +
        'Informationen som visas loggas enligt Patientdatalagen (PDL).',
    'label.sjukfall.start.merstatistik.header': 'Var kan jag hitta mer statistik?',
    'label.sjukfall.start.merstatistik.body': 'Om du vill se mer statistik för din enhet eller på nationell nivå kan du använda Intygsstatistik.<br><br>När du klickar på länken nedan öppnas Intygsstatistik i en ny flik, och du blir automatiskt inloggad om du har giltig behörighet till Intygsstatistik.',

    // Om SRS
    'label.sjukfall.start.srs.header': 'Var kan jag hitta mer information om Stöd för rätt sjukskrivning (SRS)?',
    'label.sjukfall.start.srs.body': 'Om du vill se mer information om SRS, t.ex. hur prediktionen räknas ut, fler åtgärder, mer nationell statistik så kan du gå till SRS webbplats. När du klickar på länken nedan öppnas SRS webbplats i ett nytt fönster.',

    // Sjukfall Stat
    'label.sjukfall.stat.totalt': 'Antal sjukfall',
    'label.sjukfall.stat.gender': 'Könsfördelning',
    'label.sjukfall.stat.diagnoses': 'Diagnosgrupp',
    'label.sjukfall.stat.sickleavedegree': 'Sjukskrivningsgrad',
    'label.stat.nosjukfall.rehab': 'Det finns inga pågående sjukfall på %0',
    'label.stat.nosjukfall.lakare': 'Du har inga pågående sjukfall på %0',

    // Sjukfall Result
    'label.sjukfall.result.lakare.header': 'Mina pågående sjukfall på ',
    'label.sjukfall.result.lakare.subheader': 'Mina pågående sjukfall på enheten',

    'label.sjukfall.result.rehab.header': 'Alla pågående sjukfall på ',
    'label.sjukfall.result.rehab.subheader': 'Alla pågående sjukfall på enheten',

    'label.sjukfall.result.back': 'Tillbaka till översiktssidan',

    // Läkarutlåtande
    'label.lakarutlatande.result.lakare.header': 'Alla läkarutlåtanden på ',
    'label.lakarutlatande.result.lakare.headerend': ' de senaste tre åren.',
    'label.lakarutlatande.result.rehab.header': 'Mina läkarutlåtanden på ',
    'label.lakarutlatande.result.rehab.headerend': ' de senaste tre åren.',

    // Filter
    'label.filter.show': 'Visa sökfilter',
    'label.filter.hide': 'Dölj sökfilter',
    'label.filter.diagnos': 'Välj diagnos',
    'label.filter.diagnos.help': 'Filtrerar på den diagnos som skrivs ut först för sjukfallet uppdelat på kapitel. Diagnoskapitel som saknar data är inte valbara.',
    'label.filter.lakare': 'Välj läkare',
    'label.filter.lakare.help': 'Filtrerar på den läkare som har utfärdat det aktiva intyget. Endast läkare som utfärdat aktiva intyg visas i listan.',
    'label.filter.langd': 'Välj sjukskrivningslängd',
    'label.filter.langd.help': 'Filtrerar på total längd för det sjukfall som det aktiva intyget ingår i.',
    'label.filter.alder': 'Välj åldersspann',
    'label.filter.alder.help': 'Filtrerar på patientens nuvarande ålder.',
    'label.filter.slutdatum': 'Välj slutdatum',
    'label.filter.slutdatum.help': 'Filtrerar på slutdatum för det sjukfall som det aktiva intyget ingår i. Det är möjligt att välja ett intervall genom att klicka på två olika datum, eller ett enskilt datum genom att klicka på samma datum två gånger.',
    'label.filter.qa-status': 'Välj ärendestatus',
    'label.filter.qa-status.help': 'Filtrerar på sjukfall med eller utan obesvarade kompletteringar eller administrativa frågor och svar. ',
    'label.filter.filter': 'Sök fritext eller personnummer',
    'label.filter.filter.help': 'Filtrerar på all synlig text och personnummer i tabellen.',
    'label.filter.filter.placeholder': 'Hitta sjukfall som innehåller...',
    'label.filter.personuppgifter': 'Visa personuppgifter',
    'label.filter.personuppgifter.help': 'Visar eller döljer patienternas namn och personnummer i tabellen. ',
    'label.filter.allselected': 'Alla valda',
    'label.filter.reset': 'Återställ filter',
    'label.filter.search': 'Sök',
    'label.filter.signdatum': 'Välj signeringsdatum',
    'label.filter.signdatum.help': 'Filtrerar på signeringsdatum. Det är möjligt att välja ett intervall genom att klicka på två olika datum, eller ett enskilt datum genom att klicka på samma datum två gånger.',
    'label.filter.lakarutlatande': 'Välj läkarutlåtande',
    'label.filter.lakarutlatande-status.help': 'Filtrerar på typ av läkarutlåtande.',
    'label.filter.lakarutlatande.qa-status.help': 'Filtrerar på läkarutlåtanden med eller utan obesvarade kompletteringar eller administrativa frågor och svar.',

    // Settings
    'settings.modal.header': 'Inställningar',

    'settings.modal.pdlconsentgiven.title': 'PDL-godkännande',
    'settings.modal.pdlconsentgiven.help': '',
    'settings.modal.pdlconsentgiven.description': 'Har godkänt att loggning sker i enlighet med PDL:',

    'settings.modal.standardenhet.title': 'Förvald enhet',
    'settings.modal.standardenhet.description': 'Välj en enhet som du automatiskt ska bli inloggad på vid start av Rehabstöd:',
    'settings.modal.standardenhet.help': 'Att ha en förvald enhet gör att du inte behöver välja en enhet när du loggar in i Rehabstöd. Att ha en förvald enhet är frivilligt och du förlorar inte möjligheten att byta till en annan av dina enheter.',

    'settings.modal.maxantaldagarmellanintyg.title': 'Antal dagar mellan intyg',
    'settings.modal.maxantaldagarmellanintyg.help': 'Välj hur många dagars uppehåll det maximalt får vara mellan två intyg för att de ska räknas till samma sjukfall. Välj 0-90 dagar.',
    'settings.modal.maxantaldagarmellanintyg.description': 'Välj max antal dagars uppehåll mellan intygen:',

    'settings.modal.maxantaldagarsedansjukfallavslut.title': 'Visa nyligen avslutade sjukfall',
    'settings.modal.maxantaldagarsedansjukfallavslut.help': 'Välj maximalt antal dagar som får ha passerat efter ett sjukfalls slutdatum för att sjukfallet ska visas upp i sjukfallstabellen. Med denna funktion kan du bevaka de sjukfall som är nyligen avslutade. Välj 0-14 dagar.',
    'settings.modal.maxantaldagarsedansjukfallavslut.description': 'Välj maximalt antal dagar efter slutdatum som ett sjukfall ska visas:',

    //
    'label.gender.male': 'Man',
    'label.gender.male.plural': 'Män',
    'label.gender.female': 'Kvinna',
    'label.gender.female.plural': 'Kvinnor',
    'label.gender.undefined': '-',

    // Table
    'label.table.column.number': '#',
    'label.table.column.patientid': 'Person&shy;nummer',
    'label.table.column.sort.patient.id': 'patientid',
    'label.table.column.patientname': 'Namn',
    'label.table.column.sort.patient.namn': 'patientname',
    'label.table.column.patientage': 'Ålder',
    'label.table.column.sort.patient.alder': 'patientage',
    'label.table.column.gender': 'Kön',
    'label.table.column.sort.patient.konshow': 'gender',
    'label.table.column.dxs': 'Diagnos/diagnoser',
    'label.table.column.sort.diagnos.intygsvarde': 'dxs',
    'label.table.column.dxs.help': 'Diagnos/diagnoser i nuvarande intyg. Om det finns flera diagnoser så är den som anges först den som påverkar arbetsförmågan mest. För muspekaren över koden för att se vilken diagnos den motsvarar.',
    'label.table.column.startdate': 'Startdatum',
    'label.table.column.sort.start': 'startdate',
    'label.table.column.startdate.help': 'Datum då sjukfallet började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukfall. Max antal dagars uppehåll mellan intyg kan ställas in i inställningar.',
    'label.table.column.enddate': 'Slutdatum',
    'label.table.column.enddate.help': 'Slutdatum för sjukfallet, dvs. den sista dagen då det finns ett giltigt intyg.',
    'label.table.column.sort.slut': 'enddate',
    'label.table.column.degree': 'Grad',
    'label.table.column.sort.aktivgrad': 'degree',
    'label.table.column.degree.help': 'Sjukskrivningsgrad i nuvarande intyg. Om det innehåller flera grader anges de ordnade i tidsföljd med markering av den just nu gällande graden.',
    'label.table.column.qa': 'Ärenden',
    'label.table.column.sort.arenden': 'qa',
    'label.table.column.qa.help': 'Visar om det finns intyg i sjukfallet som har obesvarade kompletteringsbegäran eller administrativa frågor och svar och hur många det är.',
    'label.table.column.doctor': 'Läkare',
    'label.table.column.sort.lakare.namn': 'doctor',
    'label.table.column.doctor.help': 'Läkaren som utfärdat nuvarande intyg. Namnet hämtas från HSA-katalogen. Om det inte går att slå upp läkaren i HSA-katalogen visas bara HSA-id.',
    'label.table.column.days': 'Längd',
    'label.table.column.sort.dagar': 'days',
    'label.table.column.days.help': 'Sjukfallets totala längd i dagar, från startdatum till slutdatum. Eventuella dagar mellan intyg räknas inte in.',
    'label.table.column.antal': 'Antal',
    'label.table.column.sort.intyg': 'antal',
    'label.table.column.antal.help': 'Antalet intyg som ingår i sjukfallet.',
    'label.table.column.srs': 'Risk',
    'label.table.column.sort.risksignal.riskkategori': 'srs',
    'label.table.column.srs.help': 'Risksignalen för att sjukfallet varar mer än 90 dagar beräknas med matematiska metoder tillämpade på en rad variabler som till exempel ålder, kön, bostadsort och tidigare vårdkontakter för att försöka detektera om den aktuella individens risk skiljer sig från andra patienter inom samma diagnosgrupp. Metoden ska ses som ett komplement inför den egna professionella bedömningen.',

    'label.lakarutlatanden.table.column.number': '#',
    'label.lakarutlatanden.table.column.type': 'Intyg',
    'label.lakarutlatanden.table.column.type.help': 'Typ av läkarutlåtande: FK 7800 -Läkarutlåtande för sjukersättning, FK 7801 -Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga, FK 7802 - Läkarutlåtande för aktivitetsersättning vid förlängd skolgång',
    'label.lakarutlatanden.table.column.signeringsdatum': 'Signeringsdatum',
    'label.lakarutlatanden.table.column.signeringsdatum.help': 'Datum då läkarutlåtandet signerades.',
    'label.lakarutlatanden.table.column.diagnos': 'Diagnos/diagnoser',
    'label.lakarutlatanden.table.column.diagnos.help': 'Diagnos/diagnoser i läkarutlåtandet. Om det finns flera diagnoser så är den som anges först den som påverkar arbetsförmågan mest. För muspekaren över koden för att se vilken diagnos den motsvarar.',
    'label.lakarutlatanden.table.column.doctor': 'Läkare',
    'label.lakarutlatanden.table.column.arenden': 'Ärenden',
    'label.lakarutlatanden.table.column.arenden.help': 'Visar om det finns läkarutlåtanden som har obesvarade kompletteringsbegäran eller obesvarade administrativa frågor och svar och hur många det är.',
    'label.lakarutlatanden.table.column.doctor.help': 'Läkaren som utfärdat läkarutlåtandet. Namnet hämtas från HSA-katalogen. Om det inte går att slå upp läkaren i HSA-katalogen visas bara HSA-id.',
    'label.lakarutlatanden.table.column.vardenhet': 'Vårdenhet',
    'label.lakarutlatanden.table.column.vardenhet.help': 'Namn på vårdenhet där läkarutlåtandet utfärdats.',
    'label.lakarutlatanden.table.column.vardgivare': 'Vårdgivare',
    'label.lakarutlatanden.table.column.vardgivare.help': 'Namn på vårdgivare som vårdenhet tillhör där läkarutlåtandet utfärdats.',

    'label.table.column.certtype': 'Intyg',
    'label.table.column.certtype.help': 'Typ av läkarutlåtande: FK 7800 -Läkarutlåtande för sjukersättning, FK 7801 -Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga, FK 7802 - Läkarutlåtande för aktivitetsersättning vid förlängd skolgång',
    'label.table.column.signdate': 'Signeringsdatum',
    'label.table.column.signdate.help': 'Datum då läkarutlåtandet signerades.',
    'label.table.column.qas': 'Ärenden',
    'label.table.column.qas.help': 'Visar om det finns läkarutlåtanden som har obesvarade kompletteringsbegäran eller obesvarade administrativa frågor och svar och hur många det är.',

    'label.table.no-result.rehab': 'Det finns inga pågående sjukfall på %0',
    'label.table.no-result.lakare': 'Du har inga pågående sjukfall på %0',
    'label.table.no-filter-result': 'Inga sjukfall matchade filtreringen.',
    'label.lakarutlatanden.table.no-result': 'Det finns inga läkarutlåtanden för patienten på %0',
    'label.lakarutlatande.table.no-result.rehab': 'Det finns inga läkarutlåtanden på %0',
    'label.lakarutlatande.table.no-result.lakare': 'Du har inga läkarutlåtanden på %0',
    'label.lakarutlatande.table.initialstate.rehab':'Tryck på Sök för att visa alla läkarutlåtanden för enheten, eller ange filterval och tryck på Sök för att visa urval av läkarutlåtanden. <br\>Läkarutlåtanden som signerats de senaste tre åren på enheten visas.',
    'label.lakarutlatande.table.initialstate.lakare':'Tryck på Sök för att visa alla dina läkarutlåtanden för enheten, eller ange filterval och tryck på Sök för att visa urval av dina läkarutlåtanden. <br\>Läkarutlåtanden som signerats de senaste tre åren på enheten visas.',

    'label.table.number.of.rows': 'Visar',
    'label.table.number.of.rows.of': 'av',
    'label.table.column.sort.desc': 'Fallande',
    'label.table.column.sort.asc': 'Stigande',
    'label.table.diagnosbeskrivning.okand': 'Diagnoskod ${kod} är okänd och har ingen beskrivning',
    'label.table.anpassa': 'Anpassa tabellen',
    'label.table.anpassa.help': 'Du har anpassat innehållet i tabellen, vilket påverkar vilka filterval du kan göra.',
    'label.table.anpassa.help.nofilter': 'Du har anpassat innehållet i tabellen.',
    'label.table.anpassa.patient': 'Anpassa tabellerna',
    'label.table.anpassa.patient.help': 'Observera att du har anpassat vilka kolumner som syns i tabellen.',

    // Patient table
    'label.patient.table.column.number': '#',
    'label.patient.table.column.intyg': 'Intyg',
    'label.patient.table.column.diagnose': 'Diagnos/diagnoser',
    'label.patient.table.column.startdate': 'Startdatum',
    'label.patient.table.column.enddate': 'Slutdatum',
    'label.patient.table.column.length': 'Längd',
    'label.patient.table.column.grade': 'Grad',
    'label.patient.table.column.arenden': 'Ärenden',
    'label.patient.table.column.doctor': 'Läkare',
    'label.patient.table.column.occupation': 'Sysselsättning',
    'label.patient.table.column.vardenhet': 'Vårdenhet',
    'label.patient.table.column.vardgivare': 'Vårdgivare',
    'label.patient.table.column.risk': 'Risk',

    // Table modal
    'label.table.custom.modal.sjukfall.title': 'Anpassa tabellen',
    'label.table.custom.modal.sjukfall.body': 'Välj vilka kolumner du vill se i sjukfallstabellen och i vilken ordning dessa ska ligga. Dina ändringar kommer att sparas tillsvidare. Du kan ändra dessa när du vill. Kolumner som du väljer att ta bort kan du inte filtrera på.',
    'label.table.custom.modal.sjukfall.selectone': 'Var god välj minst en kolumn för att kunna se sjukfallstabellen.',
    'label.table.custom.modal.sjukfall.error': '<p>Kunde inte spara dina ändringar på grund av ett tekniskt fel. Försök igen om en stund.</p><p>Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.</p>',

    'label.table.custom.modal.lakarutlatanden.title': 'Anpassa tabellen',
    'label.table.custom.modal.lakarutlatanden.body': 'Välj vilka kolumner du vill se i tabellen med läkarutlåtanden och i vilken ordning dessa ska ligga. Dina ändringar kommer att sparas tillsvidare. Du kan ändra dessa när du vill.',
    'label.table.custom.modal.lakarutlatanden.selectone': 'Var god välj minst en kolumn för att kunna se sjukfallstabellen.',
    'label.table.custom.modal.lakarutlatanden.error': '<p>Kunde inte spara dina ändringar på grund av ett tekniskt fel. Försök igen om en stund.</p><p>Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.</p>',

    'label.table.custom.modal.patient.title': 'Anpassa tabellerna',
    'label.table.custom.modal.patient.body': 'Välj vilka kolumner du vill se i sjukfallet och i vilken ordning dessa ska ligga. Dina ändringar kommer att sparas tillsvidare. Du kan ändra dessa när du vill.',
    'label.table.custom.modal.patient.selectone': 'Var god välj minst en kolumn för att kunna se sjukfallen.',
    'label.table.custom.modal.patient.error': '<p>Kunde inte spara dina ändringar på grund av ett tekniskt fel. Försök igen om en stund.</p><p>Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.</p>',

    'label.table.custom.modal.link': 'klicka här för att anpassa tabellen',

    // Export
    'label.export.button': 'Spara som',
    'label.export.pdf': 'PDF',
    'label.export.excel': 'Excel',

    // Modal
    'modal.pdlconsent.title': 'Samtycke krävs',
    'modal.pdlconsent.rehabkoordinator.body': 'Väljer du att gå vidare kommer du få se alla pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL). <LINK:lasMerOmLoggning>.<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
    'modal.pdlconsent.lakare.body': 'Väljer du att gå vidare kommer du få se dina pågående sjukfall för den enhet du har loggat in på. För varje sjukfall visas patientens personuppgifter vilket gör att åtkomst måste loggas enligt Patientdatalagen (PDL). <LINK:lasMerOmLoggning>.<br><br>Observera att vid visning av intyg sker loggningen för Webcert då intyget tekniskt öppnas i Webcert men visas i Rehabstöd.<br><br>Genom att klicka i "Jag förstår och godkänner" tar du ställning till att det är nödvändigt för dig att i din yrkesroll få ta del av patienternas uppgifter samt bekräftar din kännedom om den loggning som sker. Detta godkännande gäller för alla enheter som du har behörighet till.<br><br>Detta godkännande kommer endast krävas en gång.',
    'modal.pdlconsent.userapproval': 'Jag förstår och godkänner.',

    // Role switching
    'label.role.switch.notloggedin': 'Du har behörigheten Rehabkoordinator på någon/några av dina enheter. Var uppmärksam om att din roll kommer skifta från Läkare till Rehabkoordinator när du väljer att logga in på en sådan enhet.',
    'label.role.switch.loggedin': 'Du har olika behörigheter på olika enheter. Var uppmärksam om att din roll kan komma att skifta mellan Läkare och Rehabkoordinator beroende på vilken enhet du väljer att byta till.',

    // Patient History Dialog
    'label.patienthistory.ag.checkbox' : 'Visa intyg till arbetsgivare',
    'label.patienthistory.ag.checkbox.help' : 'Visa eller dölj tabell med patientens intyg till arbetsgivare.',
    'label.agcertificate.table.header': 'Intyg till arbetsgivaren för patienten på %0 - räknas inte in i patientens uppskattade position i rehabiliteringskedjan.',
    'label.agcertificate.table.empty': 'Patienten har inga intyg till arbetsgivaren på %0',
    'label.agcertificates.loading': 'Hämtar intyg till arbetsgivare...',
    'label.patienthistory.loading': 'Hämtar sjukfall för patient...',
    'label.patienthistory.empty': 'Tyvärr finns det inga giltiga intyg att visa i det här sjukfallet för tillfället. Det beror på att intygen har makulerats.',

    'label.extradiagnoser.sekretess': 'För patient med skyddade personuppgifter kan ingen ytterligare information hämtas från andra vårdenheter eller andra vårdgivare.',
    'label.extradiagnoser.none': 'Det finns för tillfället ingen ytterligare information att inhämta från andra vårdenheter eller andra vårdgivare.',
    'label.extradiagnoser.empty': 'Det finns för tillfället ingen information i denna kategori att inhämta.',
    'label.extradiagnoser.error': 'Tyvärr kan information från andra vårdgivare inte inhämtas på grund av ett tekniskt fel. Försök igen om en stund.',
    'label.extradiagnoser.notfound-pu': 'För patient där ofullständiga uppgifter hämtats från folkbokföringsregistret kan ingen ytterligare information hämtas från andra vårdenheter eller andra vårdgivare.',

    'label.extradiagnoser.osparradinom.title': 'Ospärrad information inom vårdgivare',
    'label.extradiagnoser.osparradinom': '<p>Det finns ospärrad information hos en annan vårdenhet inom din vårdgivare.</p>Du kan klicka nedan för att visa vilka vårdenheter som har denna information och få möjlighet att inhämta den.',
    'label.extradiagnoser.osparradinom.list.title': 'Vårdenheter att hämta information ifrån',

    'label.extradiagnoser.sparradinom.title': 'Spärrad information hos din vårdgivare',
    'label.extradiagnoser.sparradinom': '<p>Det finns spärrad information hos en annan vårdenhet inom din vårdgivare. Endast patienten kan få spärren hävd genom att kontakta den enhet där spärren sattes.</p>Du kan klicka nedan för att visa vilka vårdenheter som har spärrad information hos sig.',
    'label.extradiagnoser.sparradinom.list.title': 'Vårdenheter',

    'label.extradiagnoser.osparradandra.title': 'Ospärrad information hos annan vårdgivare',
    'label.extradiagnoser.osparradandra': '<p>Det finns ospärrad information hos annan vårdgivare.</p>Du kan klicka nedan för att visa vilka vårdgivare som har denna information. Men patientens samtycke krävs för att du ska kunna ta del av den faktiska informationen.',
    'label.extradiagnoser.osparradandra.list.title': 'Vårdgivare att hämta information ifrån',
    'label.extradiagnoser.osparradandra.error': 'Tyvärr kan samtycke inte registreras på grund av ett tekniskt fel. Försök igen om en stund.',

    'label.extradiagnoser.sparradandra.title': 'Spärrad information hos andra vårdgivare',
    'label.extradiagnoser.sparradandra': '<p>Det finns spärrad information hos andra vårdgivare. Endast patienten kan få spärren hävd genom att kontakta den enhet där spärren sattes.</p>Du kan klicka nedan för att visa vilka vårdgivare som har spärrad information hos sig.',
    'label.extradiagnoser.sparradandra.list.title': 'Vårdgivare',

    // Patient History - Dialog för ingen information
    'label.patienthistory.dialog.title': 'Ingen information hämtad',
    'label.patienthistory.dialog.body': 'Vårdgivarens intyg tillhör inte pågående sjukfall och inhämtas därför inte.',
    'label.patienthistory.dialog.button': 'Stäng',

    //Rest 500 exception error messages:
    'server.error.default.title': 'Ett fel uppstod',
    'server.error.default.text': 'Tyvärr har ett tekniskt problem uppstått i tjänsten. Kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',

    'server.error.getsjukfall.title': 'Sjukfall för enheten kunde inte hämtas',
    'server.error.getlakarutlatande.title': 'Läkarutlåtanden för enheten kunde inte hämtas',
    'server.error.changeunit.title': 'Byte av enhet misslyckades',
    'server.error.changeurval.title': 'Kunde inte visa sjukfall',
    'server.error.getsummary.text': 'Statistik för enheten kan inte visas',
    'server.error.giveconsent.title': 'Kunde inte lämna medgivande om PDL-loggning',
    'server.error.loadagcertificates.text' : 'Historik för patientens AG-intyg kan inte visas på grund av ett tekniskt fel. Försök igen om en liten stund. Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
    'server.error.loadpatienthistory.text': 'Sjukfallshistorik kan inte visas på grund av ett tekniskt fel. Försök igen om en liten stund. Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
    'server.error.loadlakarutlatanden.text': 'Historik för patientens läkarutlåtanden kan inte visas på grund av ett tekniskt fel. Försök igen om en liten stund. Om felet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
    'server.error.getarenden.text': 'Tyvärr kunde inte information om ärendekommunikation inhämtas på grund av ett tekniskt fel. Därför är den kolumnen tom. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',

    // FAQ
    'faq.sickness.1.title': 'Vad är ett sjukfall?',
    'faq.sickness.1.body': '<p>Ett sjukfall omfattar alla de elektroniska läkarintyg som utfärdats för en viss patient vid en sjukskrivning, ' +
        'där uppehållet mellan intygens giltighetstider inte överskrider ett angivet maximalt antal dagar. ' +
        'Det maximala antalet dagar är förinställt till 5 dagar, men du kan enkelt ändra det till vilket värde du vill mellan 0 och 90 dagar.</p>' +
        '<p>Exempel: Om max antal dagar är inställt på 5 dagar, och intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti, ' +
        'så räknas de båda intygen till samma sjukfall. Om intyg 2 istället hade varit giltigt från den 21 augusti så skulle intygen ha räknats som två separata sjukfall.</p>',

    'faq.sickness.2.title': 'Vad är ett pågående sjukfall?',
    'faq.sickness.2.body': '<p>Med pågående sjukfall menas de sjukfall som har ett giltigt intyg vid inloggningstillfället.</p>',

    'faq.sickness.3.title': 'Varför ser jag fler sjukfall i <LINK:statistiktjansten> än i Rehabstöd?',
    'faq.sickness.3.body': '<p>I Intygsstatistik redovisas totalt antal sjukfall för en månad åt gången. ' +
        'Alla sjukfall som pågått någon gång under månaden, även de som har avslutats tidigare under månaden, räknas med där. ' +
        'I Rehabstöd däremot visas bara sjukfall som pågår just nu och som inte har avslutats än.</p>' +
        '<p>Exempel: Det är den 23 mars och i Intygsstatistik ser man idag att en viss enhet har 120 sjukfall under mars månad. ' +
        'I Rehabstöd däremot ser man samma dag att enheten endast har 100 sjukfall. Det beror på att 20 sjukfall har avslutats mellan 1 och 22 mars, ' +
        'och dessa räknas med i Intygsstatistiks statistik för mars men inte i Rehabstöd som bara visar pågående sjukfall.</p>',

    'faq.sickness.4.title': 'Vad kan jag göra åt sjukfall med felaktigt slutdatum?',
    'faq.sickness.4.body': '<p>Informationen i Rehabstöd hämtas från de läkarintyg som är utfärdade på enheten. ' +
        'Om läkaren av misstag har angett ett slutdatum väldigt långt fram i tiden kommer sjukfallet att synas i Rehabstöd ända till intyget går ut, ' +
        'eftersom sjukfallet räknas som pågående under den tiden. För att rätta felet finns det två alternativ. ' +
        'Om intygsutfärdaren arbetar i Webcert kan denne ersätta det felaktiga intyget med ett nytt. ' +
        'Annars måste intyget makuleras i journalsystemet och ett nytt intyg skapas.</p>',

    'faq.sickness.5.title': 'Varför kan jag inte få upp information om vad en diagnoskod i sjukfallstabellen betyder?',
    'faq.sickness.5.body': '<p>Det finns två olika anledningar till att betydelsen av en diagnoskod inte kan visas när du för muspekaren över den:</p>' +
        '<ol>' +
        '<li>Diagnoskoden som läkaren har angett i intyget är inte giltig och går inte att slå upp i något av kodverken för diagnoser (ICD-10-SE och KSH97-P).</li>' +
        '<li>Läkaren har felaktigt angett flera diagnoskoder i ett och samma fält i intyget och det går därför inte att utläsa och slå upp kodernas betydelse.</li>' +
        '</ol>',

    'faq.sickness.6.title': 'Varför ser rehabkoordinatorn fler sjukfall i Rehabstöd än vad läkaren kan se?',
    'faq.sickness.6.body': '<p>Läkaren kan endast se de pågående intyg som läkaren själv utfärdat. Rehabkoordinatorn har behörighet att se samtliga intyg på vårdenheten och tillhörande underenheter.</p>',

    'faq.sickness.7.title': 'Kan jag se sjukfall från två olika vårdenheter samtidigt?',
    'faq.sickness.7.body': '<p>Nej, i dagsläget kan man inte det. Det är endast möjligt att se en vårdenhet åt gången. ' +
        'Om vårdenheten har underenheter upplagda i HSA-katalogen visas dock som standard alla sjukfall för dessa enheter samtidigt när man tittar på vårdenheten.</p>',

    'faq.sickness.8.title': 'Kan jag se intyg från andra vårdgivare eller från andra vårdenheter inom samma vårdgivare?',
    'faq.sickness.8.body': '<p>Nej, av juridiska skäl är det i dagsläget inte möjligt att se intygsinformation från andra vårdgivare eller från andra vårdenheter inom samma vårdgivare.</p>',

    'faq.sickness.9.title': 'Kan jag se avslutade sjukfall för en patient?',
    'faq.sickness.9.body': '<p>Ja, om patienten har ett pågående intyg på din vårdenhet eller om du i rollen som läkare har skrivit det pågående intyget för en patient finns det möjlighet att se alla patientens tidigare sjukfall på din vårdenhet. ' +
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

    'faq.patient.1.title': 'Vad menas med skyddad personuppgift?',
    'faq.patient.1.body': '<p>Med skyddad personuppgift menas att Skatteverket har bedömt att patientens personuppgifter är extra viktiga att skydda. ' +
        'Det finns speciella riktlinjer om hur personuppgifter för invånare med skyddade personuppgifter ska hanteras. I Rehabstöd innebär det att:</p>' +
        '<ul>' +
        '<li>Namnet på patienten har bytts ut till "Skyddad personuppgift".</li>' +
        '<li>Endast läkare, inloggad på den vårdenhet där denne utfärdade intyget kan se sjukfallet.</li>' +
        '</ul>',

    'label.consent.modal.title': 'Om samtycke',
    'label.consent.modal.body': '<p>För att ta del av uppgifter via sammanhållen journalföring behöver du ha</p>\n' +
                                '  <ul>\n' +
                                '    <li><p>Dels en pågående vårdrelation med patienten</p></li>\n' +
                                '    <li><p>Dels patientens samtycke</p></li>\n' +
                                '  </ul>\n' +
                                '  <p>Samtycket kan ha getts muntligen eller skriftligen.</p>',
    'label.about.sjf.modal.title': 'Om sammanhållen journalföring',
    'label.about.sjf.modal.body': '<p>Med sammanhållen journalföring avses möjligheten för en vårdgivare att läsa journaluppgifter från en annan vårdgivare direkt, på elektronisk väg.</p><p>Observera att åtkomst och läsning av uppgifter via sammanhållen journalföring loggas.</p>'

  },
  'en': {
    'common.ok': 'OK',
    'common.cancel': 'Cancel'
  }
};
