package se.inera.privatlakarportal.page

class RegisterPage extends AbstractPage {
    static url = System.getProperty("privatlakarportal.baseUrl")
    static at = { title == "Privatläkarportalen" }
}
