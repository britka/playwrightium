package org.brit.test.selenide.local;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.TextReport;
import org.brit.driver.PWDriverDeviceProvider;
import org.brit.driver.PWDriverProvider;
import org.brit.emulation.Device;
import org.openqa.selenium.Dimension;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Serhii Bryt
 * 01.04.2024 16:18
 **/
@Listeners({TextReport.class})
public final class SelenideDevicesTests {

    @BeforeMethod
    @AfterMethod
    public void afterMethod(){
        closeWebDriver();
        Configuration.textCheck = FULL_TEXT;
    }

    @Test
    public void deviceTest() {
        Configuration.browser = PWDriverDeviceProvider.class.getName();
        open("https://google.com");
        Dimension dimension = webdriver().object().manage().window().getSize();
        Device device = PWDriverDeviceProvider.device;
        assertThat(dimension.getHeight()).isEqualTo(device.getViewport().height);
        assertThat(dimension.getWidth()).isEqualTo(device.getViewport().width);
        String timeZone = executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().timeZone");
        assertThat(timeZone).isEqualTo("Europe/Athens");
        String locale = executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().locale");
        assertThat(locale).isEqualTo("el-GR");
    }

    @Test
    public void geolocationTest() {
        Configuration.browser = PWDriverProvider.class.getName();
        open("https://www.bing.com/maps");
        $("button.locateMeBtn,a#LocateMeButton").click();
        webdriver().shouldHave(urlContaining("46.655")).shouldHave(urlContaining("32.617"));
    }
}
