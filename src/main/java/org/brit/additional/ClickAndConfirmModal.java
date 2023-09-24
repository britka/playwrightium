package org.brit.additional;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.brit.element.PlaywrightWebElement;

import javax.annotation.Nullable;
import java.io.IOException;

public class ClickAndConfirmModal implements Command<SelenideElement> {

    @Nullable
    @Override
    public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
        Locator locatorProxy = ((PlaywrightWebElement) proxy.getWrappedElement()).getLocator();
        Page page = locatorProxy.page();
        page.onceDialog(Dialog::accept);
        locatorProxy.click();
        return proxy;
    }
}
