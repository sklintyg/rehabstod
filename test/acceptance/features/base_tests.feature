# language: sv
@base @notReady
Egenskap: Bas test för Rehabstöd

Bakgrund: Jag befinner mig på rehabstöd förstasida
	Givet att jag är inloggad som Läkare

@Dummy
# Scenario: Logga in på Rehabstöd 
	# Givet att jag är inloggad som "Läkare - Jan Nilsson"
	# Så synns all innehålll


Scenario: Logga in och kontrollera att informationen på flikarna presenteras korrekt.
	När jag byter till flik "Om Rehabstöd"
	Så elementen "Om Rehabstöd" synns
	#Så loggas jag ut 

Scenario: Logga in och kontrollera att informationen på flikarna presenteras korrekt.
	När elementen "Sjukfall på enhet" synns
	Så jag byter till flik "Om Rehabstöd"
	Så elementen ska "Om Rehabstöd" synas
	#Så loggas jag ut 

Scenario: Logga in och kontrollera innehållet i Mina Sjukfall
	Och jag väljer "Visa mina sjukfall"
	Så elementen "Mina sjukfall" synns

Scenario: Logga in och kontrollera innehållet i Alla Sjukfall
	Och jag väljer "Visa alla sjukfall"
	Så elementen "Alla sjukfall" synns