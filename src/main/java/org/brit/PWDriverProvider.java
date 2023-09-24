package org.brit;

import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.commands.Commands;
import org.brit.additional.PlaywrightiumSelectOption;
import org.brit.driver.PlaywrightWebDriver;
import org.brit.options.PlaywrightWebdriverOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;


public class PWDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightWebdriverOptions chromeOptions = new PlaywrightWebdriverOptions();
        chromeOptions.setCapability("headless", false);
        //Commands.getInstance().add("selectOption", new PlaywrightiumSelectOption());

        return new PlaywrightWebDriver(chromeOptions);
    }
}
