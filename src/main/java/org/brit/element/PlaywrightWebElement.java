package org.brit.element;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BoundingBox;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaywrightWebElement extends RemoteWebElement {

    Locator locator;
    ElementHandle elementHandle;

    public PlaywrightWebElement(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void click() {
        locator.click();
    }

    public void clickWithAlert(Consumer<Dialog> clickWithAlertOptions) {
        locator
                .page()
                .onDialog(clickWithAlertOptions);
        locator.click();
    }

    public File download() {
        Download download = locator.page().waitForDownload(() -> {
            locator.click();
        });
        return download.path().toFile();
    }

    public void upload(File file) {
        locator.setInputFiles(file.toPath());
    }

    @Override
    public void submit() {
        locator.evaluate("locator => locator.submit();");
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        StringBuilder toSend = new StringBuilder();
        for (CharSequence charSequence : keysToSend) {
            toSend.append(charSequence);
        }
        if ("file".equals(locator.getAttribute("type"))) {
            locator.setInputFiles(Paths.get(toSend.toString()));
        } else {
            locator.type(toSend.toString());
        }
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
        // sometimes relative href attribute is returned without leading /
        if (name.equals("href"))
            return locator.evaluate("node => node.href").toString();
        String attributeValue = locator.getAttribute(name);
        if (attributeValue == null) {
            attributeValue = (String) locator.evaluate("node => node.%s".formatted(name));
        }
        return attributeValue;
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
        return new PlaywrightWebElement(getLocatorFromBy(by).first());
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
            case "partial link text", "link text" ->
                    locator.locator("a", new Locator.LocatorOptions().setHasText(value));
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

    public Locator getLocator() {
        return locator;
    }

    public ElementHandle getElementHandle() {
        Page page = locator.page();

        try {
            Field selector = locator.getClass().getDeclaredField("selector");
            selector.setAccessible(true);
            String selectorString = selector.get(locator).toString();
            elementHandle = page.querySelector(selectorString);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return elementHandle;
    }

    @Override
    public String getDomAttribute(String name) {
        return locator.getAttribute(name);
    }

}
