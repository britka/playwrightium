package org.brit.driver;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ViewportSize;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.Logs;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlawrightWebDriver implements WebDriver, TakesScreenshot {
    private Playwright playwright;
    private BrowserContext browserContext;
    private Page page;


    public PlawrightWebDriver() {
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
        return getLocatorFromBy(by).all()
                .stream().map(PlaywrightWebElement::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public WebElement findElement(By by) {
        return new PlaywrightWebElement(getLocatorFromBy(by));
    }

    private Locator getLocatorFromBy(By by) {
        String using = ((By.Remotable) by).getRemoteParameters().using();
        String value = ((By.Remotable) by).getRemoteParameters().value().toString();
        return switch (using) {
            case "css selector" -> page.locator(value);
            case "class name" -> page.locator("xpath=//*[@class='%s']".formatted(value));
            case "xpath" -> page.locator("xpath=" + value);
            case "tag name" -> page.locator("xpath=//" + value);
            case "name" -> page.locator("[name='%s']".formatted(value));
            case "partial link text" -> page.locator("xpath=//a[contains(.,'%s')]".formatted(value));
            case "link text" -> page.locator("xpath=//a[text()='%s']".formatted(value));
            case "id" -> page.locator("#%s".formatted(value));
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
        return new PlaywrightWebdriverNavigation(page);
    }

    @Override
    public Options manage() {
        return new PlawrightWebdriverOptions(page);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

        if (target.getClass() == OutputType.FILE.getClass()) {
            return target.convertFromPngBytes(screenshot);
        } else if (target.getClass() == OutputType.BYTES.getClass()) {
            return (X) screenshot;
        } else if (target.getClass() == OutputType.BASE64.getClass()) {
            return (X) Base64.getEncoder().encodeToString(screenshot);
        }
        return null;
    }


    public static class PlawrightWebdriverOptions implements Options {
        Page page;


        public PlawrightWebdriverOptions(Page page) {
            this.page = page;
        }

        @Override
        public void addCookie(Cookie cookie) {
            page.context().addCookies(List.of(convertToPlaywrightCookie(cookie)));
        }

        @Override
        public void deleteCookieNamed(String name) {
            List<com.microsoft.playwright.options.Cookie> cookies = page.context().cookies();
            List<com.microsoft.playwright.options.Cookie> cookiesToAdd = cookies
                    .stream()
                    .filter(c -> !c.name.equals(name))
                    .toList();
            page.context().clearCookies();
            page.context().addCookies(cookiesToAdd);
        }

        @Override
        public void deleteCookie(Cookie cookie) {
            deleteCookieNamed(cookie.getName());
        }

        @Override
        public void deleteAllCookies() {
            page.context().clearCookies();
        }

        @Override
        public Set<Cookie> getCookies() {
            List<com.microsoft.playwright.options.Cookie> cookies = page.context().cookies();
            return cookies.stream().map(this::convertToWebDriverCookie).collect(Collectors.toSet());
        }


        private Cookie convertToWebDriverCookie(com.microsoft.playwright.options.Cookie cookie) {
            Cookie wdCookie = new Cookie(
                    cookie.name,
                    cookie.value,
                    cookie.domain,
                    cookie.path,
                    Date.from(Instant.ofEpochSecond(cookie.expires.longValue())),
                    cookie.secure,
                    cookie.httpOnly,
                    cookie.sameSite.name());
            return wdCookie;
        }

        private com.microsoft.playwright.options.Cookie convertToPlaywrightCookie(Cookie cookie) {
            com.microsoft.playwright.options.Cookie pwCookie =
                    new com.microsoft.playwright.options.Cookie(cookie.getName(), cookie.getValue());
            pwCookie.setDomain(cookie.getDomain())
                    .setPath(cookie.getPath());
            if (cookie.getDomain() == null || cookie.getDomain().equals("")) {
                String domain = page.url().split("//")[1].split(":")[0].split("/")[0];
                pwCookie.setDomain(domain);
            } else {
                pwCookie.setDomain(cookie.getDomain());
            }
            pwCookie.setSecure(cookie.isHttpOnly());

            pwCookie.setExpires(cookie.getExpiry() == null ? -1.0 : cookie.getExpiry().getTime() / 1000.0);
            return pwCookie;
        }

        @Override
        public Cookie getCookieNamed(String name) {
            return convertToWebDriverCookie(page.context()
                    .cookies()
                    .stream()
                    .filter(c -> c.name.equals(name))
                    .findFirst().get());
        }

        @Override
        public Timeouts timeouts() {
            return new PlaywrightWebdriverTimeouts(page);
        }

        @Override
        public Window window() {
            return new PpaywrightWebdriverWindow(page);
        }

        @Override
        public Logs logs() {
            return null;
        }
    }

    public static class PlaywrightWebdriverNavigation implements Navigation {
        Page page;

        public PlaywrightWebdriverNavigation(Page page) {
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

    public static class PlaywrightWebdriverTargetLocator implements TargetLocator {

        Page page;

        public PlaywrightWebdriverTargetLocator(Page page) {
            this.page = page;
        }

        @Override
        public WebDriver frame(int index) {
            return null;
        }

        @Override
        public WebDriver frame(String nameOrId) {
            return null;
        }

        @Override
        public WebDriver frame(WebElement frameElement) {
            return null;
        }

        @Override
        public WebDriver parentFrame() {
            return null;
        }

        @Override
        public WebDriver window(String nameOrHandle) {
            return null;
        }

        @Override
        public WebDriver newWindow(WindowType typeHint) {
            return null;
        }

        @Override
        public WebDriver defaultContent() {
            return null;
        }

        @Override
        public WebElement activeElement() {
            return null;
        }

        @Override
        public Alert alert() {
            return null;
        }
    }

    public static class PlaywrightWebdriverTimeouts implements Timeouts {

        Page page;

        public PlaywrightWebdriverTimeouts(Page page) {
            this.page = page;
        }

        @Override
        public Timeouts implicitlyWait(long time, TimeUnit unit) {
            return null;
        }

        @Override
        public Timeouts implicitlyWait(Duration duration) {
            page.setDefaultTimeout(duration.toMillis());
            return this;
        }

        @Override
        public Duration getImplicitWaitTimeout() {
            return null;
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

    public static class PpaywrightWebdriverWindow implements Window {

        Page page;

        public PpaywrightWebdriverWindow(Page page) {
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

        }

        @Override
        public void minimize() {

        }

        @Override
        public void fullscreen() {

        }
    }
}
