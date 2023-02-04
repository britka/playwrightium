package org.brit.driver.impl;

import com.microsoft.playwright.Locator;
import org.brit.driver.PlayWrightDriver;
import org.brit.driver.PlaywrightElement;

public class PlaywrightElementImpl implements PlaywrightElement {
    private Locator elementLocator;

    public PlaywrightElementImpl(Locator elementLocator) {
        this.elementLocator = elementLocator;
    }

    @Override
    public void click() {
        elementLocator.click();
    }

    @Override
    public PlaywrightElement setValue(String value) {
        elementLocator.clear();
        elementLocator.fill(value);
        return this;
    }

    @Override
    public String getText() {
        return elementLocator.textContent();
    }

    @Override
    public PlaywrightElement pressEnter() {
        elementLocator.focus();
        elementLocator.press("Enter");
        return this;
    }

    @Override
    public PlaywrightElement pressTab() {
        elementLocator.focus();
        elementLocator.press("Tab");
        return this;
    }

    @Override
    public PlaywrightElement pressEscape() {
        elementLocator.focus();
        elementLocator.press("Escape");
        return this;
    }

    @Override
    public String getInnerHTML() {
        return elementLocator.innerHTML();
    }

    @Override
    public String getOuterHTML() {
        return elementLocator.getAttribute("outerHTML");
    }

    @Override
    public String getAttribute(String attributeName) {
        return elementLocator.getAttribute(attributeName);
    }

    @Override
    public String getValue() {
        return elementLocator.inputValue();
    }

    //TODO
    @Override
    public PlaywrightElement selectRadio(String value) {
        return null;
    }

    @Override
    public String getCssValue(String cssProperty) {
        return elementLocator.evaluate("node =>" +
                        "window.getComputedStyle(node).getPropertyValue('%s')"
                                .formatted(cssProperty))
                .toString();
    }

    @Override
    public boolean isVisible() {
        return elementLocator.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return elementLocator.isEnabled();
    }

    @Override
    public PlaywrightElement parent() {
        return new PlaywrightElementImpl(elementLocator.locator(".."));
    }

    @Override
    public PlaywrightElement $(String cssLocator) {
        return new PlaywrightElementImpl(elementLocator.locator(cssLocator));
    }

    @Override
    public PlaywrightElement $x(String xpathLocator) {
        return new PlaywrightElementImpl(elementLocator.locator("xpath=" + xpathLocator));
    }

    @Override
    public PlaywrightElementsCollectionImpl $$(String cssLocator) {
        return new PlaywrightElementsCollectionImpl(this.elementLocator.locator(cssLocator));
    }

    @Override
    public PlaywrightElementsCollectionImpl $$x(String xpathLocator) {
        return new PlaywrightElementsCollectionImpl(this.elementLocator.locator("xpath=" + xpathLocator));
    }

    @Override
    public PlaywrightElement hover() {
        elementLocator.hover();
        return this;
    }

    @Override
    public PlaywrightElement dragAndDrop(PlaywrightElement elementToDragTo) {
       elementLocator.dragTo(getWrappedLocator());
       return this;
    }

    @Override
    public Locator getWrappedLocator() {
        return elementLocator;
    }


}
