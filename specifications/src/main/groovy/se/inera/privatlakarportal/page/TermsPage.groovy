package se.inera.privatlakarportal.page

class TermsPage extends StartPage {
    static url = "/#/terms"
    static at = { doneLoading() && termsModal.isDisplayed() }

    static content = {
        dismissBtn(to: StartPage, toWait:true) { $("#dismissBtn")}
        printBtn { $("#printBtn")}
    }

    public void dismissDialog() {
        dismissBtn.click();
    }

    public void print() {
        printBtn.click();
    }
}
