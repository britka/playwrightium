package org.brit.test.selenide.local;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.TextReport;
import com.microsoft.playwright.options.AriaRole;
import net.datafaker.Faker;
import org.apache.commons.io.FileUtils;
import org.brit.driver.PWDriverWithVideoProvider;
import org.brit.locators.ArialSearchOptions;
import org.brit.locators.PlaywrightiumBy;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Serhii Bryt
 * 27.02.2024 18:03
 **/
@Listeners({TextReport.class})
public class SelenideVideoRecordLocalTests {

    @BeforeClass
    public void beforeAll(){
        closeWebDriver();
    }

    @BeforeMethod
    public void beforeMethod(){
        closeWebDriver();
        Configuration.browser = PWDriverWithVideoProvider.class.getName();
    }

    @AfterClass
    public void afterClass(){
        closeWebDriver();
    }

    @Test
    public void downloadWithVideoRecordLocalTest() throws IOException {
        Faker faker = new Faker();
        File test = File.createTempFile("test", null);
        String paragraph = faker.lorem().paragraph(6);
        FileUtils.writeStringToFile(test, paragraph, Charset.defaultCharset());
        open("https://the-internet.herokuapp.com/upload");
        $("#file-upload").uploadFile(test);
        $(PlaywrightiumBy.byRole(AriaRole.BUTTON, new ArialSearchOptions().setName("Upload"))).click();
        $("#uploaded-files").shouldHave(text(test.getName()));
        open("https://the-internet.herokuapp.com/download");
        File download = $(By.linkText(test.getName())).download();
        assertThat(test).hasSameTextualContentAs(download);
        closeWebDriver();
        File lastRecordedFile = getLastRecordedFile();
        assertThat(lastRecordedFile.length()).isGreaterThan(0);

    }

    private File getLastRecordedFile() throws IOException {
        Path recordsFolder = Path.of("build/video");

        if (Files.isDirectory(recordsFolder)){
            Path path = Files.list(recordsFolder)
                    .filter(p -> !Files.isDirectory(p))
                    .max(Comparator.comparing(p -> p.toFile().lastModified())).orElse(null);
            if (path != null){
                return path.toFile();
            }
        }
        return null;
    }

}
