package se.inera.privatlakarportal.page

class RegisterStep1Page extends RegisterPage {
    static url = "/#/registrera/steg1"
    static at = { doneLoading() && $("#step1").isDisplayed() }

    static content = {
        continueBtn(to: RegisterStep2Page, toWait:true) { $("#continueBtn")}
        abortBtn(to: RegisterStep1AbortPage, toWait: true) { $("#abortBtn")}
        befattning { $("#befattning") }
        verksamhetensnamn { $("#verksamhetensnamn") }
        vardform { $("#vardform") }
        verksamhetstyp { $("#verksamhetstyp") }
        arbetsplatskod { $("#arbetsplatskod") }
    }

    public void fortsätt() {
        continueBtn.click();
    }

    public void avbryt() {
        abortBtn.click();
    }

    public void angeBefattning(value) {
        befattning = value;
    }

    public void angeVerksamhetensnamn(value) {
        verksamhetensnamn = value;
    }

    public void angeVardform(value) {
        vardform = value;
    }

    public void angeVerksamhetstyp(value) {
        verksamhetstyp = value;
    }

    public void angeArbetsplatskod(value) {
        arbetsplatskod = value;
    }

}
