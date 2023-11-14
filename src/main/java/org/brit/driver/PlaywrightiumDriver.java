package org.brit.driver;

import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.text.CaseUtils;
import org.brit.element.PlaywrightWebElement;
import org.brit.options.PlaywrightWebdriverOptions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaywrightiumDriver extends RemoteWebDriver implements TakesScreenshot, Interactive {
    private Playwright playwright;
    private BrowserContext browserContext;
    private Page page;

    private Page activePage;

    private Frame mainFrameCopy = null;


    public PlaywrightiumDriver() {
        playwright = Playwright.create();
        browserContext = getBrowserType("chromium")
                .launch(new BrowserType.LaunchOptions().setHeadless(false).setDownloadsPath(Paths.get("downloads")))
                .newContext(new Browser.NewContextOptions().setViewportSize(null).setAcceptDownloads(true));
        page = browserContext.newPage();
    }

    public PlaywrightiumDriver(PlaywrightWebdriverOptions options) {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        Boolean headless = (Boolean) options.getCapability("headless");
        launchOptions.setHeadless(headless).setDownloadsPath(Paths.get("downloads"));
        String browserType = (String) options.getCapability("browserName");
        //    .setChannel("chrome");


        browserContext = getBrowserType(browserType)
                .launch(launchOptions)
                .newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
        page = browserContext.newPage();
        // page.setViewportSize(1024, 768);
    }


    private BrowserType getBrowserType(String browser) {
        switch (browser) {
            case "chromium" -> {
                return playwright.chromium();
            }
            case "firefox" -> {
                return playwright.firefox();
            }
            case "webkit" -> {
                return playwright.webkit();
            }
            default -> throw new IllegalStateException("Unexpected value: " + browser);
        }
    }

    public PlaywrightiumDriver(String connectUrl) {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();

        connectUrl = connectUrl.replace("https", "wss")
                .replace("http:", "ws:")
                .replace("/wd/hub", "");
        connectUrl = connectUrl + "/playwright/chromium/playwright-1.36.0?headless=true";

        // Boolean headless = (Boolean) capabilities.getCapability("headless");
        launchOptions.setHeadless(false).setDownloadsPath(Paths.get("downloads"));
        browserContext = playwright
                .chromium()
                .connect(connectUrl)
                .newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
        page = browserContext.newPage();
    }

    @Override
    public void get(String url) {
        page.navigate(url);
    }

    @Override
    public String getCurrentUrl() {
        return page.evaluate("page => document.URL;").toString();
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
        return new PlaywrightWebElement(getLocatorFromBy(by).first());
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
        page.close();
    }

    @Override
    public void quit() {
        playwright.close();
    }

    @Override
    public Set<String> getWindowHandles() {
        Set<String> handles = new LinkedHashSet<>();
        try {
            PlaywrightiumDriver.this.page.bringToFront();
        } catch (Exception ignore) {

        }
        List<Page> pages = PlaywrightiumDriver.this.page.context().pages();
        for (Page pageTemp : pages) {
            Field guid = null;
            try {
                guid = pageTemp.getClass().getSuperclass().getDeclaredField("guid");
                guid.setAccessible(true);
                handles.add(guid.get(pageTemp).toString());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return handles;
    }

    @Override
    public String getWindowHandle() {
        try {
            Field guid = page.getClass().getSuperclass().getDeclaredField("guid");
            guid.setAccessible(true);
            return guid.get(page).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TargetLocator switchTo() {
        return new PlaywrightWebdriverTargetLocator();
    }

    @Override
    public Navigation navigate() {
        return new PlaywrightWebdriverNavigation();
    }

    @Override
    public Options manage() {
        return new PlawrightWebdriverOptions();
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


    public class PlawrightWebdriverOptions implements Options {

        public PlawrightWebdriverOptions() {
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
            return new PlaywrightWebdriverTimeouts();
        }

        @Override
        public Window window() {
            return new PpaywrightWebdriverWindow();
        }

        @Override
        public Logs logs() {
            return null;
        }
    }


    public class PlaywrightWebdriverNavigation implements Navigation {

        public PlaywrightWebdriverNavigation() {

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

    public class PlaywrightWebdriverTargetLocator implements TargetLocator {

        @Override
        public WebDriver frame(int index) {
            Frame frame = page.frames().get(index);
            setMainFrame(frame);
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver frame(String nameOrId) {
            Locator frameLocator = page.locator("[name='%s'], #%s".formatted(nameOrId, nameOrId));
            Frame frame = page.frame(frameLocator.getAttribute("name"));
            setMainFrame(frame);
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver frame(WebElement frameElement) {
            PlaywrightWebElement element = (PlaywrightWebElement) frameElement;
            String nameOrId = element.getLocator().getAttribute("id");
            if (nameOrId == null) {
                nameOrId = element.getLocator().getAttribute("name");
            }
            return frame(nameOrId);
        }

        private void setMainFrame(Frame frame) {
            if (mainFrameCopy == null) {
                mainFrameCopy = page.mainFrame();
            }
            try {
                Field mainFrame = page.getClass().getDeclaredField("mainFrame");
                mainFrame.setAccessible(true);
                mainFrame.set(page, frame);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public WebDriver parentFrame() {
            page.mainFrame().parentFrame();
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver window(String nameOrHandle) {
            List<Page> pages = page.context().pages();
            for (Page pageElement : pages) {
                try {
                    Field guid = pageElement.getClass().getSuperclass().getDeclaredField("guid");
                    guid.setAccessible(true);
                    String evaluate = (String) pageElement.evaluate("page => window.name");
                    if (guid.get(pageElement).toString().equals(nameOrHandle) || nameOrHandle.equals(evaluate)) {
                        pageElement.bringToFront();
                        PlaywrightiumDriver.this.page = pageElement;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver newWindow(WindowType typeHint) {

            return null;
        }

        @Override
        public WebDriver defaultContent() {
            if (mainFrameCopy != null) {
                try {
                    Field mainFrame = page.getClass().getDeclaredField("mainFrame");
                    mainFrame.setAccessible(true);
                    mainFrame.set(page, mainFrameCopy);
                    mainFrameCopy = null;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebElement activeElement() {
            ElementHandle element = page.evaluateHandle("() => document.activeElement").asElement();
            return getPlaywrightElement(element);
        }

        @Override
        public Alert alert() {
            return new PlaywrightuimAlert();
        }
    }

    private class PlaywrightuimAlert implements Alert {

        private AtomicReference<String> text = new AtomicReference<>();

        LinkedList<AlertAction> alertActions = new LinkedList<>();

        Consumer<Dialog> handler;


        public PlaywrightuimAlert() {
            handler = new Consumer<Dialog>() {
                @Override
                public void accept(Dialog dialog) {
                    for (AlertAction action : alertActions) {
                        switch (action.action()) {
                            case "dismiss" -> {
                                page.offDialog(this);
                                dialog.dismiss();
                            }
                            case "accept" -> dialog.accept();
                            case "getText" -> text.set(dialog.message());
                            case "sendKeys" -> dialog.accept(action.sendKeys());
                        }
                    }
                    page.offDialog(this);
                }
            };
            alertActions.add(new AlertAction().action("getText"));
            addToHandler();
        }

        @Override
        public void dismiss() {
            page.offDialog(handler);
            alertActions.add(new AlertAction().action("dismiss"));
            addToHandler();
        }

        @Override
        public void accept() {
            page.offDialog(handler);
            if (alertActions.stream().noneMatch(p -> p.action().equals("sendKeys"))) {
                alertActions.add(new AlertAction().action("accept"));
            }
            addToHandler();
        }

        @Override
        public String getText() {
            return text.get();
        }

        @Override
        public void sendKeys(String keysToSend) {
            page.offDialog(handler);
            alertActions.add(new AlertAction().action("sendKeys").sendKeys(keysToSend));
            addToHandler();
        }

        private void addToHandler() {
            page.onDialog(handler);
        }

        @Data
        @Accessors(fluent = true)
        private static class AlertAction {
            public String action;
            public String sendKeys;
        }
    }


    public class PlaywrightWebdriverTimeouts implements Timeouts {
        public PlaywrightWebdriverTimeouts() {
        }

        @Override
        public Timeouts implicitlyWait(long time, TimeUnit unit) {
            page.setDefaultTimeout(Duration.of(time, unit.toChronoUnit()).toMillis());
            return this;
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

    public class PpaywrightWebdriverWindow implements Window {

        public PpaywrightWebdriverWindow() {
        }

        @Override
        public Dimension getSize() {
            ViewportSize viewportSize = PlaywrightiumDriver.this.page.viewportSize();
            return new Dimension(viewportSize.width, viewportSize.height);
        }

        @Override
        public void setSize(Dimension targetSize) {
            PlaywrightiumDriver.this.page.setViewportSize(targetSize.getWidth(), targetSize.getHeight());
        }

        @Override
        public Point getPosition() {
            Integer x = (Integer) page.evaluate("() => window.screenX");
            Integer y = (Integer) page.evaluate("() => window.screenY");
            return new Point(x, y);
        }

        @Override
        public void setPosition(Point targetPosition) {
            page.evaluate("() => window.moveTo(%s,%s);".formatted(targetPosition.getX(), targetPosition.getY()));
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

    @Override
    public void perform(Collection<Sequence> actions) {
        List<Sequence> list = new ArrayList<>(actions);
        int sizeOfActions = ((LinkedList) list.get(0).encode().get("actions")).size();
        List<List<HashMap<String, Object>>> newActions = new ArrayList<>();
        for (int i = 0; i < sizeOfActions; i++) {
            List<HashMap<String, Object>> neList = new ArrayList<>();
            for (Sequence sequence : list) {
                neList.add(((LinkedList<HashMap<String, Object>>) sequence.encode().get("actions")).get(i));
            }
            newActions.add(neList);
        }

        for (List<HashMap<String, Object>> currentActionList : newActions) {
            HashMap<String, Object> actionToApply = new HashMap<>();
            for (HashMap<String, Object> map : currentActionList) {
                if (map.get("type").equals("pause")) {
                    if ((Long) map.get("duration") != 0) {
                        actionToApply = map;
                        break;
                    }
                } else {
                    actionToApply = map;
                    break;
                }
                actionToApply = map;
            }
            switch (actionToApply.get("type").toString()) {
                case "pause": {
                    page.waitForTimeout((Long) actionToApply.get("duration"));
                    break;
                }
                case "pointerDown": {
                    int button = (int) actionToApply.get("button");
                    Mouse.DownOptions downOptions = new Mouse.DownOptions();
                    switch (button) {
                        case 0 -> downOptions.setButton(MouseButton.LEFT);
                        case 2 -> downOptions.setButton(MouseButton.RIGHT);
                        case 1 -> downOptions.setButton(MouseButton.MIDDLE);
                    }
                    page.mouse().down(downOptions);
                    break;
                }
                case "pointerUp": {
                    int button = (int) actionToApply.get("button");
                    Mouse.UpOptions upOptions = new Mouse.UpOptions();
                    switch (button) {
                        case 0 -> upOptions.setButton(MouseButton.LEFT);
                        case 2 -> upOptions.setButton(MouseButton.RIGHT);
                        case 1 -> upOptions.setButton(MouseButton.MIDDLE);
                    }
                    page.mouse().up(upOptions);
                    break;
                }
                case "pointerMove": {
                    PlaywrightWebElement element = (PlaywrightWebElement) actionToApply.get("origin");
                    int x = (int) actionToApply.get("x");
                    int y = (int) actionToApply.get("y");
                    if (element == null) {
                        page.mouse().move(x, y);
                    } else {
                        element.getLocator().hover();
                    }
                    break;
                }
                case "keyDown": {

                    String value = actionToApply.get("value").toString();
                    char ch = value.toCharArray()[0];
                    String keyToPress = "";
                    if (Keys.getKeyFromUnicode(ch) != null) {
                        keyToPress = CaseUtils
                                .toCamelCase(Keys.getKeyFromUnicode(ch).name(), true, ' ');
                        page.keyboard().down(keyToPress);
                    } else {
                        keyToPress = Character.toString(ch);
                        page.keyboard().press(keyToPress);
                    }
                    break;
                }
                case "keyUp": {
                    String value = actionToApply.get("value").toString();

                    char ch = value.toCharArray()[0];
                    String keyToPress = "";
                    if (Keys.getKeyFromUnicode(ch) != null) {
                        keyToPress = CaseUtils
                                .toCamelCase(Keys.getKeyFromUnicode(ch).name(), true, ' ');
                    } else {
                        keyToPress = Character.toString(ch);
                    }
                    page.keyboard().up(keyToPress);
                    break;
                }
                //TODO add scroll action
                case "scroll": {
                    break;
                }

            }
        }
    }

    @Override
    public Object executeScript(String script, Object... args) {
        if (args.length > 0) {
            var arguments = transformArguments(args);
            JSHandle jsHandle = page.evaluateHandle("(arguments) => " + script.replaceFirst("return", ""), arguments);
            if (Boolean.parseBoolean(jsHandle.evaluate("node => node instanceof HTMLCollection").toString())) {
                int length = (int) page.evaluate("node => node.length", jsHandle);
                List<PlaywrightWebElement> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    list.add(getPlaywrightElement(page.evaluateHandle("node => node.item(%s)".formatted(i), jsHandle).asElement()));
                }
                return list;
            } else if (Boolean.parseBoolean(jsHandle.evaluate("node => node instanceof HTMLElement").toString())) {
                return getPlaywrightElement(jsHandle.asElement());
            } else if (Boolean.parseBoolean(jsHandle.evaluate("node => typeof node === 'string' || node instanceof String").toString())) {
                return jsHandle.toString();
            } else if (Boolean.parseBoolean(jsHandle.evaluate("node => node instanceof Array").toString())) {
                int length = (int) page.evaluate("node => node.length", jsHandle);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    Object evaluate = page.evaluate("node => node[%s]".formatted(i), jsHandle);
                    list.add(evaluate != null ? evaluate.toString() : null);
                }
                return list;
            }
        } else {
            return page.evaluate(script.replaceFirst("return", ""));
        }
        return Map.of();
    }

    private List<Object> transformArguments(Object... args) {
        List<Object> result = new LinkedList<>();
        for (int i = 0; i < args.length; i++) {
            ElementHandle elementHandle = null;
            if (args[i] instanceof WebElementSource) {
                elementHandle = ((PlaywrightWebElement) (((WebElementSource) args[i]).getWebElement())).getLocator().elementHandle();
            } else if (args[i] instanceof WrapsElement) {
                elementHandle = ((PlaywrightWebElement) (((WrapsElement) args[i]).getWrappedElement())).getElementHandle();
            } else if (args[i] instanceof WebElement) {
                Locator locator = ((PlaywrightWebElement) args[i]).getLocator();
                elementHandle = locator.elementHandle();
            } else if (args[i] instanceof Collection<?>) {
                ArrayList<Object> arrayList = new ArrayList<>((Collection<?>) args[i]);
                if (!arrayList.isEmpty() && arrayList.get(0) instanceof WebElement) {
                    List<ElementHandle> list = arrayList.stream().map(e -> {
                        Locator locator = ((PlaywrightWebElement) e).getLocator();
                        return locator.elementHandle();
                    }).toList();
                    result.add(list);
                    continue;
                }
            }
            if (elementHandle != null) {
                result.add(elementHandle);
            } else {
                if (args[i] instanceof Collection<?>) {
                    ArrayList<Object> objects = new ArrayList<>();
                    objects.addAll((Collection<?>) args[i]);
                    result.add(objects);
                } else {
                    result.add(args[i]);
                }
            }
        }
        return result;
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (args.length > 0) {
            return page.evaluate("async () => {" + script.replace("return", "") + "}", transformArguments(args));
        } else {
            return page.evaluate("async () => {" + script.replace("return", "") + "}");
        }
    }


    public PlaywrightWebElement getPlaywrightElement(ElementHandle node) {
        String string = page.evaluate("""
                node =>
                {
                    names = [];                
                    do {
                        index = 0;
                        cursorElement = node;
                        while (cursorElement !== null) {
                            ++index;
                            cursorElement = cursorElement.previousElementSibling;
                        }
                        names.unshift(node.tagName + ":nth-child(" + index + ")");
                        node = node.parentElement;
                    } while (node !== null);
                                
                    return names.join(" > ");
                }
                """, node).toString();

        Locator locator = page.locator(string);
        return new PlaywrightWebElement(locator);

    }

//    @Override
//    public Pdf print(PrintOptions printOptions) throws WebDriverException {
//        Page.PdfOptions pdfOptions = new Page.PdfOptions();
//        //pdfOptions.
//        printOptions.
//        page.pdf()
//    }
}
