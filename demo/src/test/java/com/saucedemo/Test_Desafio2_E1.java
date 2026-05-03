package com.saucedemo;

import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Test_Desafio2_E1 {
    String BASE_URL = "https://www.saucedemo.com/";

    @Test
    public void testGenerarOrden() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get(BASE_URL);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");

        // Agregar productos al carrito
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();

        // Ir al carrito
        productsPage.goToCart();

        // Proceder al checkout
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        // Llenar información de checkout
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCheckoutInfo("John", "Doe", "12345");

        // Completar la orden
        checkoutPage.completeOrder();

        driver.quit();
    }
}