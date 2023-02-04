package org.brit.driver;

import com.microsoft.playwright.Locator;

import java.util.List;

public class ListSizeGreaterThen extends PWCollectionCondition {
    private int size;
    public ListSizeGreaterThen(int size) {
        this.size = size;
    }

    @Override
    public boolean test(List<Locator> locators) {
        return locators.size() > size;
    }
}
