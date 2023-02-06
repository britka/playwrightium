package org.brit;

import com.codeborne.selenide.WebDriverProvider;
import org.brit.driver.PlawrightWebDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

public class PWDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        return new PlawrightWebDriver();
    }
}
