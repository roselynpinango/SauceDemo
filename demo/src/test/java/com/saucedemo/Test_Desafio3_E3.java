package com.saucedemo;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ExcelReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test_Desafio3_E3 {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String EXCEL_PATH = "Datos/testdata.xlsx";
    private WebDriver driver;
    private int screenshotCounter = 0;

    @BeforeSuite
    public void setupSuite() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupMethod() {
        ChromeOptions options = new ChromeOptions();
        
        // 1. Deshabilitar el Password Leak Detection (la causa real del popup)
        options.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck,SafeBrowsingEnhancedProtection,AutofillServerCommunication");

        // 2. Mantener las flags útiles que ya tenías
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--no-first-run");

        // 3. Preferencias del perfil — aquí está la clave
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false); // ← LA IMPORTANTE
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("safebrowsing.enabled", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        // 4. Excluir el switch que reactiva algunas features
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        takeScreenshot("LoginPage");
    }

    @DataProvider(name = "testData")
    public Object[][] getData() {
        return ExcelReader.getTestData(EXCEL_PATH);
    }

    @Test(dataProvider = "testData")
    public void testGenerarPreOrden(String username, String password, String firstname, String lastname, String zipcode) {
        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        takeScreenshot("AfterLogin");

        // Verificar que estamos en la página de productos
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "No se redirigió a la página de productos");

        // Agregar productos al carrito
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        takeScreenshot("AfterAddingProducts");

        // Ir al carrito
        productsPage.goToCart();
        takeScreenshot("CartPage");

        // Verificar que los productos están en el carrito
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "No se redirigió al carrito");

        // Proceder al checkout
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        takeScreenshot("CheckoutPage");

        // Llenar información de checkout
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        if (username.equals("problem_user")) {
            // Para problem_user, el checkout falla intencionalmente
            try {
                checkoutPage.fillCheckoutInfo(firstname, lastname, zipcode);
                Assert.fail("Se esperaba que el checkout fallara para problem_user");
            } catch (TimeoutException e) {
                // Comportamiento esperado, el test pasa
                takeScreenshot("CheckoutFailedAsExpected");
            }
        } else {
            // Para standard_user, continuar normalmente
            checkoutPage.fillCheckoutInfo(firstname, lastname, zipcode);
            takeScreenshot("AfterCheckoutInfo");

            // Completar la orden
            checkoutPage.completeOrder();
            takeScreenshot("OrderComplete");

            // Verificar que la orden se completó
            String message = checkoutPage.getOrderCompleteMessage();
            Assert.assertEquals(message, "Thank you for your order!", "La orden no se completó correctamente");
        }
    }

    @AfterMethod
    public void teardownMethod() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void takeScreenshot(String stepName) {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
        String destPath = "screenshots/" + stepName + "_" + (++screenshotCounter) + ".png";
        try {
            Files.createDirectories(Paths.get("screenshots"));
            Files.copy(srcFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}