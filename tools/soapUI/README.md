### Populera intygsdata

1. Starta SoapUI och Importera projektet som ligger under /rehabstod/tools/soapUI/Rehabstod-RegisterMedicalCertificate-soapui-project.xml
2. Öppna ev. teststep "init test data" och justera antalet patienter och intygsglapp. Största antal patient är ca 13000, sedan kommer personnummer börja återanvändas.
3. Om du inte kör lokalt, öppna teststep "SOAP request" och korrigera URL:en http://localhost:8080/inera-certificate/register-certificate/v3.0 till något som passar ditt ändamål.
4. Dubbelklicka på "TestCase - RMC" så testdialogen öppnas.
5. Starta datapopulering genom att klicka på den gröna pilen. 10 000 poster motsv. 80 000 test steps, tar lokalt några minuter på en bättre laptop. 