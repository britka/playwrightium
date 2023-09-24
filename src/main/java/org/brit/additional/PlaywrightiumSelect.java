package org.brit.additional;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import org.brit.element.PlaywrightWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;

import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<WebElement> getAllSelectedOptions() {
        return element
                .locator("option[selected]")
                .all()
                .stream().map(PlaywrightWebElement::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public WebElement getFirstSelectedOption() {
        return new PlaywrightWebElement(element
                .locator("option[selected]")
                .first());
    }

    @Override
    public void selectByVisibleText(String text) {
        element.selectOption(text);
    }

    @Override
    public void selectByIndex(int index) {
        element.selectOption(new SelectOption().setIndex(index));
    }

    @Override
    public void selectByValue(String value) {
        element.selectOption(new SelectOption().setValue(value));
    }

    @Override
    public void deselectAll() {
        getAllSelectedOptions()
                .forEach(WebElement::click);
    }

    @Override
    public void deselectByValue(String value) {
        getAllSelectedOptions()
                .stream()
                .filter(p -> p.getAttribute("value").equals(value))
                .findFirst()
                .get()
                .click();
    }

    @Override
    public void deselectByIndex(int index) {
        getOptions()
                .get(index)
                .click();
    }

    @Override
    public void deselectByVisibleText(String text) {
        getAllSelectedOptions()
                .stream()
                .filter(p -> p.getText().equals(text))
                .findFirst()
                .get()
                .click();
    }
}
