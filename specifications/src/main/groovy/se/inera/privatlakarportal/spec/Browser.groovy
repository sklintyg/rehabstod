package se.inera.privatlakarportal.spec

import geb.driver.CachingDriverFactory

import org.openqa.selenium.Alert
import org.openqa.selenium.Cookie
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

import se.inera.privatlakarportal.page.AbstractPage

import org.openqa.selenium.support.ui.ExpectedConditions

public class Browser {

    private static geb.Browser browser

    static void öppna() {
        if (browser) throw new IllegalStateException("Browser already initialized")
        browser = new geb.Browser()
    }

    public void stäng() {
        if (!browser) throw new IllegalStateException("Browser not initialized")
        browser.quit()
        CachingDriverFactory.clearCache()
    }

    public void laddaOm(acceptBrowserDialog) {
        if (!browser) throw new IllegalStateException("Browser not initialized")
        browser.driver.navigate().refresh()
        WebDriver webDriver = browser.driver

        browser.drive {
            if(acceptBrowserDialog == "true") {
                try {
                    WebDriverWait wait = new WebDriverWait(driver, 2);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                }
                catch(TimeoutException e) {}
            }
            waitFor {
                js.doneLoading && js.dialogDoneLoading
            }
        }
    }

    static geb.Browser drive(Closure script) {
        if (!browser) throw new IllegalStateException("Browser not initialized")
        script.delegate = browser
        script()
        browser
    }

    static String getJSession() {
        browser.getDriver().manage().getCookieNamed("JSESSIONID").getValue()
    }

    static String deleteCookie(cookieName) {
        Cookie cookie = new Cookie(cookieName, "")
        browser.getDriver().manage().deleteCookie(cookie)
    }

    static String setCookie(cookieName, cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue)
        browser.getDriver().manage().addCookie(cookie)
    }

    static String getTitle() {
        browser.getDriver().getTitle()
    }

    static getDriver() {
        return browser.getDriver();
    }
}
