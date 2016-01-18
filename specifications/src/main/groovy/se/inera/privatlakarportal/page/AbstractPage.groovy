package se.inera.privatlakarportal.page

import geb.Browser
import geb.Page
import org.openqa.selenium.JavascriptExecutor
import se.inera.privatlakarportal.spec.util.NgClientSideScripts

import java.util.concurrent.TimeUnit

abstract class AbstractPage extends Page {

    static boolean doneLoading() {
        waitForAngularRequestsToFinish();
        true
    }

    /**
     * Executes Javascript in browser and then waits for 'callback' to be invoked.
     * If statementPattern should reference the magic (function) variable 'callback' which should be
     * called to provide this method's result.
     * If the statementPattern contains the magic variable 'arguments'
     * the parameters will also be passed to the statement. In the latter case the parameters
     * must be a number, a boolean, a String, WebElement, or a List of any combination of the above.
     * @link http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeAsyncScript(java.lang.String,%20java.lang.Object...)
     * @param statementPattern javascript to run, possibly with placeholders to be replaced.
     * @param parameters placeholder values that should be replaced before executing the script.
     * @return return value from statement.
     */
    static public Object waitForJavascriptCallback(String statementPattern, Object... parameters) {
        def driver = se.inera.privatlakarportal.spec.Browser.getDriver();
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
        String script = "var callback = arguments[arguments.length - 1];" + String.format(statementPattern, parameters);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        if (statementPattern.contains("arguments")) {
            return jse.executeAsyncScript(script, parameters);
        } else {
            return jse.executeAsyncScript(script);
        }
    }

    static protected void waitForAngularRequestsToFinish(String root) {
        if (root == null) {
            root = "body"
        }
        Object result = waitForJavascriptCallback(NgClientSideScripts.WaitForAngular, root);
        if (result != null) {
            throw new RuntimeException(result.toString());
        }
    }

    static protected void waitForAngularBoot() {
        Object result = waitForJavascriptCallback(NgClientSideScripts.TestForAngular, 5);
        if (result != null && result[0] != true) {
            throw new RuntimeException(result.toString());
        }
    }

    static void scrollIntoView(elementId){
        def jqScrollToVisible = "jQuery(\'#" + elementId + "\')[0].scrollIntoView();var current=jQuery('body').scrollTop(); jQuery('body').scrollTop(current-400);"
        Browser.drive {
            js.exec(jqScrollToVisible)
        }
    }

    static boolean isButtonDisabled(button){
        return button.@disabled == 'true';
    }

    // use inside content definitions to prevent wait success until the element is displayed
    // with the option element(wait:true){ displayed($('#element-id')) }
    static displayed(elem) {
        (elem?.displayed) ? elem : null
    }

}
