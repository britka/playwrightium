package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

public class PWDriverWithVideoProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
        playwrightiumOptions.setHeadless(false);
        playwrightiumOptions.setRecordVideo(true);
        playwrightiumOptions.merge(capabilities);
        return new PlaywrightiumDriver(playwrightiumOptions);
    }
}
