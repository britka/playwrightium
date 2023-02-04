package org.brit.driver.impl;

import com.microsoft.playwright.Locator;
import org.brit.driver.*;

import java.util.ArrayList;
import java.util.List;

public class PlaywrightElementsCollectionImpl implements PlaywrightElementsCollection {
    List<Locator> elements;
    Locator locator;

    public PlaywrightElementsCollectionImpl(Locator locator) {
        this.locator = locator;
        elements = all();
    }

    public PlaywrightElementsCollectionImpl(Locator locator, List<Locator> elements) {
        this.locator = locator;
        this.elements = elements;
    }

    private List<Locator> all() {
        int count = this.locator.count();
        return new ArrayList<>() {{
            for (int i = 0; i < count; i++) {
                add(locator.nth(i));
            }
        }};
    }


    @Override
    public List<String> texts() {
        return locator.allTextContents();
    }

    @Override
    public PlaywrightElement first() {
        return new PlaywrightElementImpl(locator.first());
    }

    @Override
    public PlaywrightElement last() {
        return new PlaywrightElementImpl(locator.last());
    }

    @Override
    public PlaywrightElementsCollection first(int count) {
        List<Locator> all = all();
        return new PlaywrightElementsCollectionImpl(locator, all.subList(0, count + 1));

    }

    @Override
    public PlaywrightElementsCollection last(int count) {
        List<Locator> all = all();
        return new PlaywrightElementsCollectionImpl(locator, all.subList(all.size() - 1 - count, all.size()));
    }

    @Override
    public long size() {
        return locator.count();
    }

    @Override
    public PlaywrightElementsCollection should(PWCollectionCondition condition) {
        return waitForCondition(condition);
    }

    private PlaywrightElementsCollection waitForCondition(PWCollectionCondition condition) {
        var defaultTimeout = Configuration.defaultTimeout;
        var curTimeout = 0;
        while (curTimeout < defaultTimeout) {
            if (condition.test(all())) {
                return this;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            curTimeout += 300;

        }
        try {
            throw new Exception("Smth wrong");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
