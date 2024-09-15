package org.brit.test.playwrightium;

import com.github.javafaker.Faker;
import com.microsoft.playwright.options.AriaRole;
import org.brit.additional.PlaywrightiumSelect;
import org.brit.driver.PlaywrightiumDriver;
import org.brit.locators.ArialSearchOptions;
import org.brit.locators.PlaywrightiumBy;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class PlaywrightiumBasicTests {

    static WebDriver driver;

    @BeforeClass
    public void beforeClass() {
      //  PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
       // playwrightiumOptions.setRecordsFolder(Paths.get("videos"));
        driver = new PlaywrightiumDriver();
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void basicFunctionalTests() {
        final String url = "https://testpages.eviltester.com/styled/basic-html-form-test.html";

        Faker faker = new Faker();
        driver.get(url);

        String name = faker.name().firstName();
        String password = faker.internet().password();

        driver.findElement(By.name("username")).sendKeys(name);
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);

        String paragraph = faker.lorem().paragraph(5);
        WebElement textarea = driver.findElement(By.cssSelector("textarea[name=comments]"));
        textarea.clear();
        textarea.sendKeys(paragraph);

        int checkBoxNum = faker.number().numberBetween(1, 4);
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        checkboxes.stream().filter(WebElement::isSelected).forEach(WebElement::click);
        WebElement checkBoxChecked = checkboxes.get(checkBoxNum - 1);
        String checkBoxValue = checkBoxChecked.getAttribute("value");
        checkBoxChecked.click();

        int radioButtonNum = faker.number().numberBetween(1, 4);
        WebElement radioButton = driver.findElement(By.xpath("//input[@type='radio'][@value='rd%s']".formatted(radioButtonNum)));
        String radioButtonValue = radioButton.getAttribute("value");
        radioButton.click();

        ISelect selectMulti = new PlaywrightiumSelect(driver.findElement(By.name("multipleselect[]")));
        selectMulti.deselectAll();
        int firstItem = faker.number().numberBetween(0, 4);
        int secondItem = randomIntExcept(0, 4, firstItem);

        selectMulti.selectByIndex(firstItem);
        selectMulti.selectByIndex(secondItem);

        List<String> selectMultyValues = selectMulti.getAllSelectedOptions().stream().map(p -> p.getAttribute("value")).toList();

        ISelect select = new PlaywrightiumSelect(driver.findElement(By.name("dropdown")));
        select.selectByValue("dd" + faker.number().numberBetween(1, 7));
        String selectValue = select.getFirstSelectedOption().getAttribute("value");

        File file = new File(getClass().getClassLoader().getResource("textLorem.txt").getFile());
        driver.findElement(By.name("filename")).sendKeys(file.getAbsolutePath());

        driver.findElement(By.cssSelector("input[value=submit][name=submitbutton]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(.,'Processed Form Details')]")));

        assertThat(getWebElementTextById("_valueusername", driver))
                .isEqualTo(name);
        assertThat(getWebElementTextById("_valuepassword", driver))
                .isEqualTo(password);
        assertThat(getWebElementTextById("_valuecomments", driver))
                .isEqualTo(paragraph);
        assertThat(getWebElementsTextsBy("_valuecheckboxes", driver))
                .asList()
                .isEqualTo(List.of(checkBoxValue));
        assertThat(getWebElementTextById("_valueradioval", driver))
                .isEqualTo(radioButtonValue);
        assertThat(getWebElementsTextsBy("_valuemultipleselect", driver))
                .asList()
                .isEqualTo(selectMultyValues);
        assertThat(getWebElementTextById("_valuedropdown", driver))
                .isEqualTo(selectValue);
        assertThat(getWebElementTextById("_valuefilename", driver))
                .isEqualTo(file.getName());
        ((JavascriptExecutor) driver).executeScript("return alert();");
    }


    @Test(dataProvider = "dataProvider")
    public void framesTest(String frameName, String frameText, int elementsCount) {
        driver.get("https://testpages.eviltester.com/styled/frames/frames-test.html");
        driver.switchTo().frame(frameName);
        String text = driver.findElement(By.xpath("//h1")).getText();
        int size = driver.findElements(By.xpath("//ul/li")).size();
        assertThat(text).isEqualTo(frameText);
        assertThat(size).isEqualTo(elementsCount);
        driver.switchTo().defaultContent();
        assertThat(driver.findElements(By.xpath("//frame")).size()).isEqualTo(5);
    }

    @Test
    public void alertsTest() {
        driver.get("https://testpages.eviltester.com/styled/alerts/alert-test.html");

        Alert alert = driver.switchTo().alert();
        alert.accept();
        driver.findElement(By.id("alertexamples")).click();
        assertThat(alert.getText()).isEqualTo("I am an alert box!");

        alert = driver.switchTo().alert();
        alert.accept();
        driver.findElement(By.id("confirmexample")).click();
        assertThat(alert.getText()).isEqualTo("I am a confirm alert");
        assertThat(driver.findElement(By.id("confirmexplanation")).getText()).isEqualTo("You clicked OK, confirm returned true.");
        assertThat(driver.findElement(By.id("confirmreturn")).getText()).isEqualTo("true");

        alert = driver.switchTo().alert();
        alert.dismiss();
        driver.findElement(By.id("confirmexample")).click();
        assertThat(alert.getText()).isEqualTo("I am a confirm alert");
        assertThat(driver.findElement(By.id("confirmexplanation")).getText()).isEqualTo("You clicked Cancel, confirm returned false.");
        assertThat(driver.findElement(By.id("confirmreturn")).getText()).isEqualTo("false");


        String testString = "Test string";

        alert = driver.switchTo().alert();
        alert.sendKeys(testString);
        alert.accept();
        driver.findElement(By.id("promptexample")).click();
        assertThat(alert.getText()).isEqualTo("I prompt you");
        assertThat(driver.findElement(By.id("promptexplanation")).getText()).isEqualTo("You clicked OK. 'prompt' returned  " + testString);
        assertThat(driver.findElement(By.id("promptreturn")).getText()).isEqualTo(testString);

        alert = driver.switchTo().alert();
        alert.sendKeys(testString);
        alert.dismiss();
        driver.findElement(By.id("promptexample")).click();
        assertThat(alert.getText()).isEqualTo("I prompt you");
        assertThat(driver.findElement(By.id("promptexplanation")).getText()).isEqualTo("You clicked Cancel. 'prompt' returned null");
    }

    @Test
    public void dragAndDropTest() {
        driver.get("https://testpages.eviltester.com/styled/drag-drop-javascript.html");
        assertThat(driver.findElement(By.id("droppable1")).getText().trim()).contains("Drop here");
        new Actions(driver)
                .dragAndDrop(driver.findElement(By.id("draggable1")), driver.findElement(By.id("droppable1")))
                .pause(Duration.ofSeconds(2))
                .build()
                .perform();
        assertThat(driver.findElement(By.id("droppable1")).getText().trim()).contains("Dropped!");
    }

    @Test
    public void moveToTest() {
        driver.get("https://testpages.eviltester.com/styled/csspseudo/css-hover.html");
        Actions actions = new Actions(driver);
        actions
                .moveToElement(driver.findElement(By.id("hoverpara")))
                .build()
                .perform();
        assertThat(driver.findElement(By.id("hoverparaeffect")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("hoverparaeffect")).getText())
                .isEqualTo("You can see this paragraph now that you hovered on the above 'button'.");

        actions
                .moveToElement(driver.findElement(By.id("hoverdiv")))
                .perform();
        driver.findElement(By.id("hoverlink")).click();
        WebElement aReturn = driver.findElement(PlaywrightiumBy.byRole(AriaRole.LINK,
                new ArialSearchOptions().setName(Pattern.compile("Return"))));
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOf(aReturn));
    }

    @Test
    public void actionTest() {
        driver.get("https://testpages.herokuapp.com/styled/basic-html-form-test.html");
        String testMessage = "Test message 1";
        String testMessage2 = "Test 2 message";

        WebElement comments = driver.findElement(By.name("comments"));

        Actions actions = new Actions(driver);

        Keys cmdCtrl = Platform.getCurrent().is(Platform.MAC) ? Keys.COMMAND : Keys.CONTROL;

        comments.clear();
        new Actions(driver)
                .sendKeys(comments, "Selenium!")
                .sendKeys(Keys.ARROW_LEFT)
                .keyDown(Keys.SHIFT)
                .sendKeys(Keys.ARROW_UP)
                .keyUp(Keys.SHIFT)
                .keyDown(cmdCtrl)
                .sendKeys("xvv")
                .keyUp(cmdCtrl)
                .perform();
        assertThat(comments.getAttribute("value")).isEqualTo("SeleniumSelenium!");
        WebElement username = driver.findElement(By.name("username"));
        actions
                .keyDown(cmdCtrl)
                .sendKeys("ac")
                .keyUp(cmdCtrl)
                .keyDown(Keys.DELETE)
                .keyUp(Keys.DELETE)
                .keyDown(username, cmdCtrl)
                .sendKeys("v")
                .keyUp(cmdCtrl)
                .perform();

        assertThat(username.getAttribute("value")).isEqualTo("SeleniumSelenium!");
    }

    @Test
    public void sendKeysTest(){
        driver.get("https://google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("Playwright.", "dev", Keys.ENTER);
        List<WebElement> until = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#rso div.g a>h3"), 8));
        assertThat(until.stream().map(WebElement::getText).toList()).allMatch(s -> s.toLowerCase().contains("playwright"));
    }

    private Object[][] dataProvider() {
        return new Object[][]{
                {"left", "Left", 30},
                {"middle", "Middle", 40},
                {"right", "Right", 50}
        };
    }

    private String getWebElementTextById(String id, WebDriver driver) {
        return driver.findElement(By.id(id)).getText();
    }

    private List<String> getWebElementsTextsBy(String idMask, WebDriver driver) {
        return driver.findElements(By.xpath("//*[contains(@id, '%s')]".formatted(idMask))).stream().map(WebElement::getText).toList();
    }

    private int randomIntExcept(int start, int finish, int except) {
        Faker faker = new Faker();
        int result = faker.number().numberBetween(start, finish);
        if (result == except) {
            randomIntExcept(start, finish, except);
        }
        return result;

    }
}
