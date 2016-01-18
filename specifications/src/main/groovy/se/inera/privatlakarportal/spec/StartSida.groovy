package se.inera.privatlakarportal.spec

import se.inera.privatlakarportal.page.ErrorPage
import se.inera.privatlakarportal.page.StartPage
import se.inera.privatlakarportal.page.TermsPage

class Startsida {

    public void gåTillStartsidan() {
        Browser.drive {
            to StartPage
        }
    }

    public boolean felsidanVisas() {
        boolean result
        Browser.drive {
            result = at ErrorPage
        }
        return result
    }

    public boolean startsidanVisas() {
        boolean result
        Browser.drive {
            result = at StartPage
        }
        return result
    }

    public void startaRegistrering() {
        Browser.drive {
            page.startaRegistrering()
        }
    }

    public void visaAnvändarvillkor() {
        Browser.drive {
            page.visaAnvändarvillkoren()
        }
    }

    public boolean användarvillkorenVisas() {
        boolean result
        Browser.drive {
            result = at TermsPage
        }
        return result
    }

    public void stängAnvändarvillkoren() {
        Browser.drive {
            page.dismissDialog()
        }
    }

}
