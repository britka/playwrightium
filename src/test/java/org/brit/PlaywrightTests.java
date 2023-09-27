package org.brit;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Frame.GetByRoleOptions;
import com.microsoft.playwright.options.AriaRole;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightTests {

    Playwright playwright = Playwright.create();
    BrowserContext browserContext;

    @BeforeClass
    public void beforeClass() {
        //System.setProperty("SELENIUM_REMOTE_URL", "http://localhost:4444/wd/hub");
        browserContext = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(false)
                                .setDownloadsPath(Path.of("downloads")))
                .newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
    }

    @Test
    public void alertTests() {
        Page page = browserContext.newPage();
        page.navigate("http://the-internet.herokuapp.com/download");
        page.locator("//a[text()='file.txt']").click();
        System.out.println();
    }

    @Test
    public void testTest() {
        Page page = browserContext.newPage();
        page.navigate("http://localhost:7080/javascript_alerts");
        Page waitForPopup = page.waitForPopup(null);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Click for JS Alert")).click();

        String content = waitForPopup.content();
    }

    @Test
    public void dropDown() {
        Page page = browserContext.newPage();
        page.navigate("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        Locator dropDown = page.locator("[name='multipleselect[]']");
        dropDown.scrollIntoViewIfNeeded();
        page.keyboard().press("Meta");
        page.waitForTimeout(1000);
        dropDown.selectOption("Selection Item 1");
        page.waitForTimeout(1000);
        dropDown.selectOption("Selection Item 2");
        page.waitForTimeout(1000);
        dropDown.selectOption("Selection Item 3");
        page.waitForTimeout(1000);
        page.keyboard().up("Meta");
    }

    @Test
    public void testKeys() {
        String someText = new Faker().lorem().paragraph(4);
        String someText2 = new Faker().lorem().paragraph(4);

        Page page = browserContext.newPage();
        page.navigate("https://testpages.herokuapp.com/styled/basic-html-form-test.html");

        Locator locator = page.locator("[name=comments]");
        locator.focus();
        locator.fill(someText);

        Keyboard keyboard = page.keyboard();
        keyboard.down("Meta");
        keyboard.press("a");
        keyboard.up("Meta");
        keyboard.down("Meta");
        keyboard.press("c");
        keyboard.up("Meta");
        //  keyboard.press("a");
        keyboard.press("Delete");
        keyboard.press("Meta+v");


    }

    @Test
    public void windowsTest() {
        Page page = browserContext.newPage();
        page.navigate("http://the-internet.herokuapp.com/windows");

        page.getByText("Click Here").click();

    }


}
