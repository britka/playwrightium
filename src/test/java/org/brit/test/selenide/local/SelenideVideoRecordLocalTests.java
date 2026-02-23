package org.brit.test.selenide.local;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.TextReport;
import com.microsoft.playwright.options.AriaRole;
import org.brit.driver.PWDriverWithVideoProvider;
import org.brit.locators.ArialSearchOptions;
import org.brit.locators.PlaywrightiumBy;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.brit.test.selenide.Utils.generateTextFile;
import static org.brit.test.selenide.Utils.getLastRecordedFile;

/**
 * Created by Serhii Bryt
 * 27.02.2024 18:03
 **/
@Listeners({TextReport.class})
public final class SelenideVideoRecordLocalTests {

    @BeforeMethod
    @AfterMethod
    public void beforeMethod(){
        closeWebDriver();
        Configuration.browser = PWDriverWithVideoProvider.class.getName();
        Configuration.textCheck = FULL_TEXT;
    }

    @Test
    public void downloadWithVideoRecordLocalTest() throws IOException {
        File test = generateTextFile();

        open("https://the-internet.herokuapp.com/upload");
        $("#file-upload").uploadFile(test);
        $(PlaywrightiumBy.byRole(AriaRole.BUTTON, new ArialSearchOptions().setName("Upload"))).click();
        $("#uploaded-files").shouldHave(text(test.getName()));

        open("https://the-internet.herokuapp.com/download");
        File download = $(By.linkText(test.getName())).download();
        assertThat(test).hasSameTextualContentAs(download);
        closeWebDriver();

        File lastRecordedFile = getLastRecordedFile();
        assertThat(lastRecordedFile).content().isNotEmpty();
    }
}
