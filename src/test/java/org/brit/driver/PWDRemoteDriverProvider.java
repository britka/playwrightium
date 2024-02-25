package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import com.google.gson.Gson;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.util.Map;

public class PWDRemoteDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();
        chromeOptions.setConnectionByWS(false);
        chromeOptions.setHeadless(false);
        chromeOptions.setRecordVideo(true);

        PlaywrightiumDriver playwrightiumDriver =
                new PlaywrightiumDriver("http://localhost:4444/wd/hub", chromeOptions);
        return playwrightiumDriver;
    }
}
