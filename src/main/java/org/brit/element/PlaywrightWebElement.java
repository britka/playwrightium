package org.brit.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import org.openqa.selenium.*;

import java.util.*;
import java.util.stream.Collectors;

public class PlaywrightWebElement implements WebElement {

    Locator locator;

    public PlaywrightWebElement(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void click() {
        locator.click();
    }

    @Override
    public void submit() {

    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        StringBuilder toSend = new StringBuilder();
        for (CharSequence charSequence: keysToSend){
            toSend.append(charSequence);
        }
        locator.type(toSend.toString());
    }

    @Override
    public void clear() {
        locator.clear();
    }

    @Override
    public String getTagName() {
        return locator.evaluate("node => node.tagName").toString();
    }

    @Override
    public String getAttribute(String name) {
        return locator.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return locator.isChecked();
    }

    @Override
    public boolean isEnabled() {
        return locator.isEnabled();
    }

    @Override
    public String getText() {
        return locator.textContent();
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
            case "css selector" -> locator.locator(value);
            case "class name" -> locator.locator("xpath=.//*[@class='%s']".formatted(value));
            case "xpath" -> locator.locator("xpath=" + value);
            case "tag name" -> locator.locator("xpath=.//" + value);
            case "name" -> locator.locator("[name='%s']".formatted(value));
            case "partial link text" -> locator.locator("xpath=.//a[contains(.,'%s')]".formatted(value));
            case "link text" -> locator.locator("xpath=.//a[text()='%s']".formatted(value));
            case "id" -> locator.locator("#%s".formatted(value));
            default -> null;
        };
    }

    @Override
    public boolean isDisplayed() {
        return locator.isVisible();
    }

    @Override
    public Point getLocation() {
        BoundingBox boundingBox = locator.boundingBox();
        Point point = new Point((int) boundingBox.x, (int) boundingBox.y);
        return point;
    }

    @Override
    public Dimension getSize() {
        BoundingBox boundingBox = locator.boundingBox();
        Dimension dimension = new Dimension((int) boundingBox.width, (int) boundingBox.height);
        return dimension;
    }

    @Override
    public Rectangle getRect() {
        Rectangle rectangle = new Rectangle(getLocation(), getSize());
        return rectangle;
    }

    @Override
    public String getCssValue(String propertyName) {
        return locator
                .evaluate("element => " +
                        "window.getComputedStyle(element).getPropertyValue('%s')"
                                .formatted(propertyName))
                .toString();

    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        byte[] screenshot = locator.screenshot();

        if (target.getClass() == OutputType.FILE.getClass()) {
            return target.convertFromPngBytes(screenshot);
        } else if (target.getClass() == OutputType.BYTES.getClass()) {
            return (X) screenshot;
        } else if (target.getClass() == OutputType.BASE64.getClass()) {
            return (X) Base64.getEncoder().encodeToString(screenshot);
        }
        return null;
    }
}
