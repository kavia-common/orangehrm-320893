package org.orangehrm.selenium.driver;

import org.openqa.selenium.*;
import org.openqa.selenium.logging.Logs;

import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A WebDriver implementation that fails fast if any real browser operation is attempted.
 *
 * Used for "discovery-only" validation to ensure tests are runnable/discoverable
 * without requiring any browser/driver binaries.
 */
public final class DiscoveryOnlyWebDriver implements WebDriver, JavascriptExecutor, TakesScreenshot, HasCapabilities {

    private static UnsupportedOperationException notAllowed() {
        return new UnsupportedOperationException("Discovery-only mode: WebDriver operations are not allowed.");
    }

    @Override
    public void get(String url) {
        throw notAllowed();
    }

    @Override
    public String getCurrentUrl() {
        throw notAllowed();
    }

    @Override
    public String getTitle() {
        throw notAllowed();
    }

    @Override
    public List<WebElement> findElements(By by) {
        throw notAllowed();
    }

    @Override
    public WebElement findElement(By by) {
        throw notAllowed();
    }

    @Override
    public String getPageSource() {
        throw notAllowed();
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void quit() {
        // no-op
    }

    @Override
    public Set<String> getWindowHandles() {
        return Collections.emptySet();
    }

    @Override
    public String getWindowHandle() {
        return "DISCOVERY_ONLY";
    }

    @Override
    public TargetLocator switchTo() {
        throw notAllowed();
    }

    @Override
    public Navigation navigate() {
        throw notAllowed();
    }

    @Override
    public Options manage() {
        throw notAllowed();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        throw notAllowed();
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        throw notAllowed();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        throw notAllowed();
    }

    @Override
    public Capabilities getCapabilities() {
        return new Capabilities() {};
    }

    /**
     * Some Selenium APIs in newer versions may attempt to cast to HasDevTools etc.
     * This skeleton intentionally keeps behavior minimal.
     */
}
