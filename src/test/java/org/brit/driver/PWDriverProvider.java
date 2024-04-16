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
        PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
        playwrightiumOptions.setHeadless(false);
        playwrightiumOptions.setGeolocation(new Geolocation(46.655, 32.617));
        playwrightiumOptions.setPermissions(List.of(Permissions.GEOLOCATION));
        playwrightiumOptions.merge(capabilities);
        return new PlaywrightiumDriver(playwrightiumOptions);
    }
}
