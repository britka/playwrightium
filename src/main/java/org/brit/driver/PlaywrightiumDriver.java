package org.brit.driver;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.Geolocation;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.ViewportSize;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.text.CaseUtils;
import org.brit.driver.adapters.JsExecutionAdapter;
import org.brit.driver.adapters.FindElementAdapter;
import org.brit.element.PlaywrightWebElement;
import org.brit.emulation.Device;
import org.brit.locators.ArialSearchOptions;
import org.brit.options.Browsers;
import org.brit.options.PlaywrightiumOptions;
import org.brit.options.TracingOptions;
import org.brit.permission.Permissions;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.microsoft.playwright.options.WaitForSelectorState.ATTACHED;

public class PlaywrightiumDriver extends RemoteWebDriver implements TakesScreenshot, Interactive {
    private Playwright playwright;
    private BrowserContext browserContext;
    @Getter
    protected Page page;

    private Page activePage;

    private Frame mainFrameCopy = null;
    private static final Locator.WaitForOptions elementExists = new Locator.WaitForOptions().setState(ATTACHED);
    private PlaywrightiumOptions options;
    private final static JsExecutionAdapter jsExecutionAdapter = new JsExecutionAdapter();

    @SneakyThrows
    public PlaywrightiumDriver() {
        this(new PlaywrightiumOptions());
    }

