package org.brit.driver;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ViewportSize;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.Logs;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PWDriver implements WebDriver {
    private Playwright playwright;
    private BrowserContext browserContext;
    private Page page;

    public PWDriver() {
        playwright = Playwright.create();
        browserContext = playwright
                .chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false))
                .newContext();
        page = browserContext.newPage();
    }

    @Override
    public void get(String url) {
        page.navigate(url);
    }

    @Override
    public String getCurrentUrl() {
        return page.url();
    }

    @Override
    public String getTitle() {
        return page.title();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return Collections.unmodifiableList(new ArrayList<PWWebElement>());
    }

    @Override
    public WebElement findElement(By by) {
        String using = ((By.Remotable) by).getRemoteParameters().using();
        String value = ((By.Remotable) by).getRemoteParameters().value().toString();
        return switch (using) {
            case "css selector" -> new PWWebElement(page.locator(value));
            case "class name" -> new PWWebElement(page.locator("xpath=//*[@class='%s']".formatted(value)));
            case "xpath" -> new PWWebElement(page.locator("xpath=" + value));
            case "tag name" -> new PWWebElement(page.locator("xpath=//" + value));
            case "name" -> new PWWebElement(page.locator("[name='%s']".formatted(value)));
            case "partial link text" -> new PWWebElement(page.locator("xpath=//a[contains(.,'%s')]".formatted(value)));
            case "link text" -> new PWWebElement(page.locator("xpath=//a[text()='%s']".formatted(value)));
            case "id" -> new PWWebElement(page.locator("#%s".formatted(value)));
            default -> null;
        };
    }

    @Override
    public String getPageSource() {
        return page.content();
    }

    @Override
    public void close() {
        browserContext.close();
    }

    @Override
    public void quit() {
        playwright.close();
    }

    @Override
    public Set<String> getWindowHandles() {

        return null;
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return null;
    }

    @Override
    public Navigation navigate() {
        return new PWNavigation(page);
    }

    @Override
    public Options manage() {
        return new PWOptions(page);
    }



    public static class PWOptions implements Options{
        Page page;


        public PWOptions(Page page) {
            this.page = page;
        }

        @Override
        public void addCookie(Cookie cookie) {
           // page.context().addCookies(List.of(cookie));
        }

        @Override
        public void deleteCookieNamed(String name) {
            List<com.microsoft.playwright.options.Cookie> cookies = page.context().cookies();
        }

        @Override
        public void deleteCookie(Cookie cookie) {

        }

        @Override
        public void deleteAllCookies() {

        }

        @Override
        public Set<Cookie> getCookies() {
            return null;
        }

        @Override
        public Cookie getCookieNamed(String name) {
            return null;
        }

        @Override
        public Timeouts timeouts() {
            return new PWTimeouts();
        }

        @Override
        public Window window() {
            return new PWWindow(page);
        }

        @Override
        public Logs logs() {
            return null;
        }
    }

    public static class PWTimeouts implements Timeouts{

        @Override
        public Timeouts implicitlyWait(long time, TimeUnit unit) {
            return null;
        }

        @Override
        public Timeouts implicitlyWait(Duration duration) {
            return Timeouts.super.implicitlyWait(duration);
        }

        @Override
        public Duration getImplicitWaitTimeout() {
            return Timeouts.super.getImplicitWaitTimeout();
        }

        @Override
        public Timeouts setScriptTimeout(long time, TimeUnit unit) {
            return null;
        }

        @Override
        public Timeouts setScriptTimeout(Duration duration) {
            return Timeouts.super.setScriptTimeout(duration);
        }

        @Override
        public Timeouts scriptTimeout(Duration duration) {
            return Timeouts.super.scriptTimeout(duration);
        }

        @Override
        public Duration getScriptTimeout() {
            return Timeouts.super.getScriptTimeout();
        }

        @Override
        public Timeouts pageLoadTimeout(long time, TimeUnit unit) {
            return null;
        }

        @Override
        public Timeouts pageLoadTimeout(Duration duration) {
            return Timeouts.super.pageLoadTimeout(duration);
        }

        @Override
        public Duration getPageLoadTimeout() {
            return Timeouts.super.getPageLoadTimeout();
        }
    }

    public static class PWWindow implements Window{

        Page page;

        public PWWindow(Page page) {
            this.page = page;
        }

        @Override
        public Dimension getSize() {
            ViewportSize viewportSize = page.viewportSize();
            return new Dimension(viewportSize.width, viewportSize.height);
        }

        @Override
        public void setSize(Dimension targetSize) {
            page.setViewportSize(targetSize.getWidth(), targetSize.getHeight());
        }

        @Override
        public Point getPosition() {
            return null;
        }

        @Override
        public void setPosition(Point targetPosition) {

        }

        @Override
        public void maximize() {
            page.setViewportSize(0,0);
        }

        @Override
        public void minimize() {

        }

        @Override
        public void fullscreen() {

        }
    }

    public static class PWNavigation implements Navigation{
        Page page;

        public PWNavigation(Page page) {
            this.page = page;
        }

        @Override
        public void back() {
            page.goBack();
        }

        @Override
        public void forward() {
            page.goForward();
        }

        @Override
        public void to(String url) {
            page.navigate(url);
        }

        @Override
        public void to(URL url) {
            page.navigate(url.toString());
        }

        @Override
        public void refresh() {
            page.reload();
        }
    }


}
