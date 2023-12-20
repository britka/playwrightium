package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

public class PWDRemoteDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();

//        chromeOptions.setCapability("selenoid:options", Map.<String, Object>of(
//                "enableVNC", true,
//                "enableVideo", true
//        ));


        PlaywrightiumDriver playwrightiumDriver = new PlaywrightiumDriver("http://moon.aerokube.local");
        return playwrightiumDriver;
    }
}
