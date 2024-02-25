package org.brit.driver;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.nio.file.Paths;


public class PWDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightiumOptions chromeOptions = new PlaywrightiumOptions();
        chromeOptions.setHeadless(false);
        chromeOptions.setRecordVideo(true);
        chromeOptions.setBrowserName("webkit");
        chromeOptions.setRecordsFolder(Paths.get("build/video"));
        chromeOptions.merge(capabilities);
        return new PlaywrightiumDriver(chromeOptions);
    }
}
