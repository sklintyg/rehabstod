### Bra att veta
* Största antal patient är ca 13000, sedan kommer personnummer börja återanvändas.
* Varje patient ges 1 - 7 intyg.
* Glappet (antal dagar) mellan intyg bestäms av 'maxIntygsGlapp'. 
Sätts maxIntygsGlapp = 5 betyder det att glappet mellan två intyg kommer att sättas till ett värde mellan 0 och 5 dagar.
* Längden (antal dagar) på ett intyg bestäms av 'minIntygsLangd' och 'maxIntygsLangd'. 
Sätts minIntygsLangd = 3 och maxIntygsLangd = 20 betyder det att längden på ett intyg kommer att 
sättas till ett värde mellan 3 och 30 dagar.
* Slumpen kommer att bestämma ifall en patient ges ett framtida intyg. Endast ett framtida intyg kommer att skapas.

### Defaultvärden

    def defaultNumberOfPatients = 10
    def defaultMaxIntygsGlapp = 15
    def defaultMinIntygsLangd = 3
    def defaultMaxIntygsLangd = 30
    
### Populera intygsdata via soapUI

1. Starta soapUI och importera projektet som ligger under /rehabstod/tools/soapUI/rehabstod-registerMedicalCertificate-soapui-project.xml
2. Öppna ev. teststep "init test data" och justera antalet patienter och intygsglapp.
3. Om du inte kör lokalt, öppna teststep "SOAP request" och korrigera URL:en http://localhost:8080/inera-certificate/register-certificate/v3.0 till något som passar ditt ändamål.
4. Dubbelklicka på "TestCase - RMC" så testdialogen öppnas.
5. Starta datapopulering genom att klicka på den gröna pilen. 10 000 poster motsv. 80 000 test steps, tar lokalt några minuter på en bättre laptop.

### Populera intygsdata via Maven

Det går att populera intygsdata via command line genom att exekvera Maven

    mvn com.smartbear.soapui:soapui-maven-plugin:5.1.3:test -Psuite-localhost -DnumberOfPatients=10 -DmaxIntygsGlapp=5  -DminIntygsLangd=3  -DmaxIntygsLangd=30

I kommandot ovan så populeras Intygstjänsten
* med 10 patienter
* glappet mellan intyg är 0-5 dagar.
* längden på ett intyg kan vara 3-30 dagar

#### Profiler

Det finns profiler för att kunna bestämma vilken endpoint (Intygstjänst) som är mottagare av SOAP-anropen. För tillfället finns det två profiler:
* suite-localhost
* suite-demo (deprecated, för gamla demo)
* suite-tunnel (för nya demo)

#### Sätt upp SSH-tunnel

Nya demomiljöerna går inte att komma åt intygstjänsten på, ens med VPN igång. Lösningen är att sätta upp en SSH-tunnel in till burken ifråga och köra port-forwarding.

1) Starta Basefarm VPN och logga in
2) Sätt upp SSH-tunnel, byt ut _myuser_ mot ditt BF-användarnamn


    ssh -L 9000:localhost:8084 myuser@ine-dib-app01.sth.basefarm.net

3) Nu har vi en tunnel uppe. Kör:

 
    mvn com.smartbear.soapui:soapui-maven-plugin:5.1.3:test -Psuite-tunnel -DnumberOfPatients=10 -DmaxIntygsGlapp=5

#### Properties

Följande properties kan skickas med:
* debug -- true/false, bestämmer om intygsvärden skrivs till filen soapui-rehabstod.log
* numberOfPatients -- antalet patienter med sjukintyg som ska skapas i Intygstjänsten
* maxIntygsGlapp -- maximala antalet dagar mellan två intyg
* minIntygsLangd -- minsta antalet dagar för ett intyg
* maxIntygsLangd -- maximala antalet dagar för ett intyg

Om inte några av dessa properties skickas med används de värden som är default i soapUI-projektet.
Se tidigare avsnitt.

### Loggar och rapporter

Under /rehabstod/tools/soapUI/target hamnar loggar och testrapporter i katalogerna
* soapui-logs resp.
* soapui-reports
