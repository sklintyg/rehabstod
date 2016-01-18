package se.inera.privatlakarportal.page

class RegisterStep3AbortPage extends RegisterStep3Page {
    static url = "/#/registrera/steg3/avbryt"
    static at = { doneLoading() && title == "Privatläkarportalen" }

    static content = {
        abortConfirmBtn { $("#abortConfirmBtn")}
        abortBackBtn(to: RegisterStep3Page, toWait:true) { $("#abortBackBtn")}
    }

    public void jaJagVillAvbrytaRegistreringen() {
        abortConfirmBtn.click();
    }

    public void tillbaka() {
        abortBackBtn.click();
    }
}
