package org.brit.additional;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.SelectOptionByTextOrIndex;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import org.brit.element.PlaywrightWebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.commands.Util.merge;


@ParametersAreNonnullByDefault
public class PlaywrightiumSelectOption extends SelectOptionByTextOrIndex {

    @Nullable
    @Override
    public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
        Arguments arguments = new Arguments(args);
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Missing arguments");
        } else if (args[0] instanceof String firstOptionText) {
            List<String> texts = merge(firstOptionText, arguments.nth(1));
            selectOptionsByTexts(selectField, texts);
            return null;
        } else if (args[0] instanceof Integer firstOptionIndex) {
            int[] otherIndexes = arguments.nth(1);
            selectOptionsByIndexes(selectField, merge(firstOptionIndex, otherIndexes));
            return null;
        } else {
            throw new IllegalArgumentException("Unsupported argument (expected String or Integer): " + Arrays.toString(args));
        }
    }

    private void selectOptionsByTexts(WebElementSource selectField, List<String> texts) {
        Locator locator = ((PlaywrightWebElement) selectField.getWebElement()).getLocator();
        locator.selectOption(texts.toArray(new String[]{}));
    }

    private void selectOptionsByIndexes(WebElementSource selectField, List<Integer> indexes) {
        Locator locator = ((PlaywrightWebElement) selectField.getWebElement()).getLocator();
        SelectOption[] array = indexes.stream().map(p -> new SelectOption().setIndex(p))
                .toArray(SelectOption[]::new);
        locator.selectOption(array);
    }
}
