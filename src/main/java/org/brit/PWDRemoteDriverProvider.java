package org.brit;

import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.commands.Commands;
import org.brit.additional.PlaywrightiumSelectOption;
import org.brit.driver.PlaywrightWebDriver;
import org.brit.options.PlaywrightWebdriverOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

public class PWDRemoteDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightWebdriverOptions chromeOptions = new PlaywrightWebdriverOptions();
        chromeOptions.setCapability("headless", false);
        Commands.getInstance().add("selectOption", new PlaywrightiumSelectOption());

        chromeOptions.setCapability("browserName", "chrome");

//        chromeOptions.setCapability("selenoid:options", Map.<String, Object>of(
//                "enableVNC", true,
//                "enableVideo", true
//        ));


        PlaywrightWebDriver playwrightWebDriver = new PlaywrightWebDriver("http://moon.aerokube.local/wd/hub");
        return playwrightWebDriver;
    }
}
