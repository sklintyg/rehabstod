package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.page.RegisterStep1Page
import se.inera.privatlakarportal.page.RegisterStep2Page
import se.inera.privatlakarportal.page.RegisterStep3Page
import se.inera.privatlakarportal.page.CompletePage
import se.inera.privatlakarportal.page.TermsPage
import se.inera.privatlakarportal.page.WaitingForHospPage

class Registrera {

    public boolean registreringSteg1SidanVisas() {
        boolean result
        Browser.drive {
            result = at RegisterStep1Page
        }
        return result
    }

    public void angeBefattning(String value) {
        Browser.drive {
            page.angeBefattning(value);
        }
    }

    public void angeVerksamhetensnamn(String value) {
        Browser.drive {
            page.angeVerksamhetensnamn(value);
        }
    }

    public void angeVardform(String value) {
        Browser.drive {
            page.angeVardform(value);
        }
    }

    public void angeVerksamhetstyp(String value) {
        Browser.drive {
            page.angeVerksamhetstyp(value);
        }
    }

    public void angeArbetsplatskod(String value) {
        Browser.drive {
            page.angeArbetsplatskod(value);
        }
    }

    public void fortsätt() {
        Browser.drive {
            page.fortsätt();
        }
    }

    public boolean registreringSteg2SidanVisas() {
        boolean result
        Browser.drive {
            result = at RegisterStep2Page
        }
        return result
    }

    public void checkAnvandarvillkor() {
        Browser.drive {
            page.checkAnvandarvillkor();
        }
    }

    public void skapaKonto() {
        Browser.drive {
            page.skapaKonto();
        }
    }

    public boolean klarSidanVisas() {
        boolean result
        Browser.drive {
            result = at CompletePage
        }
        return result
    }

    public boolean vantarSidanVisas() {
        boolean result
        Browser.drive {
            result = at WaitingForHospPage
        }
        return result
    }

    public void angeTelefonnummer(String value) {
        Browser.drive {
            page.angeTelefonnummer(value);
        }
    }

    public void angeEpost(String value) {
        Browser.drive {
            page.angeEpost(value);
        }
    }

    public void angeEpost2(String value) {
        Browser.drive {
            page.angeEpost2(value);
        }
    }

    public void angeGatuadress(String value) {
        Browser.drive {
            page.angeGatuadress(value);
        }
    }

    public void angePostnummer(String value) {
        Browser.drive {
            page.angePostnummer(value);
        }
    }

    public boolean registreringSteg3SidanVisas() {
        boolean result
        Browser.drive {
            result = at RegisterStep3Page
        }
        return result
    }

    public String personnummer() {
        String result
        Browser.drive {
            result = page.hamtaPersonnummer()
        }
        return result
    }

    public String namn() {
        String result
        Browser.drive {
            result = page.hamtaNamn()
        }
        return result
    }

    public String befattning(){
        String result
        Browser.drive {
            result = page.hamtaBefattning()
        }
        return result
    }

    public String verksamhetensnamn() {
        String result
        Browser.drive {
            result = page.hamtaVerksamhetensnamn()
        }
        return result
    }

    public String agarform() {
        String result
        Browser.drive {
            result = page.hamtaAgarform()
        }
        return result
    }

    public String vardform() {
        String result
        Browser.drive {
            result = page.hamtaVardform()
        }
        return result
    }

    public String verksamhetstyp() {
        String result
        Browser.drive {
            result = page.hamtaVerksamhetstyp()
        }
        return result
    }

    public String arbetsplatskod() {
        String result
        Browser.drive {
            result = page.hamtaArbetsplatskod()
        }
        return result
    }


    public String telefonnummer() {
        String result
        Browser.drive {
            result = page.hamtaTelefonnummer()
        }
        return result
    }

    public String epost() {
        String result
        Browser.drive {
            result = page.hamtaEpost()
        }
        return result
    }

    public String gatuadress() {
        String result
        Browser.drive {
            result = page.hamtaAdress()
        }
        return result
    }

    public String postnummer() {
        String result
        Browser.drive {
            result = page.hamtaPostnummer()
        }
        return result
    }

    public String postort() {
        String result
        Browser.drive {
            result = page.hamtaPostort()
        }
        return result
    }

    public String kommun() {
        String result
        Browser.drive {
            result = page.hamtaKommun()
        }
        return result
    }

    public String lan() {
        String result
        Browser.drive {
            result = page.hamtaLan()
        }
        return result
    }


    public String legitimeradYrkesgrupp() {
        String result
        Browser.drive {
            result = page.hamtaLegitimeradYrkesgrupp()
        }
        return result
    }

    public String specialitet() {
        String result
        Browser.drive {
            result = page.hamtaSpecialitet()
        }
        return result
    }

    public String forskrivarkod() {
        String result
        Browser.drive {
            result = page.hamtaForskrivarkod()
        }
        return result
    }

    public String hospMeddelande() {
        String result
        Browser.drive {
            result = page.hamtaHospMeddelandeId()
        }
        return result
    }

    public void visaAnvändarvillkor() {
        Browser.drive {
            page.visaAnvändarvillkoren()
        }
    }

    public boolean användarvillkorenVisas() {
        boolean result
        Browser.drive {
            result = at TermsPage
        }
        return result
    }

    public void stängAnvändarvillkoren() {
        Browser.drive {
            page.dismissDialog()
        }
    }

    def vanta(int sekunder) {
        Browser.drive {
            Thread.sleep(sekunder * 1000)
        }
        true
    }

}
