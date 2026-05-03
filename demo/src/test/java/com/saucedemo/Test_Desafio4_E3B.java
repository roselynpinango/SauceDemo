package com.saucedemo;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Test_Desafio4_E3B {
    String BASE_URL = "https://www.saucedemo.com/";
    private static final String SCREENSHOTS_DIR = "screenshots/incognito";
    private WebDriver driver;
    private int screenshotCounter = 0;

    @BeforeMethod
    public void setupMethod() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        takeScreenshot("LoginPage");
    }

    @Test
    public void testGenerarOrdenIncognito() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "No se redirigió a la página de productos");
        takeScreenshot("ProductsPage");

        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.goToCart();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "No se redirigió al carrito");
        takeScreenshot("CartPage");

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        takeScreenshot("CheckoutPage");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");
        checkoutPage.completeOrder();
        takeScreenshot("OrderComplete");

        Assert.assertEquals(checkoutPage.getOrderCompleteMessage(), "Thank you for your order!", "La orden no se completó correctamente");
    }

    @AfterMethod
    public void teardownMethod() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void takeScreenshot(String stepName) {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destPath = SCREENSHOTS_DIR + "/" + (++screenshotCounter) + "_" + stepName + ".png";
        try {
            Files.createDirectories(Paths.get(SCREENSHOTS_DIR));
            Files.copy(srcFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
