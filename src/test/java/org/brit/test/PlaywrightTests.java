package org.brit.test;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Frame.GetByRoleOptions;
import com.microsoft.playwright.options.AriaRole;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.core5.net.URIBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightTests {

    Playwright playwright;
    BrowserContext browserContext;

    @BeforeClass
    public void beforeClass() {
        //System.setProperty("SELENIUM_REMOTE_URL", "http://localhost:4444/wd/hub");
//        Dotenv load = Dotenv.configure().systemProperties().load();
//        browserContext = playwright.chromium()
//                .connect(load.get("PLAYWRIGHT_SERVICE_URL") + "?cap={\"os\": \"linux\", \"runId\":\"%s\"}".formatted(new Date().getTime()),
//                        new BrowserType.ConnectOptions().setHeaders(
//                                Map.of("x-mpt-access-key", load.get("PLAYWRIGHT_SERVICE_ACCESS_TOKEN"))
//                        ))
//                .newContext();

        Map<String,String> env = new HashMap<>();
        env.put("SELENIUM_REMOTE_URL","http://localhost:4444/wd/hub");
        playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env));
        browserContext = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000)
                                .setDownloadsPath(Path.of("downloads")))
                .newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
    }

    @AfterClass
    public void afterClass() {
        browserContext.close();
        playwright.close();
    }

    @Test
    public void alertTests() {
        Page page = browserContext.newPage();
        page.navigate("http://the-internet.herokuapp.com/download");
        page.locator("//a[text()='file.txt']").click();
        System.out.println();
        page.close();
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
    public void testTest1() {
        Page page = browserContext.newPage();
        page.navigate("http://the-internet.herokuapp.com/hovers");
        Locator figures = page.locator(".figure");
        int count = 1;
        for (Locator figure : figures.all()) {
            figure.hover();
            assertThat(figure.locator("xpath=./div/h5")).hasText("name: user" + count);
            count++;
        }
        page.close();
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
    public void toPdf(){
        Page page = browserContext.newPage();
        page.navigate("https://www.selenium.dev/blog/2021/a-tour-of-4-authentication/");
        page.pdf(new Page.PdfOptions().setPath(Paths.get("asPdf.pdf")).setPreferCSSPageSize(true)
                .setLandscape(true));
    }

    @Test
    public void windowsTest() {
        Page page = browserContext.newPage();
        page.navigate("http://the-internet.herokuapp.com/windows");

        page.getByText("Click Here").click();

    }


}
