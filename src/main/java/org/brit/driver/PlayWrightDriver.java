package org.brit.driver;

import com.microsoft.playwright.*;

public class PlayWrightDriver {
    private static PlayWrightDriver instance = null;
    private ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private ThreadLocal<BrowserContext> browserContextThreadLocal = new ThreadLocal<>();
    private ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public static synchronized PlayWrightDriver getInstance() {
        if (instance == null){
            instance = new PlayWrightDriver();
        }
        return instance;
    }

    public Playwright getNewPlayWright(){
        if (playwrightThreadLocal.get() == null){
            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);
        }
        return playwrightThreadLocal.get();
    }

    public BrowserContext getContext(){
        if (playwrightThreadLocal.get() == null){
            getNewPlayWright();
        }
        if (browserContextThreadLocal.get() == null) {
            BrowserContext browserContext = getBrowserType()
                    .launch(getLaunchOptions())
                    .newContext(getContextOptions());
            browserContextThreadLocal.set(browserContext);
        }
        return browserContextThreadLocal.get();
    }

    public Page getPage(){
        if (pageThreadLocal.get() == null){
            pageThreadLocal.set(getContext().newPage());
        }
        return pageThreadLocal.get();
    }


    private BrowserType getBrowserType(){
        Playwright playwright = playwrightThreadLocal.get();
        switch (Configuration.browser) {
            case "chromium" -> {
                return playwright.chromium();
            }
            case "firefox" -> {
                return playwright.firefox();
            }
            case "safari" -> {
                return playwright.webkit();
            }
        }
        return null;
    }


    private BrowserType.LaunchOptions getLaunchOptions() {
        BrowserType.LaunchOptions launchOptions = new BrowserType
                .LaunchOptions();
        return launchOptions
                .setHeadless(Configuration.headless)
                .setDownloadsPath(Configuration.downloadsPath)
                .setTimeout(Configuration.defaultTimeout)
                .setSlowMo(Configuration.slowMoTimeout);
    }

    private Browser.NewContextOptions getContextOptions(){
        return new Browser.NewContextOptions()
                .setBaseURL(Configuration.baseUrl)
                .setAcceptDownloads(Configuration.acceptDownloads);
    }


}
