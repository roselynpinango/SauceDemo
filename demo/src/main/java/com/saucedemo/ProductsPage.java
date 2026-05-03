package com.saucedemo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProductsPage {
    @FindBy(id = "add-to-cart-sauce-labs-backpack")
    private WebElement addToCartBackpack;

    @FindBy(id = "add-to-cart-sauce-labs-bike-light")
    private WebElement addToCartBikeLight;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    public ProductsPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void addBackpackToCart() {
        addToCartBackpack.click();
    }

    public void addBikeLightToCart() {
        addToCartBikeLight.click();
    }

    public void goToCart() {
        cartIcon.click();
    }
}