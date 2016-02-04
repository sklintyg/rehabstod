# Rehabstod

Rehabstöd ska ge rehabkoordinatorer och läkare en detaljerad bild över pågående sjukfall i vården. Den detaljerade bilden ska i sin tur vara en hjälp vid planering av rehabilitering.

##Komma igång med lokal installation

Den här sektionen beskriver hur man bygger applikationen för att kunna köras helt fristående.

Vi använder Gradle, för närvarande version 2.7 för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

`git clone https://github.com/sklintyg/rehabstod.git`

Efter att man har klonat repository navigera till den klonade katalogen rehabstod och kör följande kommando:

`gradle build`

Det här kommandot kommer att bygga samtliga moduler i systemet. 

När applikationen har byggt klart, kan man gå till `/web` och köra kommandot

`gradle appRun`

för att starta applikationen lokalt.

Nu går det att öppna en webbläsare och surfa till 

`http://localhost:8790/welcome.html` 

Observera jetty körs i gradleprocessen, så gradle "blir inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.

## Köra Integrationstester
Vi har integrationstester skrivna med [REST-Assured](https://github.com/jayway/rest-assured)

De körs inte automatiskt vid bygge av applikationen utan man behöver köra dem med kommandot

`gradle integrationTest`