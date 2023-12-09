package org.brit.options;

import org.openqa.selenium.MutableCapabilities;

public class PlaywrightWebdriverOptions extends MutableCapabilities {

    public PlaywrightWebdriverOptions() {
        super();
        setHeadless(true);
        setBrowserName("chromium");
    }

    public void setHeadless(boolean headless) {
        setCapability("headless", headless);
    }

    public Boolean getHeadless() {
        return (Boolean) getCapability("headless");
    }

    public void setBrowserName(String browserName) {
        setCapability("browserName", browserName);
    }
}
