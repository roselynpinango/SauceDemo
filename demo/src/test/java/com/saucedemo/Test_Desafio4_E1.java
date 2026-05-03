package com.saucedemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Test_Desafio4_E1 {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private WebDriver driver;

    @BeforeMethod
    @Parameters({"browser"})
    public void setupMethod(String browser) {
        if (browser.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
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
        } else if (browser.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(new FirefoxOptions());
        }
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
    }

    @Test
    public void testGenerarOrden() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "No se redirigió a la página de productos");

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.goToCart();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "No se redirigió al carrito");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");
        checkoutPage.completeOrder();

        Assert.assertEquals(checkoutPage.getOrderCompleteMessage(), "Thank you for your order!", "La orden no se completó correctamente");
    }

    @AfterMethod
    public void teardownMethod() {
        if (driver != null) {
            driver.quit();
        }
    }
}