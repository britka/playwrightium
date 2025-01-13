package org.brit.additional;

import com.microsoft.playwright.Locator;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @author Serhii Bryt
 *
 * This is implementation of ISelect interface
 * It is made to act like in Webdriver
 * In Selenide it is not used.
 * @see org.openqa.selenium.support.ui.ISelect
 */

public class PlaywrightiumSelect implements ISelect {

    Locator element;
    private final boolean isMulti;


    public PlaywrightiumSelect(WebElement element) {
        this.element = ((PlaywrightWebElement) element).getLocator();
        String multiple = this.element.getAttribute("multiple");
        isMulti = (multiple != null && !"false".equals(multiple));
    }

    @Override
    public boolean isMultiple() {
        return isMulti;
    }

    @Override
    public List<WebElement> getOptions() {
        return element
                .locator("option")
                .all()
                .stream().map(PlaywrightWebElement::new)
                .collect(toUnmodifiableList());
    }

    @Override
    public List<WebElement> getAllSelectedOptions() {
        return element
                .locator("option")
                .all()
                .stream()
                .filter(locator -> ((Boolean) locator.evaluate("node => node.selected")))
                .map(PlaywrightWebElement::new)
                .collect(toUnmodifiableList());
    }

    private Locator getOptionByIndex(int index) {
        return element
                .locator("option")
                .all()
                .get(index);
    }

    private Locator getOptionByValue(String value) {
        return element
                .locator("option")
                .all()
                .stream()
                .filter(p -> p.evaluate("node => node.value").equals(value))
                .findFirst()
                .get();
    }

    @Override
    public WebElement getFirstSelectedOption() {
        return getAllSelectedOptions().get(0);
    }

    @Override
    public void selectByVisibleText(String text) {
        if (isMultiple()) {
            Set<String> array = getAllSelectedOptions()
                    .stream()
                    .map(WebElement::getText)
                    .map(String::trim)
                    .collect(toSet());
            array.add(text);
            element.selectOption(array.toArray(new String[]{}));
        } else {
            element.selectOption(text);
        }
    }

    @Override
    public void selectByContainsVisibleText(String text) {
        if (isMultiple()) {
            Set<String> array = getAllSelectedOptions().stream()
                    .map(WebElement::getText)
                    .map(String::trim)
                    .collect(toSet());
            array.add(text);
            element.selectOption(array.toArray(new String[]{}));
        } else {
            getOptions().stream()
                    .map(WebElement::getText)
                    .map(String::trim)
                    .filter(label -> label.contains(text))
                    .findFirst()
                    .ifPresent(label -> element.selectOption(label));
        }
    }

    /**
     * Select option by index. Index is 0 based
     *
     * @param index The option at this index will be selected
     */
    @Override
    public void selectByIndex(int index) {
        String string = getOptionByIndex(index).textContent().trim();
        selectByVisibleText(string);
    }

    @Override
    public void selectByValue(String value) {
        String string = getOptionByValue(value).textContent().trim();
        selectByVisibleText(string);
    }

    @Override
    public void deselectAll() {
        element.evaluate("node => node.selectedIndex = -1");
    }

    @Override
    public void deselectByValue(String value) {
        String string = getOptionByValue(value).textContent().trim();
        String[] array = getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.equals(string))
                .toArray(String[]::new);
        deselectAll();
        element.selectOption(array);
    }

    @Override
    public void deselectByIndex(int index) {
        String string = getOptionByIndex(index).textContent().trim();
        String[] array = getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.equals(string))
                .toArray(String[]::new);
        deselectAll();
        element.selectOption(array);
    }

    @Override
    public void deselectByVisibleText(String text) {
        deselect(textElement -> !textElement.equals(text.trim()));
    }

    @Override
    public void deSelectByContainsVisibleText(String text) {
        deselect(textElement -> !textElement.contains(text.trim()));
    }

    private void deselect(Predicate<String> predicate) {
        String[] array = getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(predicate)
                .toArray(String[]::new);
        deselectAll();
        element.selectOption(array);
    }
}
