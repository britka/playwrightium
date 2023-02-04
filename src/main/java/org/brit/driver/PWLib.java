package org.brit.driver;

import com.microsoft.playwright.Locator;
import org.brit.driver.impl.PlaywrightElementImpl;
import org.brit.driver.impl.PlaywrightElementsCollectionImpl;

import java.net.URL;

public class PWLib {
    public static void open(String url) {
        PlayWrightDriver.getInstance().getPage().navigate(url);
    }

    public static void open(URL url) {
        open(url.toExternalForm());
    }

    public static void sleep(double ms) {
        PlayWrightDriver.getInstance().getPage().waitForTimeout(ms);
    }

    public static PlaywrightElement $(String cssLocator) {
        Locator locator = PlayWrightDriver.getInstance().getPage().locator(cssLocator);
        return new PlaywrightElementImpl(locator);
    }

    public static PlaywrightElement $x(String xpathLocator) {
        return $("xpath=" + xpathLocator);
    }

    public static PlaywrightElementsCollection $$(String cssLocator){
        return new PlaywrightElementsCollectionImpl(PlayWrightDriver.getInstance().getPage().locator(cssLocator));
    }

    public static PlaywrightElementsCollection $$x(String xpathLocator){
        return $$("xpath=" + xpathLocator);
    }

}
