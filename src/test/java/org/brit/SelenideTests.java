package org.brit;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.actions;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideTests {
    @BeforeClass
    public void beforeClass() {
        // Configuration.browser = PWDriverProvider.class.getName();
        // Configuration.fileDownload = FileDownloadMode.HTTPGET;
        // Configuration.baseUrl = "http://the-internet.herokuapp.com";
        //  Configuration.browser = "chrome";
        // Configuration.remote = "https://moon-hsc.dev.internal.lanehealth.com/wd/hub";
    }

    @Test
    public void test5() throws IOException {
        List<String> paragraphs = new Faker().lorem().paragraphs(3);

        File file = new File("textLorem_" + new Date().getTime() + ".txt");
        FileUtils.writeLines(file, paragraphs);

        open("http://the-internet.herokuapp.com/upload");
        $("#file-upload").uploadFile(file);
        $("#file-submit").click();
        $("#uploaded-files").shouldBe(Condition.visible, text(file.getName()));


        open("http://the-internet.herokuapp.com/download");
        File download = $(byLinkText(file.getName())).download();
        System.out.println(FileUtils.readFileToString(download));
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
