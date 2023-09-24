package org.brit.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class PageMenu {
    SelenideElement topMenu = $(".top-menu");

    public CategoriesPage selectMainCategory(String category) {
        topMenu.$$("li > a").find(Condition.text(category)).click();
        return new CategoriesPage();
    }

    public ProductsPage selectCategorySubCategory(String category, String subcategory) {
        SelenideElement subcategoriesMenu = topMenu.$$("li > a").find(Condition.text(category)).hover();
        subcategoriesMenu
                .parent()
                .$(".sublist")
                .shouldBe(Condition.visible)
                .$$("li > a").find(Condition.text(subcategory))
                .click();
        sleep(3000);
        return new ProductsPage();
    }
}
