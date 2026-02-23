package org.brit.element;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import lombok.Getter;
import org.apache.commons.text.CaseUtils;
import org.brit.element.adapters.GetAttributeAdapter;
import org.brit.driver.adapters.FindElementAdapter;
import org.brit.locators.ArialSearchOptions;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author Serhii Bryt
 * WebElement implementation
 * @see org.openqa.selenium.WebElement
 */
public class PlaywrightWebElement extends RemoteWebElement {

    private static final Pattern NOT_CHECKBOX_OR_RADIO = Pattern.compile("Not a checkbox or radio button");

    @Getter
    private final Locator locator;

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
        if ("file".equals(locator.getAttribute("type"))) {
            StringBuilder toSend = new StringBuilder();
            for (CharSequence charSequence : keysToSend) {
                toSend.append(charSequence);
            }
            locator.setInputFiles(Paths.get(toSend.toString()));
        } else {
            StringBuilder toSend = new StringBuilder();
            for (CharSequence charSequence : keysToSend) {
                if (charSequence.length() == 1 && Keys.getKeyFromUnicode(charSequence.charAt(0)) != null) {
                    var keyToPress = CaseUtils
                            .toCamelCase(Keys.getKeyFromUnicode(
                                            charSequence.charAt(0)).name(),
                                    true,
                                    ' ');
                    keyToPress = switch (keyToPress) {
                        case "Left" -> "ArrowLeft";
                        case "Up" -> "ArrowUp";
                        case "Down" -> "ArrowDown";
                        case "Right" -> "ArrowRight";
                        default -> keyToPress;
                    };
                    locator.press(keyToPress);
                } else {
                    toSend.append(charSequence);
                }
            }
            locator.fill(toSend.toString());
        }
    }

    @Override
    public void clear() {
        locator.clear(new Locator.ClearOptions().setForce(true));
    }

    @Override
    public String getTagName() {
        return String.valueOf(locator.evaluate("node => node.tagName")).toLowerCase();
    }

    @Nullable
    @Override
    public String getAttribute(String name) {
        return GetAttributeAdapter.getAttribute(locator, name);
    }

    @Override
    public boolean isSelected() {
        try {
            return locator.isChecked();
        } catch (PlaywrightException e) {
            if (NOT_CHECKBOX_OR_RADIO.matcher(e.getMessage()).find()) {
                return false;
            }
            throw e;
        }
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
        return FindElementAdapter.findElements(getLocatorFromBy(by));
    }

    @Override
    public WebElement findElement(By by) {
        return FindElementAdapter.findElement(getLocatorFromBy(by), by);
    }

    @Nullable
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
            default -> {
                List<Object> list = (List<Object>) ((By.Remotable) by).getRemoteParameters().value();
                yield switch (using) {
                    case "getByRole" -> {
                        AriaRole role = (AriaRole) list.get(0);
                        ArialSearchOptions getByRoleOptions = ((ArialSearchOptions) list.get(1));
                        yield locator.getByRole(role, convertOption(getByRoleOptions));
                    }
                    case "getByTestId" -> locator.getByTestId((String) list.get(0));
                    case "getByAltText" -> locator.getByAltText((String) list.get(0),
                            new Locator.GetByAltTextOptions().setExact((Boolean) list.get(1)));
                    case "getByLabel" -> locator.getByLabel((String) list.get(0),
                            new Locator.GetByLabelOptions().setExact((Boolean) list.get(1)));
                    case "getByPlaceholder" -> locator.getByPlaceholder((String) list.get(0),
                            new Locator.GetByPlaceholderOptions().setExact((Boolean) list.get(1)));
                    case "getByText" -> locator.getByText((String) list.get(0),
                            new Locator.GetByTextOptions().setExact((Boolean) list.get(1)));
                    case "getByTitle" -> locator.getByTitle((String) list.get(0),
                            new Locator.GetByTitleOptions().setExact((Boolean) list.get(1)));
                    default -> null;
                };
            }
        };
    }

    private Locator.GetByRoleOptions convertOption(ArialSearchOptions arialSearchOptions) {
        Object name = arialSearchOptions.getName();
        Locator.GetByRoleOptions getByRoleOptions = new Locator.GetByRoleOptions();
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
    public boolean isDisplayed() {
        return locator.isVisible();
    }

    @Override
    public Point getLocation() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Point((int) boundingBox.x, (int) boundingBox.y);
    }

    @Override
    public Dimension getSize() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Dimension((int) boundingBox.width, (int) boundingBox.height);
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(getLocation(), getSize());
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
        return target.convertFromPngBytes(screenshot);
    }

    public ElementHandle getElementHandle() {
        Page page = locator.page();

        try {
            Field selector = locator.getClass().getDeclaredField("selector");
            selector.setAccessible(true);
            String selectorString = selector.get(locator).toString();
            return page.querySelector(selectorString);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public String getDomAttribute(String name) {
        return GetAttributeAdapter.getDomAttribute(locator, name);
    }

    @Nullable
    @Override
    public String getDomProperty(String name) {
        return GetAttributeAdapter.getDomProperty(locator, name);
    }

}
