package se.inera.privatlakarportal.page

class RegisterStep2Page extends RegisterPage {
    static url = "/#/registrera/steg2"
    static at = { doneLoading() && $("#step2").isDisplayed() }

    static content = {
        continueBtn(to: RegisterStep3Page, toWait:true) { $("#continueBtn")}
        backBtn(to: RegisterStep1Page, toWait:true) { $("#backBtn")}
        abortBtn(to: RegisterStep1AbortPage, toWait: true) { $("#abortBtn")}
    }

    public void fortsätt() {
        continueBtn.click();
    }

    public void tillbaka() {
        backBtn.click();
    }

    public void avbryt() {
        abortBtn.click();
    }

    public void angeTelefonnummer(value) {
        telefonnummer = value;
    }

    public void angeEpost(value) {
        epost = value;
    }

    public void angeEpost2(value) {
        epost2 = value;
    }

    public void angeGatuadress(value) {
        adress = value;
    }

    public void angePostnummer(value) {
        postnummer = value;
    }

}
