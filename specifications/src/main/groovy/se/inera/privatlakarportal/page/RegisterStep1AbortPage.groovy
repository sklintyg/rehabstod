package se.inera.privatlakarportal.page

class RegisterStep1AbortPage extends RegisterStep1Page {
    static url = "/#/registrera/steg1/avbryt"
    static at = { doneLoading() && title == "Privatläkarportalen" }

    static content = {
        abortConfirmBtn { $("#abortConfirmBtn")}
        abortBackBtn(to: RegisterStep1Page, toWait:true) { $("#abortBackBtn")}
    }

    public void jaJagVillAvbrytaRegistreringen() {
        abortConfirmBtn.click();
    }

    public void tillbaka() {
        abortBackBtn.click();
    }
}
