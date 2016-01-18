package se.inera.privatlakarportal.page

class RegisterTermsPage extends StartPage {
    static url = "/#/registrera/steg3terms"
    static at = { doneLoading() && termsModal.isDisplayed() }

    static content = {
        dismissBtn(to: RegisterStep3Page, toWait:true) { $("#dismissBtn")}
        printBtn { $("#printBtn")}
    }

    public void dismissDialog() {
        dismissBtn.click();
    }

    public void print() {
        printBtn.click();
    }
}
