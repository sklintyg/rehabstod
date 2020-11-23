# Rehabstöd
Rehabstöd ska ge rehabkoordinatorer och läkare en detaljerad bild över pågående sjukfall i vården. Den detaljerade bilden ska i sin tur vara en hjälp vid planering av rehabilitering.

## Komma igång med lokal installation
Den här sektionen beskriver hur man bygger applikationen för att kunna köras helt fristående.

Vi använder Gradle för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

    $ git clone https://github.com/sklintyg/rehabstod.git
    
Läs vidare i gemensam dokumentation [devops/develop README-filen](https://github.com/sklintyg/devops/tree/release/2021-1/develop/README.md)

### Köra mot lokal intygstjänst
För att ta bort stubbad intygstjänst och köra mot en riktig (lokal) sådan så gå in i /web/build.gradle och plocka bort "rhs-it-stub" ur gretty-konfigurationen, dvs:

    '-Dspring.profiles.active=dev,rhs-srs-stub,caching-enabled',   // rhs-it-stub,

### Testa Visa intyg lokalt
Med fake-inloggning fungerar inte den automatiska authentiseringen mellan RS-WC när intyget skall visas i iframen i patienthistoriken.
För att testa lokalt kan man göra så här:

1. Ändra i gretty-konfigurationens aktiva profiler (-Dspring.profiles.active=) så att RS kör mot en riktig IT och WC, dvs ta bort profilerna
   
    - `rhs-it-stub`
    - `rhs-wc-stub`
    
2. Starta igång IT,WC och RS. För RS är det bäst att används https://rs.localtest.me/welcome.html för att man skall kunna vara inloggad i RS/WC samtidigt - navigerar man mot localhost i både wc/rs så krockar session-cookies.
3. Använd i samtliga följande steg en user som finns i både WC/RS fake-inloggning som `Arnold Johansson`
4. I WC - skapa, signera och skicka intyget till FK
5. i wc ärendeverktyg (/pubapp/simulator/index.html) skall du nu kunna lägga en komplettering på det nyskapade intyget.
6. Logga in på samma user i RS, du skall se 1 sjukfall med 1 obbesvarad komplettering i sjukfallslistan. Tryck upp PatientHistoriken.
7. _**Innan**_ man klickar på Visa intyg, växla till WC fliken och välj samma användare, bocka i READONLY och klicka på login.
8. Växla till RS och visa intyget. Det skall nu visas och även kompletteringen skall även dyka upp.

Vid stängning av patienthistoriken loggas man ut ur WC, så steg 7 behövs göras om mellan varje öppning.
Vill man se något i SRS fliken måste man även se till att intygets diagnos är en diagnos som SRS-stubben har info om.

## Licens
Copyright (C) 2021 Inera AB (http://www.inera.se)

Rehabstöd is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Rehabstöd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.

Se även [LICENSE](LICENSE). 