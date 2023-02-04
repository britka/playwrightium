package org.brit.driver;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import org.openqa.selenium.*;

import java.util.*;

public class PWWebElement implements WebElement {

    Locator locator;

    public PWWebElement(Locator locator) {
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
        return Collections.unmodifiableList(new LinkedList<PWWebElement>());
    }

    @Override
    public WebElement findElement(By by) {
        String using = ((By.Remotable) by).getRemoteParameters().using();
        String value = ((By.Remotable) by).getRemoteParameters().value().toString();
        return switch (using) {
            case "css selector" -> new PWWebElement(locator.locator(value));
            case "class name" -> new PWWebElement(locator.locator("xpath=.//*[@class='%s']".formatted(value)));
            case "xpath" -> new PWWebElement(locator.locator("xpath=" + value));
            case "tag name" -> new PWWebElement(locator.locator("xpath=.//" + value));
            case "name" -> new PWWebElement(locator.locator("[name='%s']".formatted(value)));
            case "partial link text" -> new PWWebElement(locator.locator("xpath=.//a[contains(.,'%s')]".formatted(value)));
            case "link text" -> new PWWebElement(locator.locator("xpath=.//a[text()='%s']".formatted(value)));
            case "id" -> new PWWebElement(locator.locator("#%s".formatted(value)));
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
        return null;
    }
}
