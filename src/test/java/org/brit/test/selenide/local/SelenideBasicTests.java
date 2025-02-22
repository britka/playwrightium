package org.brit.test.selenide.local;

import com.codeborne.selenide.*;
import com.codeborne.selenide.testng.TextReport;
import com.github.javafaker.Faker;
import com.microsoft.playwright.options.AriaRole;
import org.apache.commons.io.FileUtils;
import org.brit.driver.PWDriverProvider;
import org.brit.locators.ArialSearchOptions;
import org.brit.locators.PlaywrightiumBy;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners({TextReport.class})
public class SelenideBasicTests {
    @BeforeClass
    public void beforeAll() {
        closeWebDriver();
    }

    @BeforeMethod
    public void beforeMethod() {
        Configuration.browser = PWDriverProvider.class.getName();
    }

    @AfterClass
    public void afterAll() {
        closeWebDriver();
    }

    @Test
    public void basicFunctionalTests() {
        Configuration.timeout = 10000;
        Configuration.headless = false;
        final String url = "https://testpages.herokuapp.com/styled/basic-html-form-test.html";
        open(url);
        Faker faker = new Faker();
        String name = faker.internet().emailAddress("some+2334777");
        String password = faker.internet().password();

        $(By.name("username")).setValue(name);
        $x("//input[@name='password']").setValue(password);

        String paragraph = faker.lorem().paragraph(5);
        $("textarea[name=comments]").setValue(paragraph);

        int checkBoxNum = faker.number().numberBetween(1, 4);
        ElementsCollection checkboxes = $$x("//input[@type='checkbox']");
        checkboxes.filter(Condition.checked).forEach(SelenideElement::click);

        SelenideElement checkBoxChecked = checkboxes.get(checkBoxNum - 1);
        SelenideElement checkBoxChecked1 = checkboxes.get(randomIntExcept(1, 4, checkBoxNum) - 1);
        String checkBoxValue = checkBoxChecked.attr("value");
        String checkBoxValue1 = checkBoxChecked1.getValue();
        checkBoxChecked.click();
        checkBoxChecked1.click();

        int radioButtonNum = faker.number().numberBetween(1, 4);
        SelenideElement radioButton = $$x("//input[@type='radio']")
                .find(Condition.value("rd" + radioButtonNum));
        String radioButtonValue = radioButton.getValue();
        radioButton.click();

        int firstItem = faker.number().numberBetween(0, 4);
        int secondItem = randomIntExcept(0, 4, firstItem);

        SelenideElement selectMulti = $(By.name("multipleselect[]"));

        selectMulti.selectOption(firstItem, secondItem);
        List<String> selectMultyValues = selectMulti.getSelectedOptions().attributes("value");

        SelenideElement select = $(By.name("dropdown"));
        select.selectOptionByValue("dd" + faker.number().numberBetween(1, 7));
        String selectValue = select.getValue();

        File file = new File(getClass().getClassLoader().getResource("textLorem.txt").getFile());
        $(By.name("filename")).uploadFile(file);

        $$("input[value=submit]")
                .find(Condition.attribute("name", "submitbutton"))
                .click();
        $x("//h1[contains(.,'Processed Form Details')]").shouldBe(Condition.visible);

        assertThat(getWebElementTextById("_valueusername")).isEqualTo(name);
        assertThat(getWebElementTextById("_valuepassword")).isEqualTo(password);
        assertThat(getWebElementTextById("_valuecomments")).isEqualTo(paragraph);
        assertThat(getWebElementsTextsBy("_valuecheckboxes"))
                .asList()
                .hasSameElementsAs(List.of(checkBoxValue, checkBoxValue1));
        assertThat(getWebElementTextById("_valueradioval")).isEqualTo(radioButtonValue);
        assertThat(getWebElementsTextsBy("_valuemultipleselect")).asList().isEqualTo(selectMultyValues);
        assertThat(getWebElementTextById("_valuedropdown")).isEqualTo(selectValue);

    }

    /*
    Unfortunately alerts are not working in Playwright as in Selenium.
    You should at first describe all actions as you wish and after that
    make actions that will invoke the alert.
    After that you may get alert text.

    But Selenide methods Selenide.confirm(), Selenide.dismiss() work as expected
    but returned text value will be null

    @see https://playwright.dev/java/docs/dialogs
     */
    @Test
    public void alertsTest() {
        Configuration.timeout = 10000;

        open("https://testpages.eviltester.com/styled/alerts/alert-test.html");

        $(By.id("alertexamples")).click();

        Alert alert = webdriver().object().switchTo().alert();
        alert.accept();

        $(By.id("confirmexample")).click();
        assertThat(alert.getText()).isEqualTo("I am a confirm alert");
        $(byId("confirmexplanation")).shouldHave(exactText("You clicked OK, confirm returned true."));
        $(byId("confirmreturn")).shouldHave(text("true"));

        alert = webdriver().object().switchTo().alert();
        alert.dismiss();
        $(byId("confirmexample")).click();
        assertThat(alert.getText()).isEqualTo("I am a confirm alert");
        $(byId("confirmexplanation")).shouldHave(exactText("You clicked Cancel, confirm returned false."));
        $(byId("confirmreturn")).shouldHave(text("false"));

        String testString = "Test string";
        alert = webdriver().object().switchTo().alert();
        alert.sendKeys(testString);
        alert.accept();
        $(byId("promptexample")).click();
        assertThat(alert.getText()).isEqualTo("I prompt you");
        $(By.id("promptexplanation")).shouldHave(exactText("You clicked OK. 'prompt' returned  " + testString));
        $(By.id("promptreturn")).shouldHave(text(testString));

    }

    @Test
    public void downloadTest() throws IOException {
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
    }

    @Test(dataProvider = "dataProvider")
    public void framesTest(String frameName, String frameText, int elementsCount) {
        open("https://testpages.eviltester.com/styled/frames/frames-test.html");
        switchTo().frame(frameName);
        $(By.xpath("//h1")).shouldHave(exactText(frameText));
        $$(By.xpath("//ul/li")).shouldHave(CollectionCondition.size(elementsCount));
        switchTo().defaultContent();
        $$x("//frame").shouldHave(CollectionCondition.size(5));
    }

    @Test
    public void issuesTest(){
//        open("https://the-internet.herokuapp.com/upload");
//        $("#file-submit").shouldNotHave(attribute("href"));
//        open("https://dequeuniversity.com/library/aria/liveregion-playground");
//        $("#default")
//                .shouldHave(attribute("aria-describedby", "role-help"));
        open("https://testpages.herokuapp.com/styled/basic-html-form-test.html");
        $(byName("username")).sendKeys(Keys.ESCAPE, "Some text", Keys.ENTER);
    }


    @DataProvider
    private Object[][] dataProvider() {
        return new Object[][]{
                {"left", "Left", 30},
                {"middle", "Middle", 40},
                {"right", "Right", 50}
        };
    }

    private String getWebElementTextById(String id) {
        return $(By.id(id)).shouldBe(Condition.visible).text();
    }

    private List<String> getWebElementsTextsBy(String idMask) {
        return $$(By.xpath("//*[contains(@id, '%s')]".formatted(idMask)))
                .shouldHave(CollectionCondition.sizeGreaterThan(1))
                .texts();
    }

    private int randomIntExcept(int start, int finish, int except) {
        Faker faker = new Faker();
        while (true) {
            int result = faker.number().numberBetween(start, finish);
            if (result != except) {
                return result;
            }
        }
    }
}
