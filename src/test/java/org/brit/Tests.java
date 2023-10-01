package org.brit;

import com.codeborne.selenide.*;
import com.codeborne.selenide.commands.SelectOptionByTextOrIndex;
import com.github.javafaker.Faker;
import org.apache.commons.io.FileUtils;
import org.brit.additional.ClickAndConfirmReturnTextModal;
import org.brit.driver.PlaywrightWebDriver;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class Tests {

    public Tests() {
        Optional<SelectOptionByTextOrIndex> first = ServiceLoader.load(SelectOptionByTextOrIndex.class).findFirst();
    }

    @BeforeClass
    public void beforeClass() {
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.fileDownload = FileDownloadMode.FOLDER;
        Configuration.downloadsFolder = "downloads";
        Configuration.baseUrl = "http://the-internet.herokuapp.com";
        Configuration.headless = false;
        Configuration.timeout = 16000;
        Configuration.browserSize = "1880x960";

//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--remote-allow-origins=*");
//        chromeOptions.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//
//        chromeOptions.setHeadless(false);
//        chromeOptions.addArguments("--no-sandbox", "--disable-gpu", "window-size=1920,1080", "--disable-dev-shm-usage");
//        chromeOptions.setCapability("browserName", "chrome");
////            chromeOptions.setCapability("enableVNC", true);
//        chromeOptions.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//        chromeOptions.setCapability("selenoid:options", Map.<String, Object>of(
//                "enableVNC", true,
//                "enableVideo", false
//        ));
//        Configuration.browserCapabilities = chromeOptions;
//
//       Configuration.remote = "http://a210de77c35d149d6a9b3696eea1f295-1124507381.us-east-1.elb.amazonaws.com/:4444/wd/hub";
//    }
    }


    @Test
    public void test() throws IOException {
        open("http://the-internet.herokuapp.com/login");
        //Selenide.webdriver().driver().getWebDriver().manage().window().maximize();
        $(byId("content"))
                .$("#login")
                .$x(".//input[@name='username']")
                .setValue("some+23344444@provectus.com");
        $(byId("password"))
                .setValue("SuperSecretPassword!");
        $("button.radius").click();
        $("#flash.success")
                .shouldBe(Condition.visible)
                .shouldHave(text("You logged into a secure area!"));
        File screenshot = screenshot(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File("screen.png"));
        sleep(2000);
    }


    @Test
    public void test1() {
        //  Configuration.browserSize = "1960x1080";
        open("http://the-internet.herokuapp.com/checkboxes");
        ElementsCollection selenideElements = $$("input");
        for (SelenideElement element : selenideElements) {
            System.out.println(element.val());
        }

        webdriver().object().manage().addCookie(new Cookie("Some", "eeeeee"));
        webdriver().object().manage().deleteCookieNamed("Some");

    }

    @Test
    public void rest2() {
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setHeadless(true);

        open("http://the-internet.herokuapp.com/dynamic_loading/1");
        $("#start button").click();
        Wait().until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
        String h4 = $("#finish")
                .$("h4").text();

        assertThat(h4).isEqualTo("Hello World!");

    }


    @Test
    public void rest3() throws IOException {
        open("http://the-internet.herokuapp.com/dynamic_loading/1");
        sleep(3000);

        File screenshot1 = $("#start button").screenshot();
        //File screenshot = screenshot(OutputType.FILE);
        FileUtils.copyFile(screenshot1, new File("screen.png"));

    }

    @Test
    public void test4() {
        Configuration.browser = PWDriverProvider.class.getName();
        Configuration.timeout = 15000;

        open("http://the-internet.herokuapp.com/drag_and_drop");
        actions()
                .dragAndDrop($("#column-a"), $("#column-b"))
                .pause(Duration.ofSeconds(3))
                .build()
                .perform();
        $("#column-a").shouldHave(text("B"));
    }

    @Test
    public void test5() throws IOException {
        List<String> paragraphs = new Faker().lorem().paragraphs(3);

        File file = new File("textLorem_" + new Date().getTime() + ".txt");
        file.deleteOnExit();
        FileUtils.writeLines(file, paragraphs);

        open("/upload");
        $("#file-upload").uploadFile(file);
        $("#file-submit").click();
        $("#uploaded-files").shouldBe(Condition.visible, text(file.getName()));


        open("/download");
        Selenide.webdriver().object().getCurrentUrl();
        File download = $(byLinkText(file.getName())).download(DownloadOptions.using(FileDownloadMode.FOLDER));
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
                .sendKeys(Keys.META, "A")
                // .keyUp(Keys.META)
                .sendKeys(Keys.META, "C")
                .sendKeys(Keys.DELETE)
                // .keyDown(Keys.META)
                .sendKeys(Keys.META, "V")
                // .keyUp(Keys.META)
                .build()
                .perform();
        String text = comments.text();
        assertThat(text).isEqualTo(someText2);

    }

    @Test(dataProvider = "dataProvider")
    public void test6(String topFrame, String lowFrame, String textToVerify) {
        open("http://the-internet.herokuapp.com/nested_frames");
        if (topFrame != null) {
            switchTo()
                    .frame(topFrame);
        }
        if (lowFrame != null) {
            switchTo()
                    .frame(lowFrame);
        }
        String body = $("body").text();
        assertThat(body).contains(textToVerify);
        switchTo().defaultContent();
    }

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][]{
                {"frame-top", "frame-left", "LEFT"},
                {"frame-top", "frame-middle", "MIDDLE"},
                {"frame-top", "frame-right", "RIGHT"},
                {null, "frame-bottom", "BOTTOM"},
        };
    }


    @Test
    public void test7() {
        open("http://the-internet.herokuapp.com/javascript_alerts");
        PlaywrightWebElement pe = (PlaywrightWebElement) $$("button").find(Condition.exactText("Click for JS Alert")).getWrappedElement();


        final String[] textFromDialog = {null};

        pe.clickWithAlert(dialog -> {
            textFromDialog[0] = dialog.message();
            dialog.accept();
        });


        String s = textFromDialog[0];
        System.out.println(s);
    }

    @Test
    public void test8() {
        open("http://the-internet.herokuapp.com/dropdown");
        SelenideElement $ = $x("//select[@id='dropdown']");

        $.selectOption("Option 1");
        $.selectOption(2);

        $.selectOptionByValue("1");
        $.selectOptionContainingText("on 1");

        SelenideElement selectedOption = $.getSelectedOption();
        ElementsCollection selectedOptions = $.getSelectedOptions();

        System.out.println($.getSelectedOptionText());
        System.out.println($.getSelectedOptionValue());
    }

    @Test
    public void test9() {
        open("http://the-internet.herokuapp.com/javascript_alerts");

//        Alert playwrightuimAlert =  switchTo().alert();
//        playwrightuimAlert.sendKeys("Some text");

        prompt("Some text");
        $(byText("Click for JS Prompt")).click();
        $("#result").shouldHave(text("You entered: Some text"));

        confirm();
        $(byText("Click for JS Alert")).click();
        $("#result").shouldHave(text("You successfully clicked an alert"));

        dismiss();
        $(byText("Click for JS Confirm")).click();
        $("#result").shouldHave(text("You clicked: Cancel"));


//        String clickForJsConfirm = $(byText("Click for JS Confirm")).execute(new ClickAndConfirmReturnTextModal());
//        assertThat(clickForJsConfirm)
//                .isEqualTo("I am a JS Confirm");
    }


    private String getScript() {
        return "(function (select, values) {\n" +
               "  if (select.disabled) {\n" +
               "    return {disabledSelect: 'Cannot select option in a disabled select'};\n" +
               "  }\n" +
               "  select.focus();\n" +
               "\n" +
               "  function optionByValue(requestedValue) {\n" +
               "    return Array.from(select.options).find(option => option.value === requestedValue)\n" +
               "  }\n" +
               "\n" +
               "  const missingOptionsValues = values.filter(value => !optionByValue(value));\n" +
               "  if (missingOptionsValues.length > 0) {\n" +
               "    return {optionsNotFound: missingOptionsValues};\n" +
               "  }\n" +
               "\n" +
               "  const disabledOptionsValues = values.filter(value => optionByValue(value).disabled);\n" +
               "  if (disabledOptionsValues.length > 0) {\n" +
               "    return {disabledOptions: disabledOptionsValues};\n" +
               "  }\n" +
               "\n" +
               "  function getSelectedOptionsString(select) {\n" +
               "    return Array.from(select.options).map(option => option.selected).join(\",\");\n" +
               "  }\n" +
               "\n" +
               "  let previousSelectedOptions = getSelectedOptionsString(select);\n" +
               "  for (let requestedValue of values) {\n" +
               "    optionByValue(requestedValue).selected = 'selected';\n" +
               "  }\n" +
               "\n" +
               "  const event = document.createEvent('HTMLEvents');\n" +
               "  event.initEvent('click', true, true);\n" +
               "  select.dispatchEvent(event);\n" +
               "  if (getSelectedOptionsString(select) !== previousSelectedOptions) {\n" +
               "    event.initEvent('change', true, true);\n" +
               "    select.dispatchEvent(event);\n" +
               "  }\n" +
               "\n" +
               "  return {};\n" +
               "})(arguments[0], arguments[1])\n" +
               "\n";
    }


    @Test
    public void switchWindows() {
        open("http://the-internet.herokuapp.com/windows");

        String windowHandle = webdriver().object().getWindowHandle();

        $(byLinkText("Click Here")).click();
        sleep(1000);
        Selenide.switchTo().window(1);
        $("h3").shouldHave(exactText("New Window"));
        closeWindow();
        switchTo().window(0);
        $("h3").shouldHave(text("Opening a new window"));
        assertThat(switchTo().activeElement().getText())
                .isEqualTo("Click Here");

    }

}
