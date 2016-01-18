package se.inera.privatlakarportal.page

class WaitingForHospPage extends AbstractPage {
    static at = { doneLoading() && $("#waiting").isDisplayed() }

    static content = {
        goToMyPageBtn { $("#goToMyPageBtn")}
        logoutBtn { $("#logoutBtn")}
    }

    public void goToMyPage() {
        goToMyPageBtn.click();
    }

    public void logout() {
        logoutBtn.click();
    }
}
