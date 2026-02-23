package org.brit.driver.adapters;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.microsoft.playwright.options.WaitForSelectorState.ATTACHED;

public class FindElementAdapter {
    private static final Pattern INVALID_SELECTOR = Pattern.compile("(is not a valid XPath expression|Unexpected token \".*?\" while parsing selector)");
    private static final Locator.WaitForOptions elementExists = new Locator.WaitForOptions().setState(ATTACHED);

    public static List<WebElement> findElements(Locator locator) {
        try {
            return locator.all()
                    .stream().map(PlaywrightWebElement::new)
                    .collect(Collectors.toUnmodifiableList());
        } catch (PlaywrightException e) {
            if (INVALID_SELECTOR.matcher(e.getMessage()).find()) {
                throw new InvalidSelectorException(e.getMessage());
            }
            throw e;
        }
    }

    public static WebElement findElement(Locator locator, By by) {
        try {
          locator.first().waitFor(elementExists);
        } catch (TimeoutError e) {
            throw new NoSuchElementException("Unable to locate element: " + by, e);
        } catch (PlaywrightException e) {
            if (INVALID_SELECTOR.matcher(e.getMessage()).find()) {
                throw new InvalidSelectorException(e.getMessage());
            }
            throw e;
        }
        return new PlaywrightWebElement(locator.first());
    }

}
