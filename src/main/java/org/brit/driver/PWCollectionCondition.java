package org.brit.driver;

import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.function.Predicate;

public abstract class PWCollectionCondition implements Predicate<List<Locator>> {

    public static PWCollectionCondition hasSize(int size){
        return new ListSizeCheck(size);
    }

    public static PWCollectionCondition hasSizeGreaterThen(int size){
        return new ListSizeGreaterThen(size);
    }

    @Override
    public abstract boolean test(List<Locator> locators);

    @Override
    public Predicate<List<Locator>> and(Predicate<? super List<Locator>> other) {
        return Predicate.super.and(other);
    }

    @Override
    public Predicate<List<Locator>> negate() {
        return Predicate.super.negate();
    }

    @Override
    public Predicate<List<Locator>> or(Predicate<? super List<Locator>> other) {
        return Predicate.super.or(other);
    }
}
