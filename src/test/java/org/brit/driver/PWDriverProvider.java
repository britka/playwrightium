package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import com.microsoft.playwright.options.Geolocation;
import org.brit.options.Browsers;
import org.brit.options.PlaywrightiumOptions;
import org.brit.permission.Permissions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.util.List;


public class PWDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
        playwrightiumOptions.setHeadless(false);
        playwrightiumOptions.setGeolocation(new Geolocation(46.655, 32.617));
        playwrightiumOptions.setPermissions(List.of(Permissions.GEOLOCATION));
        playwrightiumOptions.setBrowserName(Browsers.CHROMIUM);
        playwrightiumOptions.merge(capabilities);
        playwrightiumOptions.setEnableTracing(true);
        return new PlaywrightiumDriver(playwrightiumOptions);
    }
}
