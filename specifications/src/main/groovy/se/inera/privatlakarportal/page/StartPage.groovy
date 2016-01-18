package se.inera.privatlakarportal.page

class StartPage extends AbstractPage {
    static url = "http://localhost:8090"
    static at = { doneLoading() && registerBtn.isDisplayed() && !termsModal.isDisplayed() }

    static content = {
        termsModal(required:false) { $("#termsModal") }
        registerBtn(to: RegisterStep1Page, toWait:true) { $("#registerBtn")}
        termsLink(to: TermsPage, toWait: true) { $("#termsLink")}
    }

    public void startaRegistrering() {
        registerBtn.click();
    }

    public void visaAnvändarvillkoren() {
        termsLink.click();
    }
}
