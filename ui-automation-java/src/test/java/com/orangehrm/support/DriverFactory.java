package com.orangehrm.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;

/**
 * PUBLIC_INTERFACE
 * Creates and manages local WebDriver instances for Chrome/Firefox/Edge.
 *
 * No Selenium Grid is used; drivers run locally.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory() {
        // utility
    }

    // PUBLIC_INTERFACE
    public static WebDriver getDriver() {
        WebDriver d = TL_DRIVER.get();
        if (d == null) {
            throw new IllegalStateException("WebDriver not initialized. Did you forget to run Cucumber hooks?");
        }
        return d;
    }

    // PUBLIC_INTERFACE
    public static void createDriver() {
        if (TL_DRIVER.get() != null) {
            return;
        }

        String browser = TestConfig.browser().toLowerCase();
        boolean headless = TestConfig.headless();

        WebDriver driver;

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new org.openqa.selenium.firefox.FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new org.openqa.selenium.edge.EdgeDriver(options);
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new org.openqa.selenium.chrome.ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser + " (use chrome|firefox|edge)");
        }

        // Baseline stability defaults
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        TL_DRIVER.set(driver);
    }

    // PUBLIC_INTERFACE
    public static void quitDriver() {
        WebDriver driver = TL_DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                TL_DRIVER.remove();
            }
        }
    }
}
