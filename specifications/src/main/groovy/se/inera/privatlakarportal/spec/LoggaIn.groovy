package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.page.WelcomePage

class LoggaIn {

    def loggaInSom(String id) {
        Browser.drive {
            to WelcomePage
            page.loginAs(id)
        }
    }
}
