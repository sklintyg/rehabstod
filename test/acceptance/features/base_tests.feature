# language: sv
@base
Egenskap: Bas test för Rehabstöd

Bakgrund: Jag befinner mig på rehabstöd förstasida

@Dummy
Scenario: Logga in på Rehabstöd 
	Givet att jag är inloggad som en Läkare
	Och att jag är inloggad som "Läkare - Jan Nilsson"
	Så synns all innehåll

@no
Scenario: Logga in och kontrollera att användaren kan byta flikar korrekt.
	Givet att jag är inloggad som en Läkare
	Och att jag är inloggad som "Läkare - Jan Nilsson"
	När jag byter till flik "Pågående sjukfall"
	Och elementen "Sjukfall på enhet" synns
	Så jag byter till flik "Om Rehabstöd"
	Och elementen "Om Rehabstöd" synns
	Så loggas jag ut 