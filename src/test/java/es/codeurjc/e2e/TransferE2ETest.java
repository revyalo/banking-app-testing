package es.codeurjc.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferE2ETest {

    @LocalServerPort
    int port;

    private WebDriver driver;
    private WebDriverWait wait;
    @BeforeEach
    public void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=/tmp/chrome-test-profile");

        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");

        options.setExperimentalOption("prefs", Map.of("credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "autofill.profile_enable", false,
        "autofill.credit_card_enabled", false));

        //Todo este codigo se aplica debido a un pop-up de chrome que no permitia dejar avanzar y bloquear el programa

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localHost:" + this.port + "/login");
        driver.findElement(By.id("username")).sendKeys("customer");
        driver.findElement(By.id("password")).sendKeys("Cu5t0m3r");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

    }

    @AfterEach
    public void tearDown(){
        if (driver != null){
            driver.quit();
        }
    }

    @Test
    @DisplayName("Error por cantidad negativa")
    public void testNegativeTransfer(){
        try_transfer("-50", "ES0001234568", "Amount must be positive");
    }

    @Test
    @DisplayName("Error por limite de 20.000€ superado")
    public void testGT20K(){
        try_transfer("25000", "ES0001234568", "Amount exceeds maximum transfer limit");
    }

    @Test
    @DisplayName("Error por saldo insuficiente")
    public void testNotEnought(){
        try_transfer("16000", "ES0001234568", "Insufficient funds");
    }



    private void try_transfer(String value, String to, String message){
        driver.get("http://localhost:" + this.port + "/transfer");

        WebElement fromAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromAccount")));
        WebElement toAccount = wait.until(ExpectedConditions.elementToBeClickable(By.id("toAccount")));
        WebElement amount = wait.until(ExpectedConditions.elementToBeClickable(By.id("amount")));
        WebElement transferButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("transferButton")));

        Select sourceSelect = new Select(fromAccount);
        sourceSelect.selectByIndex(1);
        String initialAccount = sourceSelect.getFirstSelectedOption().getText();

        toAccount.clear();
        toAccount.sendKeys(to);

        amount.clear();
        amount.sendKeys(value);

        transferButton.click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(error.getText().contains(message), "No es coincidente el mensaje de error: " + error.getText());

        Select sourceSelectEnd = new Select(driver.findElement(By.id("fromAccount")));
        String accountText = sourceSelectEnd.getOptions().get(1).getText();

        assertEquals(initialAccount, accountText, "El saldo no debe cambiar");

    }
    
}
