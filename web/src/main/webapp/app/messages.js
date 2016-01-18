/* jshint maxlen: false, unused: false */
var ppMessages = {
    'sv': {

        'common.logout': 'Logga ut',
        'common.savelogout': 'Spara och logga ut',
        'common.resetchanges': 'Ångra ändringar',
        'common.continue': 'Fortsätt',
        'common.yes': 'Ja',
        'common.no': 'Nej',
        'common.yes.caps': 'JA',
        'common.no.caps': 'NEJ',
        'common.nodontask': 'Nej, fråga inte igen',
        'common.ok': 'OK',
        'common.cancel': 'Avbryt',
        'common.goback': 'Tillbaka',
        'common.revoke': 'Intyget ska återtas',
        'common.sign': 'Signera',
        'common.save': 'Spara',
        'common.send': 'Skicka',
        'common.copy': 'Kopiera',
        'common.delete': 'Radera',
        'common.print': 'Skriv ut',
        'common.close': 'Stäng',
        'common.sign.intyg': 'Signera intyget',
        'common.date': 'Datum',
        'common.when': 'När?',
        'common.notset': 'Ej angivet',
        'common.about.cookies': '<p>Så kallade kakor (cookies) används för att underlätta för besökaren på webbplatsen. En kaka är en textfil som lagras på din dator och som innehåller information. Denna webbplats använder så kallade sessionskakor. Sessionskakor lagras temporärt i din dators minne under tiden du är inne på en webbsida. Sessionskakor försvinner när du stänger din webbläsare. Ingen personlig information om dig sparas vid användning av sessionskakor.</p><p>Om du inte accepterar användandet av kakor kan du stänga av det via din webbläsares säkerhetsinställningar. Du kan även ställa in webbläsaren så att du får en varning varje gång webbplatsen försöker sätta en kaka på din dator.</p><p><strong>Observera!</strong> Om du stänger av kakor i din webbläsare kan du inte logga in i Webcert.</p><p>Allmän information om kakor (cookies) och lagen om elektronisk kommunikation finns på Post- och telestyrelsens webbplats.</p><p><a href="http://www.pts.se/sv/Bransch/Regler/Lagar/Lag-om-elektronisk-kommunikation/Cookies-kakor/" target="_blank">Mer om kakor (cookies) på Post- och telestyrelsens webbplats</a></p>',

        'common.label.saving': 'Sparar',
        'common.label.loading': 'Laddar',

        // wc-common-directives-resources
        'nav.label.loggedinas': 'Inloggad som:',

        // Shared common errors between intygstjänster applications
        'common.error.unknown': '<strong>Tekniskt fel.</strong>',
        'common.error.authorization_problem': '<strong>Behörighet saknas.</strong><br>Du saknar behörighet att använda denna resurs.',
        'common.error.cantconnect': '<strong>Kunde inte kontakta servern.</strong>',
        'common.error.certificatenotfound': '<strong>Intyget finns inte.</strong>',
        'common.error.certificateinvalid': '<strong>Intyget är inte korrekt ifyllt.</strong>',
        'common.error.certificateinvalidstate': '<strong>Intyget är inte ett utkast.</strong>Inga operationer kan utföras på det längre.',
        'common.error.invalid_state': '<strong>Operation är inte möjlig.</strong><br>Förmodligen har en annan användare ändrat informationen medan du arbetat på samma utkast. Ladda om sidan och försök igen',
        'common.error.sign.general': '<strong>Intyget kunde inte signeras.</strong><br>Försök igen senare.',
        'common.error.sign.netid': '<strong>Intyget kunde inte signeras.</strong><br>Kunde inte kontakta Net iD-klienten. Försök igen senare eller kontakta din support.',
        'common.error.sign.not_ready_yet': '<strong>Intyget är nu signerat.</strong><br>Tyvärr kan inte intyget visas än då det behandlas. Prova att ladda om sidan lite senare. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand Nationell kundservice på 0771-251010.',
        'common.error.sign.concurrent_modification': '<strong>Det går inte att signera utkastet.</strong><br/>Utkastet har ändrats av en annan användare sedan du började arbeta på det. Ladda om sidan, kontrollera att uppgifterna stämmer och försök signera igen.<br/>Utkastet ändrades av ${name}.',
        'common.error.unknown_internal_problem': '<strong>Tekniskt fel i Webcert.</strong><br>Försök igen senare.',
        'common.error.data_not_found': '<strong>Intyget kunde inte hittas.</strong><br>Intyget är borttaget eller så saknas behörighet.',
        'common.error.could_not_load_cert': '<strong>Intyget gick inte att läsa in.</strong><br>Prova att ladda om sidan. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand Nationell kundservice på 0771-251010.',
        'common.error.could_not_load_cert_not_auth': '<strong>Kunde inte hämta intyget eftersom du saknar behörighet.</strong>',
        'common.error.module_problem': '<strong>Tekniskt fel i Webcert.</strong><br>Problem att kontakta intygsmodulen.',
        'common.error.discard.concurrent_modification': '<strong>Kan inte ta bort utkastet. Utkastet har ändrats av en annan användare medan du arbetat på samma utkast.</strong><br>Ladda om sidan och försök igen. Utkastet ändrades av: ${name}',
        'common.error.save.unknown': '<strong>Okänt fel.</strong> Det går för tillfället inte att spara ändringar.',
        'common.error.save.data_not_found': '<strong>Okänt fel.</strong> Det går för tillfället inte att spara ändringar.',
        'common.error.save.concurrent_modification': '<strong>Kan inte spara utkastet. Utkastet har ändrats av en annan användare medan du arbetat på samma utkast.</strong><br>Ladda om sidan och försök igen. Utkastet ändrades av: ${name}',
        'common.error.save.unknown_internal_problem': '<strong>Tappade anslutningen till servern.</strong><br>Det går för tillfället inte att spara ändringar.',
        'common.error.save.invalid_state': '<strong>Tekniskt fel.</strong><br>Intyget kunde inte laddas korrekt. (invalid_state).',

        // General form errors
        'label.form.error.pastenotallowed': 'Du måste ange e-postadresserna genom att skriva in dem.',
        'label.form.error.nopermission': '<p>Du har inte behörighet att logga in i Webcert. Det kan bero på att: </p><ul><li>dina legitimationsuppgifter inte har hämtats från Socialstyrelsens register ännu. Om du har fått mejl upprepade gånger om att uppgifterna inte kunnat hämtas bör du kontakta Socialstyrelsen.</li><li>du enligt Socialstyrelsens uppgifter inte är legitimerad läkare.</li><li>Inera AB av någon anledning har beslutat att stänga av dig från tjänsten. Kontakta Inera Nationell Kundservice för att ta reda på mer.</li></ul>',
        'label.form.error.load': 'Kunde inte ladda formuläret. Försök igen senare. Kontakta Inera Nationell Kundservice om felet kvarstår.',
        'label.villkor.error.cantload': 'Kunde inte hämta villkoren. Försök igen senare. Kontakta Inera Nationell Kundservice om felet kvarstår.',

        // Header
        'label.header.changeaccount': 'Ändra uppgifter',
        'label.header.backtocomplete': 'Tillbaka',
        'label.header.backtoapp': 'Tillbaka',

        // Start
        'label.start.header': 'Skapa konto i Webcert för privatläkare',
        'label.start.intro': 'Du som har eller är på väg att få en läkarlegitimation kan skapa ett konto i Webcert.',
        'label.start.1': 'Du lämnar uppgifter om dig själv och din verksamhet. Uppgifterna behövs för att du ska kunna använda alla funktioner i Webcert.',
        'label.start.2': 'Din yrkeslegitimation verifieras automatiskt mot Socialstyrelsens register över legitimerad hälso- och sjukvårdspersonal (HOSP).',
        'label.start.3': 'Du går vidare till Webcert och godkänner <a id="termsLink" ui-sref=".terms">Webcerts användarvillkor</a> (endast första gången).',
        'label.start.4': 'Du kan börja använda Webcert.',

        'label.modal.title.webcertvillkor': 'Godkännande av användarvillkor',
        'label.modal.content.title.webcertvillkor': 'Användarvillkor för Webcert',

        // Register
        'register.label.grundinformation': 'Grundinformation',
        'register.label.kontaktuppgifter': 'Kontaktuppgifter i verksamheten',
        'register.label.bekraftelse': 'Bekräftelse',

        'label.abort.dialogbodytext': 'Vill du verkligen avbryta? Dina inmatade uppgifter sparas inte och du loggas ut ur tjänsten. Klicka på Tillbaka för att fortsätta skapa ett konto.',
        'label.abort.yes': 'Ja, jag vill avbryta',

        // Register form step 1
        'label.form.grunduppgifter': 'Dina och verksamhetens uppgifter',
        'label.step1intro': 'Ange information om dig själv och din verksamhet.',
        'label.alert.pu': 'Ditt namn har uppdaterats från folkbokföringens register. För att ditt nya namn ska användas i Webcert behöver du logga ut ur Webcert och sedan logga in igen.',

        'label.form.personnummer': 'Personnummer',
        'label.form.personnummer.help': 'Personnummer hämtas från den e-legitimation som används vid inloggning. Personnummer går inte att ändra.',

        'label.form.name': 'Namn',
        'label.form.name.help': 'Namn hämtas från folkbokföringen. Namn går inte att ändra.',

        'label.form.befattning.valjbefattning': 'Välj befattning',
        'label.form.befattning': 'Befattning',
        'label.form.befattning.help': 'Välj din huvudsakliga befattning enligt AID-etikett (Arbetsidentifikation kommuner och landsting).',
        'label.form.befattning.error.required': 'Befattning måste anges innan du kan fortsätta.',

        'label.form.verksamhetensnamn': 'Verksamhetens namn',
        'label.form.verksamhetensnamn.help': 'Ange verksamhetens fullständiga namn.',
        'label.form.verksamhetensnamn.error.required': 'Verksamhetens namn måste anges innan du kan fortsätta.',

        'label.form.agarform': 'Ägarform',
        'label.form.agarform.help': 'Ägarformen "Privat" är förvald och kan inte ändras. Ägarformen följer kodverket för HSA (Hälso- och sjukvårdens adressregister).',

        'label.form.vardform': 'Vårdform',
        'label.form.vardform.help': 'Ange verksamhetens huvudsakliga vårdform enligt definition i Socialstyrelsens termbank.',

        'label.form.verksamhetstyp.valjverksamhetstyp': 'Välj verksamhetstyp',
        'label.form.verksamhetstyp': 'Verksamhetstyp',
        'label.form.verksamhetstyp.help': 'Välj den typ av verksamhet som huvudsakligen bedrivs. Med \'övrig medicinsk verksamhet\' avses paramedicinsk verksamhet som bedrivs av exempelvis sjukgymnaster, arbetsterapeuter, kiropraktorer och logopeder. Med \'övrig medicinsk serviceverksamhet\' avses all medicinsk serviceverksamhet undantaget laboratorieverksamhet och radiologisk verksamhet. Välj \'medicinsk verksamhet\' om den verksamhet du bedriver inte stämmer med några andra verksamhetstyper i denna lista.',
        'label.form.verksamhetstyp.error.required': 'Verksamhetstyp måste anges innan du kan fortsätta.',

        'label.form.arbetsplatskod': 'Arbetsplatskod <i>(valfritt)</i>',
        'label.form.arbetsplatskod.help': 'Ange verksamhetens arbetsplatskod. Arbetsplatskod används för att identifiera vid vilken arbetsplats receptutfärdaren tjänstgör i samband med läkemedelsförskrivning. Vid intygsutfärdande används arbetsplatskod av Försäkringskassan för att samla in information om vid vilken arbetsplats den intygsutfärdande läkaren tjänstgör. Insamlingen sker på Socialstyrelsens uppdrag. Arbetsplatskod är inte en obligatorisk uppgift.',

        // Step 2
        'label.form.kontaktuppgifter': 'Verksamhetens kontaktuppgifter',
        'label.step2intro': 'Ange verksamhetens kontaktuppgifter.',

        'label.form.telefonnummer': 'Telefonnummer',
        'label.form.telefonnummer.help': 'Ange det telefonnummer där du vill bli kontaktad om mottagaren av intyget behöver nå dig för kompletterade frågor.',
        'label.form.telefonnummer.popover': 'Telefonnummer fylls i med siffror 0-9.',
        'label.form.telefonnummer.error.required': 'Telefonnummer måste anges innan du kan fortsätta.',

        'label.form.epost': 'E-postadress',
        'label.form.epost.help': 'E-postadressen används för att kontakta dig då en mottagare av intyg behöver nå dig för kompletterande frågor samt då Inera behöver nå dig i ärenden som gäller användningen av Webcert. Till exempel för att meddela när du är godkänd för att använda Webcert',
        'label.form.epost.popover': 'Din e-postadress är viktig! Ange den e-postadress du vill bli kontaktad på om mottagaren av intyget behöver nå dig för kompletterande frågor.',
        'label.form.epost.error.required': 'E-postadress måste anges innan du kan fortsätta.',
        'label.form.epost.error.email': 'En korrekt e-postadress måste anges innan du kan fortsätta.',

        'label.form.epost2': 'Upprepa e-postadress',
        'label.form.epost2.error.required': 'E-postadress måste anges innan du kan fortsätta.',
        'label.form.epost2.error.email': 'En korrekt e-postadress måste anges innan du kan fortsätta.',
        'label.form.epost2.error.confirmemail': 'E-postadressen är inte identisk med ovanstående angiven e-post.',

        'label.form.adress': 'Postadress',
        'label.form.adress.help': 'Ange den postadress (såsom gatuadress eller boxadress) som du vill bli kontaktad på om mottagaren av intyget behöver nå dig för kompletterande frågor. Postadressen är även nödvändig för Ineras eventuella fakturering för användning av Webcert.',
        'label.form.adress.error.required': 'Postadress måste anges innan du kan fortsätta.',

        'label.form.postnummer': 'Postnummer',
        'label.form.postnummer.help': 'Ange postadressens postnummer i fem siffor 0-9, med eller utan mellanslag. Postort, kommun och län fylls i automatiskt.',
        'label.form.postnummer.popover': 'Ange postadressens postnummer i fem siffor 0-9, med eller utan mellanslag. Postort, kommun och län fylls i automatiskt.',
        'label.form.postnummer.error.required': 'Postnummer måste anges innan du kan fortsätta.',
        'label.form.postnummer.error.format': 'Felaktigt postnummer. Postnummer måste anges på formaten XXXXX eller XXX XX.',
        'label.form.postnummer.error.region': 'Ett giltigt postnummer som ger en postort, kommun och län måste anges.',

        'label.form.postort': 'Postort',

        'label.form.kommun': 'Kommun',
        'label.form.kommun.help': 'Uppgift om kommun går inte att redigera. Om systemet får fler träffar för kommun vid hämtning av uppgiften ska du ange vilken kommun som är rätt.',
        'label.form.kommun.popover': 'Kommun där verksamheten finns.',
        'label.form.kommun.option': 'Välj kommun',
        'label.form.kommun.morehits': 'Uppgift om kommun har ${hits} träffar. Ange den kommun som är rätt.',
        'label.form.kommun.nohits': 'Inga träffar för postnummer ${postnummer}. Var vänlig kontrollera postnumret och försök igen.',
        'label.form.kommun.error.required': 'Kommun måste väljas innan du kan fortsätta.',
        'label.form.kommun.error.general': 'Ett tekniskt fel har uppstått. Postnumret kunde inte hämtas. Prova igen senare.',
        'label.form.kommun.error.toomanydigits': 'Felaktigt format på postnumret. Ange postnummer med 5 siffror på formaten XXXXX eller XXX XX.',

        'label.form.lan': 'Län',
        'label.form.lan.help': 'Län där verksamheten finns. Uppgift om län går inte att ändra.',

        // Step 3 - summary
        'label.form.socialuppgifter': 'Socialstyrelsens uppgifter',
        'label.step3intro': 'Kontrollera att sammanfattningen av din information stämmer innan du går vidare. Du kan justera de uppgifter som du själv har angett. Information hämtad från din e-legitimation och från Socialstyrelsens register går inte att redigera.',
        'label.godkannvillkor': 'Jag medger <a ui-sref=".terms">behandling av mina och verksamhetens uppgifter</a>.',
        'label.createaccount': 'Skapa konto',

        'label.summary.logoutinfo': 'För att ändringen ska visas i nya utkast som du skapar måste du spara och logga ut och sedan logga in i Webcert igen. Ändringen kommer inte att visas på redan signerade intyg.',

        'label.modal.title.portalvillkor': 'Godkännande av lagring och hämtning av uppgifter',
        'label.modal.content.title.portalvillkor': 'Medgivande av villkor för lagring och hämtning av uppgifter',

        // Complete
        'label.complete.header': 'Ditt konto är skapat',
        'label.complete.text': '<p>Dina och verksamhetens uppgifter finns nu sparade och du kan nå dem via länken "Ändra uppgifter" om du behöver uppdatera dem.</p><p>När du första gången går till Webcert för att skriva intyg måste du godkänna de villkor som gäller för att använda tjänsten.</p>',
        'label.gotowebcert': 'Gå till Webcert',

        // Waiting
        'label.waiting.header': 'Väntar på uppgifter om läkarlegitimation',
        'label.waiting.text': '<p>Ditt konto är skapat.</p><p>Uppgifter om att du har läkarlegitimation behöver dock hämtas från Socialstyrelsen. När uppgifterna har hämtats får du ett mejl till din registrerade mejladress om att du kan börja använda Webcert.</p><p>Om uppgifter om din läkarlegitimation efter 10 dagar fortfarande inte kunnat hämtas hos Socialstyrelsen får du ett mejl om detta. Du bör då kontakta Socialstyrelsen för att verifiera att dina legitimationsuppgifter är korrekta. De uppgifter du har lämnat om dig själv och din verksamhet i Webcert är sparade. Du kan ändra dem när du vill.</p><p>När du första gången går till Webcert för att skriva intyg måste du godkänna de villkor som gäller för att använda tjänsten.</p>',
        'label.waiting.gotoyouraccount': 'Gå till ditt konto',

        // Min sida
        'heading.mypage': 'Min sida'
    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
