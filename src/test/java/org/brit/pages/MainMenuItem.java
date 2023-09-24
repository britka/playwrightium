package org.brit.pages;

public enum MainMenuItem {
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    WISHLIST("wishlist"),
    SHOPPING_CART("cart"),
    MY_ACCOUNT("account");

    private String value;

    MainMenuItem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
