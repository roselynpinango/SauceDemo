package com.saucedemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class Test_Desafio6_E1B {

    private static final String BASE_URL = "https://www.saucedemo.com/";
    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-save-password-bubble", "--disable-infobars");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
    }

    @Test
    public void testGenerarOrden() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                "Login fallido: no se redirigió a la página de productos");

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.goToCart();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"),
                "No se redirigió al carrito");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCheckoutInfo("Tester", "User", "1001");
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two"),
                "No se avanzó al resumen de la orden");

        checkoutPage.completeOrder();

        String mensaje = checkoutPage.getOrderCompleteMessage();
        Assert.assertEquals(mensaje, "Thank you for your order!",
                "El mensaje de confirmación de orden no es el esperado");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
