package org.brit.test;

import com.codeborne.selenide.*;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.brit.driver.PWDriverProvider;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.print.PageMargin;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.actions;
import static java.util.Base64.getDecoder;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideTests {
    @BeforeClass
    public void beforeClass() {
         //Configuration.browser = PWDriverProvider.class.getName();
        // Configuration.fileDownload = FileDownloadMode.HTTPGET;
        // Configuration.baseUrl = "http://the-internet.herokuapp.com";
        //  Configuration.browser = "chrome";
        // Configuration.remote = "https://moon-hsc.dev.internal.lanehealth.com/wd/hub";
        Configuration.browser = "firefox";
    }

    @Test
    public void test5() throws IOException {
        List<String> paragraphs = new Faker().lorem().paragraphs(3);

        File file = new File("textLorem_" + new Date().getTime() + ".txt");
        FileUtils.writeLines(file, paragraphs);

        open("http://the-internet.herokuapp.com/upload");
        $("#file-upload").highlight().uploadFile(file);
        $("#file-submit").highlight().click();
        $("#uploaded-files").highlight().shouldBe(Condition.visible, text(file.getName()));

        open("http://the-internet.herokuapp.com/download");
        File download = $(byLinkText(file.getName())).download();
        System.out.println(FileUtils.readFileToString(download));
    }

    @SneakyThrows
    @Test
    public void toPdf() {
        open("https://base64.guru/developers/java/examples/decode-pdf#:~:text=To%20convert%20a%20Base64%20string,array%2C%20not%20a%20string).");
        PrintOptions printOptions = new PrintOptions();
        printOptions.setShrinkToFit(true);
        Pdf print = ((RemoteWebDriver) webdriver().object()).print(printOptions);
        FileUtils.writeByteArrayToFile(new File("asPdf.pdf"), getDecoder().decode(print.getContent()));
    }

    @Test
    public void combinationsTests() {
        String someText = new Faker().lorem().paragraph(4);
        String someText2 = new Faker().lorem().paragraph(4);

        open("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        SelenideElement comments = $(byName("comments"));
        comments.setValue(someText).click();
        sleep(2000);
        actions()
                // .keyDown(Keys.META)
                .sendKeys(Keys.chord(Keys.META, "A"))
                .pause(Duration.ofSeconds(2))
                // .keyUp(Keys.META)
                .sendKeys(Keys.chord(Keys.META, "C"))
                .pause(Duration.ofSeconds(2))
                .sendKeys(Keys.DELETE)
                .pause(Duration.ofSeconds(2))
                // .keyDown(Keys.META)
                .sendKeys(Keys.chord(Keys.META, "V"))
                .pause(Duration.ofSeconds(2))
                // .keyUp(Keys.META)
                .build()
                .perform();
        String text = comments.text();
        assertThat(text).isEqualTo(someText2);

    }


}
