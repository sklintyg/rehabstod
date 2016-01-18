package se.inera.privatlakarportal.page

class RegisterStep2AbortPage extends RegisterStep2Page {
    static url = "/#/registrera/steg2/avbryt"
    static at = { doneLoading() && title == "Privatläkarportalen" }

    static content = {
        abortConfirmBtn { $("#abortConfirmBtn")}
        abortBackBtn(to: RegisterStep2Page, toWait:true) { $("#abortBackBtn")}
    }

    public void jaJagVillAvbrytaRegistreringen() {
        abortConfirmBtn.click();
    }

    public void tillbaka() {
        abortBackBtn.click();
    }
}
