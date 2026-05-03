package com.saucedemo;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Test_Desafio3_E1 {
    String BASE_URL = "https://www.saucedemo.com/";
    WebDriver driver;
    int screenshotCounter = 0;

    @BeforeSuite
    public void setupSuite() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupMethod() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        takeScreenshot("LoginPage");
    }

    @Test
    public void testGenerarOrden() {
        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
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
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");
        takeScreenshot("AfterCheckoutInfo");

        // Completar la orden
        checkoutPage.completeOrder();
        takeScreenshot("OrderComplete");

        // Esperar a que la página de orden completada cargue
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("checkout-complete"));

        // Verificar que la orden se completó
        String message = checkoutPage.getOrderCompleteMessage();
        Assert.assertEquals(message, "Thank you for your order!", "La orden no se completó correctamente");
    }

    @AfterMethod
    public void teardownMethod() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void teardownSuite() {
        // Cleanup if needed
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