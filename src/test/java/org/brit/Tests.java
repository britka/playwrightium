package org.brit;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.appium.java_client.android.AndroidDriver;
import org.brit.driver.PWWDRunner;
import org.brit.driver.PlayWrightDriver;
import org.brit.driver.PlaywrightElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.brit.driver.FindLocatorsBy.byId;
import static org.brit.driver.FindLocatorsBy.byName;
import static org.brit.driver.PWCollectionCondition.hasSize;
import static org.brit.driver.PWCollectionCondition.hasSizeGreaterThen;
import static org.brit.driver.PWLib.*;

public class Tests {

    @Test
    public void test() {
        Configuration.browser = PWWDRunner.class.getName();
        Configuration.headless = false;
      //  Configuration.browserSize = "1960x1080";
        Selenide.open("http://the-internet.herokuapp.com/login");
        //Selenide.webdriver().driver().getWebDriver().manage().window().maximize();
        Selenide
                .$(byId("content"))
                .$("#login")
                .$x(".//input[@name='username']")
                .setValue("Some value");
        Selenide.sleep(2000);
    }
}
