package org.brit.pages;

import com.codeborne.selenide.SelenideElement;

import java.lang.reflect.InvocationTargetException;

import static com.codeborne.selenide.Selenide.$;

public class MainMenu {

    SelenideElement mainMenu = $(".header-links ul");

    private <T extends BasePage> T selectMenu(MainMenuItem menuItem, Class<T> pageToReturn){
        mainMenu.$("a.ico-" + menuItem.getValue()).click();
        try {
            return pageToReturn.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginPage selectLogin(){
        return selectMenu(MainMenuItem.LOGIN, LoginPage.class);
    }

    public RegisterPage selectRegister(){
        return selectMenu(MainMenuItem.REGISTER, RegisterPage.class);
    }

    public WhishListPage selectWishList(){
        return selectMenu(MainMenuItem.WISHLIST, WhishListPage.class);
    }

    public ShoppingCartPage selectShoppingCart(){
        return selectMenu(MainMenuItem.SHOPPING_CART, ShoppingCartPage.class);
    }

    public BasePage selectLogout(){
        return selectMenu(MainMenuItem.LOGOUT, BasePage.class);
    }

    public MyAccountPage selectMyAccount(){
        return selectMenu(MainMenuItem.MY_ACCOUNT, MyAccountPage.class);
    }
}
