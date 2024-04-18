package org.brit.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import org.brit.emulation.Device;
import org.brit.emulation.DeviceUtils;
import org.brit.options.PlaywrightiumOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;


public class PWDriverDeviceProvider implements WebDriverProvider {

    public static Device device = DeviceUtils.getDeviceByName("Galaxy S5 landscape");

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        Configuration.browserSize = null;
        PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
        playwrightiumOptions.setHeadless(false);
        playwrightiumOptions.merge(capabilities);
        playwrightiumOptions.setEmulation(device);
        playwrightiumOptions.setLocale(new Locale("el", "GR"));
        playwrightiumOptions.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Athens")));
        return new PlaywrightiumDriver(playwrightiumOptions);
    }
}
