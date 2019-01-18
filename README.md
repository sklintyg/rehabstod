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
    $ ./gradlew clean appRunWar -Prehabstod.useMinifiedJavaScript

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

### Köra med SAML aktiverat.

Starta riktig ActiveMQ, MySQL och Redis.

MySQL kan behöva följande först:

    CREATE USER 'rehabstod'@'localhost' IDENTIFIED BY 'rehabstod';
    GRANT ALL PRIVILEGES ON *.* TO 'rehabstod'@'localhost' IDENTIFIED BY 'rehabstod' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
    CREATE DATABASE rehabstod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;