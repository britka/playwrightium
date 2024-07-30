package org.brit.options;

import java.util.List;

/**
 * Created by Serhii Bryt
 * 23.06.2024 18:18
 **/
public enum Browsers {
    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit"),
    CHROME_CHANNEL("chrome"),
    MSEDGE_CHANNEL("msedge"),
    CHROME_BETA_CHANNEL("chrome-beta"),
    MSEDGE_BETA_CHANNEL("msedge-beta"),
    MSEDGE_DEV_CHANNEL("msedge-dev");

    private String value;

    private Browsers(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public static List<String> channelBrowsers = List.of(CHROME_CHANNEL.getValue(), MSEDGE_CHANNEL.getValue(),
            CHROME_BETA_CHANNEL.getValue(), MSEDGE_BETA_CHANNEL.getValue(), MSEDGE_DEV_CHANNEL.getValue());
}
