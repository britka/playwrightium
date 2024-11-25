package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

public class PWDRemoteDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();
        chromeOptions.setConnectionByWS(false);
        chromeOptions.setHeadless(true);
     //   chromeOptions.merge(capabilities);
        //new PlaywrightiumDriver("http://moon.aerokube.local", chromeOptions);
        return new PlaywrightiumDriver("http://localhost:4444/wd/hub", chromeOptions);
    }
}
