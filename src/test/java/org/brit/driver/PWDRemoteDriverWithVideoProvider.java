package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

public class PWDRemoteDriverWithVideoProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();
        chromeOptions.setConnectionByWS(false);
        chromeOptions.setHeadless(false);
        chromeOptions.setRecordVideo(true);
        chromeOptions.merge(capabilities);
        //new PlaywrightiumDriver("http://moon.aerokube.local", chromeOptions);
        return new PlaywrightiumDriver("http://localhost:4444/wd/hub", chromeOptions);
    }
}
