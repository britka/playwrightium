package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;


public class PWDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();
        chromeOptions.setHeadless(false);
        chromeOptions.merge(capabilities);
        return new PlaywrightiumDriver(chromeOptions);
    }
}
