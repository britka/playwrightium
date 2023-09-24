package org.brit.additional;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class ClickAndConfirmReturnTextModal implements Command<String> {
    @Nullable
    @Override
    public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
        Locator locatorProxy = ((PlaywrightWebElement) proxy.getWrappedElement()).getLocator();
        Page page = locatorProxy.page();
        AtomicReference<String> str = new AtomicReference<>();
        page.onceDialog(dialog -> {
            str.set(dialog.message());
            dialog.accept();
        });
        locatorProxy.click();
        return str.get();
    }
}
