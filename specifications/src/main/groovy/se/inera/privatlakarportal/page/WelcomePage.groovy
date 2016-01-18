package se.inera.privatlakarportal.page

import se.inera.privatlakarportal.spec.Browser

class WelcomePage extends AbstractPage {
    static url = "/welcome.html"
    static at = { $("#loginForm").isDisplayed() && doneLoading() }

    static content = {
        userSelect { $("#jsonSelect") }
        loginBtn { $("#loginBtn") }
    }

    def loginAs(String id) {
        userSelect = $("#${id}").value();
        loginBtn.click()
        // The login page is running a separate angular app, here we need to wait for the "real" angular app to start.
        Browser.drive {
            waitFor {
                js.doneLoading
            }
        }
    }
}
