# language: sv
@filter
Egenskap: Filtrera intyg

Bakgrund: Jag befinner mig på rehabstöd förstasida
	Givet att jag är inloggad som Läkare
	Och jag väljer "Visa mina sjukfall"

Scenario: Filtrera med fritext
	När jag anger "1990" i fritextfältet
	Så ska det endast visas rader med "1990" i texten
