# Rehabstöd
Rehabstöd ska ge rehabkoordinatorer och läkare en detaljerad bild över pågående sjukfall i vården. Den detaljerade bilden ska i sin tur vara en hjälp vid planering av rehabilitering.

## Komma igång med lokal installation
Den här sektionen beskriver hur man bygger applikationen för att kunna köras helt fristående.

Vi använder Gradle för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

    $ git clone https://github.com/sklintyg/rehabstod.git

Efter att man har klonat repository navigera till den klonade katalogen rehabstod och kör följande kommando:

    $ cd rehabstod
    $ ./gradlew build

Det här kommandot kommer att bygga samtliga moduler i systemet. 

När applikationen har byggt klart, kan man gå till roten och köra kommandot

    $ ./gradlew appRun

för att starta applikationen lokalt.

Gå sedan till `/web` och kör

    $ grunt server

Nu går det att öppna en webbläsare och surfa till 

    http://localhost:8790/welcome.html 

Observera jetty körs i gradleprocessen, så gradle "blir inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.

För att starta applikationen i debugläge används:

    $ cd rehabstod
    $ ./gradlew appRunDebug
    
Applikationen kommer då att starta upp med debugPort = **5008**. Det är denna port du ska använda när du sätter upp din 
debug-konfiguration i din utvecklingsmiljö.

För att testa applikationen i ett mer prodlikt läge kan man även starta med en flagga för att köra i minifierat läge då css/js är packade och sammanslagna genom att starta:

    $ cd rehabstod
    $ ./gradlew clean appRunWar -PuseMinifiedJavaScript

### Köra integrationstester
Vi har integrationstester skrivna med [REST-Assured](https://github.com/jayway/rest-assured)

De körs inte automatiskt vid bygge av applikationen utan man behöver köra dem med kommandot

    $ cd rehabstod
    $ ./gradlew restAssuredTest
    
### Kör Protractor
För att köra Protractor-testerna måste Intygstjänsten och Rehabstöd vara igång:

    $ cd rehabstod/test
    $ grunt


### Köra mot lokal intygstjänst
För att ta bort stubbad intygstjänst och köra mot en riktig (lokal) sådan så gå in i /web/build.gradle och plocka bort "rhs-it-stub" ur gretty-konfigurationen, dvs:

    '-Dspring.profiles.active=dev,rhs-srs-stub,caching-enabled',   // rhs-it-stub,

### Testa Visa intyg lokalt
Med fake-inloggning fungerar inte den automatiska authentiseringen mellan RS-WC när intyget skall visas i iframen i patienthistoriken.
För att testa lokalt kan man göra så här:

1. Ändra i gretty-konfigurationens aktiva profiler (-Dspring.profiles.active=) så att RS kör mot en riktig IT och WC, dvs ta bort profilerna
   
    - `rhs-it-stub`
    - `rhs-wc-stub`
    
2. Starta igång IT,WC och RS. För RS är det bäst att används localtest.me (tex rs.localtest.me:8790/welcome.html) för att man skall kunna vara inloggad i RS/WC samtidigt - navigerar man mot localhost i både wc/rs så krockar session-cookies.
3. Använd i samtliga följande steg en user som finns i både WC/RS fake-inloggning som `Tóth Gergő Mészáros`
4. I WC - skapa, signera och skicka intyget till FK
5. i wc ärendeverktyg (/pubapp/simulator/index.html) skall du nu kunna lägga en komplettering på det nyskapade intyget.
6. Logga in på samma user i RS, du skall se 1 sjukfall med 1 obbesvarad komplettering i sjukfallslistan. Tryck upp PatientHistoriken.
7. _**Innan**_ man klickar på Visa intyg, växla till WC fliken och välj samma användare, bocka i READONLY och klicka på login.
8. Växla till RS och visa intyget. Det skall nu visas och även kompletteringen skall även dyka upp.

Vid stängning av patienthistoriken loggas man ut ur WC, så steg 7 behövs göras om mellan varje öppning.
Vill man se något i SRS fliken måste man även se till att intygets diagnos är en diagnos som SRS-stubben har info om.
  


### Köra med SAML aktiverat.

Starta riktig ActiveMQ, MySQL och Redis.

MySQL kan behöva följande först:

    CREATE USER 'rehabstod'@'localhost' IDENTIFIED BY 'rehabstod';
    GRANT ALL PRIVILEGES ON *.* TO 'rehabstod'@'localhost' IDENTIFIED BY 'rehabstod' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
    CREATE DATABASE rehabstod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    
Byt ut grettys JVM-args mot följande:

        /***************** Konfiguration för att köra med SITHS och HSA aktiverat lokalt. *******************/
            jvmArgs = [
                    '-Dspring.profiles.active=test,rhs-security-test,rhs-samtyckestjanst-stub,rhs-sparrtjanst-stub,rhs-it-stub,rhs-srs-stub,wc-pu-stub,wc-hsa-stub,caching-enabled',
                    '-Dconfig.folder=' + projectDir + '/../devops/openshift/test/config',
                    '-Dconfig.file=' + projectDir + '/../devops/openshift/test/config/rehabstod.properties',
                    '-Dcredentials.file=' + projectDir + '/src/main/resources/dev-credentials.properties',
                    '-Dresources.folder=/' +projectDir + '/../src/main/resources'
            ]
            
**OBS!** Man behöver gå in i sp-sambi.xml i devops/openshift/test och ändra entityID och URL:ar att peka på "http://localhost:8790".