    public PlaywrightiumDriver(PlaywrightiumOptions options) {
        this.options = options;

        Boolean doNotDownloadBrowsers = this.options.getSkipDownloadBrowsers();

        if (doNotDownloadBrowsers != null && doNotDownloadBrowsers) {
            playwright = Playwright.create(new Playwright.CreateOptions().setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")));
        } else {
            playwright = Playwright.create();
        }

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        Boolean headless = (Boolean) this.options.getCapability("headless");
        launchOptions.setHeadless(headless).setDownloadsPath(Paths.get("downloads"));
        Device device = this.options.getEmulation();
        String browserType = (String) this.options.getCapability("browserName");
        Path recordVideoPath = (Path) this.options.getCapability("recordsFolder");
        Locale locale = this.options.getLocale();
        TimeZone timeZone = this.options.getTimeZone();
        Geolocation geolocation = this.options.getGeolocation();
        List<Permissions> permissions = this.options.getPermissions();
        Boolean enableTracing = this.options.getEnableTracing();
        TracingOptions tracingOptions = this.options.getTracingOptions();

        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions().setAcceptDownloads(true);
        newContextOptions.setIgnoreHTTPSErrors(options.getIgnoreHTTPSErrors());
        boolean recordVideo = this.options.getRecordVideo() != null && options.getRecordVideo();

        if (recordVideo) {
            if (recordVideoPath != null) {
                newContextOptions.setRecordVideoDir(recordVideoPath);
            } else {
                newContextOptions.setRecordVideoDir(Paths.get("build/videos"));
            }
        }

        if (locale != null) {
            newContextOptions.setLocale(locale.toLanguageTag());
        }
        if (timeZone != null) {
            newContextOptions.setTimezoneId(timeZone.getID());
        }
        if (geolocation != null) {
            newContextOptions.setGeolocation(geolocation);
        }
        if (permissions != null) {
            newContextOptions.setPermissions(permissions.stream().map(Permissions::getValue).toList());
        }

        if (Browsers.channelBrowsers.contains(browserType)) {
            launchOptions.setChannel(browserType);
        }


        if (device != null) {
            newContextOptions
                    .setDeviceScaleFactor(device.getDeviceScaleFactor())
                    .setHasTouch(device.isHasTouch())
                    .setIsMobile(device.isMobile())
                    .setUserAgent(device.getUserAgent())
                    .setViewportSize(device.getViewport());
            browserContext =
                    getBrowserType(device.getDefaultBrowserType())
                            .launch(launchOptions)
                            .newContext(newContextOptions);
        } else {
            browserContext = getBrowserType(browserType)
                    .launch(launchOptions)
                    .newContext(newContextOptions);
        }

        if (enableTracing != null && enableTracing) {
            if (tracingOptions == null) {
                browserContext.tracing().start(new TracingOptions().getStartOptions());
            } else {
                browserContext.tracing().start(tracingOptions.getStartOptions());
            }
        }
        page = browserContext.newPage();
    }


    private BrowserType getBrowserType(String browser) {
        switch (browser) {
            case "chromium", "chrome", "msedge", "chrome-beta", "msedge-beta", "msedge-dev" -> {
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

    /**
     * Init browser to run remotely. Fo example, on Selenoid, Selenium Grid or Aerokube Moon.
     * If you connect to the Moon, then you should specify connection via ws in options.
     *
     * @param connectUrl the url where you want to connect to. It can be as http as ws
     * @param options    option for connection
     */
    public PlaywrightiumDriver(String connectUrl, PlaywrightiumOptions options) {
        this.options = options;
        String browserName = options.getBrowserName();
        boolean headless = options.getHeadless();
        boolean getConnectionType = options.getConnectionByWS() != null && options.getConnectionByWS();


        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions().setAcceptDownloads(true);
        boolean recordVideo = options.getRecordVideo() != null && options.getRecordVideo();
        newContextOptions.setIgnoreHTTPSErrors(options.getIgnoreHTTPSErrors());

        if (recordVideo) {
            if (options.getRecordsFolder() == null) {
                newContextOptions.setRecordVideoDir(Paths.get("build/video"));
            } else {
                newContextOptions.setRecordVideoDir(options.getRecordsFolder());
            }
        }

        if (getConnectionType) {
            playwright = Playwright.create();
            String playwrightVersion = Playwright.class
                    .getClassLoader().getDefinedPackage("com.microsoft.playwright")
                    .getImplementationVersion();

            connectUrl = connectUrl.replace("https:", "wss:")
                    .replace("http:", "ws:")
                    .replace("/wd/hub", "");
            connectUrl = connectUrl + "/playwright/%s/playwright-%s?headless=%s&enableVNC=true&enableVideo=%s"
                    .formatted(browserName, playwrightVersion, headless, recordVideo);
            browserContext = getBrowserType(browserName)
                    .connect(connectUrl, new BrowserType.ConnectOptions().setExposeNetwork("*"))
                    .newContext(newContextOptions);
        } else {
            playwright = Playwright.create(new Playwright.CreateOptions()
                    .setEnv(Map.of("SELENIUM_REMOTE_URL", connectUrl)));
            browserContext = getBrowserType(browserName)
                    .launch(new BrowserType.LaunchOptions().setHeadless(headless))
                    .newContext(newContextOptions);
        }
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
        // Selenide uses getTitle() to check if the browser is alive, so it will be nice to properly re-throw the expected exception.
        try {
            return page.title();
        } catch (PlaywrightException e) {
            throw new UnreachableBrowserException(e.getMessage(), e);
        }
    }

    @Override
    public List<WebElement> findElements(By by) {
        return FindElementAdapter.findElements(getLocatorFromBy(by));
    }

    @Override
    public WebElement findElement(By by) {
        return FindElementAdapter.findElement(getLocatorFromBy(by), by);
    }

    private Locator getLocatorFromBy(By by) {
        String using = ((By.Remotable) by).getRemoteParameters().using();
        String value = ((By.Remotable) by).getRemoteParameters().value().toString();
        switch (using) {
            case "css selector" -> {
                return page.locator(value);
            }
            case "class name" -> {
                return page.locator("xpath=//*[@class='%s']".formatted(value));
            }
            case "xpath" -> {
                return page.locator("xpath=" + value);
            }
            case "tag name" -> {
                return page.locator("xpath=//" + value);
            }
            case "name" -> {
                return page.locator("[name='%s']".formatted(value));
            }
            case "partial link text" -> {
                return page.locator("xpath=//a[contains(.,'%s')]".formatted(value));
            }
            case "link text" -> {
                return page.locator("xpath=//a[text()='%s']".formatted(value));
            }
            case "id" -> {
                return page.locator("#%s".formatted(value));
            }
            default -> {
                List<Object> list = (List<Object>) ((By.Remotable) by).getRemoteParameters().value();
                return switch (using) {
                    case "getByRole" -> {
                        AriaRole role = (AriaRole) list.get(0);
                        ArialSearchOptions arialSearchOptions = ((ArialSearchOptions) list.get(1));
                        yield page.getByRole(role, convertOption(arialSearchOptions));
                    }
                    case "getByTestId" -> page.getByTestId((String) list.get(0));
                    case "getByAltText" -> page.getByAltText((String) list.get(0),
                            new Page.GetByAltTextOptions().setExact((Boolean) list.get(1)));
                    case "getByLabel" -> page.getByLabel((String) list.get(0),
                            new Page.GetByLabelOptions().setExact((Boolean) list.get(1)));
                    case "getByPlaceholder" -> page.getByPlaceholder((String) list.get(0),
                            new Page.GetByPlaceholderOptions().setExact((Boolean) list.get(1)));
                    case "getByText" -> page.getByText((String) list.get(0),
                            new Page.GetByTextOptions().setExact((Boolean) list.get(1)));
                    case "getByTitle" -> page.getByTitle((String) list.get(0),
                            new Page.GetByTitleOptions().setExact((Boolean) list.get(1)));
                    default -> null;
                };
            }
        }
    }

    private Page.GetByRoleOptions convertOption(ArialSearchOptions arialSearchOptions) {
        Object name = arialSearchOptions.getName();
        Page.GetByRoleOptions getByRoleOptions = new Page.GetByRoleOptions();
        if (arialSearchOptions.checked != null) {
            getByRoleOptions.setChecked(arialSearchOptions.getChecked());
        }
        if (arialSearchOptions.exact != null) {
            getByRoleOptions.setExact(arialSearchOptions.getExact());
        }
        if (arialSearchOptions.disabled != null) {
            getByRoleOptions.setDisabled(arialSearchOptions.getDisabled());
        }
        if (arialSearchOptions.expanded != null) {
            getByRoleOptions.setExpanded(arialSearchOptions.getExpanded());
        }
        if (arialSearchOptions.pressed != null) {
            getByRoleOptions.setPressed(arialSearchOptions.getPressed());
        }
        if (arialSearchOptions.selected != null) {
            getByRoleOptions.setSelected(arialSearchOptions.getSelected());
        }
        if (arialSearchOptions.includeHidden != null) {
            getByRoleOptions.setIncludeHidden(arialSearchOptions.getIncludeHidden());
        }
        if (arialSearchOptions.level != null) {
            getByRoleOptions.setLevel(arialSearchOptions.getLevel());
        }
        if (arialSearchOptions.name != null) {
            if (name instanceof Pattern) {
                getByRoleOptions.setName((Pattern) name);
            } else if (name instanceof String) {
                getByRoleOptions.setName((String) name);
            }
        }
        return getByRoleOptions;
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
        page.close();
        saveVideoIfNeeded();
        if (!page.isClosed()) {
            page.close();
        }
        if (this.options.getEnableTracing() != null && this.options.getEnableTracing()) {
            TracingOptions tracingOptions = this.options.getTracingOptions();
            if (tracingOptions == null) {
                browserContext.tracing().stop(new TracingOptions().getStopOptions());
            } else {
                browserContext.tracing().stop(this.options.getTracingOptions().getStopOptions());
            }
        }
        browserContext.close();
        playwright.close();
    }

    private void saveVideoIfNeeded() {
        if (options.getConnectionByWS() != null
                && options.getConnectionByWS()
                && options.getRecordVideo() != null
                && options.getRecordVideo()) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            page.video().saveAs(Path.of(options.getRecordsFolder().toString(), uuid + ".webm"));
        }
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
            try {
                Field guid = pageTemp.getClass().getSuperclass().getDeclaredField("guid");
                guid.setAccessible(true);
                handles.add(guid.get(pageTemp).toString());
            } catch (NoSuchFieldException | IllegalAccessException e) {
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
        return new PlaywrightWebdriverOptions();
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


    public class PlaywrightWebdriverOptions implements Options {

        public PlaywrightWebdriverOptions() {
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
            Frame frame;
            try {
                frame = page.frames().get(index);
            } catch (IndexOutOfBoundsException | TimeoutError e) {
                throw new NoSuchFrameException("No frame at index " + index);
            }
            setMainFrame(frame);
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver frame(String nameOrId) {
            Frame frame;
            try {
                Locator frameLocator = page.locator("[name='%s'], #%s".formatted(nameOrId, nameOrId));
                frame = page.frame(frameLocator.getAttribute("name"));
            } catch (TimeoutError e) {
                throw new NoSuchFrameException(nameOrId);
            }
            setMainFrame(frame);
            return PlaywrightiumDriver.this;
        }

        @Override
        public WebDriver frame(WebElement frameElement) {
            String nameOrId;
            try {
                nameOrId = frameElement.getAttribute("id");
                if (nameOrId == null || nameOrId.isEmpty()) {
                    nameOrId = frameElement.getAttribute("name");
                }
            } catch (TimeoutError e) {
                throw new NoSuchFrameException(frameElement.toString(), e);
            }
            if (nameOrId == null || nameOrId.isEmpty() ) {
                throw new NoSuchFrameException(frameElement.toString());
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
                        return PlaywrightiumDriver.this;
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new NoSuchWindowException("No such window: " + nameOrHandle);
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
            return (WebElement) PlaywrightiumDriver.this.executeScript("() => document.activeElement");
        }

        @Override
        public Alert alert() {
            return new PlaywrightuimAlert(PlaywrightiumDriver.this);
        }
    }


    public class PlaywrightWebdriverTimeouts implements Timeouts {
        public PlaywrightWebdriverTimeouts() {
        }

        @Override
        public Timeouts implicitlyWait(long time, TimeUnit unit) {
            long millis = Duration.of(time, unit.toChronoUnit()).toMillis();
            page.setDefaultTimeout(millis);
            page.context().setDefaultTimeout(millis);
            return this;
        }

        @Override
        public Timeouts implicitlyWait(Duration duration) {
            page.setDefaultTimeout(duration.toMillis());
            page.context().setDefaultTimeout(duration.toMillis());
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
            long millis = Duration.of(time, unit.toChronoUnit()).toMillis();
            page.setDefaultNavigationTimeout(millis);
            page.context().setDefaultNavigationTimeout(millis);
            return this;
        }

        @Override
        public Timeouts pageLoadTimeout(Duration duration) {
            page.setDefaultNavigationTimeout(duration.toMillis());
            page.context().setDefaultNavigationTimeout(duration.toMillis());
            return this;
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
                        keyToPress = switch (keyToPress) {
                            case "Left" -> "ArrowLeft";
                            case "Up" -> "ArrowUp";
                            case "Down" -> "ArrowDown";
                            case "Right" -> "ArrowRight";
                            default -> keyToPress;
                        };
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
                        keyToPress = switch (keyToPress) {
                            case "Left" -> "ArrowLeft";
                            case "Up" -> "ArrowUp";
                            case "Down" -> "ArrowDown";
                            case "Right" -> "ArrowRight";
                            default -> keyToPress;
                        };
                    } else {
                        keyToPress = Character.toString(ch);
                    }
                    page.keyboard().up(keyToPress);
                    break;
                }
                case "scroll": {
                    int x = (int) actionToApply.get("x");
                    int y = (int) actionToApply.get("y");
                    int deltaX = (int) actionToApply.get("deltaX");
                    int deltaY = (int) actionToApply.get("deltaY");
                    Object origin = actionToApply.get("origin");
                    if (origin instanceof PlaywrightWebElement element) {
                        page.mouse().wheel(element.getLocation().getX(), element.getLocation().getY());
                        page.mouse().wheel(x, y);
                        page.mouse().wheel(deltaX, deltaY);
                        break;
                    }
                    if (origin.equals("viewport")) {
                        page.mouse().wheel(x, y);
                        page.mouse().wheel(deltaX, deltaY);
                    }
                    break;
                }

            }
        }
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return jsExecutionAdapter.executeScript(page, script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return jsExecutionAdapter.executeAsyncScript(page, script, args);
    }

    public PlaywrightiumOptions getOptions() {
        return options;
    }
}
