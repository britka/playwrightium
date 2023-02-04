package org.brit.driver;

import com.microsoft.playwright.Locator;

import java.util.List;

public class ListSizeCheck extends PWCollectionCondition {
    int size;

    public ListSizeCheck(int size) {
        this.size = size;
    }

    @Override
    public boolean test(List<Locator> locators) {
        return locators.size() == this.size;
    }
}
