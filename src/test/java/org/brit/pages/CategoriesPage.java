package org.brit.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$$;

public class CategoriesPage extends BasePage{

    public ProductsPage selectSubCategory(String subcategory){
        $$(".item-box h2 a").find(Condition.text(subcategory)).click();
        return new ProductsPage();
    }
}
