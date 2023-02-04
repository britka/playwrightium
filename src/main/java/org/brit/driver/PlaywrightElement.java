package org.brit.driver;

import com.microsoft.playwright.Locator;
import org.brit.driver.impl.PlaywrightElementsCollectionImpl;

public interface PlaywrightElement {
    void click();
    PlaywrightElement setValue(String value);
    String getText();

    PlaywrightElement pressEnter();

    PlaywrightElement pressTab();
    PlaywrightElement pressEscape();

    String getInnerHTML();
    String getOuterHTML();
    String getAttribute(String attributeName);
    String getValue();
    PlaywrightElement selectRadio(String value);
    String getCssValue(String cssProperty);
    boolean isVisible();
    boolean isEnabled();

    // TODO add conditions

    String toString();

    PlaywrightElement parent();
    PlaywrightElement $(String cssLocator);
    PlaywrightElement $x(String xpathLocator);
    PlaywrightElementsCollectionImpl $$(String cssLocator);
    PlaywrightElementsCollectionImpl $$x(String xpathLocator);

    PlaywrightElement hover();

    PlaywrightElement dragAndDrop(PlaywrightElement elementToDragTo);

    Locator getWrappedLocator();

}
