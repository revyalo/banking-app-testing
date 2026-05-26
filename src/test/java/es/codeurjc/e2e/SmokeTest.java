package es.codeurjc.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmokeTest {

    private WebDriver driver;
    private static final String APP_URL = "https://bankingapp-production-urjc-bhbcebcag5cte5e4.spaincentral-01.azurewebsites.net";

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @Test
    public void testVersionIsCorrect() {
        String expectedVersion = System.getProperty("appVersion", "1.2.0");

        driver.get(APP_URL + "/login");

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(expectedVersion),
                "La versión desplegada no coincide. Se esperaba: " + expectedVersion);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}