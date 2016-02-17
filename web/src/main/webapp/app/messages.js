/* jshint maxlen: false, unused: false */
var ppMessages = {
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
        'label.sjukfall.start.header' : 'Sjukfall på enhet',

        // Sjukfall Stat
        'label.sjukfall.stat.ongoing' : 'Antalet pågående sjukfall på',
        'label.sjukfall.stat.gender' : 'Könsfördelning',

        // Sjukfall Result Lakare
        'label.sjukfall.result.lakare.header': 'Mina sjukfall',
        'label.sjukfall.result.lakare.subheader': ' - Endast de sjukfall där jag utfärdat det aktiva intyget',

        'label.sjukfall.result.all.header': 'Alla sjukfall',
        'label.sjukfall.result.all.subheader': ' - Samtliga pågående fall på enheten',

        //
        'label.gender.male': 'Man',
        'label.gender.female': 'Kvinna',
        'label.gender.undefined': '-',

        // Table
        'label.table.column.pnr': 'Personnummer',
        'label.table.column.namn': 'Namn',
        'label.table.column.kon': 'Kön',
        'label.table.column.diagnos': 'Nuvarande diagnos',
        'label.table.column.diagnos.help': 'Huvuddiagnosen i det intyg som är giltigt just nu. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.startve': 'Startdatum',
        'label.table.column.startve.help': 'Datum då sjukskrivningen började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukskrivning. Max antal dagars uppehåll mellan intyg kan ställas in i filter.',
        'label.table.column.slut': 'Slutdatum',
        'label.table.column.slut.help': 'Slutdatum för det intyg som är giltigt just nu, dvs. det datum då sjukskrivningen beräknas avslutas om den inte förlängs.',
        'label.table.column.grad': 'Nuvarande grad',
        'label.table.column.grad.help': 'Sjukskrivningsgraden i det intyg som är giltigt just nu. Om det innehåller flera grader anges de ordnade i tidsföljd med den just nu gällande graden fetstilsmarkerad.',
        'label.table.column.lakare': 'Nuvarande läkare',
        'label.table.column.lakare.help': 'Läkaren som utfärdat det intyg som är giltigt just nu.',
        'label.table.column.langd': 'Sjukskrivningslängd',
        'label.table.column.langd.help': 'Sjukskrivningens totala längd i dagar, från ”Startdatum vårdenhet” till ”Slutdatum”. Dagar då patienten inte haft något giltigt intyg räknas inte.',

        // Filter
        'label.filter.diagnos' : 'Välj nuvarande diagnos',
        'label.filter.lakare' : 'Välj nuvarande läkare',
        'label.filter.langd' : 'Välj sjukskrivningslängd',
        'label.filter.show' : 'Visa sökfilter',
        'label.filter.hide' : 'Dölj sökfilter',
        'label.filter.filter' : 'Filter',
        'label.filter.allselected' : 'Alla valda'
    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
