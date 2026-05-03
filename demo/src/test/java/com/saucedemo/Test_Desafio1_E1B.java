package com.saucedemo;

import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Test_Desafio1_E1B {
    @Test
    public void login() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/");
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
                
        WebElement txtUsuario = driver.findElement(By.id("user-name"));
        txtUsuario.sendKeys("standard_user");
        WebElement txtPassword = driver.findElement(By.id("password"));
        txtPassword.sendKeys("secret_sauce");
        WebElement btnLogin = driver.findElement(By.id("login-button"));
        btnLogin.click();
    }
}
