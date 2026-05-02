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

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

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
        try_transfer("-50", "ES0001234568", "Amount must be positive","ES0001234567");
    }

    @Test
    @DisplayName("Error por limite de 20.000€ superado")
    public void testGT20K(){
        try_transfer("25000", "ES0001234568", "Amount exceeds maximum transfer limit","ES0001234567");
    }

    @Test
    @DisplayName("Error por saldo insuficiente")
    public void testNotEnought(){
        try_transfer("16000", "ES0001234568", "Insufficient funds","ES0001234567");
    }



    private void try_transfer(String value, String to, String message,String from){
        driver.get("http://localhost:" + this.port + "/transfer");

        WebElement fromAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromAccount")));
        WebElement toAccount = wait.until(ExpectedConditions.elementToBeClickable(By.id("toAccount")));
        WebElement amount = wait.until(ExpectedConditions.elementToBeClickable(By.id("amount")));
        WebElement transferButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("transferButton")));

        Select sourceSelect = new Select(fromAccount);
        sourceSelect.selectByValue(from);
        String initialAccount = sourceSelect.getFirstSelectedOption().getText();

        toAccount.clear();
        toAccount.sendKeys(to);

        amount.clear();
        amount.sendKeys(value);

        transferButton.click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(error.getText().contains(message), "No es coincidente el mensaje de error: " + error.getText());

        Select sourceSelectEnd = new Select(driver.findElement(By.id("fromAccount")));
        sourceSelectEnd.selectByValue(from);
        String accountText = sourceSelectEnd.getFirstSelectedOption().getText();

        assertEquals(initialAccount, accountText, "El saldo no debe cambiar");

    }
    @Test
    @DisplayName("Transferencia entre cuentas propias")
    public void testTransferBetweenOwnAccounts() {
        driver.get("http://localhost:" + this.port + "/transfer");

        WebElement fromAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromAccount")));
        WebElement toAccount = wait.until(ExpectedConditions.elementToBeClickable(By.id("toAccount")));
        WebElement amount = wait.until(ExpectedConditions.elementToBeClickable(By.id("amount")));
        WebElement transferButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("transferButton")));

        Select sourceSelect = new Select(fromAccount);
        sourceSelect.selectByValue("ES0001234567");

        toAccount.clear();
        toAccount.sendKeys("ES0001234568");

        amount.clear();
        amount.sendKeys("1000");

        transferButton.click();

        // Esperar a que redirija al dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        WebElement balanceFrom = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-ES0001234567")));
        WebElement balanceTo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-ES0001234568")));

        assertEquals("4000.0", balanceFrom.getText(), "Saldo origen debe ser 4000");
        assertEquals("16000.0", balanceTo.getText(), "Saldo destino debe ser 16000");
    }
    @Test
    @DisplayName("Transferencia exitosa entre cuentas de distintos usuarios")
    public void testTransferBetweenDifferentUsers() {
        // Ir al dashboard para ver saldo inicial
        driver.get("http://localhost:" + this.port + "/dashboard");
        WebElement balanceFromBefore = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-ES0001234567")));
        double initialBalanceFrom = Double.parseDouble(balanceFromBefore.getText());

        // Ir a transfer
        driver.get("http://localhost:" + this.port + "/transfer");

        WebElement fromAccount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromAccount")));
        WebElement toAccount = wait.until(ExpectedConditions.elementToBeClickable(By.id("toAccount")));
        WebElement amount = wait.until(ExpectedConditions.elementToBeClickable(By.id("amount")));
        WebElement transferButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("transferButton")));

        Select sourceSelect = new Select(fromAccount);
        sourceSelect.selectByValue("ES0001234567");

        toAccount.clear();
        toAccount.sendKeys("ES0002345678");

        amount.clear();
        amount.sendKeys("500");

        transferButton.click();

        // Verificar saldo origen en dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        WebElement balanceFromAfter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-ES0001234567")));
        double finalBalanceFrom = Double.parseDouble(balanceFromAfter.getText());
        assertEquals(initialBalanceFrom - 500, finalBalanceFrom, "Saldo origen debe reducirse en 500");

        // Logout y login como maria para verificar saldo destino
        driver.get("http://localhost:" + this.port + "/logout");
        driver.get("http://localhost:" + this.port + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("maria");
        driver.findElement(By.id("password")).sendKeys("maria123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        WebElement balanceTo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-ES0002345678")));
        double finalBalanceTo = Double.parseDouble(balanceTo.getText());
        assertEquals(8500.0, finalBalanceTo, "Saldo destino debe ser 8500");
    }
    @Test
    @DisplayName("No se puede transferir a la misma cuenta")
    public void testTransferToSameAccountError() {
        try_transfer("100", "ES0001234567", "Cannot transfer to same account", "ES0001234567");
    }

    @Test
    @DisplayName("No se puede transferir a cuenta invalida o que no existe")
    public void testTransferToInvalidAccountError() {
        try_transfer("100", "ES9999999999", "Account not found", "ES0001234567");
    }
    
}
