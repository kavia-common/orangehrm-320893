package com.orangehrm.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Creates and manages local WebDriver instances for Chrome/Firefox/Edge.
 *
 * Demo stabilization rules:
 *  - Chrome is always forced headless for container/CI safety.
 *  - ChromeOptions are set exactly as requested for the demo.
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

        // For non-Chrome browsers we keep the original "headless if configured or no display" behavior.
        boolean headlessForNonChrome = TestConfig.headless() || isNoDisplayAvailable();

        WebDriver driver;

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headlessForNonChrome) {
                    options.addArguments("-headless");
                }
                driver = new org.openqa.selenium.firefox.FirefoxDriver(options);
                System.out.println("[DriverFactory] Browser launched: firefox (headless=" + headlessForNonChrome + ")");
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headlessForNonChrome) {
                    options.addArguments("--headless=new");
                }
                driver = new org.openqa.selenium.edge.EdgeDriver(options);
                System.out.println("[DriverFactory] Browser launched: edge (headless=" + headlessForNonChrome + ")");
            }
            case "chrome" -> {
                // Demo requirement: force headless Chrome with specific ChromeOptions.
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");

                // Ensure WebDriverManager resolves a ChromeDriver matching the Chrome binary in this environment.
                WebDriverManager wdm = WebDriverManager.chromedriver();

                String chromeBinary = resolveChromeBinaryPath();
                if (chromeBinary != null) {
                    // Ensure Selenium uses the same browser binary we use for driver resolution.
                    options.setBinary(chromeBinary);

                    // WebDriverManager 5.8.0 does not have browserPath(...).
                    // Provide an explicit browser version detection command so WDM can
                    // resolve a matching driver even when Chrome is not on PATH.
                    String versionCommand = chromeBinary.contains(" ")
                            ? "\"" + chromeBinary + "\" --version"
                            : chromeBinary + " --version";
                    wdm.browserVersionDetectionCommand(versionCommand);

                    System.out.println("[DriverFactory] Using Chrome binary: " + chromeBinary);
                } else {
                    System.out.println("[DriverFactory] No explicit Chrome binary configured/discovered; using system default.");
                }

                wdm.setup();
                driver = new org.openqa.selenium.chrome.ChromeDriver(options);

                // Keep an explicit window size at runtime as well (some environments ignore the arg).
                driver.manage().window().setSize(new Dimension(1920, 1080));

                System.out.println("[DriverFactory] Browser launched: chrome (headless=true)");
                System.out.println("[DriverFactory] ChromeOptions: --headless=new --no-sandbox --disable-dev-shm-usage --disable-gpu --window-size=1920,1080");
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser + " (use chrome|firefox|edge)");
        }

        // Baseline stability defaults
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Maximize can be flaky in headless/container runs; only do it when likely safe.
        if (!"chrome".equals(browser) && !headlessForNonChrome) {
            driver.manage().window().maximize();
        }

        TL_DRIVER.set(driver);
    }

    private static String resolveChromeBinaryPath() {
        String configured = TestConfig.chromeBinary();
        if (configured != null && !configured.isBlank()) {
            return validateExecutable(configured.trim());
        }

        // Common Chrome/Chromium paths in Linux and container images.
        List<String> candidates = List.of(
                "/usr/bin/google-chrome",
                "/usr/bin/google-chrome-stable",
                "/opt/google/chrome/google-chrome",
                "/usr/bin/chromium",
                "/usr/bin/chromium-browser"
        );

        for (String candidate : candidates) {
            String ok = validateExecutable(candidate);
            if (ok != null) {
                return ok;
            }
        }

        return null;
    }

    private static String validateExecutable(String path) {
        try {
            Path p = Path.of(path);
            if (Files.exists(p) && Files.isRegularFile(p) && Files.isExecutable(p)) {
                return p.toAbsolutePath().toString();
            }
        } catch (Exception ignored) {
            // best-effort only; fall back to defaults if anything is odd in the environment
        }
        return null;
    }

    private static boolean isNoDisplayAvailable() {
        String display = System.getenv("DISPLAY");
        String wayland = System.getenv("WAYLAND_DISPLAY");
        return (display == null || display.isBlank()) && (wayland == null || wayland.isBlank());
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
