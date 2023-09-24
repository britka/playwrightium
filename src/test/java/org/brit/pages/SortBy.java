package org.brit.pages;

public enum SortBy {
    POSITION("0"),
    NAME_A_TO_Z("5"),
    NAME_Z_TO_A("6"),
    PRICE_LOW_TO_HIGH("10"),
    PRICE_HIGH_TO_LOW("11"),
    CREATED_ON("15");

    private String optionValue;

    SortBy(String optionValue) {
        this.optionValue = optionValue;
    }

    public String optionValue() {
        return optionValue;
    }
}
